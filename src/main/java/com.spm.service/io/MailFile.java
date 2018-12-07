package com.spm.service.io;

import com.spm.service.Mail;
import com.spm.service.MailAtt;

import java.io.*;
import java.util.Iterator;

/**
 * Store and retrieve a mail into and from its corresponding file.
 *
 * @author BoxCatGarden
 */
public class MailFile {

    /**
     * prepare a buffer for .read()
     */
    private static char[] cbuf = new char[1024];
    /**
     * the file corresponding to the mail
     */
    private File file;

    /**
     * Default constructor
     */
    public MailFile() {
    }

    /**
     * To open the file for the mail.
     *
     * @param filename The filename of the file corresponding to the mail.
     */
    public void open(String filename) {
        file = new File(filename);
    }

    /**
     * @param mail The mail to be stored in the file.
     */
    public void write(Mail mail) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(mail.getFrom() + "\n");
            writer.write(mail.getTo() + "\n");
            writer.write(mail.getSub() + "\n");
            writer.write(mail.getTsp() + "\n");
            writer.write(mail.getText().length() + "\n");
            Iterator<MailAtt> it = mail.getAttList().iterator();
            while (it.hasNext()) {
                MailAtt att = it.next();
                writer.write("\"" + att.getAttName() + "\"\"" + att.getFilename() + "\"\n");
            }
            writer.write("\n" + mail.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The mail retrieved from the file.
     */
    public Mail read() {
        Mail mail = new Mail();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            //parse header
            mail.setFrom(readLine(bufferedReader));
            mail.setTo(readLine(bufferedReader));
            mail.setSub(readLine(bufferedReader));
            mail.setTsp(readLine(bufferedReader));
            int len = Integer.parseInt(readLine(bufferedReader));
            if (len < 0) {
                throw new IllegalArgumentException("Invalid format.");
            }

            //add attachment
            String attrec = readLine(bufferedReader);
            while (!attrec.equals("")) {
                //extract attName
                if (attrec.charAt(0) != '"') {
                    throw new IllegalArgumentException("Invalid format.");
                }
                int p = attrec.indexOf('"', 1);
                if (p == -1) {
                    throw new IllegalArgumentException("Invalid format.");
                }
                String attName = attrec.substring(1, p);

                //extract filename
                if (attrec.charAt(p + 1) != '"') {
                    throw new IllegalArgumentException("Invalid format.");
                }
                int q = attrec.indexOf('"', p + 2);
                if (q != attrec.length() - 1) {
                    throw new IllegalArgumentException("Invalid format.");
                }
                String attFilename = attrec.substring(p + 2, q);
                if (attFilename.equals("")) {
                    throw new IllegalArgumentException("Invalid format.");
                }

                //add att
                MailAtt att = new MailAtt();
                att.setAttName(attName);
                att.setFilename(attFilename);
                mail.addAtt(att);

                //for loop
                attrec = readLine(bufferedReader);
            }

            //read content
            String s = readStr(bufferedReader, len);
            if (s.length() != len || bufferedReader.read() != '\n') {
                throw new IllegalArgumentException("Invalid format.");
            }
            mail.setText(s);
            mail.setHtml(readStr(bufferedReader, Integer.MAX_VALUE));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format.");
        }
        return mail;
    }

    /**
     * Read a string of specific length.
     */
    private String readStr(BufferedReader reader, int len) throws IOException {
        StringBuilder sb = new StringBuilder();
        int bufLen = cbuf.length;
        while (len > 0) {
            int num = reader.read(cbuf, 0, bufLen < len ? bufLen : len);
            if (num == -1) {
                break;
            }
            sb.append(cbuf, 0, num);
            len -= num;
        }
        return sb.toString();
    }

    /**
     * Read a line.
     */
    private String readLine(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        if (s == null) throw new IllegalArgumentException("Invalid format.");
        return s;
    }

    /**
     * To close the file.
     */
    public void close() {
        // nothing...
    }
}
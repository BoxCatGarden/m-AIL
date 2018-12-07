package com.spm.service.io;

import com.spm.domain.MailRec;
import com.spm.domain.MailStorage;
import com.spm.service.ApplicationContext;
import com.spm.service.Callback;
import com.spm.service.Mail;
import com.spm.service.acc.Account;
import com.spm.service.acc.DownRange;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Li, BoxCatGarden
 */
public class InBox {

    /**
     *
     */
    private final Account account;
    private final MailStorage storage;
    private boolean on;
    /**
     * Default constructor
     */
    public InBox() {
        account = ApplicationContext.getContext().getAccount();
        storage = new MailStorage(account.getAddr() + "/inbox/mail_list");
        on = false;
    }

    /**
     * Create a session.
     *
     * @author BoxCatGarden
     */
    private static Session createSession(String inServer, String addr, String pwd) {
        Properties prop = new Properties();
        prop.setProperty("mail.host", inServer);
        prop.setProperty("mail.user", addr);
        prop.setProperty("mail.store.protocol", "pop3");
        prop.setProperty("mail.pop3.allow8bitmime", "true");
        prop.setProperty("mail.pop3.auth", "true");
        prop.setProperty("mail.pop3.ssl.enable", "true");
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.pop3.ssl.socketFactory", sf);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            prop.setProperty("mail.pop3.ssl.trust", "*");
        }

        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(addr, pwd);
            }
        });
    }

    /**
     * Get the number of mails to download.
     *
     * @author BoxCatGarden
     */
    private static int getPullNum(int count, DownRange range) {
        int rn = 0;
        switch (range) {
            case HFIFTY:
                rn = 25;
                break;
            case FIFTY:
                rn = 50;
                break;
            case HUND:
                rn = 100;
                break;
            case DHUND:
                rn = 200;
                break;
            case ALL:
                rn = count;
                break;
            default:
                assert false : "Invalid down range.";
        }
        return count < rn ? count : rn;
    }

    /**
     * @param callback
     */
    public void collect(Callback callback) {
        //return if collecting
        synchronized (this) {
            if (on) {
                return;
            }
            on = true;
        }

        //store the account settings into local variables
        String addr;
        String pwd;
        String server;
        DownRange range;
        boolean dad;
        synchronized (account) {
            addr = account.getAddr();
            pwd = account.getPwd();
            server = account.getInServer();
            range = account.getDownRange();
            dad = account.getDownAndDel();
        }

        //start collecting
        new Thread(() -> {
            List<MailRec> recs = storage.getRecList();

            Session session = createSession(server, addr, pwd);

            try (Store store = session.getStore("pop3")) {
                store.connect();
                POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE); //打开收件箱

                //get the total number of available mails
                int count;
                try {
                    count = folder.getMessageCount();
                } catch (Exception e) {
                    e.printStackTrace();
                    count = 0;
                }
                if (count > 0) {
                    //get the message list
                    Message[] messages;
                    int start = count - getPullNum(count, range) + 1;
                    messages = folder.getMessages(start, count);

                    //download the messages
                    for (Message msg : messages) {
                        String uid = folder.getUID(msg);

                        //find duplication
                        for (MailRec rec : recs) {
                            if (uid.equals(rec.getUid())) {
                                uid = null;
                                break;
                            }
                        }
                        if (uid == null) {
                            continue;
                        }

                        //down load the mail
                        try (BufferedOutputStream out = new BufferedOutputStream(
                                new FileOutputStream(addr + "/temp"))) {
                            msg.writeTo(out);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }

                        try {
                            //write mailfile
                            Mail mail = MIME.unescape(addr + "/temp",
                                    addr + "/inbox/attachment/" + uid + "_");
                            MailFile mailFile = new MailFile();
                            mailFile.open(addr + "/inbox/" + uid);
                            mailFile.write(mail);
                            mailFile.close();

                            //add mailrec
                            MailRec mailRec = new MailRec();
                            mailRec.setUid(uid);
                            mailRec.setFrom(mail.getFrom());
                            mailRec.setSub(mail.getSub());
                            mailRec.setTsp(mail.getTsp());
                            mailRec.setFilename(addr + "/inbox/" + uid);
                            storage.add(mailRec);

                            //down-and-del
                            if (dad) {
                                msg.setFlag(Flags.Flag.DELETED, true);
                                //msg.saveChanges();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //close
                folder.close(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (callback != null) {
                callback.invoke(null);
            }

            //mark the end of the collecting
            synchronized (this) {
                on = false;
            }
        }).start();
    }

    /**
     * @return
     */
    public List<MailItem> getMailList() {
        List<MailItem> temp = new ArrayList<>();
        List<MailRec> recs = this.storage.getRecList();
        for (MailRec rec : recs) {
            MailItem a = new MailItem();
            a.setRec(rec);
            temp.add(a);
        }
        return temp;
    }

    /**
     * Delete the local files for a mail, and try to remove the mail on the server if <code>sync == true</code>.
     *
     * @param mit
     * @param sync
     */
    public void delete(MailItem mit, boolean sync) throws IOException {
        MailRec rec = mit.getRec();

        //remove local files
        remove(rec);

        //remove remote storage
        if (sync) {
            //get account settings
            String addr, pwd, inServer;
            synchronized (account) {
                addr = account.getAddr();
                pwd = account.getPwd();
                inServer = account.getInServer();
            }

            //try to delete
            new Thread(() -> {

                Session session = createSession(inServer, addr, pwd);

                try (Store store = session.getStore("pop3")) {
                    store.connect();

                    POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
                    folder.open(Folder.READ_WRITE); //打开收件箱

                    //find the mail to delete
                    String uid = rec.getUid();
                    for (Message msg : folder.getMessages()) {
                        if (folder.getUID(msg).equals(uid)) {
                            msg.setFlag(Flags.Flag.DELETED, true);
                            //msg.saveChanges();
                            break;
                        }
                    }

                    //closing
                    folder.close(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Remove the files for a mail in the inbox, as respect to the mailrec.
     *
     * @param rec The mailrec corresponding to the mail to delete.
     * @throws IOException Cannot remove the files.
     * @author BoxCatGarden
     */
    private void remove(MailRec rec) throws IOException {
        //del mailrec
        storage.del(rec);

        String filename = rec.getFilename();
        int p = filename.lastIndexOf('/') + 1;
        String attFilenamePrefix = filename.substring(0, p) + "attachment/" + filename.substring(p) + "_";

        //remove mailfile
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        //remove attachments
        int i = 1;
        file = new File(attFilenamePrefix + i);
        while (file.exists() && file.isFile()) {
            file.delete();
            ++i;
            file = new File(attFilenamePrefix + i);
        }
    }
}
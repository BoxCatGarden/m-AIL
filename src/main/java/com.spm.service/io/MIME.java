package com.spm.service.io;

import com.spm.service.Mail;
import com.spm.service.MailAtt;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.Date;

/**
 * @author 张哲源
 * @author BoxCatGarden, modified on 2018/12/01
 */
public class MIME {
    private static final int START_ATT_COUNT = 1;

    /**
     * @param filename 使用MIME封装后的文件应当存放的路径
     * @param mail     想要封装的文件信息
     *                 <p>
     *                 本方法用与获取一个可以发送的邮件文件
     *                 </p>
     */
    public static void escape(String filename, Mail mail) {
        //创建MIMEMessage对象
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage message = new MimeMessage(session);

        //设置头部
        try {
            message.setFrom(mail.getFrom());
            message.setRecipients(Message.RecipientType.TO, mail.getTo());
            if (mail.getSub() != null) {
                message.setSubject(mail.getSub());
            }
            message.setSentDate(new Date());
        } catch (MessagingException me) {
            me.printStackTrace();
        }


        /*
         * @author BoxCatGarden
         */
        try {
            //multipart
            MimeMultipart multipart = new MimeMultipart();

            //Text
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(mail.getText(), "utf-8");
            multipart.addBodyPart(bodyPart);

            //att
            for (MailAtt att : mail.getAttList()) {
                bodyPart = new MimeBodyPart();
                //att name & filename
                bodyPart.setFileName(att.getAttName());
                bodyPart.setDataHandler(new DataHandler(new FileDataSource(att.getFilename())));
                multipart.addBodyPart(bodyPart);
            }

            //set the message
            message.setContent(multipart);

            //write the file
            try (BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(filename))) {
                message.writeTo(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename          想要解析的邮件文件路径
     * @param attFilenamePrefix 解析出来后邮件附件的路径前缀
     * @return 返回一个Mail类的实例
     * <p>
     * 从路径为 filename 的 文件中读取并解析，将结果写入一个 Mail 对象中，最后返回该对象。
     * 附件的存储文件路径将以“attFilenamePrefix_”加编号[1, 2, 3, …]的形式生成。
     * 超文本会被写入 Mail 对象
     * 除此以外，错误的输入文件格式将抛出 IllegalArgumentException，
     * 调用者收到异常后 应该 删除错误 的输入文件
     * </p>
     */
    public static Mail unescape(String filename, String attFilenamePrefix) throws IllegalArgumentException {
        Mail retMail = new Mail();

        Session session = Session.getDefaultInstance(System.getProperties());
        try {
            //获取邮件对象
            FileInputStream in = new FileInputStream(filename);
            MimeMessage message = new MimeMessage(session, in);

            //解析邮件头部
            retMail.setFrom(MimeUtility.decodeText(message.getFrom()[0].toString()));
            retMail.setSub(message.getSubject());
            retMail.setTo(MimeUtility.decodeText(message.getRecipients(Message.RecipientType.TO)[0].toString()));
            retMail.setTsp(message.getHeader("Date")[0]);

            //解析邮件内容
            String messageContenType = message.getContentType();
            if (messageContenType.toLowerCase().startsWith("text/plain"))/*邮件是单纯的文本邮件*/ {
                retMail.setText(message.getContent().toString());
            } else if (messageContenType.toLowerCase().startsWith("text/html"))/*邮件是包含html的文本文件*/ {
                retMail.setHtml(message.getContent().toString());
            } else if (messageContenType.toLowerCase().startsWith("multipart"))/*邮件是多部分邮件*/ {
                Multipart multipart = (Multipart) message.getContent();
                unescapeMultipart(multipart, retMail, attFilenamePrefix, START_ATT_COUNT);
            } else/*不是正确的文件*/ {
                throw new IllegalArgumentException();
            }
        } catch (MessagingException me) {
            me.printStackTrace();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //创建返回的Mail类的对象
        return retMail;
    }

    /**
     * @param multipart         想要解析的Multipart
     * @param retMail           想要修改的retMail(即Mail类的对象)
     * @param attFilenamePrefix 解析文件后的前缀路径
     * @return 返回修改后的retMail
     * <p>
     * 该方法用于解析一个Multipart中所有的内容(其中一个bodypart中包含multipart的时候使用递归)
     * </p>
     * @author 张哲源
     */
    /**
     * @param multipart         想要解析的Multipart
     * @param retMail           想要修改的retMail(即Mail类的对象)
     * @param attFilenamePrefix 解析文件后的前缀路径
     * @param attCount          附件计数器
     * @return 变化后的附件计数器
     * @author BoxCatGarden (modify)
     */
    private static int unescapeMultipart(Multipart multipart, Mail retMail, String attFilenamePrefix, int attCount) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.getContentType().toLowerCase().startsWith("text/plain"))/*内有单纯文本*/ {
                retMail.setText(bodyPart.getContent().toString());
            } else if (bodyPart.getContentType().toLowerCase().startsWith("text/html"))/*内有包含html文本*/ {
                retMail.setHtml(bodyPart.getContent().toString());
            } else if (bodyPart.getContentType().toLowerCase().startsWith("multipart"))/*是multipart类型的bodypart*/ {
                //递归调用unescapeMultipart用以解析multipart类型的bodypart
                Multipart innerMultipart = (Multipart) bodyPart.getContent();
                attCount = unescapeMultipart(innerMultipart, retMail, attFilenamePrefix, attCount);
            } else/*bodypart是文件*/ {
                //读取数据写入文件
                InputStream inputStream = bodyPart.getDataHandler().getInputStream();
                File outputFile = new File(attFilenamePrefix + attCount);
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                int ret = 0;
                byte[] bys = new byte[1024];
                while ((ret = inputStream.read(bys)) != -1) {
                    outputStream.write(bys);
                }
                outputStream.close();
                inputStream.close();

                //构建retMail
                MailAtt mailAtt = new MailAtt();
                mailAtt.setAttName(bodyPart.getFileName());
                mailAtt.setFilename(attFilenamePrefix + attCount);
                retMail.addAtt(mailAtt);
                attCount++;
            }
        }
        return attCount;
    }
}
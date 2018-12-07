package com.spm.service.io;

import com.spm.domain.MailRec;
import com.spm.domain.MailStorage;
import com.spm.service.ApplicationContext;
import com.spm.service.Callback;
import com.spm.service.Mail;
import com.spm.service.acc.Account;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The out box for sending mail.
 *
 * @author BoxCatGarden
 */
public class OutBox {

    /**
     * local reference of account
     */
    private final Account acc;
    /**
     * the storage of mailrec
     */
    private final MailStorage storage;
    /**
     * for generating uid of sent mail
     */
    private long uidCount;
    /**
     * mark whether it is dispatching
     */
    private boolean isDispatching = false;

    /**
     * Default constructor
     */
    public OutBox() {
        //inject account
        acc = ApplicationContext.getContext().getAccount();

        //create the storage
        storage = new MailStorage(acc.getAddr() + "/outbox/mail_list");
        List<MailRec> recList = storage.getRecList();

        //init uidCount
        if (recList.size() > 0) {
            uidCount = Long.parseLong(recList.get(recList.size() - 1).getUid()) + 1;
        }

        //free mailrec
        for (MailRec rec : recList) {
            rec.setUsing(false);
        }
    }

    /**
     * Create an smtp mail session, in ssl, allowing 8bit-MIME.
     *
     * @param outServer The out server to send the mail.
     * @param addr      The user name to LOGIN the server.
     * @param pwd       The password to LOGIN the server.
     * @return A mail session with the specific server, user, and password.
     */
    private static Session createSession(String outServer, String addr, String pwd) {
        Properties prop = new Properties();
        prop.setProperty("mail.host", outServer);
        prop.setProperty("mail.user", addr);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.allow8bitmime", "true");
        prop.setProperty("mail.smtp.ssl.enable", "true");
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.socketFactory", sf);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            prop.setProperty("mail.smtp.ssl.trust", "*");
        }

        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(addr, pwd);
            }
        });
    }

    /**
     * To send a mail immediately and asynly. If fail store it for .dispatch().
     *
     * @param mail     The mail to send.
     * @param callback The callback to be invoked after sending the mail.
     */
    public void send(Mail mail, Callback callback) throws IOException {
        //get account settings
        String addr, sender, pwd, outServer;
        synchronized (acc) {
            addr = acc.getAddr();
            sender = acc.getSender();
            pwd = acc.getPwd();
            outServer = acc.getOutServer();
        }

        try {
            mail.setFrom(MimeUtility.encodeText(sender) + " <" + addr + ">");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            mail.setFrom(addr);
        }
        String uid = "" + uidCount;
        ++uidCount;

        //write the mailrec
        MailRec rec = new MailRec();
        rec.setUid(uid);
        rec.setFrom(mail.getTo());
        rec.setSub(mail.getSub());
        rec.setTsp(mail.getTsp());
        rec.setFilename(addr + "/outbox/" + uid);

        //write the mimefile
        MIME.escape(addr + "/outbox/mime/" + uid, mail);

        //write the mailfile
        MailFile file = new MailFile();
        file.open(rec.getFilename());
        file.write(mail);
        file.close();
        mail.setFrom(addr);

        //store the mailrec
        storage.add(rec);

        //start async sending
        new Thread(() -> {
            //preparations
            Session session = createSession(outServer, addr, pwd);
            Transport transport = null;
            try {
                transport = session.getTransport();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.invoke(null);
                }
                return;
            }
            RmvSentTransportListener listener = new RmvSentTransportListener();
            listener.addr = addr;
            listener.rec = rec;
            transport.addTransportListener(listener);

            //send mail
            try (BufferedInputStream ins = new BufferedInputStream(
                    new FileInputStream(addr + "/outbox/mime/" + uid));
                 Transport trans = transport) {
                Message msg = new MimeMessage(session, ins);
                ins.close();
                transport.connect();
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
            } catch (Exception e) {
                e.printStackTrace();
                synchronized (rec) {
                    rec.setUsing(false);
                }
            }

            if (callback != null) {
                callback.invoke(null);
            }
        }).start();
    }

    /**
     * Dispatch all once failed mail to their destination.
     *
     * @param callback The callback that will be invoked after having
     *                 tried to send all currently unsent mails.
     */
    public void dispatch(Callback callback) {
        //return immediately if currently dispatching
        synchronized (this) {
            if (isDispatching) {
                return;
            }
            isDispatching = true;
        }

        //get account settings
        String addr, pwd, outServer;
        synchronized (acc) {
            addr = acc.getAddr();
            pwd = acc.getPwd();
            outServer = acc.getOutServer();
        }

        //start async sending
        new Thread(() -> {
            //preparations
            Session session = createSession(outServer, addr, pwd);
            Transport transport = null;
            try {
                transport = session.getTransport();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();

                //after dispatching, invoke the callback, if any
                if (callback != null) {
                    callback.invoke(null);
                }

                //mark the end of this dispatching
                synchronized (this) {
                    isDispatching = false;
                }
                return;
            }
            RmvSentTransportListener listener = new RmvSentTransportListener();
            listener.addr = addr;
            transport.addTransportListener(listener);

            //send the currently unsent mails
            for (MailRec rec : storage.getRecList()) {
                //try to lock the rec
                synchronized (rec) {
                    if (rec.isUsing()) {
                        continue;
                    }
                    rec.setUsing(true);
                }

                //send mail
                try (BufferedInputStream ins = new BufferedInputStream(
                        new FileInputStream(addr + "/outbox/mime/" + rec.getUid()));
                     Transport trans = transport) {
                    listener.rec = rec;
                    Message msg = new MimeMessage(session, ins);
                    ins.close();
                    transport.connect();
                    transport.sendMessage(msg, msg.getAllRecipients());
                    transport.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    synchronized (rec) {
                        rec.setUsing(false);
                    }
                }
            }

            //after dispatching, invoke the callback, if any
            if (callback != null) {
                callback.invoke(null);
            }

            //mark the end of this dispatching
            synchronized (this) {
                isDispatching = false;
            }
        }).start();
    }

    /**
     * @return The current mail list in out box.
     */
    public List<MailItem> getMailList() {
        ArrayList<MailItem> list = new ArrayList<>();
        for (MailRec mailRec : storage.getRecList()) {
            MailItem item = new MailItem();
            item.setRec(mailRec);
            list.add(item);
        }
        return list;
    }

    /**
     * Cancel the sending of an unsent mail.
     *
     * @param mit The mailitem representing the mail.
     */
    public void cancel(MailItem mit) throws IOException {
        if (!mit.tryLock()) {
            assert false : "Invalid canceling.";
            throw new IOException("Cannot cancel the sending.");
        }
        remove(mit.getRec());
    }

    /**
     * Remove all the data corresponding to the mailrec.
     *
     * @param rec The mailrec representing the data to be removed.
     * @throws IOException If the removing failed.
     */
    private void remove(MailRec rec) throws IOException {
        //del the record
        storage.del(rec);

        String filename = rec.getFilename();
        //remove the mailfile
        File file = new File(filename);
        assert file.exists() && file.isFile() : "Invalid file.";
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        //remove the mimefile
        int p = filename.lastIndexOf('/') + 1;
        assert p > 0 : "Invalid filename";
        file = new File(filename.substring(0, p) + "mime/" + filename.substring(p));
        assert file.exists() && file.isFile() : "Invalid file.";
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * Transport listener to remove the sent mails.
     */
    private class RmvSentTransportListener implements TransportListener {

        /**
         * the mailrec corresponding to the being sent mail
         */
        private MailRec rec;

        /**
         * account-addr
         */
        private String addr;

        @Override
        public void messageDelivered(TransportEvent transportEvent) {
            //remove all the data if succeeded
            try {
                remove(rec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void messageNotDelivered(TransportEvent transportEvent) {
            //free the mailrec for farther use if failed
            synchronized (rec) {
                rec.setUsing(false);
            }
        }

        @Override
        public void messagePartiallyDelivered(TransportEvent transportEvent) {
            //update the recipients when partly succeeded
            Message message = transportEvent.getMessage();
            try (BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(addr + "/outbox/mime/" + rec.getUid()))) {
                message.setRecipients(Message.RecipientType.TO, transportEvent.getValidUnsentAddresses());
                message.writeTo(out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //free the mailrec for farther use
            synchronized (rec) {
                rec.setUsing(false);
            }
        }
    }
}
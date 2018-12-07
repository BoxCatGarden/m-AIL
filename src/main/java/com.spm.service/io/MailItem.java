package com.spm.service.io;

import com.spm.domain.MailRec;
import com.spm.service.Mail;

/**
 * Wrapping MailRec.
 *
 * @author BoxCatGarden
 */
public class MailItem {

    /**
     * the mailrec to be wrapped
     */
    private MailRec rec;
    /**
     * mark that this mailitem has been locked
     */
    private boolean locked = false;

    /**
     * Default constructor
     */
    MailItem() {
    }

    /**
     * @return 'from' of the mail.
     */
    public String getFrom() {
        return rec.getFrom();
    }

    /**
     * @return 'subject' of the mail.
     */
    public String getSub() {
        return rec.getSub();
    }

    /**
     * @return Timestamp of the mail.
     */
    public String getTsp() {
        return rec.getTsp();
    }

    /**
     * Try to lock the mail for using.
     *
     * @return true if locking is successful; otherwise, false.
     * In other words, if the mail is not being locked by others, return true,
     * and subsequent tryings will return true.
     */
    public boolean tryLock() {
        //has locked
        if (locked) {
            return true;
        }

        //try to lock
        synchronized (rec) {
            if (rec.isUsing()) {
                return false;
            }
            rec.setUsing(true);
        }
        locked = true;
        return true;
    }

    /**
     * To get the corresponding mail object.
     *
     * @return A Mail object corresponding to this mailitem.
     */
    public Mail getMail() {
        MailFile file = new MailFile();
        file.open(rec.getFilename());
        Mail mail = file.read();
        file.close();
        return mail;
    }

    /**
     * To unlock the mail. It will do nothing if the mail is not being locked.
     */
    public void unlock() {
        //hasn't locked
        if (!locked) {
            return;
        }

        //unlocking
        locked = false;
        synchronized (rec) {
            rec.setUsing(false);
        }
    }

    /**
     * @return The wrapped MailRec object.
     */
    MailRec getRec() {
        return rec;
    }

    /**
     * @param rec The MailRec object to be wrapped.
     */
    void setRec(MailRec rec) {
        this.rec = rec;
    }

}
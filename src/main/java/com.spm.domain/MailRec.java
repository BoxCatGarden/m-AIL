package com.spm.domain;

/**
 * A pure data class.
 *
 * @author BoxCatGarden
 */
public class MailRec {

    /**
     * 'from' of a mail
     */
    private String from = "";
    /**
     * 'subject' of a mail
     */
    private String sub = "";
    /**
     * 'timestamp' of a mail
     */
    private String tsp = "";
    /**
     * the mailfile for a mail
     */
    private String filename = "";
    /**
     * mark whether this is being used by some other
     * true if this is being used
     */
    private boolean usingFlag = true;
    /**
     * 'uid' of a mail
     */
    private String uid = "";

    /**
     * Default constructor
     */
    public MailRec() {
    }

    /**
     * @return 'from' of a mail
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param value the 'form' of a mail
     */
    public void setFrom(String value) {
        from = value;
    }

    /**
     * @return 'subject' of a mail
     */
    public String getSub() {
        return sub;
    }

    /**
     * @param value the 'subject' of a mail
     */
    public void setSub(String value) {
        sub = value;
    }

    /**
     * @return 'timestamp' of a mail
     */
    public String getTsp() {
        return tsp;
    }

    /**
     * @param value the 'timestamp' of a mail
     */
    public void setTsp(String value) {
        tsp = value;
    }

    /**
     * @return the mailfile for a mail
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param value the mailfile for a mail
     */
    public void setFilename(String value) {
        filename = value;
    }

    /**
     * @return True, if this is being used; false, otherwise.
     */
    public boolean isUsing() {
        return usingFlag;
    }

    /**
     * To mark whether this is being used.
     *
     * @param value true to mark using and, false to mark freedom.
     */
    public void setUsing(boolean value) {
        usingFlag = value;
    }

    /**
     * @return 'uid' of a mail
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param value the 'uid' of a mail
     */
    public void setUid(String value) {
        uid = value;
    }

}
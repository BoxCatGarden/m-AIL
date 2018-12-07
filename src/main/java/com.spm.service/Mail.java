package com.spm.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a mail.
 *
 * @author BoxCatGarden
 */
public class Mail {

    /**
     * 'from' of a mail
     */
    private String from = "";
    /**
     * 'to' of a mail
     */
    private String to = "";
    /**
     * 'subject' of a mail
     */
    private String sub = "";
    /**
     * the pure text part of a mail
     */
    private String text = "";
    /**
     * the html part of a mime-mail
     */
    private String html = "";
    /**
     * the timestamp of a mail
     */
    private String tsp = "";
    /**
     * the attachment list of a mail
     */
    private List<MailAtt> att;

    /**
     * Default constructor
     */
    public Mail() {
        att = new ArrayList<>();
    }

    /**
     * @return 'from' of a mail.
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param value The 'from' of a mail.
     */
    public void setFrom(String value) {
        from = value;
    }

    /**
     * @return 'to' of a mail.
     */
    public String getTo() {
        return to;
    }

    /**
     * @param value The 'to' of a mail.
     */
    public void setTo(String value) {
        to = value;
    }

    /**
     * @return 'subject' of a mail.
     */
    public String getSub() {
        return sub;
    }

    /**
     * @param value The 'subject' of a mail.
     */
    public void setSub(String value) {
        sub = value;
    }

    /**
     * @return The pure text part of a mail.
     */
    public String getText() {
        return text;
    }

    /**
     * @param value The pure text part of a mail.
     */
    public void setText(String value) {
        text = value;
    }

    /**
     * @return The html part of a mail.
     */
    public String getHtml() {
        return html;
    }

    /**
     * @param value The html part of a mail.
     */
    public void setHtml(String value) {
        html = value;
    }

    /**
     * @return The timestamp of a mail.
     */
    public String getTsp() {
        return tsp;
    }

    /**
     * @param value The timestamp of a mail.
     */
    public void setTsp(String value) {
        tsp = value;
    }

    /**
     * @return The pure text part joined with html part by '\n'.
     */
    public String getContent() {
        return text + "\n" + html;
    }

    /**
     * @param att The attachment to be added.
     */
    public void addAtt(MailAtt att) {
        this.att.add(att);
    }

    /**
     * @param att The attachment to be deleted.
     */
    public void delAtt(MailAtt att) {
        this.att.remove(att);
    }

    /**
     * @return The list consists of all the attachments attached to this mail.
     */
    public List<MailAtt> getAttList() {
        return att;
    }

}
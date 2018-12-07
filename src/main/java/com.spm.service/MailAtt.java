package com.spm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Represent the attachment of a mail.
 *
 * @author BoxCatGarden
 */
public class MailAtt {

    /**
     * the name of the attachment
     */
    private String attName = "";
    /**
     * the file that virtually stores the attachment
     */
    private String filename = "";

    /**
     * Default constructor
     */
    public MailAtt() {
    }

    /**
     * @return The name of the attachment. It can be null if not been set before.
     */
    public String getAttName() {
        return attName;
    }

    /**
     * @param value The name of the attachment.
     */
    public void setAttName(String value) {
        attName = value;
    }

    /**
     * @return The filename of the file that virtually stores the attachment.
     * It can be null if not been set before.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param value The filename of the file that virtually stores the attachment.
     */
    public void setFilename(String value) {
        filename = value;
    }

    /**
     * To copy the file of the attachment into another file.
     *
     * @param newFilename The file to store the copy of the file of the attachment.
     * @apiNote If this object hasn't been set 'filename' before, this
     * interface will do nothing.
     */
    public void saveAs(String newFilename) {
        if (filename == null) {
            return;
        }

        File file = new File(filename);
        assert file.exists() : "Try to copy nonexistent attachment file.";

        FileChannel cin;
        FileChannel cout;
        try {
            cin = new FileInputStream(file).getChannel();
            cout = new FileOutputStream(newFilename).getChannel();
            cout.transferFrom(cin, 0, cin.size());
            cout.close();
            cin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
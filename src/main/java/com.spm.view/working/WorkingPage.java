package com.spm.view.working;

import com.spm.view.Page;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 */
public class WorkingPage extends Page {

    static final String NAV = "nav";
    static final String SEND = "send";
    static final String INBOX = "inbox";
    static final String INREAD = "inread";
    static final String OUTBOX = "outbox";
    static final String OUTREAD = "outread";
    static final String ACC = "acc";
    static File savedDir;
    private static Pattern fromPattern = Pattern.compile("^(.*?) <.*?>$");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Default constructor
     */
    public WorkingPage() {
        super();

        setLayout(null);

        NavigatorElement nav = new NavigatorElement();
        addElement(nav);
        addElement(new SendingElement());
        InBoxElement inbox = new InBoxElement();
        addElement(inbox);
        addElement(new InReadingElement());
        addElement(new OutBoxElement());
        addElement(new OutReadingElement());
        addElement(new AccountElement());

        nav.display();
        add(nav);
        nav.setCurrentElement(inbox);
    }

    static String getFileSizeString(File file) {
        long fileLen = file.length();
        if (fileLen < 1024) {
            return fileLen + "B";
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileLen < 1048576) {
                return df.format(fileLen / 1024.0) + "KB";
            } else if (fileLen < 1073741824) {
                return df.format(fileLen / 1048576.0) + "MB";
            } else {
                return df.format(fileLen / 1073741824.0) + "GB";
            }
        }
    }

    static String getSenderName(String from) {
        if (fromPattern.matcher(from).matches()) {
            return from.split(" <")[0];
        }
        return from;
    }

    static String getDate(String tsp) {
        return dateFormat.format(new Date(tsp));
    }

    /**
     *
     */
    public void display() {
        super.display();
    }

    /**
     *
     */
    public void refresh() {
        super.refresh();
    }
}
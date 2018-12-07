package com.spm.service.acc;

import java.io.*;

/**
 * A class that has the settings of an account.
 *
 * @author Li
 */
public class Account {

    /**
     * User's address
     */
    private String addr;
    /**
     * User's name
     */
    private String sender;
    /**
     * User's password
     */
    private String pwd;
    /**
     * Server for receiving
     */
    private String inServer;
    /**
     * Server for sending
     */
    private String outServer;
    /**
     * Tell whether logging out and delete the ifo
     */
    private boolean downAndDel;
    /**
     * Rate for ref
     */
    private RefRate refRate;
    /**
     * Range fof downing
     */
    private DownRange downRange;

    /**
     * Default constructor
     */
    public Account() {
        this.addr = "";
        this.pwd = "";
        this.sender = "";
        this.inServer = "";
        this.outServer = "";
        this.downRange = DownRange.HFIFTY;
        this.downAndDel = false;
        this.refRate = RefRate.HAND;
    }

    /**
     * @return the User's address
     */
    public String getAddr() {
        return this.addr;
    }

    /**
     * @param value
     */
    public void setAddr(String value) {
        this.addr = value;
    }

    /**
     * @return the User's name
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * @param value
     */
    public void setSender(String value) {
        this.sender = value;
    }

    /**
     * @return the User's password
     */
    public String getPwd() {
        return this.pwd;
    }

    /**
     * @param value
     */
    public void setPwd(String value) {
        this.pwd = value;
    }

    /**
     * @return the server for receiving
     */
    public String getInServer() {
        return this.inServer;
    }

    /**
     * @param value
     */
    public void setInServer(String value) {
        this.inServer = value;
    }

    /**
     * @return the Server for sending
     */
    public String getOutServer() {
        return this.outServer;
    }

    /**
     * @param value
     */
    public void setOutServer(String value) {
        this.outServer = value;
    }

    /**
     * @return whether logging out and del the ifo
     */
    public boolean getDownAndDel() {
        return this.downAndDel;
    }

    /**
     * @param value
     */
    public void setDownAndDel(boolean value) {
        this.downAndDel = value;
    }

    /**
     * @return the rate for ref
     */
    public RefRate getRefRate() {
        return this.refRate;
    }

    /**
     * @param rr
     */
    public void setRefRate(RefRate rr) {
        this.refRate = rr;
    }

    /**
     * @return the range for downing
     */
    public DownRange getDownRange() {
        return this.downRange;
    }

    /**
     * @param dr
     */
    public void setDownRange(DownRange dr) {
        this.downRange = dr;
    }

    /**
     * @return whether signing in
     */
    public boolean signIn() {
        File file = new File("acc");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.length() == 0) {
                return false;
            } else {
                try (FileReader reader = new FileReader(file);
                     BufferedReader bReader = new BufferedReader(reader)) {
                    String s = "";
                    int temp = 0;
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setAddr(s.substring(temp));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setSender(s.substring(temp));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setPwd(s.substring(temp));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setInServer(s.substring(temp));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setOutServer(s.substring(temp));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setRefRate(RefRate.valueOf(s.substring(temp)));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    setDownRange(DownRange.valueOf(s.substring(temp)));
                    s = bReader.readLine();
                    temp = s.indexOf("=") + 1;
                    if (s.substring(temp).equals("0")) {
                        setDownAndDel(false);
                    } else {
                        setDownAndDel(true);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param addr
     * @param pwd
     */
    public void signInAndSave(String addr, String pwd) {
        int dad;
        int index = addr.indexOf('@') + 1;
        String addrCut = addr.substring(index);
        this.addr = addr;
        this.pwd = pwd;
        this.sender = "";
        this.inServer = "pop." + addrCut;
        this.outServer = "smtp." + addrCut;
        this.downRange = DownRange.HFIFTY;
        this.downAndDel = false;
        this.refRate = RefRate.HAND;
        dad = 0;

        File file = new File("acc");
        String writer = "addr=" + this.addr + "\n" +
                "sender=" + this.sender + "\n" +
                "pwd=" + this.pwd + "\n" +
                "in=" + this.inServer + "\n" +
                "out=" + this.outServer + "\n" +
                "refrate=" + this.refRate + "\n" +
                "downrange=" + this.downRange + "\n" +
                "dad=" + dad;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(writer);
            fileWriter.flush();
            fileWriter.close();

            /*
             * Edited BoxCatGarden 2018/12/01
             */
            //initialize the folders of the account, if no folder of the account exists
            file = new File(addr);
            if (!file.exists() || file.isFile()) {
                new File(addr + "/inbox/attachment").mkdirs();
                new File(addr + "/outbox/mime").mkdirs();
                new File(addr + "/inbox/mail_list").createNewFile();
                new File(addr + "/outbox/mail_list").createNewFile();
                new File(addr + "/temp").createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * logging out and del the ifo
     */
    public void signOutAndRmv() {
        File file = new File("acc");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param newAcc
     */
    public void copyAndSave(Account newAcc) {
        synchronized (this) {
            this.downAndDel = newAcc.downAndDel;
            this.downRange = newAcc.downRange;
            this.refRate = newAcc.refRate;
            this.outServer = newAcc.outServer;
            this.inServer = newAcc.inServer;
            this.pwd = newAcc.pwd;
            this.sender = newAcc.sender;
        }
        int dad;
        if (downAndDel) {
            dad = 1;
        } else {
            dad = 0;
        }
        File file = new File("acc");
        String writer = "addr=" + this.addr + "\n" +
                "sender=" + this.sender + "\n" +
                "pwd=" + this.pwd + "\n" +
                "in=" + this.inServer + "\n" +
                "out=" + this.outServer + "\n" +
                "refrate=" + this.refRate + "\n" +
                "downrange=" + this.downRange + "\n" +
                "dad=" + dad;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(writer);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
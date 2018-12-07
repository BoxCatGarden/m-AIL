package com.spm.view.working;

import com.spm.service.ApplicationContext;
import com.spm.service.acc.Account;
import com.spm.service.acc.DownRange;
import com.spm.service.acc.RefRate;
import com.spm.service.timer.InTimer;
import com.spm.view.PageElement;
import com.spm.view.Window;
import com.spm.view.signin.SignInPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class AccountElement extends PageElement {
    private final int posX = 450;
    private final int posY = 50;
    private final int elemPanelWidth = 450;
    private final int elemPanelHeight = 500;
    private final int contorlElemGap = 42;
    private final int elemHeight = 30;
    private final int labelWidth = 240;
    private final int textFieldWidth = 250;
    private final int getTextFieldPosX = 200;

    private JTextField senderTextField;
    private JTextField pwdTextField;
    private JTextField pop3ServerTextField;
    private JTextField smtpServerTextField;
    private JComboBox refreshRateComboBox;
    private JComboBox downloadPosComboBox;
    private JCheckBox delAfterDownFlagCheckBox;
    /**
     *
     */
    private Account acc;

    /**
     * Default constructor
     */
    public AccountElement() {
        super();

        setId(WorkingPage.ACC);
        acc = new Account();

        //layout
        this.setLayout(null);
        JPanel accountmentManagementPanel = this;
        accountmentManagementPanel.setBounds(posX, posY, elemPanelWidth, elemPanelHeight);
        accountmentManagementPanel.setLayout(null);

        JPanel accLabelPanel = new JPanel();
        accLabelPanel.setBounds(0, 0, 150, elemHeight);
        accLabelPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
        accLabelPanel.add(new JLabel("账号管理"));
        accountmentManagementPanel.add(accLabelPanel);

        JButton exitAccBtn = new JButton("退出账号");
        exitAccBtn.setBounds(0, contorlElemGap * 1, 100, elemHeight);
        accountmentManagementPanel.add(exitAccBtn);
        exitAccBtn.addActionListener(new ExitAccBtnListener());

        JLabel accountmentManagementLabel = new JLabel("账号设置");
        accountmentManagementLabel.setBounds(0, contorlElemGap * 2, 80, elemHeight);
        accountmentManagementPanel.add(accountmentManagementLabel);

        JButton saveAccBtn = new JButton("保存");
        saveAccBtn.setBounds(0, contorlElemGap * 3 - 10, 100, elemHeight);
        accountmentManagementPanel.add(saveAccBtn);

        JLabel mailBoxLabel = new JLabel("邮箱");
        mailBoxLabel.setBounds(0, contorlElemGap * 4, labelWidth, elemHeight);
        accountmentManagementPanel.add(mailBoxLabel);
        JLabel mailBoxSetLabel = new JLabel(getAccMailAddr());
        mailBoxSetLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        mailBoxSetLabel.setOpaque(true);
        mailBoxSetLabel.setBackground(new Color(216, 191, 216));
        mailBoxSetLabel.setBounds(getTextFieldPosX, contorlElemGap * 4, textFieldWidth, elemHeight);
        accountmentManagementPanel.add(mailBoxSetLabel);

        JLabel senderLabel = new JLabel("使用该名称发送邮件");
        senderLabel.setBounds(0, contorlElemGap * 5, labelWidth, elemHeight);
        accountmentManagementPanel.add(senderLabel);
        JTextField senderTextField = new JTextField();
        senderTextField.setBounds(getTextFieldPosX, contorlElemGap * 5, textFieldWidth, elemHeight);
        accountmentManagementPanel.add(senderTextField);

        JLabel pwdLabel = new JLabel("密码");
        pwdLabel.setBounds(0, contorlElemGap * 6, labelWidth, elemHeight);
        accountmentManagementPanel.add(pwdLabel);
        JTextField pwdTextField = new JTextField();
        pwdTextField.setBounds(getTextFieldPosX, contorlElemGap * 6, textFieldWidth, elemHeight);
        accountmentManagementPanel.add(pwdTextField);

        JLabel pop3ServerLabel = new JLabel("传入(POP3)电子邮件服务器");
        pop3ServerLabel.setBounds(0, contorlElemGap * 7, labelWidth, elemHeight);
        accountmentManagementPanel.add(pop3ServerLabel);
        JTextField pop3ServerTextField = new JTextField();
        pop3ServerTextField.setBounds(getTextFieldPosX, contorlElemGap * 7, textFieldWidth, elemHeight);
        accountmentManagementPanel.add(pop3ServerTextField);

        JLabel smtpServerLabel = new JLabel("传出(SMTP)电子邮件服务器");
        smtpServerLabel.setBounds(0, contorlElemGap * 8, labelWidth, elemHeight);
        accountmentManagementPanel.add(smtpServerLabel);
        JTextField smtpServerTextField = new JTextField();
        smtpServerTextField.setBounds(getTextFieldPosX, contorlElemGap * 8, textFieldWidth, elemHeight);
        accountmentManagementPanel.add(smtpServerTextField);

        JLabel refreshRateLabel = new JLabel("刷新频率");
        refreshRateLabel.setBounds(0, contorlElemGap * 9, labelWidth, elemHeight);
        accountmentManagementPanel.add(refreshRateLabel);
        JComboBox refreshRateComboBox = new JComboBox();
        refreshRateComboBox.setBounds(getTextFieldPosX, contorlElemGap * 9, textFieldWidth, elemHeight);
        refreshRateComboBox.addItem("15分钟");
        refreshRateComboBox.addItem("30分钟");
        refreshRateComboBox.addItem("1小时");
        refreshRateComboBox.addItem("手动");
        accountmentManagementPanel.add(refreshRateComboBox);

        JLabel downloadPosLabel = new JLabel("从以下位置下载邮件");
        downloadPosLabel.setBounds(0, contorlElemGap * 10, labelWidth, elemHeight);
        accountmentManagementPanel.add(downloadPosLabel);
        JComboBox downloadPosComboBox = new JComboBox();
        downloadPosComboBox.setBounds(getTextFieldPosX, contorlElemGap * 10, textFieldWidth, elemHeight);
        downloadPosComboBox.addItem("前二十五封");
        downloadPosComboBox.addItem("前五十封");
        downloadPosComboBox.addItem("前一百封");
        downloadPosComboBox.addItem("前两百封");
        downloadPosComboBox.addItem("全部");
        accountmentManagementPanel.add(downloadPosComboBox);

        JLabel deleteAfterDownloadFlagLabel = new JLabel("下载后删除服务器上的邮件");
        deleteAfterDownloadFlagLabel.setBounds(0, contorlElemGap * 11, labelWidth, elemHeight);
        accountmentManagementPanel.add(deleteAfterDownloadFlagLabel);
        JCheckBox delAfterDownFlagCheckBox = new JCheckBox();
        delAfterDownFlagCheckBox.setBounds(getTextFieldPosX, contorlElemGap * 11 - 10, 50, 50);
        accountmentManagementPanel.add(delAfterDownFlagCheckBox);

        this.senderTextField = senderTextField;
        this.pwdTextField = pwdTextField;
        this.pop3ServerTextField = pop3ServerTextField;
        this.smtpServerTextField = smtpServerTextField;
        this.refreshRateComboBox = refreshRateComboBox;
        this.downloadPosComboBox = downloadPosComboBox;
        this.delAfterDownFlagCheckBox = delAfterDownFlagCheckBox;

        saveAccBtn.addActionListener(new SaveAccBtnListener());
    }

    //获取邮箱地址显示在不可修改的label中
    private String getAccMailAddr() {
        acc.setAddr(ApplicationContext.getContext().getAccount().getAddr());
        return acc.getAddr();
    }

    /**
     *
     */
    public void display() {
        //copy
        Account account = ApplicationContext.getContext().getAccount();
        senderTextField.setText(account.getSender());
        pwdTextField.setText(account.getPwd());
        pop3ServerTextField.setText(account.getInServer());
        smtpServerTextField.setText(account.getOutServer());
        RefRate rate = account.getRefRate();
        if (rate == RefRate.P15M) {
            refreshRateComboBox.setSelectedIndex(0);
        } else if (rate == RefRate.P30M) {
            refreshRateComboBox.setSelectedIndex(1);
        } else if (rate == RefRate.PH) {
            refreshRateComboBox.setSelectedIndex(2);
        } else {
            refreshRateComboBox.setSelectedIndex(3);
        }
        DownRange range = account.getDownRange();
        if (range == DownRange.HFIFTY) {
            downloadPosComboBox.setSelectedIndex(0);
        } else if (range == DownRange.FIFTY) {
            downloadPosComboBox.setSelectedIndex(1);
        } else if (range == DownRange.HUND) {
            downloadPosComboBox.setSelectedIndex(2);
        } else if (range == DownRange.DHUND) {
            downloadPosComboBox.setSelectedIndex(3);
        } else {
            downloadPosComboBox.setSelectedIndex(4);
        }
        delAfterDownFlagCheckBox.setSelected(account.getDownAndDel());

        super.display();
    }

    /**
     * Clear the acc file, reset the system, and jump to the signin page.
     */
    public void signOutAndRmv() {
        //signout
        ApplicationContext.getContext().getAccount().signOutAndRmv();

        //sweep & jump
        Window win = Window.getDefaultWindow();
        win.close();
        win.setCurrentPage(new SignInPage());
    }

    /**
     * Save the new account settings.
     */
    public void save() {
        Account account = ApplicationContext.getContext().getAccount();
        RefRate oldRate = account.getRefRate();
        //copy & save
        account.copyAndSave(acc);
        //restart timer
        if (acc.getRefRate() != oldRate) {
            if (acc.getRefRate() == RefRate.HAND) {
                InTimer.getIns().stop();
            } else {
                InTimer.getIns().start();
            }
        }
    }

    /**
     * @param str
     */
    public void setSenderName(String str) {
        acc.setSender(str);
    }

    /**
     * @param str
     */
    public void setPwd(String str) {
        acc.setPwd(str);
    }

    /**
     * @param str
     */
    public void setInServer(String str) {
        acc.setInServer(str);
    }

    /**
     * @param str
     */
    public void setOutServer(String str) {
        acc.setOutServer(str);
    }

    /**
     * @param rate
     */
    public void setRefreshRate(RefRate rate) {
        acc.setRefRate(rate);
    }

    /**
     * @param range
     */
    public void setDownloadRange(DownRange range) {
        acc.setDownRange(range);
    }

    /**
     * @param dad
     */
    public void setDownAndDel(boolean dad) {
        acc.setDownAndDel(dad);
    }

    //退出账号按钮监听器
    private class ExitAccBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int exitAccFlag = JOptionPane.showConfirmDialog(AccountElement.this, "确认退出账号?", "退出账号", JOptionPane.YES_NO_OPTION);
            if (exitAccFlag == 0) {
                signOutAndRmv();
            }
        }
    }

    //保存账号按钮监听器
    private class SaveAccBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int saveAccFlag = JOptionPane.showConfirmDialog(AccountElement.this, "确认保存?", "保存", JOptionPane.YES_NO_OPTION);
            if (saveAccFlag == 0) {
                setSenderName(senderTextField.getText());
                setPwd(pwdTextField.getText());
                setInServer(pop3ServerTextField.getText());
                setOutServer(smtpServerTextField.getText());
                String refreshRate = refreshRateComboBox.getSelectedItem().toString();
                switch (refreshRate) {
                    case "15分钟":
                        setRefreshRate(RefRate.P15M);
                        break;
                    case "30分钟":
                        setRefreshRate(RefRate.P30M);
                        break;
                    case "1小时":
                        setRefreshRate(RefRate.PH);
                        break;
                    default:
                        setRefreshRate(RefRate.HAND);
                        break;
                }
                String downloadPos = downloadPosComboBox.getSelectedItem().toString();
                switch (downloadPos) {
                    case "前二十五封":
                        setDownloadRange(DownRange.HFIFTY);
                        break;
                    case "前五十封":
                        setDownloadRange(DownRange.FIFTY);
                        break;
                    case "前一百封":
                        setDownloadRange(DownRange.HUND);
                        break;
                    case "前两百封":
                        setDownloadRange(DownRange.DHUND);
                        break;
                    default:
                        setDownloadRange(DownRange.ALL);
                        break;
                }
                setDownAndDel(delAfterDownFlagCheckBox.isSelected());

                save();
            }
        }
    }

}
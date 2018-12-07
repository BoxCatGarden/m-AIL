package com.spm.view.working;

import com.spm.service.ApplicationContext;
import com.spm.service.Callback;
import com.spm.service.Mail;
import com.spm.service.MailAtt;
import com.spm.service.io.OutBox;
import com.spm.view.PageElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class SendingElement extends PageElement {
    private final int posX = 450;
    private final int posY = 50;
    private final int elemPanelPosY = 50;
    private final int elemPanelWidth = 450;
    private final int elemPanelHeigth = 500;

    private final int writeMailAboutBtnPanelHeight = 45;
    private final int writeMailEditPanelHeight = 435;
    private final int writeMailEditPanelPosY = 65;
    private final int writeMailEditPanelPos = 5;
    private final int writeMailEditPanelLabelSize = 30;
    private final int ithAttcShowPanelPosY = writeMailEditPanelPos + 31 * 3;
    private final int attcPanelHeight = 55;
    private final OutBox outbox;
    private JTextField toTextField;
    private JTextField subTextField;
    private JTextArea editTextArea;
    private JScrollPane editTextAreaScroll;
    private JPanel outWriteMailEditPanel;
    private JPanel writeMailEditPanel;
    private JScrollBar writeMailEditVerScrollBar;
    /**
     *
     */
    private Mail mail;

    public SendingElement() {
        super();

        setId(WorkingPage.SEND);
        outbox = ApplicationContext.getContext().getOutBox();

        setLayout(null);
        setBounds(posX, posY, elemPanelWidth, elemPanelHeigth);

        JPanel writeMailAboutBtnPanel = new JPanel();
        writeMailAboutBtnPanel.setBounds(0, 0, elemPanelWidth, writeMailAboutBtnPanelHeight);
        writeMailAboutBtnPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
        JButton attachmentBtn = new JButton("附件");
        JButton sendBtn = new JButton("发送");
        writeMailAboutBtnPanel.add(attachmentBtn);
        writeMailAboutBtnPanel.add(sendBtn);
        add(writeMailAboutBtnPanel);

        sendBtn.addActionListener(new SendBtnActionListener());
        attachmentBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("添加附件");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);

            if (WorkingPage.savedDir != null) {
                fileChooser.setCurrentDirectory(WorkingPage.savedDir);
            }

            fileChooser.setApproveButtonText("添加");
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //change saved dir
                WorkingPage.savedDir = fileChooser.getCurrentDirectory();

                //add attachments
                addAtt(fileChooser.getSelectedFiles());
            }
        });

        writeMailEditPanel = new JPanel();
        writeMailEditPanel.setLayout(null);
        outWriteMailEditPanel = new JPanel();
        outWriteMailEditPanel.setLayout(null);
        outWriteMailEditPanel.add(writeMailEditPanel);
        JScrollPane writeMailEditScrollPanel = new JScrollPane(outWriteMailEditPanel);
        writeMailEditVerScrollBar = writeMailEditScrollPanel.getVerticalScrollBar();
        writeMailEditVerScrollBar.setUnitIncrement(16);//调整下拉速度
        writeMailEditScrollPanel.setBounds(0, writeMailEditPanelPosY, elemPanelWidth, writeMailEditPanelHeight);
        add(writeMailEditScrollPanel);

        toTextField = new JTextField();
        toTextField.setBounds(writeMailEditPanelPos + 60, writeMailEditPanelPos + 31 * 1, elemPanelWidth - 100, 30);
        subTextField = new JTextField();
        subTextField.setBounds(writeMailEditPanelPos + 60, writeMailEditPanelPos + 31 * 2, elemPanelWidth - 100, 30);
        editTextArea = new JTextArea();
        //自动换行不断字
        editTextArea.setLineWrap(true);
        editTextArea.setWrapStyleWord(true);
        //添加滚动条
        editTextAreaScroll = new JScrollPane(editTextArea);
    }

    private void consructWriteMailEditPanel() {
        writeMailEditPanel.removeAll();

        List<MailAtt> attList = getAttList();

        writeMailEditPanel.setBounds(0, 0, elemPanelWidth - 20, writeMailEditPanelHeight + (attcPanelHeight) * attList.size());
        outWriteMailEditPanel.setPreferredSize(new Dimension(elemPanelWidth - 20,
                writeMailEditPanelHeight - 5 + (attcPanelHeight) * attList.size()));


        JLabel fromLabel = new JLabel("发件人:");
        fromLabel.setBounds(writeMailEditPanelPos, writeMailEditPanelPos, writeMailEditPanelLabelSize + 30, writeMailEditPanelLabelSize);
        writeMailEditPanel.add(fromLabel);
        JLabel fromTextLabel = new JLabel(getFromTextLabelContent());
        fromTextLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        fromTextLabel.setOpaque(true);
        fromTextLabel.setBackground(new Color(216, 191, 216));
        fromTextLabel.setBounds(writeMailEditPanelPos + 60, writeMailEditPanelPos, elemPanelWidth - 100, 30);
        writeMailEditPanel.add(fromTextLabel);

        JLabel toLabel = new JLabel("收件人:");
        toLabel.setBounds(writeMailEditPanelPos, writeMailEditPanelPos + 31 * 1, writeMailEditPanelLabelSize + 30, writeMailEditPanelLabelSize);
        writeMailEditPanel.add(toLabel);
        writeMailEditPanel.add(toTextField);

        JLabel subLabel = new JLabel("主题:");
        subLabel.setBounds(writeMailEditPanelPos, writeMailEditPanelPos + 31 * 2, writeMailEditPanelLabelSize + 30, writeMailEditPanelLabelSize);
        writeMailEditPanel.add(subLabel);
        writeMailEditPanel.add(subTextField);

        if (attList.size() > 0) {
            JLabel attLabel = new JLabel("附件:");
            attLabel.setBounds(writeMailEditPanelPos, writeMailEditPanelPos + 31 * 3, writeMailEditPanelLabelSize + 20, writeMailEditPanelLabelSize);
            writeMailEditPanel.add(attLabel);
            for (int i = 0; i < attList.size(); i++) {
                JPanel ithAttcShowPanel = new JPanel();
                ithAttcShowPanel.setLayout(null);
                ithAttcShowPanel.setBounds(writeMailEditPanelPos + 60, ithAttcShowPanelPosY + 1 + (attcPanelHeight) * i,
                        190, attcPanelHeight);
                ithAttcShowPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
                writeMailEditPanel.add(ithAttcShowPanel);

                MailAtt att = attList.get(i);

                JLabel attcNameLabel = new JLabel("附件名:" + att.getAttName());
                JLabel attcSizeLabel = new JLabel("附件大小:" + getAttSize(att));
                JButton attcRemoveBtn = new JButton(getImageIcon("ui/icon/delete_2.png", 15, 15));
                attcNameLabel.setBounds(5, 5, 150, 20);
                attcSizeLabel.setBounds(5, 30, 150, 20);
                attcRemoveBtn.setBounds(160, 15, 20, 20);
                attcRemoveBtn.addActionListener(new RemoveAttActionListener(att));

                ithAttcShowPanel.add(attcNameLabel);
                ithAttcShowPanel.add(attcSizeLabel);
                ithAttcShowPanel.add(attcRemoveBtn);
            }
        }

        editTextAreaScroll.setBounds(writeMailEditPanelPos,
                writeMailEditPanelPos + 95 + (attcPanelHeight) * attList.size()
                , elemPanelWidth - 2 * writeMailEditPanelPos - 20, 327);
        writeMailEditPanel.add(editTextAreaScroll);

        writeMailEditPanel.updateUI();
    }

    //获取from的文本内容
    private String getFromTextLabelContent() {
        return ApplicationContext.getContext().getAccount().getAddr();
    }

    //获取附件列表
    private List<MailAtt> getAttList() {
        return mail.getAttList();
    }

    //获取附件大小
    private String getAttSize(MailAtt att) {
        return WorkingPage.getFileSizeString(new File(att.getFilename()));
    }

    /**
     *
     */
    public void display() {
        if (isHidden()) {
            reset();
            reshow();
            super.display();
        }
    }

    public void hide() {
        if (!isHidden()) {
            mail = null;
            super.hide();
        }
    }

    /**
     * @param str
     */
    public void setToField(String str) {
        mail.setTo(str);
    }

    /**
     * @param str
     */
    public void setSubField(String str) {
        mail.setSub(str);
    }

    /**
     * @param str
     */
    public void setContentField(String str) {
        mail.setText(str);
    }

    /**
     * @param fileList
     */
    public void addAtt(File[] fileList) {
        for (File file : fileList) {
            MailAtt att = new MailAtt();
            att.setAttName(file.getName());
            att.setFilename(file.getPath());
            mail.addAtt(att);
        }
        reshow();
        writeMailEditVerScrollBar.setValue(writeMailEditVerScrollBar.getMaximum());
    }

    /**
     * @param att
     */
    public void delAtt(MailAtt att) {
        mail.delAtt(att);
        reshow();
        writeMailEditVerScrollBar.setValue(writeMailEditVerScrollBar.getValue() - attcPanelHeight);
    }

    /**
     *
     */
    public void send() {
        try {
            mail.setTsp(new Date().toString());
            outbox.send(mail, new Callback() {
                OutBoxElement outbox = ((OutBoxElement) page.getElementById(WorkingPage.OUTBOX));

                @Override
                public void invoke(Object re) {
                    SwingUtilities.invokeLater(() -> {
                        outbox.refresh();
                    });
                }
            });
            mail = null;
            reset();
            reshow();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法发送", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    void reply(Mail oldMail) {
        mail = new Mail();
        mail.setTo(oldMail.getFrom());
        mail.setSub("回复：" + oldMail.getSub());
        mail.setText("\n\n\n\n-----------------\n" +
                "原始邮件：\n" +
                "主题：" + oldMail.getSub() + "\n\n" +
                oldMail.getContent());
    }

    void forward(Mail oldMail) {
        mail = new Mail();
        mail.setSub("转发：" + oldMail.getSub());
        mail.setText("\n\n\n\n-----------------\n" +
                "原始邮件：\n" +
                "主题：" + oldMail.getSub() + "\n\n" +
                oldMail.getContent());
        for (MailAtt oldAtt : oldMail.getAttList()) {
            MailAtt att = new MailAtt();
            att.setAttName(oldAtt.getAttName());
            att.setFilename(oldAtt.getFilename());
            mail.addAtt(att);
        }
    }

    private void reshow() {
        consructWriteMailEditPanel();
    }

    private void reset() {
        if (mail == null) {
            mail = new Mail();
            toTextField.setText("");
            subTextField.setText("");
            editTextArea.setText("");
        } else {
            toTextField.setText(mail.getTo());
            subTextField.setText(mail.getSub());
            editTextArea.setText(mail.getText());
            editTextArea.setCaretPosition(0);
        }
    }

    private class RemoveAttActionListener implements ActionListener {
        MailAtt att;

        RemoveAttActionListener(MailAtt att) {
            this.att = att;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            delAtt(att);
        }
    }

    //发送按钮监听器
    private class SendBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int sendFlag = JOptionPane.showConfirmDialog(SendingElement.this, "确认发送?", "发送邮件", JOptionPane.YES_NO_OPTION);
            if (sendFlag == 0) {
                setToField(toTextField.getText());
                setSubField(subTextField.getText());
                setContentField(editTextArea.getText());
                send();
            }
        }
    }
}
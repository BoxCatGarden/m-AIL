package com.spm.view.working;

import com.spm.service.ApplicationContext;
import com.spm.service.Mail;
import com.spm.service.MailAtt;
import com.spm.service.io.MailItem;
import com.spm.service.io.OutBox;
import com.spm.view.PageElement;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class OutReadingElement extends PageElement {
    private final int posX = 450;
    private final int posY = 50;
    private final int elemPanelWidth = 450;
    private final int elemPanelHeight = 500;
    private final int iconSize = 20;

    private final int mailContentLabelPos = 5;
    private final int mailContentLabelSize = 30;
    private final int ithAttcPanelHeight = 55;

    private JPanel mailItemContentPanel;
    private JPanel inMailItemContentScrollPanel;
    private JScrollBar mailItemContentVerScorllBar;
    /**
     *
     */
    private MailItem mailItem;
    /**
     *
     */
    private Mail mail;
    private OutBox outbox;

    public OutReadingElement() {
        super();

        setId(WorkingPage.OUTREAD);
        outbox = ApplicationContext.getContext().getOutBox();

        //构造第i封邮件的展示panel
        JPanel outMailItemContentPanel = this;
        outMailItemContentPanel.setBounds(posX, posY, elemPanelWidth, elemPanelHeight);
        outMailItemContentPanel.setLayout(null);

        JPanel mailItemContenBtnAboutPanel = new JPanel();
        mailItemContenBtnAboutPanel.setBounds(0, 0, elemPanelWidth, 45);
        mailItemContenBtnAboutPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
        JButton returnBtn = new JButton("返回", getImageIcon("ui/icon/back.png", iconSize, iconSize));
        JButton resendBtn = new JButton("转发", getImageIcon("ui/icon/forward_l.png", iconSize, iconSize));
        JButton cancelSendBtn = new JButton("取消发送");
        mailItemContenBtnAboutPanel.add(returnBtn);
        mailItemContenBtnAboutPanel.add(resendBtn);
        mailItemContenBtnAboutPanel.add(cancelSendBtn);
        outMailItemContentPanel.add(mailItemContenBtnAboutPanel);

        //按钮监听器
        returnBtn.addActionListener(new ReturnBtnActionListener());
        resendBtn.addActionListener(new ResendBtnActionListener());
        cancelSendBtn.addActionListener(new CancelResendBtnActionListener());

        mailItemContentPanel = new JPanel();
        mailItemContentPanel.setLayout(null);
        inMailItemContentScrollPanel = new JPanel();
        inMailItemContentScrollPanel.setLayout(null);
        inMailItemContentScrollPanel.add(mailItemContentPanel);
        JScrollPane mailItemContentScorllPanel = new JScrollPane(inMailItemContentScrollPanel);
        mailItemContentVerScorllBar = mailItemContentScorllPanel.getVerticalScrollBar();
        mailItemContentVerScorllBar.setUnitIncrement(16);//调整下拉速度
        mailItemContentScorllPanel.setBounds(0, 45 + 20, elemPanelWidth, elemPanelHeight - 45 - 20);
        outMailItemContentPanel.add(mailItemContentScorllPanel);
    }

    private void drawMailContent() {
        mailItemContentPanel.removeAll();

        List<MailAtt> mailAttList = getAttList();

        mailItemContentPanel.setBounds(0, 0, elemPanelWidth - 20, elemPanelHeight - 68 + 55 * mailAttList.size());
        inMailItemContentScrollPanel.setPreferredSize(new Dimension(elemPanelWidth - 20,
                elemPanelHeight - 68 + 55 * mailAttList.size()));

        JLabel fromLabel = new JLabel("发件人:");
        fromLabel.setBounds(mailContentLabelPos, mailContentLabelPos, mailContentLabelSize + 30, mailContentLabelSize);
        mailItemContentPanel.add(fromLabel);
        //--------------------展示from内容-------------------------------//
        JTextField fromAddrTextField = new JTextField(getFromLabelContent());
        //--------------------展示from内容-------------------------------//
        fromAddrTextField.setBounds(mailContentLabelPos + 60, mailContentLabelPos, elemPanelWidth - 100, 30);
        fromAddrTextField.setCaretPosition(0);
        mailItemContentPanel.add(fromAddrTextField);

        JLabel toLabel = new JLabel("收件人:");
        toLabel.setBounds(mailContentLabelPos, mailContentLabelPos + 31 * 1, mailContentLabelSize + 30, mailContentLabelSize);
        mailItemContentPanel.add(toLabel);
        JTextField toAddrTextField = new JTextField(getToLabelContent());
        toAddrTextField.setBounds(mailContentLabelPos + 60, mailContentLabelPos + 31 * 1, elemPanelWidth - 100, 30);
        toAddrTextField.setCaretPosition(0);
        mailItemContentPanel.add(toAddrTextField);

        JLabel subLabel = new JLabel("主题:");
        subLabel.setBounds(mailContentLabelPos, mailContentLabelPos + 31 * 2, mailContentLabelSize + 30, mailContentLabelSize);
        mailItemContentPanel.add(subLabel);
        JTextField subTextField = new JTextField(getSubLabelContent());
        subTextField.setBounds(mailContentLabelPos + 60, mailContentLabelPos + 31 * 2, elemPanelWidth - 100, 30);
        subTextField.setCaretPosition(0);
        mailItemContentPanel.add(subTextField);

        if (mailAttList.size() > 0) {
            JLabel attcLabel = new JLabel("附件:");
            attcLabel.setBounds(mailContentLabelPos, mailContentLabelPos + 31 * 3, mailContentLabelSize + 20, mailContentLabelSize);
            mailItemContentPanel.add(attcLabel);
            for (int i = 0; i < mailAttList.size(); i++) {
                JPanel attcShowPanel = new JPanel();
                attcShowPanel.setLayout(null);
                attcShowPanel.setBounds(mailContentLabelPos + 60, mailContentLabelPos + 31 * 3 + 1 + 55 * i,
                        190, ithAttcPanelHeight);
                attcShowPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
                mailItemContentPanel.add(attcShowPanel);
                JLabel attcNameLabel = new JLabel("附件名: " + mailAttList.get(i).getAttName());
                JLabel attcSizeLabel = new JLabel("附件大小: " + getIthAttSize(i));
                JButton attcDownloadBtn = new JButton(getImageIcon("ui/icon/save.png", 15, 15));
                attcDownloadBtn.addActionListener(new DownloadBtnActionListener(mailAttList.get(i)));
                attcNameLabel.setBounds(5, 5, 150, 20);
                attcSizeLabel.setBounds(5, 30, 150, 20);
                attcDownloadBtn.setBounds(160, 15, 20, 20);
                attcShowPanel.add(attcNameLabel);
                attcShowPanel.add(attcSizeLabel);
                attcShowPanel.add(attcDownloadBtn);
            }
        }

        JTextArea mailMainBodyShowTextArea = new JTextArea();
        //自动换行不断字
        mailMainBodyShowTextArea.setLineWrap(true);
        mailMainBodyShowTextArea.setWrapStyleWord(true);
        //--------------------展示邮件正文内容-------------------------------//
        mailMainBodyShowTextArea.setText(getMailMainBodyContent());
        //--------------------展示邮件正文内容-------------------------------//
        //添加滚动条
        JScrollPane mailMainBodyShowTextAreaScroll = new JScrollPane(mailMainBodyShowTextArea);
        mailMainBodyShowTextAreaScroll.setBounds(mailContentLabelPos,
                mailContentLabelPos + 95 + 55 * mailAttList.size()
                , elemPanelWidth - 2 * mailContentLabelPos - 20, 327);
        mailItemContentPanel.add(mailMainBodyShowTextAreaScroll);
        mailItemContentVerScorllBar.setValue(0);

        //mailItemContentPanel.updateUI();
    }

    //获取from
    private String getFromLabelContent() {
        return mail.getFrom();
    }

    //

    //获取to
    private String getToLabelContent() {
        return mail.getTo();
    }

    //获取sub
    private String getSubLabelContent() {
        return mail.getSub();
    }

    //获取附件列表
    private List<MailAtt> getAttList() {
        return mail.getAttList();
    }

    //获取邮件正文
    private String getMailMainBodyContent() {
        return mail.getContent();
    }

    //获取第i个附件大小
    private String getIthAttSize(int i) {
        return WorkingPage.getFileSizeString(new File(mail.getAttList().get(i).getFilename()));
    }

    /**
     * @param mailItem
     */
    public void readSending(MailItem mailItem) {
        this.mailItem = mailItem;
    }

    /**
     *
     */
    public void stopRead() {
        if (mailItem != null) {
            mailItem.unlock();
        }
    }

    /**
     *
     */
    public void display() {
        if (isHidden()) {
            mail = mailItem.getMail();
            drawMailContent();
            super.display();
        }
    }

    /**
     *
     */
    public void hide() {
        if (!isHidden()) {
            stopRead();
            mailItem = null;
            mail = null;
            super.hide();
        }
    }

    /**
     *
     */
    public void back() {
        ((NavigatorElement) page.getElementById(WorkingPage.NAV)).navOutBox();
    }

    /**
     *
     */
    public void forwardSending() {
        SendingElement send = (SendingElement) page.getElementById(WorkingPage.SEND);
        send.forward(mail);
        ((NavigatorElement) page.getElementById(WorkingPage.NAV)).navSending();
    }

    /**
     *
     */
    public void cancelSending() {
        try {
            outbox.cancel(mailItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailItem = null;
        back();
    }

    /**
     * @param att
     * @param filename
     */
    public void saveAtt(MailAtt att, String filename) {
        att.saveAs(filename);
    }

    //删除按钮监听器
    private class CancelResendBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int cancelSendFlag = JOptionPane.showConfirmDialog(OutReadingElement.this, "确认取消?", "取消发送", JOptionPane.YES_NO_OPTION);
            if (cancelSendFlag == 0) {
                cancelSending();
            }
        }
    }

    //下载按钮监听器
    private class DownloadBtnActionListener implements ActionListener {
        MailAtt att;

        DownloadBtnActionListener(MailAtt att) {
            this.att = att;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String attName = att.getAttName();
            int suffixPos = attName.lastIndexOf('.');

            //file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存附件");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);

            //suffix filter
            FileNameExtensionFilter filter;
            if (suffixPos != -1) {
                String suffix = att.getAttName().substring(suffixPos + 1);
                filter = new FileNameExtensionFilter("File (*." + suffix + ")", suffix);
                fileChooser.setFileFilter(filter);
            }

            //use saved dir
            if (WorkingPage.savedDir != null) {
                fileChooser.setCurrentDirectory(WorkingPage.savedDir);
            }
            //default file
            fileChooser.setSelectedFile(new File(attName));

            fileChooser.setApproveButtonText("保存");
            int returnVal = fileChooser.showSaveDialog(OutReadingElement.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                WorkingPage.savedDir = fileChooser.getCurrentDirectory();

                File file = fileChooser.getSelectedFile();
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(OutReadingElement.this,
                            "文件\"" + file.getName() + "\"已存在。\n确认覆盖吗？",
                            "覆盖文件",
                            JOptionPane.YES_NO_OPTION) == 1) {
                        return;
                    }
                }
                String savePath = file.getPath();//文件保存路径
                saveAtt(att, savePath);
                JOptionPane.showMessageDialog(OutReadingElement.this, "保存成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    //返回按钮监听器
    private class ReturnBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            back();
        }
    }

    //转发按钮监听器
    private class ResendBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            forwardSending();
        }
    }

}
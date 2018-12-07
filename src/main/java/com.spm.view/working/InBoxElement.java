package com.spm.view.working;

import com.spm.service.ApplicationContext;
import com.spm.service.io.InBox;
import com.spm.service.io.MailItem;
import com.spm.view.PageElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;


/**
 *
 */
public class InBoxElement extends PageElement {
    private final int posX = 450;
    private final int posY = 50;
    private final int elemPanelWidth = 450;
    private final int elemPanelHeight = 500;
    private final int elemHeight = 30;
    private final int mailListPanelHeight = 440;
    private final int mailItemWidth = 110;
    private final int mailItemHeight = 30;
    private final int mailItemPosY = 10;
    private final int subPanelHeight = 50;
    private final int btnSize = mailItemHeight;
    private final int iconSize = 20;
    private final InBox inbox;
    private JPanel outMailListPanel;
    private JLabel downloadingLabel;
    /**
     *
     */
    private List<MailItem> maillist;
    private boolean isRefreshing = false;

    /**
     * Default constructor
     */
    public InBoxElement() {
        super();

        setId(WorkingPage.INBOX);
        inbox = ApplicationContext.getContext().getInBox();

        setLayout(null);

        JPanel inBoxElementPanel = this;
        inBoxElementPanel.setBounds(posX, posY, elemPanelWidth, elemPanelHeight);
        inBoxElementPanel.setLayout(null);

        JPanel inBoxLabelPanel = new JPanel();
        inBoxLabelPanel.setBounds(0, 0, 150, elemHeight);
        inBoxLabelPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
        inBoxLabelPanel.add(new JLabel("收件箱"));
        inBoxElementPanel.add(inBoxLabelPanel);

        JButton refreshBtn = new JButton(getImageIcon("ui/icon/refresh.png", iconSize, iconSize));
        refreshBtn.setBounds(370, 0, btnSize, btnSize);
        inBoxElementPanel.add(refreshBtn);

        JLabel downloadingLabel = new JLabel("正在获取邮件...");
        this.downloadingLabel = downloadingLabel;
        downloadingLabel.setBounds(250, 0, 150, 30);
        downloadingLabel.setForeground(Color.red);
        inBoxElementPanel.add(downloadingLabel);
        downloadingLabel.setVisible(false);

        refreshBtn.addActionListener(new RefreshBtnActionListener());

        JPanel outMailListPanel = new JPanel();
        this.outMailListPanel = outMailListPanel;
        outMailListPanel.setBounds(0, 50, elemPanelWidth - 5, elemPanelHeight);
        outMailListPanel.setLayout(null);
        inBoxElementPanel.add(outMailListPanel);
    }

    //获取mailList
    private void drawMailList(List<MailItem> mailList) {
        outMailListPanel.removeAll();

        JPanel mailListPanel = outMailListPanel;
        FlowLayout flowLayout = new FlowLayout();
        JPanel inMailListPanel = new JPanel(flowLayout);
        flowLayout.setVgap(0);
        if (mailList.size() > 9) {
            inMailListPanel.setPreferredSize(new Dimension(elemPanelWidth - 40, subPanelHeight * mailList.size()));
        } else {
            inMailListPanel.setPreferredSize(new Dimension(elemPanelWidth - 40, mailListPanelHeight + 1));
        }

        Font mailItemLabelFont = new Font(Font.DIALOG, Font.BOLD, 14);

        for (int i = mailList.size() - 1; i >= 0; --i) {
            JPanel subPanel = new JPanel();
            subPanel.setLayout(null);
            subPanel.setPreferredSize(new Dimension(elemPanelWidth - 50, subPanelHeight));

            MailItem mailItem = mailList.get(i);

            String from = mailItem.getFrom();
            JLabel senderLabel = new JLabel(WorkingPage.getSenderName(from));
            senderLabel.setBounds(0, 10, mailItemWidth, 30);
            senderLabel.setFont(mailItemLabelFont);
            subPanel.add(senderLabel);

            JLabel mailSubjectLabel = new JLabel(mailItem.getSub());
            mailSubjectLabel.setBounds(mailItemWidth + 10, mailItemPosY, mailItemWidth + 30, mailItemHeight);
            mailSubjectLabel.setFont(mailItemLabelFont);
            subPanel.add(mailSubjectLabel);

            String tsp = mailItem.getTsp();
            JLabel timeLabel = new JLabel(WorkingPage.getDate(tsp));
            timeLabel.setBounds(mailItemWidth * 2 + 10 * 5, mailItemPosY, mailItemWidth, mailItemHeight);
            timeLabel.setFont(mailItemLabelFont);
            subPanel.add(timeLabel);

            JButton deleteBtn = new JButton(getImageIcon("ui/icon/delete.png", iconSize, iconSize));
            deleteBtn.setBounds(mailItemWidth * 3 + mailItemPosY * 3, mailItemPosY, btnSize, btnSize);
            subPanel.add(deleteBtn);

            inMailListPanel.add(subPanel);


            //列表鼠标事件监听器
            subPanel.addMouseListener(new MouseListener() {

                MailItem item = mailItem;

                @Override
                public void mouseClicked(MouseEvent e) {
                    readMail(item);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    subPanel.setBorder(BorderFactory.createRaisedBevelBorder());
                    subPanel.setBackground(new Color(230, 230, 250));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    subPanel.setBorder(BorderFactory.createEmptyBorder());
                    subPanel.setBackground(null);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }


            });

            deleteBtn.addActionListener(new DeleteBtnActionListener(mailItem));
        }


        JScrollPane inMailListScrollPane = new JScrollPane(inMailListPanel);
        inMailListScrollPane.setBounds(0, 0, elemPanelWidth - 10, mailListPanelHeight);
        inMailListScrollPane.setBorder(BorderFactory.createEmptyBorder());//去除边框
        inMailListScrollPane.getVerticalScrollBar().setUnitIncrement(16);//调整下拉速度
        mailListPanel.add(inMailListScrollPane);

        outMailListPanel.updateUI();
    }

    /**
     * Get the current list and show it.
     */
    public void display() {
        if (isHidden()) {
            reshow();
            super.display();
        }
    }

    /**
     * Get the current list and show it.
     */
    public void refresh() {
        //reset the refresh state
        if (isRefreshing) {
            isRefreshing = false;
            downloadingLabel.setVisible(false);
        }

        if (isHidden()) {
            return;
        }

        reshow();
    }

    /**
     * Start refreshing.
     */
    public void doRefreshing() {
        if (isRefreshing) {
            return;
        }
        //set refreshing state
        isRefreshing = true;
        downloadingLabel.setVisible(true);

        inbox.collect(re -> {
            SwingUtilities.invokeLater(this::refresh);
        });
    }

    /**
     * Jump to InReadingElement to show a mail.
     *
     * @param mailItem The mail to read.
     */
    public void readMail(MailItem mailItem) {
        InReadingElement inread = (InReadingElement) page.getElementById(WorkingPage.INREAD);
        inread.readMail(mailItem);
        ((NavigatorElement) page.getElementById(WorkingPage.NAV)).setCurrentElement(inread);
    }

    /**
     * Delete a mail.
     *
     * @param mailItem
     * @param sync
     */
    public void deleteMail(MailItem mailItem, boolean sync) {
        try {
            inbox.delete(mailItem, sync);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reshow();
    }

    /**
     * Get the list and show it.
     */
    private void reshow() {
        maillist = inbox.getMailList();
        drawMailList(maillist);
    }

    //手动刷新按钮监听器
    private class RefreshBtnActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doRefreshing();
        }
    }

    //删除按钮监听器
    private class DeleteBtnActionListener implements ActionListener {

        MailItem item;

        DeleteBtnActionListener(MailItem item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object[] options = {"仅删除本地", "同步删除", "取消"};
            int deleteFlag = JOptionPane.showOptionDialog(InBoxElement.this, "确认删除?", "删除邮件",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (deleteFlag == 0) {
                //local
                deleteMail(item, false);
            } else if (deleteFlag == 1) {
                //sync
                deleteMail(item, true);
            }
        }
    }
}
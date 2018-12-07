package com.spm.view.working;

import com.spm.service.ApplicationContext;
import com.spm.service.io.MailItem;
import com.spm.service.io.OutBox;
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
public class OutBoxElement extends PageElement {
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
    private final OutBox outbox;
    private JPanel outMailListPanel;
    /**
     *
     */
    private List<MailItem> maillist;

    public OutBoxElement() {
        super();

        setId(WorkingPage.OUTBOX);
        outbox = ApplicationContext.getContext().getOutBox();

        setLayout(null);

        JPanel outBoxElementPanel = this;
        outBoxElementPanel.setBounds(posX, posY, elemPanelWidth, elemPanelHeight);
        outBoxElementPanel.setLayout(null);

        JPanel outBoxLabelPanel = new JPanel();
        outBoxLabelPanel.setBounds(0, 0, 150, elemHeight);
        outBoxLabelPanel.setBorder(BorderFactory.createLineBorder(new Color(216, 191, 216), 2));
        outBoxLabelPanel.add(new JLabel("发件箱"));
        outBoxElementPanel.add(outBoxLabelPanel);

        JPanel outMailListPanel = new JPanel();
        this.outMailListPanel = outMailListPanel;
        outMailListPanel.setBounds(0, 50, elemPanelWidth - 5, elemPanelHeight);
        outMailListPanel.setLayout(null);
        outBoxElementPanel.add(outMailListPanel);
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

            String to = mailItem.getFrom();
            JLabel receiverLabel = new JLabel(to);
            receiverLabel.setBounds(0, 10, mailItemWidth, 30);
            receiverLabel.setFont(mailItemLabelFont);
            subPanel.add(receiverLabel);

            JLabel mailSubjectLabel = new JLabel(mailItem.getSub());
            mailSubjectLabel.setBounds(mailItemWidth + 10, mailItemPosY, mailItemWidth - 10, mailItemHeight);
            mailSubjectLabel.setFont(mailItemLabelFont);
            subPanel.add(mailSubjectLabel);

            String tsp = mailItem.getTsp();
            JLabel timeLabel = new JLabel(WorkingPage.getDate(tsp));
            timeLabel.setBounds(mailItemWidth * 2 + 10, mailItemPosY, mailItemWidth, mailItemHeight);
            timeLabel.setFont(mailItemLabelFont);
            subPanel.add(timeLabel);

            JButton cancelSendBtn = new JButton("取消发送");
            Font cancelSendBtnFont = new Font("宋体", Font.BOLD, 13);
            cancelSendBtn.setFont(cancelSendBtnFont);
            cancelSendBtn.setBounds(mailItemWidth * 3 + mailItemPosY * 3 - 55, mailItemPosY, 90, 30);
            subPanel.add(cancelSendBtn);

            inMailListPanel.add(subPanel);

            //列表鼠标事件监听器
            subPanel.addMouseListener(new MouseListener() {

                MailItem item = mailItem;

                @Override
                public void mouseClicked(MouseEvent e) {
                    readSending(item);
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

            cancelSendBtn.addActionListener(new CancelSendBtnActionListener(mailItem));

        }

        JScrollPane inMailListScrollPane = new JScrollPane(inMailListPanel);
        inMailListScrollPane.setBounds(0, 0, elemPanelWidth - 10, mailListPanelHeight);
        inMailListScrollPane.setBorder(BorderFactory.createEmptyBorder());//去除边框
        inMailListScrollPane.getVerticalScrollBar().setUnitIncrement(16);//调整下拉速度
        mailListPanel.add(inMailListScrollPane);

        outMailListPanel.updateUI();
    }

    /**
     *
     */
    public void display() {
        if (isHidden()) {
            reshow();
            super.display();
        }
    }

    /**
     *
     */
    public void refresh() {
        if (isHidden()) {
            return;
        }
        reshow();
    }

    /**
     * @param mailItem
     */
    public void readSending(MailItem mailItem) {
        if (mailItem.tryLock()) {
            OutReadingElement outread = (OutReadingElement) page.getElementById(WorkingPage.OUTREAD);
            outread.readSending(mailItem);
            ((NavigatorElement) page.getElementById(WorkingPage.NAV)).setCurrentElement(outread);
        } else {
            JOptionPane.showMessageDialog(this, "当前无法查看", "警告", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param mailItem
     */
    public void cancelSending(MailItem mailItem) {
        if (mailItem.tryLock()) {
            try {
                outbox.cancel(mailItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reshow();
        } else {
            JOptionPane.showMessageDialog(this, "当前无法取消", "警告", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reshow() {
        maillist = outbox.getMailList();
        drawMailList(maillist);
    }

    //删除按钮监听器
    private class CancelSendBtnActionListener implements ActionListener {
        MailItem item;

        CancelSendBtnActionListener(MailItem item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int cancelSendFlag = JOptionPane.showConfirmDialog(OutBoxElement.this, "确认取消?", "取消发送", JOptionPane.YES_NO_OPTION);

            if (cancelSendFlag == 0) {
                cancelSending(item);
            }
        }
    }
}
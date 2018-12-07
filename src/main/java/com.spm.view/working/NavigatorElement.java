package com.spm.view.working;

import com.spm.view.PageElement;

import javax.swing.*;


/**
 *
 */
public class NavigatorElement extends PageElement {

    /**
     * Default constructor
     */

    private final int navPosX = 100;
    private final int navPosY = 50;
    private final int navWidth = 300;
    private final int navHeight = 500;

    private final int navBtnPosX = 0;
    private final int navBtnPosY = 0;
    private final int navBtnWidth = 300;
    private final int navBtnHeight = 125;
    private final int navBtnGap = 125;

//    private final int navBtnPosX = 50;
//    private final int navBtnPosY = 100;
//    private final int navBtnWidth = 200;
//    private final int navBtnHeight = 25;
//    private final int navBtnGap = 75;

    private JButton writeMailBtn;
    private JButton inBoxBtn;
    private JButton accManagementBtn;
    private JButton outBoxBtn;
    /**
     *
     */
    private PageElement currentElement;


    public NavigatorElement() {
        super();

        setId(WorkingPage.NAV);

        setLayout(null);
        JPanel navigatorPanel = this;
        navigatorPanel.setBounds(navPosX, navPosY, navWidth, navHeight);
        //navigatorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        //navigatorPanel.setBackground(new Color(230, 230, 250));

        JButton writeMailBtn = new JButton("写信", getImageIcon("ui/icon/pen.png", 25, 25));
        writeMailBtn.setBounds(navBtnPosX, navBtnPosY, navBtnWidth, navBtnHeight);
        JButton inBoxBtn = new JButton("收件箱", getImageIcon("ui/icon/mail.png", 25, 25));
        inBoxBtn.setBounds(navBtnPosX, navBtnPosY + navBtnGap * 1, navBtnWidth, navBtnHeight);
        JButton accManagementBtn = new JButton("账号管理", getImageIcon("ui/icon/customer.png", 25, 25));
        accManagementBtn.setBounds(navBtnPosX, navBtnPosY + navBtnGap * 2, navBtnWidth, navBtnHeight);
        JButton outBoxBtn = new JButton("发件箱", getImageIcon("ui/icon/book.png", 25, 25));
        outBoxBtn.setBounds(navBtnPosX, navBtnPosY + navBtnGap * 3, navBtnWidth, navBtnHeight);
        this.writeMailBtn = writeMailBtn;
        this.inBoxBtn = inBoxBtn;
        this.outBoxBtn = outBoxBtn;
        this.accManagementBtn = accManagementBtn;
        navigatorPanel.add(writeMailBtn);
        navigatorPanel.add(inBoxBtn);
        navigatorPanel.add(accManagementBtn);
        navigatorPanel.add(outBoxBtn);

        writeMailBtn.addActionListener(e -> {
            navSending();
        });
        inBoxBtn.addActionListener(e -> {
            navInBox();
        });
        accManagementBtn.addActionListener(e -> {
            navAccount();
        });
        outBoxBtn.addActionListener(e -> {
            navOutBox();
        });
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
        if (!isHidden()) {
            setCurrentElement(page.getElementById(WorkingPage.INBOX));
        }
    }

    /**
     *
     */
    public void navSending() {
        setCurrentElement(page.getElementById(WorkingPage.SEND));
    }

    /**
     *
     */
    public void navInBox() {
        setCurrentElement(page.getElementById(WorkingPage.INBOX));
    }

    /**
     *
     */
    public void navOutBox() {
        setCurrentElement(page.getElementById(WorkingPage.OUTBOX));
    }

    /**
     *
     */
    public void navAccount() {
        setCurrentElement(page.getElementById(WorkingPage.ACC));
    }

    /**
     * @param element
     */
    void setCurrentElement(PageElement element) {
        //current element, do nothing
        if (element == currentElement || element == this) {
            return;
        }
        //hide the old element
        if (currentElement != null) {
            currentElement.hide();
            page.remove(currentElement);
        }
        //add new current-element
        currentElement = element;
        if (element != null) {
            page.add(element);
            element.display();
        }
        page.updateUI();
    }
}
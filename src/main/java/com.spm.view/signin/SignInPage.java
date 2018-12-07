package com.spm.view.signin;

import com.spm.service.ApplicationContext;
import com.spm.service.acc.Account;
import com.spm.view.Page;
import com.spm.view.Window;
import com.spm.view.working.WorkingPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 *
 */
public class SignInPage extends Page {

    /**
     * Default constructor
     */

    private JTextField mailAddrTextField;
    private JPasswordField pwdField;
    /**
     *
     */
    private String addr;
    /**
     *
     */
    private String pwd;
    private Pattern addrPattern = Pattern.compile("^(?:[a-zA-Z0-9-_]+(?:\\.[a-zA-Z0-9-_]+)*@|@)(?:[a-zA-Z0-9-_]+\\.)*[a-zA-Z0-9-_]+");

    public SignInPage() {
        super();
        this.setLayout(null);

        JLabel signInLabel = new JLabel("邮件客户端登录");
        signInLabel.setBounds(425, 100, 150, 50);
        Font signInLabelFont = new Font(Font.DIALOG, Font.BOLD, 20);
        signInLabel.setFont(signInLabelFont);
        this.add(signInLabel);

        JPanel signInPage = new JPanel();
        Font signInPageLabelFont = new Font(Font.DIALOG, Font.PLAIN, 16);
        signInPage.setBounds(300, 150, 400, 275);
        signInPage.setBorder(BorderFactory.createRaisedBevelBorder());
        signInPage.setBackground(new Color(230, 230, 250));
        signInPage.setLayout(null);

        JLabel mailAddrLabel = new JLabel("邮箱:");
        mailAddrLabel.setBounds(25, 25, 50, 30);
        mailAddrLabel.setFont(signInPageLabelFont);
        signInPage.add(mailAddrLabel);

        mailAddrTextField = new JTextField();
        mailAddrTextField.setBounds(25, 60, 350, 30);
        mailAddrTextField.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        signInPage.add(mailAddrTextField);

        JLabel pwdLabel = new JLabel("密码:");
        pwdLabel.setBounds(25, 105, 50, 30);
        pwdLabel.setFont(signInPageLabelFont);
        signInPage.add(pwdLabel);

        pwdField = new JPasswordField();
        pwdField.setBounds(25, 140, 350, 30);
        signInPage.add(pwdField);

        JButton signInBtn = new JButton("登录");
        signInBtn.setBounds(100, 200, 200, 40);
        signInPage.add(signInBtn);
        signInBtn.addActionListener(new SignInBtnActionListener());

        this.add(signInPage);
    }

    /**
     *
     */
    public void display() {
        addr = "";
        pwd = "";
        mailAddrTextField.setText("");
        pwdField.setText("");
        super.display();
    }

    /**
     * Signin, setup, and jump to working page
     */
    public void signIn() {
        if (addrPattern.matcher(addr).matches()) {
            //signin the account
            Account acc = ApplicationContext.getContext().getAccount();
            acc.signInAndSave(addr, pwd);

            //setup & jump to working page
            Window win = Window.getDefaultWindow();
            win.start();
            win.setCurrentPage(new WorkingPage());
        } else {
            JOptionPane.showMessageDialog(this,
                    "邮箱格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param str
     */
    public void setAddr(String str) {
        addr = str;
    }

    /**
     * @param str
     */
    public void setPwd(String str) {
        pwd = str;
    }

    private class SignInBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setAddr(mailAddrTextField.getText());
            setPwd(new String(pwdField.getPassword()));
            signIn();
        }
    }
}
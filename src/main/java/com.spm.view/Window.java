package com.spm.view;

import com.spm.service.ApplicationContext;
import com.spm.service.acc.Account;
import com.spm.service.acc.RefRate;
import com.spm.service.io.InBox;
import com.spm.service.io.OutBox;
import com.spm.service.timer.InTimer;
import com.spm.service.timer.OutTimer;
import com.spm.view.signin.SignInPage;
import com.spm.view.working.WorkingPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 */
public class Window extends JFrame {

    public static final boolean RELEASE = true;
    /**
     *
     */
    private static Window defaultWindow;
    /**
     *
     */
    private Page currentPage;

    /**
     * Default constructor
     */
    private Window() {
        super("m-AIL");

        //font
        Font f = new Font("宋体", Font.PLAIN, 15);
        UIManager.put("Button.font", f);

        //size
        final int windowWidth = 1000;
        final int windowHeight = 650;
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setLocationRelativeTo(null);

        //attach .close()
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
                System.exit(0);
            }
        });
    }

    /**
     *
     */
    public static void main(String[] args) {
        //log to file
        if (RELEASE) {
            try {
                PrintStream out = new PrintStream("log");
                System.setOut(out);
                System.setErr(out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //system style
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        defaultWindow = new Window();
        defaultWindow.close();
        if (ApplicationContext.getContext().getAccount().signIn()) {
            defaultWindow.start();
            defaultWindow.setCurrentPage(new WorkingPage());
        } else {
            defaultWindow.setCurrentPage(new SignInPage());
        }

        defaultWindow.setVisible(true);
    }

    /**
     * @return
     */
    public static Window getDefaultWindow() {
        return defaultWindow;
    }

    /**
     * @return
     */
    public Page getCurrentPage() {
        return currentPage;
    }

    /**
     * @param page
     */
    public void setCurrentPage(Page page) {
        if (currentPage != null) {
            currentPage.hide();
            currentPage.setWindow(null);
            remove(currentPage);
        }
        currentPage = page;
        add(page);
        page.setWindow(this);
        page.display();
        revalidate();
    }

    /**
     * Reset the system.
     */
    public void close() {
        //BASIC operations
        //stop timers
        InTimer.getIns().stop();
        OutTimer.getIns().stop();

        //clear global objects
        ApplicationContext ctx = ApplicationContext.getContext();
        ctx.setAccount(new Account());
        ctx.setInBox(null);
        ctx.setOutBox(null);
    }

    /**
     * Setup the system.
     */
    public void start() {
        //initialize
        ApplicationContext ctx = ApplicationContext.getContext();
        ctx.setInBox(new InBox());
        ctx.setOutBox(new OutBox());
        Account acc = ApplicationContext.getContext().getAccount();
        if (acc.getRefRate() != RefRate.HAND) {
            InTimer.getIns().start();
        }
        OutTimer.getIns().start();
    }

}
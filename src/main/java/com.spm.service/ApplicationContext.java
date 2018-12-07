package com.spm.service;

import com.spm.service.acc.Account;
import com.spm.service.io.InBox;
import com.spm.service.io.OutBox;

/**
 * A global context for the application.
 *
 * @author BoxCatGarden
 */
public class ApplicationContext {

    /**
     * single pattern
     */
    private static ApplicationContext ctx = new ApplicationContext();
    /**
     * the global instance of com.spm.service.io.OutBox
     */
    private OutBox outbox;
    /**
     * the global instance of com.spm.service.io.InBox
     */
    private InBox inbox;
    /**
     * the global instance of com.spm.service.acc.Account
     */
    private Account acc;

    /**
     * Default constructor
     */
    public ApplicationContext() {
    }

    /**
     * @return The instance of ApplicationContext.
     */
    public static ApplicationContext getContext() {
        return ctx;
    }

    /**
     * @return The global instance of com.spm.service.io.OutBox.
     */
    public OutBox getOutBox() {
        return outbox;
    }

    /**
     * @param out The instance to be set as the global instance of com.spm.service.io.OutBox.
     */
    public void setOutBox(OutBox out) {
        outbox = out;
    }

    /**
     * @return The global instance of com.spm.service.io.InBox.
     */
    public InBox getInBox() {
        return inbox;
    }

    /**
     * @param in The instance to be set as the global instance of com.spm.service.io.InBox.
     */
    public void setInBox(InBox in) {
        inbox = in;
    }

    /**
     * @return The global instance of com.spm.service.acc.Account.
     */
    public Account getAccount() {
        return acc;
    }

    /**
     * @param acc The instance to be set as the global instance of com.spm.service.acc.Account.
     */
    public void setAccount(Account acc) {
        this.acc = acc;
    }

}
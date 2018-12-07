package com.spm.service.timer;

import com.spm.service.ApplicationContext;
import com.spm.service.Callback;
import com.spm.service.acc.RefRate;
import com.spm.service.io.InBox;
import com.spm.view.Page;
import com.spm.view.PageElement;
import com.spm.view.Window;
import com.spm.view.working.WorkingPage;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The timer for InBox, which will call .collect() in an interval.
 *
 * @author BoxCatGarden
 */
public class InTimer {

    /**
     * the single instance of InTimer
     */
    private static InTimer ins = new InTimer();
    /**
     * the single instance of Callback as it needs only one
     */
    private static Callback callback = new InRefCallback();
    /**
     * the actual timer that do the schedule
     */
    private Timer timer;
    /**
     * the task to be scheduled
     */
    private TimerTask task;

    /**
     * Default constructor
     */
    public InTimer() {
    }

    /**
     * @return The single instance of InTimer.
     */
    public static InTimer getIns() {
        return ins;
    }

    /**
     * To start the timer.
     * If the timer has been started, it will restart the timer,
     * which is identical to invoking .stop() and then .start().
     */
    public void start() {
        //get the period
        RefRate rr = ApplicationContext.getContext().getAccount().getRefRate();
        long period = rr == RefRate.P15M
                ? 15 * 60000
                : rr == RefRate.P30M
                ? 30 * 60000
                : 60 * 60000;
        //scheduling
        if (timer != null) {
            task.cancel();
            timer.purge();
        } else {
            timer = new Timer();
        }
        task = new InTask();
        timer.schedule(task, 0, period);
    }

    /**
     * To stop the timer.
     * If the timer is not running (stoped or not started), it will do nothing.
     */
    public void stop() {
        if (timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
    }

    /**
     * The task to be scheduled.
     */
    private static class InTask extends TimerTask {

        InBox inbox = ApplicationContext.getContext().getInBox();

        @Override
        public void run() {
            inbox.collect(callback);
        }
    }

    /**
     * The callback that will refresh the 'inbox' page-element.
     */
    private static class InRefCallback implements Callback {

        @Override
        public void invoke(Object re) {
            //do in main-thread
            SwingUtilities.invokeLater(() -> {
                //get the current page
                Page page = Window.getDefaultWindow().getCurrentPage();
                //if the current page is working
                if (page instanceof WorkingPage) {
                    PageElement inbox = page.getElementById("inbox");
                    if (inbox != null) {
                        inbox.refresh();
                    }
                }
            });
        }
    }

}
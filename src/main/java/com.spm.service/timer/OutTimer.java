package com.spm.service.timer;

import com.spm.service.ApplicationContext;
import com.spm.service.Callback;
import com.spm.service.io.OutBox;
import com.spm.view.Page;
import com.spm.view.PageElement;
import com.spm.view.Window;
import com.spm.view.working.WorkingPage;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The timer for OutBox, which will call .dispatch() in an interval.
 *
 * @author BoxCatGarden
 */
public class OutTimer {

    /**
     * the single instance of InTimer
     */
    private static OutTimer ins = new OutTimer();
    /**
     * the single instance of Callback as it needs only one
     */
    private static Callback callback = new OutRefCallback();
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
    public OutTimer() {
    }

    /**
     * @return The single instance of OutTimer.
     */
    public static OutTimer getIns() {
        return ins;
    }

    /**
     * To start the timer.
     * If the timer has been started, it will restart the timer,
     * which is identical to invoking .stop() and then .start().
     */
    public void start() {
        if (timer != null) {
            task.cancel();
            timer.purge();
        } else {
            timer = new Timer();
        }
        task = new OutTask();
        timer.schedule(task, 0, 30 * 60000);
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
    private static class OutTask extends TimerTask {

        OutBox outbox = ApplicationContext.getContext().getOutBox();

        @Override
        public void run() {
            outbox.dispatch(callback);
        }
    }

    /**
     * The callback that will refresh the 'outbox' page-element.
     */
    private static class OutRefCallback implements Callback {

        @Override
        public void invoke(Object re) {
            //do in main-thread
            SwingUtilities.invokeLater(() -> {
                //get the current page
                Page page = Window.getDefaultWindow().getCurrentPage();
                //if the current page is working
                if (page instanceof WorkingPage) {
                    PageElement outbox = page.getElementById("outbox");
                    if (outbox != null) {
                        outbox.refresh();
                    }
                }
            });
        }
    }
}
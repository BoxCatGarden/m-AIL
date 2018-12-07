package com.spm.view;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Page extends JPanel {

    /**
     *
     */
    protected final Map<String, PageElement> eleMap;
    /**
     *
     */
    protected Window window;
    /**
     *
     */
    private boolean hidden = true;

    /**
     * Default constructor
     */
    public Page() {
        super();

        eleMap = new HashMap<>();
    }

    /**
     * @param ele
     */
    public void addElement(PageElement ele) {
        ele.setPage(this);
        eleMap.put(ele.getId(), ele);
    }

    /**
     * @param id
     * @return
     */
    public PageElement getElementById(String id) {
        return eleMap.get(id);
    }

    /**
     * @param id
     */
    public void deleteElementById(String id) {
        eleMap.get(id).setPage(null);
        eleMap.remove(id);
    }

    /**
     *
     */
    public void display() {
        hidden = false;
    }

    /**
     *
     */
    public void refresh() {
        if (hidden) {
            return;
        }
        //refresh elements
        for (PageElement ele : eleMap.values()) {
            ele.refresh();
        }
    }

    /**
     *
     */
    public void hide() {
        if (window == null) {
            return;
        }
        hidden = true;
    }

    /**
     * @param win
     */
    public void setWindow(Window win) {
        window = win;
    }

    /**
     * @return
     */
    public boolean isHidden() {
        return hidden;
    }


}
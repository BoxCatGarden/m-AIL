package com.spm.view;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class PageElement extends JPanel {

    /**
     *
     */
    protected Page page;
    /**
     *
     */
    private String id;
    /**
     *
     */
    private boolean hidden = true;
    /**
     *
     */
    private int top;
    /**
     *
     */
    private int left;

    /**
     * Default constructor
     */
    public PageElement() {
        super();
    }

    protected static ImageIcon getImageIcon(String path, int width, int height) {
        try {
            ImageIcon imageIcon = new ImageIcon(PageElement.class.getClassLoader().getResource(path));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
            return imageIcon;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        // DO NOTHING
    }

    /**
     *
     */
    public void hide() {
        hidden = true;
    }

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param str
     */
    public void setId(String str) {
        id = str;
    }

    /**
     * @return
     */
    public int getTop() {
        return top;
    }

    /**
     * @param iTop
     */
    public void setTop(int iTop) {
        top = iTop;
    }

    /**
     * @return
     */
    public int getLeft() {
        return left;
    }

    /**
     * @param iLeft
     */
    public void setLeft(int iLeft) {
        left = iLeft;
    }

    /**
     * @param pg
     */
    public void setPage(Page pg) {
        page = pg;
    }

    /**
     * @return
     */
    public boolean isHidden() {
        return hidden;
    }

}
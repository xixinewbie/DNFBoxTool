package com.xixinewbie.dnftool.ui;

import java.awt.*;


public class VerticalFlowLayout extends FlowLayout {
    private static final long serialVersionUID = 1L;
    public static final int TOP = 0;

    public static final int MIDDLE = 1;

    public static final int BOTTOM = 2;

    int hgap;
    int vgap;
    boolean hfill;
    boolean vfill;
    private int splitLineThickness=1;
    
    public VerticalFlowLayout() {
        this(TOP, 5, 5, true, false);
    }

    public VerticalFlowLayout(boolean hfill, boolean vfill) {
        this(TOP, 5, 5, hfill, vfill);
    }

    public VerticalFlowLayout(int align) {
        this(align, 5, 5, true, false);
    }

    public VerticalFlowLayout(int align, boolean hfill, boolean vfill) {
        this(align, 5, 5, hfill, vfill);
    }

    public VerticalFlowLayout(int align, int hgap, int vgap, boolean hfill, boolean vfill) {
        setAlignment(align);
        this.hgap = hgap;
        this.vgap = vgap;
        this.hfill = hfill;
        this.vfill = vfill;
    }
    
    public Dimension preferredLayoutSize(Container target) {
        Dimension tarsiz = new Dimension(0, 0);
        
        for (int i = 0; i < target.getComponentCount(); i++) {
            Component m = target.getComponent(i);
            
            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();
                tarsiz.width = Math.max(tarsiz.width, d.width);
                
                if (i > 0) {
                    // 除了第一个 item，每个 item 之前都要加 vgap 和分隔线厚度
                    tarsiz.height += vgap;
                    tarsiz.height += splitLineThickness; // 关键：这里漏加了
                }
                
                tarsiz.height += d.height;
            }
        }
        
        Insets insets = target.getInsets();
        tarsiz.width += insets.left + insets.right + hgap * 2;
        tarsiz.height += insets.top + insets.bottom + vgap * 2;
        
        return tarsiz;
    }

    public Dimension minimumLayoutSize(Container target) {
        Dimension tarsiz = new Dimension(0, 0);

        for (int i = 0; i < target.getComponentCount(); i++) {
            Component m = target.getComponent(i);

            if (m.isVisible()) {
                Dimension d = m.getMinimumSize();
                tarsiz.width = Math.max(tarsiz.width, d.width);

                if (i > 0) {
                    tarsiz.height += vgap;
                    tarsiz.height += splitLineThickness;
                }

                tarsiz.height += d.height;
            }
        }

        Insets insets = target.getInsets();
        tarsiz.width += insets.left + insets.right + hgap * 2;
        tarsiz.height += insets.top + insets.bottom + vgap * 2;

        return tarsiz;
    }

    public void setVerticalFill(boolean vfill) {
        this.vfill = vfill;
    }

    public boolean getVerticalFill() {
        return vfill;
    }

    public void setHorizontalFill(boolean hfill) {
        this.hfill = hfill;
    }

    public boolean getHorizontalFill() {
        return hfill;
    }

    private void placeItems(Container target, int x, int y, int width, int height, int first, int last) {
        int align = getAlignment();

        if (align == MIDDLE) {
            y += height / 2;
        }

        if (align == BOTTOM) {
            y += height;
        }

        for (int i = first; i < last; i++) {
            Component m = target.getComponent(i);
            Dimension md = m.getSize();

            if (m.isVisible()) {
                int px = x + (width - md.width) / 2;
                m.setLocation(px, y);
                y += md.height; // 组件高度
                
                // 如果不是最后一个组件，并且显示分隔线，则在组件下方预留分隔线和vgap的空间
                if (i < last - 1) {
                    y += splitLineThickness; // 分隔线厚度
                }
                y += vgap; // vgap
            }
        }
    }

    public void layoutContainer(Container target) {
        Insets insets = target.getInsets();
        int maxheight = target.getSize().height - (insets.top + insets.bottom + vgap * 2);
        int maxwidth = target.getSize().width - (insets.left + insets.right + hgap * 2);
        int numcomp = target.getComponentCount();
        int x = insets.left + hgap, y = 0;
        int colw = 0, start = 0;

        for (int i = 0; i < numcomp; i++) {
            Component m = target.getComponent(i);

            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();

                // fit last component to remaining height
                if ((this.vfill) && (i == (numcomp - 1))) {
                    d.height = Math.max((maxheight - y), m.getPreferredSize().height);
                }

                // fit component size to container width
                if (this.hfill) {
                    m.setSize(maxwidth, d.height);
                    d.width = maxwidth;
                } else {
                    m.setSize(d.width, d.height);
                }
                // 计算当前组件加上分隔线和vgap后的总高度
                int currentItemTotalHeight = d.height;
                if (i > 0) { // 除了第一个组件，其他组件前面都有vgap
                    currentItemTotalHeight += vgap;
                        currentItemTotalHeight += splitLineThickness;
                }
                if (y + currentItemTotalHeight > maxheight) {
                    placeItems(target, x, insets.top + vgap, colw, maxheight - y, start, i);
                    y = d.height;
                    x += hgap + colw;
                    colw = d.width;
                    start = i;
                } else {
                    if (y > 0) {
                        y += vgap;
                    }

                    y += d.height;
                    colw = Math.max(colw, d.width);
                }
            }
        }

        placeItems(target, x, insets.top + vgap, colw, maxheight - y, start, numcomp);
    }
}
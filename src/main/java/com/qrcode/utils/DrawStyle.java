package com.qrcode.utils;

import org.springframework.util.StringUtils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * 绘制二维码信息的样式
 */
public enum DrawStyle {
    RECT { // 矩形

        @Override
        public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img) {
            g2d.fillRect(x, y, w, h);
        }

    },
    CIRCLE {
        // 圆点
        @Override
        public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img) {
            g2d.fill(new Ellipse2D.Float(x, y, w, h));
        }
    },
    TRIANGLE {
        // 三角形
        @Override
        public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img) {
            int px[] = {x, x + (w >> 1), x + w};
            int py[] = {y + w, y, y + w};
            g2d.fillPolygon(px, py, 3);
        }
    },
    DIAMOND {
        // 五边形-钻石
        @Override
        public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img) {
            int cell4 = size >> 2;
            int cell2 = size >> 1;
            int px[] = {x + cell4, x + size - cell4, x + size, x + cell2, x};
            int py[] = {y, y, y + cell2, y + size, y + cell2};
            g2d.fillPolygon(px, py, 5);
        }
    },
    SEXANGLE {
        // 六边形
        @Override
        public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img) {
            int add = size >> 2;
            int px[] = {x + add, x + size - add, x + size, x + size - add, x + add, x};
            int py[] = {y, y, y + add + add, y + size, y + size, y + add + add};
            g2d.fillPolygon(px, py, 6);
        }
    },
    OCTAGON {
        // 八边形
        @Override
        public void draw(Graphics2D g2d, int x, int y, int size, int h, BufferedImage img) {
            int add = size / 3;
            int px[] = {x + add, x + size - add, x + size, x + size, x + size - add, x + add, x, x};
            int py[] = {y, y, y + add, y + size - add, y + size, y + size, y + size - add, y + add};
            g2d.fillPolygon(px, py, 8);
        }

    },
    IMAGE {
        // 自定义图片
        @Override
        public void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img) {
            g2d.drawImage(img, x, y, w, h, null);
        }
    },;

    private static Map<String, DrawStyle> map;

    static {
        map = new HashMap<>(7);
        for (DrawStyle style : DrawStyle.values()) {
            map.put(style.name(), style);
        }
    }

    public static DrawStyle getDrawStyle(String name) {
        if (StringUtils.isEmpty(name)) { // 默认返回矩形
            return RECT;
        }

        DrawStyle style = map.get(name.toUpperCase());
        return style == null ? RECT : style;
    }


    public abstract void draw(Graphics2D g2d, int x, int y, int w, int h, BufferedImage img);

}
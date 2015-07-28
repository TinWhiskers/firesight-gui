package io.tinwhiskers.firesight.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

@SuppressWarnings("serial")
class IconListRenderer extends DefaultListCellRenderer {
    private Map<File, CacheKey> cache = new HashMap<File, CacheKey>();
    
    public IconListRenderer(JList<File> list) {
        list.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cache.clear();
                repaint();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                cache.clear();
                repaint();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                cache.clear();
                repaint();
            }
        });
    }
    
    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        try {
            File file = (File) value;
            Icon icon = this.getIcon(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalTextPosition(CENTER);
            label.setVerticalTextPosition(TOP);
            label.setText(file.getName());
            label.setIcon(icon);
            label.invalidate();
            return label;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected Icon getIcon(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) throws Exception {
        File file = (File) value;
        if (cache.containsKey(value)) {
            CacheKey key = cache.get(value);
            if (key.timestamp == file.lastModified()) {
                return key.icon;
            }
        }
        try {
            Image image = ImageIO.read(file);
            BufferedImage thumbnail = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);

            int width = thumbnail.getWidth();
            int height = thumbnail.getHeight();

            double destWidth = width, destHeight = height;

            double lastWidth = width;
            double lastHeight = height;

            double lastSourceWidth = image.getWidth(null);
            double lastSourceHeight = image.getHeight(null);

            double heightRatio = lastSourceHeight / destHeight;
            double widthRatio = lastSourceWidth / destWidth;
            
            int scaledWidth, scaledHeight;

            if (heightRatio > widthRatio) {
                double aspectRatio = lastSourceWidth / lastSourceHeight;
                scaledHeight = (int) destHeight;
                scaledWidth = (int) (scaledHeight * aspectRatio);
            }
            else {
                double aspectRatio = lastSourceHeight / lastSourceWidth;
                scaledWidth = (int) destWidth;
                scaledHeight = (int) (scaledWidth * aspectRatio);
            }

            int imageX = (width / 2) - (scaledWidth / 2);
            int imageY = (height / 2) - (scaledHeight / 2);

            double scaleRatioX = lastSourceWidth / (double) scaledWidth;
            double scaleRatioY = lastSourceHeight / (double) scaledHeight;
            
            Graphics g = thumbnail.getGraphics();
            g.drawImage(image, imageX, imageY, scaledWidth, scaledHeight, null);
            g.dispose();
            Icon icon = new ImageIcon(thumbnail);
            CacheKey key = new CacheKey(file, file.lastModified(), icon);
            cache.put(file, key);
            return icon;
        }
        catch (Exception e) {
            System.out.println("Failed to create icon for " + file);
            return null;
        }
    }
    
    class CacheKey {
        private final File file;
        private final long timestamp;
        private final Icon icon;
        
        public CacheKey(File file, long timestamp, Icon icon) {
            this.file = file;
            this.timestamp = timestamp;
            this.icon = icon;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((file == null) ? 0 : file.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (file == null) {
                if (other.file != null) {
                    return false;
                }
            }
            else if (!file.equals(other.file)) {
                return false;
            }
            return true;
        }

        private IconListRenderer getOuterType() {
            return IconListRenderer.this;
        }
    }
}
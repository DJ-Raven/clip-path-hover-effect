package test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ClipPathHoverEffect extends JComponent {

    public Icon getImage() {
        return image;
    }

    public void setImage(Icon image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int size = 300;
    private Icon image;
    private Point point = new Point(-size / 2, -size / 2);
    private Shape clip = new Ellipse2D.Double(0, 0, size, size);
    private String text = "Clip Hover Effect";

    public ClipPathHoverEffect() {
        setOpaque(true);
        setBackground(new Color(70, 70, 70));
        setForeground(new Color(200, 200, 200));
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                int x = me.getX() - size / 2;
                int y = me.getY() - size / 2;
                point = new Point(x, y);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        if (image != null) {
            drawImage(g2);
        }
        g2.dispose();
        super.paintComponent(grphcs);
    }

    private void drawImage(Graphics2D g2) {
        //  Draw Text
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(text, g2);
        float x = (float) (getWidth() - r2.getWidth()) / 2;
        float y = (float) ((getHeight() - r2.getHeight()) / 2) + fm.getAscent();
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Rectangle rec = getAutoSize(image);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform tran = g.getTransform();
        g.translate(point.x, point.y);
        g.fill(clip);
        g.setComposite(AlphaComposite.SrcIn);
        g.setTransform(tran);
        g.drawImage(toImage(image), rec.x, rec.y, rec.width, rec.height, null);
        //  Create Shape Text
        GlyphVector vect = getFont().createGlyphVector(g2.getFontRenderContext(), text);
        Shape shape = vect.getOutline(x, y);
        g.setPaint(new GradientPaint(x, 0, new Color(233, 15, 15), (int) (x + r2.getWidth()), 0, new Color(89, 14, 233)));
        g.fill(shape);
        g.dispose();
        g2.drawImage(img, 0, 0, null);
    }

    private Rectangle getAutoSize(Icon image) {
        int w = getWidth();
        int h = getHeight();
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.max(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        return new Rectangle(x, y, width, height);
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }
}

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Comic.java
 * Represents a comic strip
 */
public class Comic {

    /**
     * A list of the individual panels of the comic
     */
    private List<BufferedImage> panels;

    /**
     * The maximum number of panels per row
     */
    private int width;

    /**
     * The stroke that outlines each panel in the comic
     */
    public static final BasicStroke stroke = new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

    /**
     * The right and left padding between each panel in the comic
     */
    public static final double X_PADDING = 10;

    /**
     * The top and bottom padding between each panel in the comic
     */
    public static final double Y_PADDING = 10;

    /**
     * Create a new comic comprised of a set of panels
     * @param panels A list of images (panels) that will form the comic
     * @param width The maximum number of panels per row in the comic
     */
    public Comic(List<BufferedImage> panels, int width) {
        this.panels = panels;
        this.width = width;
    }

    /**
     * Creates a comic strip from the inputting panels
     * @return A BufferedImage of the final comic
     */
    public BufferedImage toImage() {
        BufferedImage result = getBlankCanvas();
        Graphics2D ga = (Graphics2D) result.getGraphics();
        ga.setStroke(stroke);

        double currX = X_PADDING;
        double currY = Y_PADDING;
        double rowHeight = 0;

        for (int i = 0; i < panels.size(); i++) {
            BufferedImage thisPanel = panels.get(i);
            ga.drawImage(thisPanel, (int) currX, (int) currY, null);

            Rectangle2D main = new Rectangle2D.Double(currX, currY, thisPanel.getWidth(), thisPanel.getHeight());
            ga.setColor(Color.BLACK);
            ga.draw(main);


            currX += thisPanel.getWidth() + X_PADDING;

            if (thisPanel.getHeight() > rowHeight) {
                rowHeight = thisPanel.getHeight();
            }

            if ((i + 1) % (width) == 0) {
                currX = X_PADDING;
                currY += rowHeight + Y_PADDING;

            }
        }

        ga.dispose();
        return result;
    }

    /**
     * Calculate the final size of the comic and create a BufferedImage to act as a canvas
     * @return The blank comic image (no panels)
     */
    private BufferedImage getBlankCanvas() {
        double maxWidth = 0;
        double totalHeight = Y_PADDING;

        int rowWidth = 0;
        int rowHeight = 0;
        int rows = 0;
        int i;
        for (i = 0; i < panels.size(); i++) {
            BufferedImage img = panels.get(i);
            rowWidth += img.getWidth() + X_PADDING;

            if (img.getHeight() > rowHeight) {
                rowHeight = img.getHeight();
            }

            if ((i + 1) % (width) == 0) {
                if (rowWidth > maxWidth) {
                    maxWidth = rowWidth;
                }

                totalHeight += rowHeight + Y_PADDING;
                rowWidth = 0;
                rowHeight = 0;
                rows++;
            }

        }
        if(rows < (i % width)) {
            totalHeight += rowHeight + Y_PADDING;
        }
        maxWidth+= X_PADDING;

        BufferedImage result = new BufferedImage((int) maxWidth, (int) totalHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().fillRect(0, 0, (int) maxWidth, (int) totalHeight);
        return result;
    }
}

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Bubbletext.java
 * Represents a drawable speech bubble
 */
public class BubbleText {

    /**
     * The padding to the right and left of the text in pixels
     */
    public static final double X_PADDING = 15;

    /**
     * The padding to the top and bottom of the text in pixels
     */
    public static final double Y_PADDING = 10;

    /**
     * The maximum number of lines this speech bubble will contain
     */
    public static final int MAX_LINES = 5;

    /**
     * The stroke that outlines the bubble
     */
    public static final BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);




    /**
     * An array of lines of the input text split to fit to a certain width
     */
    private String[] newString;

    /**
     * The number of lines the text consumes in the speech bubble
     */
    private int lines;

    /**
     * A string of the longest line that is in the speech bubble
     */
    private String maxLine;

    /**
     * The maximum number of characters wide the speech bubble will be
     */
    private int lineLength;

    /**
     * Represents the direction the bubble points towards
     */
    public enum Pointing {
        LEFT(0.7, 0.6, 0.5),
        RIGHT(0.4, 0.3, 0.5);

        double a;
        double b;
        double c;

        Pointing(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    /**
     * Creates a new speech bubble with the specified text and maximum line length
     * @param text The text encompassed by this speech bubble
     * @param lineLength The maximum number of characters wide the speech bubble will be
     */
    private BubbleText(String text, int lineLength) {
        this.lineLength = lineLength;
        this.lines = 0;
        this.maxLine = "";
        this.newString = splitLine(text.toUpperCase());
    }

    /**
     * Draw this speech bubble into an existing graphics context
     * @param ga The graphics to draw into
     * @param posX The X position in parent graphics object to draw to
     * @param posY The Y position in the parent graphics object to draw to
     * @param point The direction to point the speech bubble towards
     */
    public void draw(Graphics2D ga, double posX, double posY, Pointing point) {

        FontRenderContext frc = ga.getFontRenderContext();
        Rectangle2D layout = this.getMaxLineBounds(frc, ga.getFont());
        ga.setStroke(stroke);

        double bX = posX;
        double bY = (posY - layout.getHeight());
        double bW = layout.getWidth() + X_PADDING;
        double bH = (newString.length * layout.getHeight() + 0.5) + Y_PADDING;

        Polygon p = new Polygon();
        p.addPoint((int) (bX + bW * point.a), (int) (bY + bH - 1));
        p.addPoint((int) (bX + bW * point.b), (int) (bY + bH - 1));
        p.addPoint((int) (bX + bW * point.c), (int) (bY + bH + 30));
        ga.setPaint(Color.WHITE);
        ga.fill(p);
        ga.setColor(Color.BLACK);

        ga.draw(p);

        RoundRectangle2D main = new RoundRectangle2D.Double(bX, bY, bW, bH, 10, 10);
        ga.setPaint(Color.WHITE);
        ga.fill(main);
        ga.setColor(Color.BLACK);
        ga.draw(main);

        ga.setPaint(Color.BLACK);
        for (int i = 0; i < newString.length; i++) {
            ga.drawString(newString[i], (int) posX + 5, (int) ((posY + 5) + i * layout.getHeight() + 0.5));
        }
    }

    /**
     * Get the individual lines of the transformed text
     * @return An array of lines contained by the speech bubble
     */
    public String[] getNewString() {
        return newString;
    }

    /**
     * Get the number of lines this speech bubble contains
     * @return The number of lines in this speech bubble
     */
    public int getNumberOfLines() {
        return lines;
    }

    /**
     * Get the bounds of the longest line in the speech bubble
     * @param frc The rendering context to make the calculation
     * @param font The font to calculate for
     * @return A rectangle that bounds the longest line
     */
    private Rectangle2D getMaxLineBounds(FontRenderContext frc, Font font) {
        Rectangle2D result = new TextLayout(maxLine, font, frc).getBounds();
        return result;
    }

    /**
     * Get the bounds for the entire speech bubble
     * @param ga The graphics context to use to make the calculation
     * @return A rectangle representing the bounds of this speech bubble
     */
    public Rectangle2D getBounds(Graphics2D ga) {
        FontRenderContext frc = ga.getFontRenderContext();
        Rectangle2D result = new TextLayout(maxLine, ga.getFont(), frc).getBounds();

        return new Rectangle2D.Double(result.getX(), result.getY(),
                result.getWidth() + X_PADDING, (newString.length * result.getHeight() + 0.5) + Y_PADDING);
    }


    /**
     * Creates a speech bubble from the input text, splits into multiple speech bubbles if necessary
     * @param text The text to be inside this speech bubble
     * @param monolouge Whether the speech bubble represents a single speech or a small reply in conversation
     * @return A list of speech bubbles generated from the input text
     */
    public static List<BubbleText> createText(String text, boolean monolouge) {
        List<BubbleText> result = new ArrayList<>();
        BubbleText t = new BubbleText(text, monolouge ? 26 : 13);

        while (true) {
            if (t.lines > MAX_LINES) {
                String[] overflow = t.getNewString();
                result.add(new BubbleText(createString(overflow, 0, MAX_LINES), t.lineLength));
                t = new BubbleText(createString(overflow, MAX_LINES, t.getNewString().length), 26);
            } else {
                result.add(t);
                break;
            }
        }

        return result;
    }

    /**
     * Join a string array with spaces
     * @param allLines The array to join
     * @param start The starting index of the array
     * @param end The ending index of the array
     * @return The joined string
     */
    private static String createString(String[] allLines, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(allLines[i]);
            sb.append(" ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

    /**
     * Split the input string into multiple lines to conform to the max line length
     * @param in The input string
     * @return An array of lines split appropriately
     */
    private String[] splitLine(String in) {
        String line = wrap(in, lineLength, "\n", true);
        String[] allLines = line.split("\n");

        lines = allLines.length;
        for (String s : allLines) {
            if (s.length() > maxLine.length()) {
                maxLine = s;
            }
        }
        return allLines;
    }

    // Taken from org.apache.commons.lang3.text.WordUtils
    public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords) {
        if (str == null) {
            return null;
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

        while (offset < inputLineLength) {
            if (str.charAt(offset) == ' ') {
                offset++;
                continue;
            }
            // only last line without leading spaces is left
            if (inputLineLength - offset <= wrapLength) {
                break;
            }
            int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

            if (spaceToWrapAt >= offset) {
                // normal case
                wrappedLine.append(str.substring(offset, spaceToWrapAt));
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;

            } else {
                // really long word or URL
                if (wrapLongWords) {
                    // wrap really long word one line at a time
                    wrappedLine.append(str.substring(offset, wrapLength + offset));
                    wrappedLine.append(newLineStr);
                    offset += wrapLength;
                } else {
                    // do not wrap really long word, just extend beyond limit
                    spaceToWrapAt = str.indexOf(' ', wrapLength + offset);
                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str.substring(offset, spaceToWrapAt));
                        wrappedLine.append(newLineStr);
                        offset = spaceToWrapAt + 1;
                    } else {
                        wrappedLine.append(str.substring(offset));
                        offset = inputLineLength;
                    }
                }
            }
        }

        // Whatever is left in line is short enough to just pass through
        wrappedLine.append(str.substring(offset));

        return wrappedLine.toString();
    }


}

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Conversation.java
 * Represents a conversation between multiple people
 */
public class Conversation {

    /**
     * A message in the conversation
     */
    private class Message {
        /**
         * The person that said the message
         */
        Person p;

        /**
         * The actual contents of the message
         */
        String msg;

        Message(Person p, String msg) {
            this.p = p;
            this.msg = msg;
        }
    }

    /**
     * A map of all participants in the conversation
     */
    private Map<String, Person> participants;

    /**
     * A list of messages that make up this conversation
     */
    private List<Message> messages;

    /**
     * Creates a new conversation from a list of comma separated Strings.
     * Each line has the following format:
     * <nickname>,<message>
     *
     * @param lines The comma separated list of messages
     */
    public Conversation(List<String> lines) {
        participants = new TreeMap<>();
        messages = new ArrayList<>();
        init(lines);
        assignCharacters();
    }

    /**
     * Convert comma separated lines into messages and establish the participants in the conversation
     *
     * @param lines The list of comma separated lines as the input
     */
    private void init(List<String> lines) {
        for (String line : lines) {
            String nick = line.split(",")[0];
            String msg = line.substring(line.indexOf(',') + 1);

            if (!participants.containsKey(nick)) {
                participants.put(nick, new Person(nick));
            }
            Message m = new Message(participants.get(nick), msg);
            messages.add(m);
        }
    }

    /**
     * Assign each participant in the conversation with a character
     */
    private void assignCharacters() {
        AssetLoader ldr = AssetLoader.getInstance();
        Set<Character> already = new TreeSet<>();

        for (Person p : participants.values()) {
            Character c = ldr.getCharacter("nat");
            p.assignCharacter(c);
        }

    }

    /**
     * Create all the panels in the comic from the messages in the conversation
     *
     * @return A list of images that represent the panels in the comic
     */
    public List<BufferedImage> toImages() {
        List<BufferedImage> panels = new ArrayList<>();
        AssetLoader ldr = AssetLoader.getInstance();

        BufferedImage image = ldr.getBackground("basket");
        image = backgroundZoom(image);

        for (Message msg : messages) {
            BufferedImage overlay = msg.p.getCharacter().getImage("neutral");
            BufferedImage resized = toInitialSize(overlay, false);

            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage combined = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);

            Graphics2D ga = (Graphics2D) combined.getGraphics();
            ga.drawImage(image, 0, 0, null);
            //ga.drawImage(resized, 0, 150, null); // left init
            //ga.drawImage(resized2, w - 150, 150, null); // right init
            ga.drawImage(resized, -40, h - 200, null); //zoomed

            ga.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));


            List<BubbleText> list = BubbleText.createText(msg.msg, true);
            BubbleText line = list.get(0);
            line.draw(ga, 10, 20, BubbleText.Pointing.LEFT);

            /*
            List<BubbleText> list2 = BubbleText.createText("ur a cunt stfu", false);
            BubbleText line2 = list2.get(0);
            Rectangle2D other = line.getBounds(ga);
            line2.draw(ga, other.getX() + other.getWidth() - 20, other.getY() + other.getHeight() + 40, BubbleText.Pointing.RIGHT);
            */
            ga.dispose();
            panels.add(combined);

        }
        return panels;
    }


    //todo: fix remaining methods in this class
    public static BufferedImage toInitialSize(BufferedImage overlay, boolean flip) {
        int height = 180;
        int width = (int) (0.9166 * height + 1);

        BufferedImage resized = new BufferedImage(width, height, overlay.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(overlay, 0, 0, width, height, 0, 0, overlay.getWidth(),
                overlay.getHeight(), null);

        if (flip) {
            resized = flip(resized);
        }

        g.dispose();
        return resized;
    }


    public static BufferedImage flip(BufferedImage in) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-in.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(in, null);
    }

    public static BufferedImage backgroundZoom(BufferedImage back) {
        BufferedImage resized = new BufferedImage(back.getWidth(), back.getHeight(), back.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(back, 0, 0, resized.getWidth(), resized.getHeight(), 0, 80, (int) (resized.getWidth() * 0.60),
                (int) ((resized.getHeight() + 80) * 0.60), null);

        g.dispose();
        return resized;
    }

    public static BufferedImage toZoomed(BufferedImage src, boolean flip) {
        Rectangle rect = new Rectangle(0, 0, src.getWidth(), 200);
        if (flip) {
            src = flip(src);
        }
        BufferedImage dest = src.getSubimage(0, 0, rect.width, rect.height);
        return dest;
    }

}

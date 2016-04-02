import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

        for (Person p : participants.values()) {
            Character c;
            if (ldr.hasCharacter(p.getNick())) {
                c = ldr.getCharacter(p.getNick());
            } else {
                c = ldr.getRandomCharacter();
            }
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
        BufferedImage background = ldr.getBackground("basket");

        Iterator<Message> it = messages.iterator();
        List<Message> temp = new ArrayList<>();
        while (it.hasNext()) {
            Message m = it.next();


            if (temp.size() == 1) {
                if (m.p.equals(temp.get(0).p)) {
                    panels.add(messagesToPanel(background, temp));
                    temp.clear();
                }
            }

            if (temp.size() == 2) {
                temp.get(0).p.setFacing(true);
                temp.get(1).p.setFacing(false);
                panels.add(messagesToPanel(background, temp));
                temp.clear();

            }

            temp.add(m);
        }

        if (!temp.isEmpty()) {
            panels.add(messagesToPanel(background, temp));
        }


        return panels;
    }

    private BufferedImage messagesToPanel(BufferedImage background, List<Message> messages) {
        int w = background.getWidth();
        int h = background.getHeight();
        BufferedImage combined = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ga = (Graphics2D) combined.getGraphics();

        ga.setFont(AssetLoader.getInstance().getFont("ldfcomicsansb"));

        List<BubbleText> list;
        BubbleText line;
        switch (messages.size()) {

            case 1:
                Message msg = messages.get(0);
                BufferedImage person = toZoomed(1, msg.p.getCharacter().getImage("neutral"), !msg.p.isFacingRight());

                ga.drawImage(backgroundZoom(1, background), 0, 0, null);
                ga.drawImage(person, -40, h - 200, null); //zoomed


                list = BubbleText.createText(msg.msg, true);
                line = list.get(0);
                line.draw(ga, 10, 20, BubbleText.Pointing.LEFT);
                break;
            case 2:
            case 3:
            case 4:
                Message msg1 = messages.get(0);
                Message msg2 = messages.get(1);

                BufferedImage person1 = toZoomed(2, msg1.p.getCharacter().getImage("neutral"), false);
                BufferedImage person2 = toZoomed(2, msg2.p.getCharacter().getImage("neutral"), true);

                ga.drawImage(backgroundZoom(1, background), 0, 0, null);
                ga.drawImage(person1, 0, 150, null); // left init
                ga.drawImage(person2, w - 150, 150, null); // right init

                list = BubbleText.createText(msg1.msg, true);
                line = list.get(0);
                line.draw(ga, 10, 20, BubbleText.Pointing.LEFT);

                list = BubbleText.createText(msg2.msg, false);
                BubbleText line2 = list.get(0);
                Rectangle2D other = line.getBounds(ga);
                line2.draw(ga, w - line2.getBounds(ga).getWidth(), other.getY() + other.getHeight() + 40, BubbleText.Pointing.RIGHT);

                break;
            default:
                break;
        }


        ga.dispose();
        return combined;

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

    public static BufferedImage backgroundZoom(int level, BufferedImage back) {
        BufferedImage resized = new BufferedImage(back.getWidth(), back.getHeight(), back.getType());
        Graphics2D g = resized.createGraphics();

        double zoom = 1.0f;
        final double[] scales = {0.6f, 1.0f, 1.0f, 1.0f};
        if (level < scales.length && level > 0) {
            zoom = scales[level - 1];
        }


        g.drawImage(back, 0, 0, resized.getWidth(), resized.getHeight(), 0, 80, (int) (resized.getWidth() * zoom),
                (int) ((resized.getHeight() + 80) * zoom), null);

        g.dispose();
        return resized;
    }

    public static BufferedImage toZoomed(int level, BufferedImage src, boolean flip) {
        BufferedImage dest;

        switch (level) {
            case 1:
                Rectangle rect = new Rectangle(0, 0, src.getWidth(), (int) (src.getHeight() * 0.5));
                if (flip) {
                    src = flip(src);
                }
                dest = src.getSubimage(0, 0, rect.width, rect.height);
                break;
            default:
                dest = toInitialSize(src, flip);
                break;
        }

        return dest;
    }

}

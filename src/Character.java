import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Character.java
 * Represents a character with several different expression sprites
 */
public class Character implements Comparable{

    /**
     * The name of this character
     */
    private String name;

    /**
     * A map between the name of the expression and the expression image. eg: "neutral"
     */
    private Map<String, BufferedImage> sprites;

    /**
     * Creates a new character
     * @param name The name of the character
     * @param sprites All the images associated with this character
     */
    public Character(String name, Map<String, BufferedImage> sprites) {
        this.name = name;
        this.sprites = sprites;
    }

    /**
     * Get all the expressions associated with this character
     * @return
     */
    public Map<String,BufferedImage> getImages() {
        return sprites;
    }

    /**
     * A a specific image of the character
     * @param expression The name of the expression
     * @return
     */
    public BufferedImage getImage(String expression) {
        return sprites.get(expression);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}

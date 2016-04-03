import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * AssetLoader.java - Loads all characters and background images
 */
public class AssetLoader {

    /**
     * The single assets instance
     */
    private static AssetLoader instance = new AssetLoader();

    /**
     * A map between character names and their own character object
     */
    private Map<String, Character> characters = new HashMap<>();

    /**
     * A map between background names and the images that contain them
     */
    private Map<String, BufferedImage> backgrounds = new HashMap<>();

    private Map<String, Font> fonts = new HashMap<>();

    /**
     * Attempts to load all characters and background images
     */
    private AssetLoader() {
        File path = new File("mschat/");

        File characters = new File(path, "characters/");
        File backgrounds = new File(path, "backgrounds/");
        File fonts = new File(path, "fonts/");

        try {
            loadBackgrounds(backgrounds);
            loadCharacters(characters);
            loadFonts(fonts);
        } catch (IOException ioe) {
            System.err.println("Error loading file: " + ioe.getLocalizedMessage());
        } catch (FontFormatException ffe) {
            System.err.println("Error loading font: " + ffe.getLocalizedMessage());
        }
    }

    /**
     * Gets the single instance of this loader
     *
     * @return The static Asset Loader
     */
    public static AssetLoader getInstance() {
        return instance;
    }

    /**
     * Get a character by their name
     *
     * @param name The name of the character to query
     * @return The character queried
     */
    public Character getCharacter(String name) {
        return characters.get(name);
    }

    /**
     * Get a random character loaded
     *
     * @return A random character
     */
    public Character getRandomCharacter() {
        String rand = (String) characters.keySet().toArray()[(int) (Math.random() * characters.size())];
        return getCharacter(rand);
    }

    public boolean hasCharacter(String name) {
        return characters.containsKey(name);
    }

    /**
     * Get a background image by name
     *
     * @param background The name of the background
     * @return A BufferedImage of the background requested
     */
    public BufferedImage getBackground(String background) {
        return backgrounds.get(background);
    }

    /**
     * Load all characters from the root directory.
     * Each character has their own folder with a selection of expressions in separate image files
     *
     * @param root The root "characters" directory
     * @throws IOException If a file is not an image or can not be loaded
     */
    private void loadCharacters(File root) throws IOException {
        for (File dir : root.listFiles()) {
            if (dir.isDirectory()) {
                String name = dir.getName();
                if (characters.containsKey(name)) {
                    continue;
                }

                Map<String, BufferedImage> sprites = new TreeMap<>();
                for (File f : dir.listFiles()) {
                    sprites.put(getName(f), ImageIO.read(f));
                }

                characters.put(name, new Character(name, sprites));
            }
        }
    }


    /**
     * Load all the background images from the specified directory
     *
     * @param dir The directory containing all the backgrounds
     * @throws IOException If a file is not an image or cannot be loaded
     */
    private void loadBackgrounds(File dir) throws IOException {
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                BufferedImage img = ImageIO.read(f);
                String name = getName(f);

                if (!backgrounds.containsKey(name)) {
                    backgrounds.put(name, img);
                }
            }
        }
    }

    private void loadFonts(File dir) throws IOException, FontFormatException {
        for (File f : dir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".ttf")) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, f.toURI().toURL().openStream());
                font = font.deriveFont(Font.PLAIN, 16);
                String name = getName(f);

                if (!fonts.containsKey(name)) {
                    fonts.put(name, font);
                }
            }
        }
    }

    public Font getFont(String name) {
        return fonts.get(name);
    }

    /**
     * Get a filename sans extension
     * eg: hello.wav -> wav
     *
     * @param f The file to get the name of
     * @return A string of the name without extension
     */
    private static String getName(File f) {
        String name = f.getName();
        int index = name.lastIndexOf('.');
        return name.substring(0, index);
    }
}

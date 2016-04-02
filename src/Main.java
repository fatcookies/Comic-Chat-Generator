import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 31/03/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        List<String> lines = new ArrayList<String>() {{
            add("will,in west phillidelphia born and rasied on the playground was where i spent most of my days");
            add("will,chillin out, maxin, relaxin all cool");
            add("phil,and all shootin some b-ball outside of the school");
            add("will,when a couple of guys");
            add("phil,who were up to no good, started makin trouble in my neighbourhood");
            add("carlton,i got into one lil fight and my mom got scared");
            add("carlton,she said you're movin with your auntie and uncle in bel air");
            add("phil,yeah");
            add("will,you see...");
            add("will,thats my theme song");
            add("will,all big willy style");

        }};

        Conversation con = new Conversation(lines);
        Comic c = new Comic(con.toImages(), 4);
        ImageIO.write(c.toImage(), "PNG", new File("combined.png"));
    }

}

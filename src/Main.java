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
            add("nat,in west phillidelphia born and rasied on the playground was where i spent most of my days");
            add("nat,chillin out, maxin, relaxin all cool");
            add("nat,and all shootin some b-ball outside of the school");
            add("nat,when a couple of guys");
            add("nat,who were up to no good, started makin trouble in my neighbourhood");
            add("nat,i got into one lil fight and my mom got scared");
            add("she said you're movin with your auntie and uncle in bel air");

        }};
        Conversation con = new Conversation(lines);
        Comic c = new Comic(con.toImages(), 4);
        ImageIO.write(c.toImage(), "PNG", new File("combined.png"));
    }

}

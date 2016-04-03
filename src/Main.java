import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 31/03/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length > 0) {
            File f = new File(args[0]);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            List<String> lines = new ArrayList<String>();
            while(br.ready()) {
                lines.add(br.readLine());
            }
            Conversation con = new Conversation(lines);
            Comic c = new Comic(con.toImages(), 4);
            ImageIO.write(c.toImage(), "PNG", new File("combined.png"));


        }


    }

}

/**
 * Created by olegartys on 28.01.15.
 */
import java.util.Properties;
import java.io.*;

/**
 * Class that contains configuration attributes for a server.
 * Attributes are getting from a CONFIG_FILE.
 *
 */
public class ServerConfig {

    private static final String CONFIG_FILE = "./server.conf";

    public static int PORT;
    public static String BOT_NAME;

    static {
        Properties properties = new Properties ();
        FileInputStream fileProperties = null;
        try {
            fileProperties = new FileInputStream (CONFIG_FILE);
            properties.load (fileProperties);

            PORT = Integer.parseInt(properties.getProperty("PORT"));
            BOT_NAME = properties.getProperty("BOT_NAME");

        } catch (FileNotFoundException e) {
            System.err.println("Properties file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Can't read PORT property.");
            e.printStackTrace();
        } finally {
            try {
                fileProperties.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
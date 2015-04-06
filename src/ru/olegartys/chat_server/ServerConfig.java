package ru.olegartys.chat_server;
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

    public static String ADDRESS;
    public static int PORT;
    public static String BOT_NAME;
    public static String HELLO_MESSAGE;
    public static String USER_CONNECT_MESSAGE;
    public static String USER_WITH_SUCH_LOGIN_EXISTS;
    public static int DELAY;
    public static String MAIN_ROOM_NAME;
    public static String MAIN_ROOM_PASSWORD;
    public static String RESPONSE_MESSAGE;

    static {
        Properties properties = new Properties ();
        FileInputStream fileProperties = null;
        try {

            fileProperties = new FileInputStream (CONFIG_FILE);
            properties.load (fileProperties);


            ADDRESS = properties.getProperty("ADDRESS");
            PORT = Integer.parseInt(properties.getProperty("PORT"));
            BOT_NAME = properties.getProperty("BOT_NAME");
            HELLO_MESSAGE = properties.getProperty("SERVER_HELLO_MESSAGE");
            USER_CONNECT_MESSAGE = properties.getProperty("USER_CONNECT_MESSAGE");
            USER_WITH_SUCH_LOGIN_EXISTS = properties.getProperty("SERVER_USER_WITH_SUCH_LOGIN_EXISTS");
            DELAY = Integer.parseInt(properties.getProperty("DELAY"));
            MAIN_ROOM_NAME = properties.getProperty("MAIN_ROOM_NAME");
            MAIN_ROOM_PASSWORD = properties.getProperty("MAIN_ROOM_PASSWORD");
            RESPONSE_MESSAGE = properties.getProperty("RESPONSE_MESSAGE");

        } catch (FileNotFoundException e) {
            Server.sendServerErrMessage("Properties file not found!");
            System.exit(1);
            e.printStackTrace();
        } catch (IOException e) {
            Server.sendServerErrMessage("Can't get property.");
            e.printStackTrace();
        } finally {
            try {
                fileProperties.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
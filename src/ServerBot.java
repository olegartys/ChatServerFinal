/**
 * Created by olegartys on 29.01.15.
 */
public class ServerBot {

    private final String BOT_NAME = ServerConfig.BOT_NAME;

    public void sendMessage (String msg) {
        Server.getCharHistory().addMessage();
    }
}

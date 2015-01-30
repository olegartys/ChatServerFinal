/**
 * Created by olegartys on 29.01.15.
 */
public class ServerBot {

    private final String BOT_NAME = ServerConfig.BOT_NAME;

    public ServerBot () {
    }

    public void sendMessage (Message msg)  {
        Server.getCharHistory().addMessage(msg);
    }

    public String getBotName () {return BOT_NAME;}


}

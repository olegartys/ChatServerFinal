package ru.olegartys.chat_server;

/**
 * Created by olegartys on 29.01.15.
 */
import ru.olegartys.chat_message.*;

public class ServerBot {

    private final String BOT_NAME = ServerConfig.BOT_NAME;

    public ServerBot () {
    }

    public void sendMessage (Message msg)  {
        Server.getCharHistory().addMessage(msg);
    }

    public String getBotName () {return BOT_NAME;}


}

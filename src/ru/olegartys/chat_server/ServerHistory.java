package ru.olegartys.chat_server;

import java.util.ArrayList;
import ru.olegartys.chat_message.*;

/**
 * Created by olegartys on 29.01.15.
 */
public class ServerHistory extends ArrayList<Message> {

    public void addMessage (Message msg) {
        this.add (msg);
    }

}

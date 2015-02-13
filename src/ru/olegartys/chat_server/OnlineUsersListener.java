package ru.olegartys.chat_server;

import ru.olegartys.chat_message.ServerUser;

/**
 * Created by olegartys on 09.02.15.
 */
public class OnlineUsersListener extends Thread {

    private ServerUser usr;

    public OnlineUsersListener (ServerUser usr) {
        this.usr = usr;
    }

    @Override
    public void run () {

    }
}

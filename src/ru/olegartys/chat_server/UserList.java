package ru.olegartys.chat_server;

import java.util.ArrayList;
import ru.olegartys.chat_message.*;

/**
 * Created by olegartys on 29.01.15.
 */
public class UserList extends ArrayList<ServerUser> {

    public void addUser (ServerUser usr) {
        this.add(usr);
    }

    public void deleteUser (ServerUser usr) {

        this.remove(usr);
    }

    public ArrayList <ServerUser> getUserList () {
        return this;
    }
}

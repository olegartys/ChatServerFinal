package ru.olegartys.chat_message; /**
 * Created by olegartys on 28.01.15.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.String;import java.lang.System;import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/*public class ru.olegartys.chat_message.Message implements Serializable {

    //private static final long serialVersionUID = -6504719361565270383L;

    //private String login;
    private ru.olegartys.chat_message.ServerUser usr;
    private String msg;
    private Date time;
    private ArrayList<ru.olegartys.chat_message.ServerUser> onlineUsers;

    public ru.olegartys.chat_message.Message (ru.olegartys.chat_message.ServerUser usr, String msg, ArrayList<ru.olegartys.chat_message.ServerUser> onlineUsers) {
        this.usr = usr;
        this.msg = msg;
        this.onlineUsers = onlineUsers;
    }

    public ru.olegartys.chat_message.Message (ru.olegartys.chat_message.ServerUser usr, String msg) {
        this.usr = usr;
        this.msg = msg;
    }

    public ru.olegartys.chat_message.Message (ru.olegartys.chat_message.ServerBot bot, String msg) {
        this.usr = usr;
        this.msg = msg;
    }

    public String getLogin () {return usr.getLogin();}

    public String getMessage () {return msg;}

}*/

public class Message implements Serializable {

    private static final long serialVersionUID = -6504719361565270383L;

    //private String login;
    private ServerUser usr;
    private String msg;
    private Date time;
    private ArrayList<ServerUser> onlineUsers;

    public Message (ServerUser usr, String msg, ArrayList<ServerUser> onlineUsers) {
        this.usr = usr;
        this.msg = msg;
        this.onlineUsers = onlineUsers;
    }

    public Message (ServerUser usr, String msg) {
        this.usr = usr;
        this.msg = msg;
    }

    public String getLogin () {return usr.getLogin();}

    public String getMessage () {return msg;}

}
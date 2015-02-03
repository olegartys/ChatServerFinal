package ru.olegartys.chat_message;

/**
 * Created by olegartys on 28.01.15.
 */
import java.io.*;
import java.io.IOException;import java.io.ObjectInputStream;import java.io.ObjectOutputStream;import java.io.Serializable;import java.lang.String;import java.lang.System;import java.net.Socket;
import java.net.SocketException;

public class ServerUser implements Serializable {

    private Socket sock;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private String login;
    private Integer num;

    private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("login", String.class),
                                                                        new ObjectStreamField("num", Integer.class)};

    public ServerUser (Socket clientSock, int num) {sock = clientSock; this.num = num;}

    public ServerUser (Socket clientSock, ObjectInputStream is, ObjectOutputStream os,
                       String login, int num) {
        this.sock = clientSock;
        this.is = is;
        this.os = os;
        this.login = login;
        this.num = num;
    }

    public ServerUser (String login) {this.login = login;}

    public ServerUser (Socket clientSock, String login) {
        this.sock = clientSock;
        try {
            this.is = new ObjectInputStream(sock.getInputStream());
            this.os = new ObjectOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            System.err.println ("IO error");
        }
        this.login = login;
    }

    public Socket getUserSocket () {return this.sock;}

    public ObjectInputStream getUserInputStream () {return this.is;}

    public ObjectOutputStream getUserOutputStream () {return this.os;}

    public String getLogin () {return this.login;}

    public void setInputStream (ObjectInputStream is) {this.is = is;}

    public void setOutputStream (ObjectOutputStream os) {this.os = os;}

    public void setLogin (String login) {this.login = login;}

    public int getNum () {return this.num;}

}


/*public class ServerUser implements Serializable {

    private String login;
    private int num;
    private static ObjectOutputStream os;

    public ServerUser (int num) {this.num = num;}

    public ServerUser (String login, int num) {
        this.login = login;
        this.num = num;
    }

    public ServerUser (String login) {this.login = login;}

    public String getLogin () {return this.login;}

    public void setLogin (String login) {this.login = login;}

    public void setOutputStream (ObjectOutputStream os) {this.os = os;}

}*/



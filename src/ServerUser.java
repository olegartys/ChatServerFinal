/**
 * Created by olegartys on 28.01.15.
 */
import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class ServerUser {

    private Socket sock;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private String login;
    private int num;

    //public ServerUser (Socket clientSock) {this.sock = clientSock;}
    public ServerUser (Socket clientSock, int num) {sock = clientSock; this.num = num;}

    public ServerUser (Socket clientSock, ObjectInputStream is, ObjectOutputStream os,
            String login, int num) {
        this.sock = clientSock;
        this.is = is;
        this.os = os;
        this.login = login;
        this.num = num;
    }

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

}


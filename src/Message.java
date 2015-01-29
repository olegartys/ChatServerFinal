/**
 * Created by olegartys on 28.01.15.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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

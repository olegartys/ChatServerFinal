import java.util.ArrayList;

/**
 * Created by olegartys on 29.01.15.
 */
public class UserList extends ArrayList<ServerUser>{

    //private ArrayList<ServerUser> onlineUsers;

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

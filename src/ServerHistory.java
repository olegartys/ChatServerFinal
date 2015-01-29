import java.util.ArrayList;

/**
 * Created by olegartys on 29.01.15.
 */
public class ServerHistory {

    private ArrayList<Message> history;

    public void addMessage (Message msg) {
        history.add (msg);
    }

}

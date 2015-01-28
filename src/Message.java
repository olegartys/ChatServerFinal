/**
 * Created by olegartys on 28.01.15.
 */
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = -6504719361565270383L;

    private String login;
    private String msg;
    private Date time;

    public String getLogin () {return login;}

    public String getMessage () {return msg;}

}

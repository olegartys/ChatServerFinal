/**
 * Created by olegartys on 28.01.15.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThread extends Thread {

    private Socket sock;
    private int num;
    private String login;
    private Message msg;
    private ServerUser newUser;

    public ServerThread (int num, Socket socket) {
        //creating new user object
        newUser = new ServerUser (sock, num);

        setDaemon (true);
        setPriority(NORM_PRIORITY);
        this.start();
    }

    @Override
    public void run() {
        try {
            //open streams for user socket
            ObjectInputStream is = new ObjectInputStream (sock.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream (sock.getOutputStream());

            //getting serialized message and information from it
            msg = (Message)is.readObject();

            newUser.setLogin (msg.getLogin());
            newUser.setInputStream (is);
            newUser.setOutputStream (os);

            //TODO : implement user list and decide do i need to carry online users separately from all the users
            //adding new message to char history

            System.out.println ("[" + login + "]: " + msg.getMessage());
            getCharHistory().addMessage(msg);

            getUserList().addUser(newUser);
            this.sendMessage(getUserList().getOnlineUsers(), new Message());
            this.sendMessage(getUserList().getOnlineUsers(), msg);


			/*byte buf[] = new byte[64*1024];

			int r = is.read(buf);

			String data = new String(buf);
			System.out.println ("Client number: " + num);
			System.out.println ("Client data: " + data);
			System.out.println ("**********************");

			String response = data + "\nresponse";
			os.write (response.getBytes());*/

            sock.close();
        } catch (IOException e) {
            System.err.println ("Error openning client socket!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendMessage (ArrayList<ServerUser> userList, Message msg)
    {
        try {
            for (ServerUser usr: userList)
                usr.getUserOutputStream().writeObject(msg);
        } catch (SocketException e) {
            //System.err.println ("Error sending message to user [" + usr.getLogin() + "]");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
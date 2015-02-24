package ru.olegartys.chat_server;

/**
 * Created by olegartys on 28.01.15.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ru.olegartys.chat_message.*;

public class ServerThread extends Thread {

    private ServerUser newUser;
    private boolean isOnline;

    private static final boolean DEBUG = true;

    public ServerThread (int num, Socket clientSock) {

        //init new user object
        newUser = new ServerUser(clientSock, num);
        try {
            newUser.setOutputStream(new ObjectOutputStream(clientSock.getOutputStream()));
            newUser.setInputStream(new ObjectInputStream(clientSock.getInputStream()));
            isOnline = true;
        } catch (IOException e) {
            Server.sendServerErrMessage("Can't get socket IO streams!");
            e.printStackTrace();
        }

        //starting new thread for client
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        this.start();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                //TODO: Решить каким образом будет происходить отслеживание онлайн ли пользователь
                /*
                // listen whether user is online for a DELAY
                Timer mTimer = new Timer();
                IsUserOnlineListener listener = new IsUserOnlineListener(this);
                mTimer.schedule(listener, 1000, 1000);*/

                //getting serialized auth message
                Message authMsg = (Message) newUser.getUserInputStream().readObject();

                //checking whether user with such login already exists
                ArrayList<String> logins = new ArrayList<String>();
                for (ServerUser usr : Server.getUserList()) {
                    logins.add(usr.getLogin());
                }
                while (logins.contains(authMsg.getLogin())) {
                    Server.sendServerErrMessage("User with login " + authMsg.getLogin() + " exists!");
                    //sending message to client about trouble
                    Server.BOT.sendMessage(newUser, "User with such login already exist!");
                    //getting new login
                    authMsg = (Message) newUser.getUserInputStream().readObject();
                }

                //Setting login to user
                newUser.setLogin(authMsg.getLogin());

                // if user send auth message (for some clients, which sending some auth message to establish connection)
                // with server
                if (authMsg.getMessage().equals(ServerConfig.USER_CONNECT_MESSAGE)) {
                    Server.sendServerMessage("[AUTH]:" + newUser.getLogin() +
                            " from " + newUser.getUserSocket().getInetAddress() +
                            " connected to PORT=" + newUser.getUserSocket().getPort());

                    ServerHistory hist = Server.getChatHistory();
                    //sending chat history to a new user if it is not empty
                    if (!hist.isEmpty()) {
                        for (Message msg : hist) {
                            newUser.getUserOutputStream().writeObject(msg);
                        }
                    }
                    // else if it is ordinary message adding his message to history
                } else {
                    System.out.println("[" + newUser.getLogin() + "]: " + authMsg.getMessage());
                    Server.getChatHistory().addMessage(authMsg);
                    this.sendMessage(Server.getUserList(), authMsg);
                }

                //say hello to user :)
                Message ms = new Message(newUser, ServerConfig.HELLO_MESSAGE);
                newUser.getUserOutputStream().writeObject(ms);

                //add new user to online user list
                Server.getUserList().addUser(newUser);

                //Sending BOT message that new user has connected to chat
                Server.BOT.sendMessage(Server.getUserList(), "User with nickname " + newUser.getLogin() + " connected!");

                //start getting messages from user
                while (true) {
                    if (isOnline) {
                        Message msg = (Message) newUser.getUserInputStream().readObject();
                        Server.getChatHistory().addMessage(msg);

                        this.sendMessage(Server.getUserList(), msg);
                        Server.sendServerMessage("[" + newUser.getLogin() + "]: " + msg.getMessage());

                    } else {
                        //mTimer.cancel();
                        this.interrupt();
                    }
                }


                /*byte buf[] = new byte[64*1024];

                int r = is.read(buf);

                String data = new String(buf);
                System.out.println ("Client number: " + num);
                System.out.println ("Client data: " + data);
                System.out.println ("**********************");

                String response = data + "\nresponse";
                os.write (response.getBytes());*/

                //clientSock.close();
            } catch (IOException e) {
                //disconnect user if he is not responding
                onDisconnect();
            } catch (ClassNotFoundException e) {
                Server.sendServerErrMessage("Can't find class! Problems with serialization!");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param userList online users list
     * @param msg msg to be send to these users
     *
     */
    private void sendMessage (ArrayList<ServerUser> userList, Message msg)
    {
        ServerUser usr = null;
        try {
            if (DEBUG) Server.sendServerMessage("[MSG]: " + msg.getMessage() + "\n\t|\n\t|\n\t|");
            //send msg to all from the user list
            for (ServerUser usrIter: userList) {
                usr = usrIter;
                usrIter.getUserOutputStream().writeObject(msg);
                Server.sendServerMessage("Sending message to user: " + usr.getLogin() + " PORT=" + usr.getUserSocket().getPort());
            }
        } catch (SocketException e) {
            //disconnect user if he is not responding
            onDisconnect();
            e.printStackTrace();
        } catch (IOException e) {
            onDisconnect();
            Server.sendServerErrMessage("IO error sending message to user [" +  usr.getLogin() + "]");
            e.printStackTrace();
        }
    }

    /**
     * on disconnect user
     */
    private void onDisconnect() {
        //delete user from list
        Server.getUserList().deleteUser(newUser);
        //send to other users message about this
        Server.BOT.sendMessage(Server.getUserList(), "User with nickname " + newUser.getLogin() + " has been disconnected!");
        //close socket
        Server.sendServerErrMessage("Can't get anything from user socket! PROBABLY he disconnected!");
        try {
            newUser.getUserSocket().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //stopping server thread
        this.interrupt();
    }

    /**
     * Sending ping messages with DELAY to check whether user is online

    class IsUserOnlineListener extends TimerTask {

        private Thread serverThread;

        public IsUserOnlineListener(Thread serverThread) {
            this.serverThread = serverThread;
        }

        @Override
        public void run() {
            System.out.println(newUser.getUserSocket().isConnected());
            isOnline = newUser.getUserSocket().isConnected() ? true : false;
            if (!isOnline) {
                Server.BOT.sendMessage(Server.getUserList(), "User with nickname " + newUser.getLogin() + " has been disconnected!");
                Server.getUserList().deleteUser(newUser);

                serverThread.interrupt();
                this.cancel();
            }
        }
    }*/

}

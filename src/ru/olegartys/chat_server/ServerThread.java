package ru.olegartys.chat_server;

/**
 * Created by olegartys on 28.01.15.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import ru.olegartys.chat_message.*;

public class ServerThread extends Thread {

    private ServerUser newUser;
    public boolean isOnline;

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
        try {
            //getting serialized auth message
            Message authMsg = (Message)newUser.getUserInputStream().readObject();

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
                authMsg = (Message)newUser.getUserInputStream().readObject();
            }

            //Setting login to user
            newUser.setLogin(authMsg.getLogin());

            // if user send auth message (for some clients, which sending some auth message to establish coonnection
            // with server
            if (authMsg.getMessage().equals(ServerConfig.USER_CONNECT_MESSAGE)) {
                Server.sendServerMessage ("[AUTH]:" + newUser.getLogin() + " connected to PORT=" + newUser.getUserSocket().getPort());

                ServerHistory hist = Server.getChatHistory();
                //sending chat history to a new user if it is not empty
                if (!hist.isEmpty()) {
                    for (Message msg : hist) {
                        newUser.getUserOutputStream().writeObject(msg);
                    }
                }
                // else if it is ordinary message adding his message to history
            } else {
                System.out.println ("[" + newUser.getLogin() + "]: " + authMsg.getMessage());
                Server.getChatHistory().addMessage(authMsg);
                this.sendMessage(Server.getUserList(), authMsg);
            }

            //Say hello to user
            Message ms = new Message (newUser, ServerConfig.HELLO_MESSAGE);
            newUser.getUserOutputStream().writeObject(ms);

            //add new user to online user list
            Server.getUserList().addUser(newUser);

            //Sending BOT message that new user has connected to chat
            Server.BOT.sendMessage(Server.getUserList(), "User with nickname " + newUser.getLogin() + " connected!");

            //listen whether user is online
            OnlineUsersListener listener = new OnlineUsersListener(newUser);
            listener.start();

            if (isOnline) {
                //start getting messages from user
                while (true) {
                    Message msg = (Message) newUser.getUserInputStream().readObject();
                    Server.getChatHistory().addMessage(msg);

                    this.sendMessage(Server.getUserList(), msg);
                    Server.sendServerMessage("[" + newUser.getLogin() + "]: " + msg.getMessage());
                }
            }

            //TODO : implement BOT that will send server messages to users
            //sending to all users message that new user has connected
            // this.sendMessage(ru.olegartys.chat_server.Server.getUserList(), new ru.olegartys.chat_message.Message(Server.BOT, "["+Server.BOT.getBotName()+"]: " +
              //      newUser.getLogin() + "connected to Chat!"));


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
            Server.sendServerErrMessage("Error interracting with  client socket!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Server.sendServerErrMessage("Can't find class!");
            e.printStackTrace();
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
            for (ServerUser usrIter: userList) {
                usr = usrIter;
                usrIter.getUserOutputStream().writeObject(msg);
                Server.sendServerMessage("Sending message to user: " + usr.getLogin() + " PORT=" + usr.getUserSocket().getPort());
            }
        } catch (SocketException e) {
            Server.sendServerErrMessage ("Connection error sending message to user [" +  usr.getLogin() + "] : ");
            Server.getUserList().deleteUser(usr);
            Server.BOT.sendMessage(Server.getUserList(), usr.getLogin() + " disconnected!");
            e.printStackTrace();
        } catch (IOException e) {
            Server.sendServerErrMessage("IO error sending message to user [" +  usr.getLogin() + "]");
            e.printStackTrace();
        }
    }

}
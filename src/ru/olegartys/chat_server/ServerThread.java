package ru.olegartys.chat_server; /**
 * Created by olegartys on 28.01.15.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import ru.olegartys.chat_message.*;

public class ServerThread extends Thread {

    private ServerUser newUser;
    private Socket clientSock;
    private int num;

    public ServerThread (int num, Socket clientSock) {
        //creating new user object
        this.clientSock = clientSock;
        this.num = num;

        newUser = new ServerUser(clientSock, num);
        try {
            newUser.setOutputStream(new ObjectOutputStream(this.clientSock.getOutputStream()));
            newUser.setInputStream(new ObjectInputStream(this.clientSock.getInputStream()));
            System.out.println("PORT: " + clientSock.getPort());
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
            //newUser = new ServerUser (this.clientSock, this.num);
            //newUser.setInputStream(new ObjectInputStream(clientSock.getInputStream()) );

            //getting serialized auth message
            Message authMsg = (Message)newUser.getUserInputStream().readObject();

            //Setting login to user
            newUser.setLogin(authMsg.getLogin());
            System.out.println (newUser.getLogin());

            //if user send auth message (for some clients, which sending some auth message to establish coonnection
            // with server
            if (authMsg.getMessage().equals(ServerConfig.USER_CONNECT_MESSAGE)) {
                Server.sendServerMessage("[AUTH]: " + newUser.getLogin() + " connected!");
                ServerHistory hist = Server.getCharHistory();
                //sending chat history to a new user if it is not empty
                if (!hist.isEmpty()) {
                    for (Message msg : hist) {
                        newUser.getUserOutputStream().writeObject(msg);
                    }
                }
                // else if it is ordinary message adding his message to history
            } else {
                System.out.println ("[" + newUser.getLogin() + "]: " + authMsg.getMessage());
                Server.getCharHistory().addMessage(authMsg);
                this.sendMessage(Server.getUserList(), authMsg);
            }

            //Say hello to user
            Message ms = new Message (newUser, ServerConfig.HELLO_MESSAGE);
            newUser.getUserOutputStream().writeObject(ms);

            //add new user to online user list
            Server.getUserList().addUser(newUser);

            //start getting messages from user

            while (true) {
                Message msg = (Message)newUser.getUserInputStream().readObject();
                Server.getCharHistory().addMessage(msg);

                this.sendMessage(Server.getUserList(), msg);
                System.out.println("[" + newUser.getLogin() + "]: " + msg.getMessage());
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

    private void sendMessage (ArrayList<ServerUser> userList, Message msg)
    {
        try {
            for (ServerUser usr: userList) {
                //Message msg1 = new Message(newUser, "I AM MSG THAT MUST BE SEND TO ALL THE USERS IF SOMEBODY CHATTED STH!");
                Server.sendServerMessage("Sending message to user " + usr.getLogin() + " PORT: " + usr.getUserSocket().getPort() + ": " + newUser.getNum());
                usr.getUserOutputStream().writeObject(msg);
                //usr.getUserOutputStream().flush();
                //System.out.println(usr.getLogin());
            }
        } catch (SocketException e) {
            Server.sendServerErrMessage ("Connection error sending message to user [" +  newUser.getLogin() + "] : ");
            e.printStackTrace();
        } catch (IOException e) {
            Server.sendServerErrMessage("IO error sending message to user [" +  newUser.getLogin() + "]");
            e.printStackTrace();
        }
    }

}
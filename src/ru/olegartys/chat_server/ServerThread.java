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
    private ChatRoom chatRoom;

    private static final boolean DEBUG = true;

    public ServerThread (int num, Socket clientSock) {

        //init new user object
        newUser = new ServerUser(clientSock, num);
        try {
            newUser.setOutputStream(new ObjectOutputStream(clientSock.getOutputStream()));
            newUser.setInputStream(new ObjectInputStream(clientSock.getInputStream()));
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
        //need to stop thread on users disconnect
        while (!this.isInterrupted()) {
            try {

                boolean successConnect = false;
                while (!successConnect && !isInterrupted()) {
                    //getting serialized auth message
                    Message authMsg = (Message) newUser.getUserInputStream().readObject();

                    //Parse message
                    int resultCode = parseAuthMessage(authMsg);

                    switch (resultCode) {
                        //If auth message is correct
                        case Message.ReturnCode.SUCCESS:

                            //Send response message that everything is alright
                            sendResponseMessage(authMsg.getMessageType(), Message.ReturnCode.SUCCESS);

                            //Send history to user
                            chatRoom.sendHistory(newUser);

                            //Add user into room
                            newUser.setLogin(authMsg.getLogin());
                            chatRoom.addUser(newUser);

                            //Leave connect loop
                            successConnect = true;

                            // Send logs
                            Server.sendServerMessage("[AUTH]:" + newUser.getLogin() +
                                    " from " + newUser.getUserSocket().getInetAddress() +
                                    " connected to PORT=" + newUser.getUserSocket().getPort());
                            break;

                        case Message.ReturnCode.LOGIN_IS_BUSY:
                            sendResponseMessage(authMsg.getMessageType(), Message.ReturnCode.LOGIN_IS_BUSY);
                            successConnect = false;
                            break;

                        case Message.ReturnCode.ROOM_NAME_IS_BUSY:
                            sendResponseMessage(authMsg.getMessageType(), Message.ReturnCode.ROOM_NAME_IS_BUSY);
                            successConnect = false;
                            break;

                        case Message.ReturnCode.ROOM_DOESNT_EXIST:
                            sendResponseMessage(authMsg.getMessageType(), Message.ReturnCode.ROOM_DOESNT_EXIST);
                            successConnect = false;
                            break;

                        case Message.ReturnCode.UNKNOWN_ERROR:
                            sendResponseMessage(authMsg.getMessageType(), Message.ReturnCode.UNKNOWN_ERROR);

                            break;
                    }
                }

                //start getting messages from user
                while (true) {
                    if (!isInterrupted()) {
                        //Creating message
                        Message msg = (Message) newUser.getUserInputStream().readObject();

                        //Adding it to the history
                        chatRoom.getRoomHistory().addMessage(msg);

                        //Seinding message to all the room
                        chatRoom.sendMessage(newUser, msg);

                        Server.sendServerMessage("[" + newUser.getLogin() + "]: " + msg.getMessage());
                    } else {
                        //disconnect user
                        onDisconnect();
                        break;
                    }
                }
    /*
                //checking whether user with such login already exists
                ArrayList<String> logins = new ArrayList<String>();
                for (ServerUser usr: Server.getUserList()) {
                    logins.add(usr.getLogin());
                }
                while (logins.contains(authMsg.getLogin())) {
                    Server.sendServerErrMessage("User with login " + authMsg.getLogin() + " exists!");
                    //sending message to client about trouble
                    Server.BOT.sendMessage(newUser, ServerConfig.USER_WITH_SUCH_LOGIN_EXISTS);
                    newUser.getUserSocket().close();
                    //FIXME: как остановить поток?
                    this.stop();
                    //getting new login
                    //authMsg = (Message) newUser.getUserInputStream().readObject();
                }

                //Setting login to user
                newUser.setLogin(authMsg.getLogin());

                //say hello to user :)
                Message ms = new Message(newUser, ServerConfig.HELLO_MESSAGE);
                newUser.getUserOutputStream().writeObject(ms);

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

                //add new user to online user list
                Server.getUserList().addUser(newUser);

                //Sending BOT message to all users that new user has connected to chat
                Server.BOT.sendMessage(Server.getUserList(), "User with nickname " + newUser.getLogin() + " connected!");

                //start getting messages from user
                while (true) {
                    if (!isInterrupted()) {
                        Message msg = (Message) newUser.getUserInputStream().readObject();
                        Server.getChatHistory().addMessage(msg);

                        this.sendMessage(Server.getUserList(), msg);
                        Server.sendServerMessage("[" + newUser.getLogin() + "]: " + msg.getMessage());

                    } else {
                        //disconnect user
                        onDisconnect();
                        break;
                    }
                }*/

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
     * Parse auth message to decide what user want to do (See types of messages in Message.MessageType).
     * @param msg auth message to parse.
     * @return result code of parse (See types of codes in Message.ReturnCode).
     */
    private int parseAuthMessage(final Message msg) {

        //List of room names
        ArrayList<String> roomNames = new ArrayList<String>();
        for (ChatRoom roomIter: Server.getRoomList())
            roomNames.add(roomIter.getRoomName());

        //Result of parse code
        int resultCode;

        switch (msg.getMessageType()) {
            //if user wants to connect to existing room
            case Message.MessageType.CONNECT_TO_ROOM_MSG:

                final String usrLogin = msg.getLogin();
                String roomName = msg.getRoomInfo().getRoomName();
                if (roomName.isEmpty())
                    roomName = ServerConfig.MAIN_ROOM_NAME;
                chatRoom = Server.getRoomByName(roomName);

                //if room name is free
                if (!roomNames.contains(roomName))
                    resultCode = Message.ReturnCode.ROOM_DOESNT_EXIST;
                else {
                    //List of logins in current room
                    ArrayList<String> logins = new ArrayList<String>();
                    for (ServerUser usr: chatRoom.getUserList()) {
                        logins.add(usr.getLogin());
                    }

                    //if password is correct
                    if (!msg.getRoomInfo().getRoomPassword().equals(chatRoom.getRoomPassword())) {
                        resultCode = Message.ReturnCode.INCORRECT_PASSWORD;
                    } else {
                        //if login is free
                        if (chatRoom.getUserList().contains(usrLogin))
                            resultCode = Message.ReturnCode.LOGIN_IS_BUSY;
                        else {
                            resultCode = Message.ReturnCode.SUCCESS;
                        }
                    }
                }
                break;

            //If user want to connect to create new room
            case Message.MessageType.CREATE_ROOM_MSG:

                if (roomNames.contains(msg.getRoomInfo().getRoomName()))
                    resultCode = Message.ReturnCode.ROOM_NAME_IS_BUSY;
                else {
                    chatRoom = new ChatRoom(msg.getRoomInfo().getRoomName(),
                            msg.getRoomInfo().getRoomPassword(), newUser);
                    Server.getRoomList().add(chatRoom);
                    System.out.println();

                    resultCode = Message.ReturnCode.SUCCESS;
                }
                break;

            default:
                /* Code Here */
                resultCode = Message.ReturnCode.UNKNOWN_ERROR;
                break;
        }

        return resultCode;
    }

    /**
     * Sends response message after parsing auth user message.
     * @param msgType type of the message identical to type of user auth message.
     * @param resultCode result of parsing user auth message (Message.ReturnCode).
     * @throws IOException if sth happened with user socket and connection interrupted. In run method this exception
     * catches.
     */
    private void sendResponseMessage(int msgType, int resultCode) throws IOException {
        Message response = new Message(newUser, ServerConfig.RESPONSE_MESSAGE,
                msgType, resultCode);
        newUser.getUserOutputStream().writeObject(response);
    }

    /**
     * on disconnect user and  stop the thread
     */
    private void onDisconnect() {
        //delete user from list
        chatRoom.getUserList().deleteUser(newUser);
        //send to other users message about this
        chatRoom.sendBotMessage("User with nickname " + newUser.getLogin() + " has been disconnected!");
        //close socket
        Server.sendServerErrMessage("Can't get anything from user socket (" + newUser.getLogin() + ")! PROBABLY he was disconnected!");
        try {
            newUser.getUserSocket().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //stopping server thread
        this.interrupt();

        this.stop();
    }
}

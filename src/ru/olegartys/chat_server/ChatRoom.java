package ru.olegartys.chat_server;

import ru.olegartys.chat_message.Message;
import ru.olegartys.chat_message.ServerBot;
import ru.olegartys.chat_message.ServerUser;

import java.io.IOException;

/**
 * Created by olegartys on 31.03.15.
 */
public class ChatRoom {

    private String roomName;
    private String roomPassword;
    private ServerUser roomCreator;

    private UserList roomUsers;
    private ServerHistory roomHistory;

    private ServerBot roomBot;

    private boolean isPrivateRoom;

    public ChatRoom(String roomName, String roomPassword, ServerUser roomCreator) {
        roomBot = new ServerBot(roomName);
        roomUsers = new UserList();
        roomHistory = new ServerHistory();

        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.roomCreator = roomCreator;

        if (roomPassword.isEmpty())
            isPrivateRoom = true;
        else
            isPrivateRoom = false;
    }

    public void sendMessage(ServerUser newUser, Message msg) throws IOException {
        ServerUser usr = null;
        //if (DEBUG) Server.sendServerMessage("[MSG]: " + msg.getMessage() + "\n\t|\n\t|\n\t|");
        //send msg to all from the user list
        for (ServerUser usrIter: roomUsers) {
            usr = usrIter;
            if (usr == newUser) continue;
            usrIter.getUserOutputStream().writeObject(msg);
            Server.sendServerMessage("Sending message to user: " + usr.getLogin() + "room=" + roomName +
                    " PORT=" + usr.getUserSocket().getPort());
        }
    }

    public void sendHistory(ServerUser user) throws IOException {
        if (!roomHistory.isEmpty())
            for (Message msg: roomHistory)
                user.getUserOutputStream().writeObject(msg);
    }

    public void sendBotMessage(String msg) {
        roomBot.sendMessage(roomUsers, msg);
    }

    public void addUser(ServerUser usr) {
        roomUsers.addUser(usr);
        sendBotMessage("User with nickname " + usr.getLogin() + " connected!");
    }

    public void deleteUser(ServerUser usr) {
        roomUsers.deleteUser(usr);
    }

    public boolean isEmpty() {
        return roomUsers.isEmpty();
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public synchronized UserList getUserList() {
        return roomUsers;
    }

    public synchronized ServerHistory getRoomHistory() {
        return roomHistory;
    }

    public void setRoomCreator(ServerUser roomCreator) {
        this.roomCreator = roomCreator;
    }

    public ServerUser getRoomCreator() {
        return roomCreator;
    }
}

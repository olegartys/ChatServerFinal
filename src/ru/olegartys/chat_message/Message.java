package ru.olegartys.chat_message;
/**
 * Created by olegartys on 28.01.15.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.String;import java.lang.System;import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = -6504719361565270383L;

    public static class MessageType {
        public static final int CREATE_ROOM_MSG = 0;
        public static final int CONNECT_TO_ROOM_MSG = 1;
        public static final int ORDINARY_MESSAGE = 2;
    }

    public static class ReturnCode {
        public static final int SUCCESS = 0;
        public static final int LOGIN_IS_BUSY = 1;
        public static final int ROOM_NAME_IS_BUSY = 2;
        public static final int UNKNOWN_ERROR = 3;
        public static final int INCORRECT_ROOM_NAME = 4;
        public static final int ROOM_DOESNT_EXIST = 5;
        public static final int INCORRECT_PASSWORD = 6;
    }

    private int msgType;
    private int returnCode;
    private RoomInfo roomInfo;

    private ServerUser usr;
    private String login;
    private String msg;
    private Date time;

    public Message(ServerUser usr, String msg, int msgType, int returnCode) {
        time = new Date();
        this.usr = usr;
        this.msg = msg;
        this.msgType = msgType;
        this.returnCode = returnCode;
        this.login = usr.getLogin();
        this.roomInfo = new RoomInfo();
    }

    public Message(ServerUser usr, String msg, int msgType) {
        time = new Date();
        this.usr = usr;
        this.msg = msg;
        this.login = usr.getLogin();
        this.msgType = msgType;
        this.roomInfo = new RoomInfo();

    }

    public Message (ServerUser usr, String msg) {
        time = new Date();
        this.usr = usr;
        this.login = usr.getLogin();
        this.msg = msg;
        this.roomInfo = new RoomInfo();
    }

    public Message (String login, String msg) {
        time = new Date();
        this.login = login;
        this.msg = msg;
        this.roomInfo = new RoomInfo();
    }

    public int getMessageType() {
        return msgType;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public String getLogin () {
        return login;
    }

    public String getMessage () {
        return msg;
    }

    public Date getTime() {
        return time;
    }

    public class RoomInfo implements Serializable {

        private String roomName;
        private String roomPassword;

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

    }
}
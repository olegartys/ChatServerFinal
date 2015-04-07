package ru.olegartys.chat_server;
/**
 * Created by olegartys on 28.01.15.
 */
import ru.olegartys.chat_message.ServerBot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server {

    private static ServerSocket serverSocket = null;
    private static ServerHistory history = new ServerHistory();
    private static UserList users = new UserList();

    public static ServerBot BOT = new ServerBot();

    public static void main(String args[]) {
        try {
            int i = 0;

            //opens server socket on address from config file
            /*serverSocket = new ServerSocket (ServerConfig.PORT, 0,
                    InetAddress.getByName(ServerConfig.ADDRESS));*/
            serverSocket = new ServerSocket(ServerConfig.PORT);

            System.out.println(serverSocket.toString());

            sendServerMessage("Server is started on " + ServerConfig.ADDRESS + "!");

            //listening port for new clients
            while(true) {
                Socket clientSock = null;
                while (clientSock == null)
                    clientSock = serverSocket.accept();

                //creating new thread for a new client
                new ServerThread(i, clientSock);
                i++;
            }
        } catch (SocketException e) {
            sendServerErrMessage("Open server socket exception");
            e.printStackTrace();
        } catch (IOException e) {
            sendServerErrMessage("Reading client socket exception");
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param msg
     * Sends @param to stdout of the server
     */
    public static void sendServerMessage(String msg) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("[SERVER_MSG::" + sdf.format(cal.getTime()) +  "]: " + msg);
    }

    /**
     *
     * @param msg
     * Sends @param to stderr of the server
     */
    public static void sendServerErrMessage(String msg) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.err.println("[ERR][SERVER_MSG::" + sdf.format(cal.getTime()) +  "]: " + msg);
    }

    public static synchronized ServerHistory getChatHistory() {
        return history;
    }

    public static synchronized UserList getUserList() {
        return users;
    }

}


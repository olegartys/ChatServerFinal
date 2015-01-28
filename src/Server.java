/**
 * Created by olegartys on 28.01.15.
 */
import java.io.IOException;
import java.net.*;

public class Server {

    private static ServerSocket serverSocket = null;

    public static void main(String args[]) {
        try {
            int i = 0;

            //opens server socket
            serverSocket = new ServerSocket (ServerConfig.PORT, 0,
                    InetAddress.getByName("localhost"));
            System.out.println("Server is started!");

            //listening port for new clients
            while(true) {
                Socket clientSock = null;
                while (clientSock == null)
                    clientSock = serverSocket.accept();
                //creating new thread for a new client

                new ServerThread (i, clientSock);
                i++;
            }
        } catch (SocketException e) {
            System.err.println ("Socket exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println ("IO exception");
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


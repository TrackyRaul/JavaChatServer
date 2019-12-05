import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        final int port = 8888;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port.");
        ArrayList<Session> sessionArrayList = new ArrayList<Session>();

        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("New client connected to the server. \n" + socket.toString());

            Session newSession = new Session(socket, sessionArrayList);
            Thread t1 = new Thread(newSession);
            t1.start();

        }


    }
}

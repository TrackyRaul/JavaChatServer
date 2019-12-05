import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Session implements Runnable {
    private Socket currentSocket;
    private ArrayList<Session> otherSessions;
    private User user;
    private String dest;

    public Session(Socket currentSocket, ArrayList<Session> otherSessions) {
        this.currentSocket = currentSocket;
        this.otherSessions = otherSessions;
        this.user = new User(currentSocket.getInetAddress().toString());
    }

    @Override
    public void run() {
        InputStreamReader inString;
        try {
            // Variables that allow clients to interract with the server
            inString = new InputStreamReader(this.currentSocket.getInputStream());
            BufferedReader inBuffer = new BufferedReader(inString);
            OutputStreamWriter stringaOut;
            BufferedWriter buffer;
            PrintWriter out;

            // Manage recieved string
            while (true) {
                // Get string from buffer
                String inStr = inBuffer.readLine();
                System.out.println(inStr);

                // Split to check message type
                String[] typeInSplitStr = inStr.split(":");
                String type = "";

                // Define messge type
                if (typeInSplitStr.length >= 1) {
                    type = typeInSplitStr[0];
                } else {
                    // Skip if type not defined
                    continue;
                }

                // Head informations
                if (type.equals("Head")) {
                    // Description structure
                    // username, password(not implemented)
                    String[] inHeadDescription = inStr.substring(5).split(",");

                    if (inHeadDescription.length >= 0) {
                        this.user.setUsername(inHeadDescription[0].trim());
                        System.out.println(this.user.getUsername());
                    }
                } else if (type.equals("Command")) {
                    // Commands to be executed by the server
                    String inCommand = inStr.substring(8).trim();
                    if (inCommand.equals("quit")) {
                        this.closeMyself();
                        break;
                    }
                } else if (type.equals("Message")) {
                    // Send messages
                    String inMessage = inStr.substring(8).trim();
                    System.out.println(inMessage);
                }
            }

            // chiusura di stream e socket
            System.out.println("EchoServer: chiudo...");
            inBuffer.close();
            this.currentSocket.close();
        } catch (IOException ex) {
            System.out.println(ex);
            // chiusura di stream e socket
            System.out.println("EchoServer: chiudo...");
        }

    }

    synchronized public void closeMyself() {
        for (int i = 0; i < this.otherSessions.size(); i++) {
            if (this.otherSessions.get(i).user.getIp().equals(this.user.getIp())) {
                this.otherSessions.remove(this);
            }
        }
    }
}

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

                if (inStr.contains(":")){
                    // Split to check message type
                    String[] typeInSplitStr = inStr.split(":");
                    String type = "";

                    // Define messge type
                    if (typeInSplitStr.length >= 2){
                        type = typeInSplitStr[0];
                    }
                    else {
                        // Skip if type not defined
                        continue;
                    }

                    // Head informations
                    if (type.equals("Head")) {
                        // Description structure
                        // username, password(not implemented)
                        System.out.println("Head message");
                        String[] inHeadDescription = inStr.substring(5).split(",");

                        if (inHeadDescription.length >= 0) {
                            this.user.setUsername(inHeadDescription[0].trim());
                            System.out.println(this.user.getUsername());
                        }
                        else {
                            continue;
                        }
                    }
                    else if (type.equals("Command")) {
                        // Commands to be executed by the server
                        String inCommand = inStr.substring(8).trim();
                        //System.out.println("Command message " + inCommand);

                        if (inCommand.split(" ").length >= 1) {
                            String commandType = inCommand.split(" ")[0];
                            if (commandType.equals("/quit")) {
                                closeMyself();
                                break;
                            }else if (commandType.equals("/dest")){
                                // Command: /dest type,name
                                String param = inCommand.substring(6).trim();

                                String[] paramLs = param.split(",");
                                if(paramLs.length == 2){
                                    if(paramLs[0].trim().equals("user")){
                                        this.dest = paramLs[1].trim();
                                        System.out.println(this.dest);
                                    }
                                }else{
                                    System.out.println("Error, command dest not used correctly");
                                    continue;
                                }
                            }
                        }
                        else if (type.equals("Message")) {
                            // Send messages

                            String inMessage = inStr.substring(8).trim();
                            for (Session s: this.otherSessions){
                                System.out.println(s.user.getUsername());
                                if(s.user.getUsername().equals(this.dest)){
                                    stringaOut = new OutputStreamWriter(s.currentSocket.getOutputStream());
                                    buffer = new BufferedWriter(stringaOut);
                                    out = new PrintWriter(buffer, true);
                                    // Message structure for users: "User:{Name}:{Message}"
                                    out.println("User:" + s.user.getUsername() + ":" + inMessage);
                                }
                            }
                        }
                    }
                }
            }

            // chiusura di stream e socket
            inBuffer.close();
            this.currentSocket.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    synchronized public void closeMyself() {
        for (int i = 0; i < this.otherSessions.size(); i++) {
            if (this.otherSessions.get(i).user.getIp().equals(this.user.getIp())) {
                this.otherSessions.remove(this);
                System.out.println("Disconnected!");
            }
        }
    }
}

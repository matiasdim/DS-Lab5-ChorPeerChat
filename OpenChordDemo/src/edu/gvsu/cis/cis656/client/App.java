package edu.gvsu.cis.cis656.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays;

public class App
{
    public static void main( String[] args )
    {
        if(args.length != 4) {
            System.out.println("usage:\n\tjava ChordClient -master|-notmaster bootstrap-host username port");
            return;
        }
        boolean theMaster = args[0].intern() == "-master" ? true: false;
        PresenceService client = new ConnectionHandler(args[1], theMaster);

        String host = "localhost";
        int port = 1099;
        // Checks existance of optional args => host and port
        if (args.length >= 2 && args[1] != null) {
            host = args[1];
        }
        if (args.length >= 3 && args[3] != null){
            port = Integer.parseInt(args[3]);
        }

        /*
         * Registriation section
         */
        String username = args[2]; // Save username coming from start process
        // Sockets!
        // -------- To listen "socket"
        ServerSocket serverSocket = null;
        Socket socket = null; // This one is to receive messages
        String userHost = "localhost"; // default user host
        int userPort = 9999; // default user port
        try {
            //Getting the local IP
            InetAddress ip = InetAddress.getLocalHost();
            userHost = ip.getHostAddress(); // update to host to local IP
            serverSocket = new ServerSocket(0);
            userPort = serverSocket.getLocalPort(); // Update port to serversocket port
            // Start listening for messages
            Thread thread = new Thread(new TextListener(serverSocket, username));
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("User host: " + userHost);
        System.out.println("User port: " + userPort);
        // Startup client (registration)
        RegistrationInfo reg = new RegistrationInfo(username, userHost, userPort, true);
        App.startup(client, reg);

        // STARTED!
        System.out.println("Welcome " + username + ".");
        System.out.print((char)27 + "[38;5;118m"+ username + " ➜ " + (char)27 + "[0m");
        while (true) {
            // Read inputs to Run chat
            Scanner reader = new Scanner(System.in);
            String clInput = reader.nextLine();
            String[] inputParts = clInput.split("\\s");
            // Verify first word of the input to know what to do...
            switch (inputParts[0]) {
                case "exit":
                    System.out.println("Exiting...");
                    App.removeUser(client, reg);
                    System.exit(0);
                    break;
                case "available":
                    if (reg.getStatus() == true) {
                        System.out.println("You are already available.");
                    } else {
                        App.updateUserStatus(client, reg, true);

                    }
                    System.out.print((char)27 + "[38;5;118m"+ username + " ➜ " + (char)27 + "[0m");
                    break;
                case "busy":
                    if (reg.getStatus() == false) {
                        System.out.println("You are already not available.");
                    } else {
                        App.updateUserStatus(client, reg, false);

                    }
                    System.out.print((char)27 + "[38;5;118m"+ username + " ➜ " + (char)27 + "[0m");
                    break;
                case "talk":
                    if (inputParts.length < 3) {
                        System.out.println("Command to send message seems to be malformed. Try again.");
                    } else {
                        RegistrationInfo user = App.lookForAUser(client, inputParts[1]);
                        if (user == null) {
                            System.out.println("User does not exists.");
                        } else {
                            if (user.getUserName().equals(reg.getUserName()) || !user.getStatus()) {
                                System.out.println("User is not available");
                            } else {
                                String message = App.joinString(2, inputParts); // Removing first two words from input to only get message
                                message = reg.getUserName() + " says ➜ " + message;
                                App.sendMessage(user.getHost(), user.getPort(), message);
                            }
                        }
                    }
                    System.out.print((char)27 + "[38;5;118m"+ username + " ➜ " + (char)27 + "[0m");
                    break;
                default:
                    System.out.println((char) 27 + "[38;5;88mCommand not detected. Try again!" + (char) 27 + "[0m");
                    System.out.print((char) 27 + "[38;5;118m" + username + " ➜ " + (char) 27 + "[0m");
                    break;
            }
        }
    }

    // Auxiliar function to format message before send it.
    public static String joinString(int index, String[] array){
        String text = "";
        for (int i = index; i < array.length; i++){
            text += array[i] + " ";
        }
        return text;
    }

    /*
     * Socket
     * Snippet to send message to a user via socket.
     */
    public static void sendMessage(String userHost, int userPort, String message){
        try{
            // -------- To talk "clientSocket"
            Socket clientSocket = new Socket(userHost, userPort);
            PrintStream os;
            os = new PrintStream(clientSocket.getOutputStream());
            os.println(message);
            clientSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    * Functions to comunicate with Presence Service using RMI
    */
    // User registration upon startup
    public static void startup(PresenceService service, RegistrationInfo reg){
        boolean isRegistered = false;
        try {
            service.register(reg);
        } catch (Exception e) {
            System.err.println("RegistrationInfo exception:");
            e.printStackTrace();
        }
    }
    // TO remove a particular user
    public static void removeUser(PresenceService service, RegistrationInfo reg){
        try {
            service.unregister(reg.getUserName());
        } catch (Exception e) {
            System.err.println("unregisterUsers exception:");
            e.printStackTrace();
        }
    }
    // Update status
    public static void updateUserStatus(PresenceService service, RegistrationInfo reg, Boolean newStatus){
        try {
            service.setStatus(reg, newStatus);
        } catch (Exception e) {
            System.err.println("updateRegistrationInfo exception:");
            e.printStackTrace();
        }
    }
    // List all users connected
    public static Vector<RegistrationInfo> listFriends(PresenceService service){
        Vector<RegistrationInfo> friends = null;
        try {
            RegistrationInfo[] users = service.listRegisteredUsers();
            friends = new Vector<>(Arrays.asList(users));
        } catch (Exception e) {
            System.err.println("listRegisteredUsers exception:");
            e.printStackTrace();
        }

        return friends;
    }
    // Looking for a particular user
    public static RegistrationInfo lookForAUser(PresenceService service, String username){
        RegistrationInfo reg = null;
        try {
            reg = service.lookup(username);
        } catch (Exception e) {
            System.err.println("lookup exception:");
            e.printStackTrace();
        }
        return reg;
    }
}

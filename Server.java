import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ArrayList<User> clients = new ArrayList<User>();

    public static void main(String[] args) {
        Server server = new Server();
        server.go();
    }

    private void go() {
        ServerSocket serverSocket = null;

        /*
         * Create the server socket and set a timeout so that it will not wait forever
         * for a client to connect when we call accept ().
         */

        try {
            serverSocket = new ServerSocket(4444);
            serverSocket.setSoTimeout(50);
            System.out.println("Listening on port 4444");
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        do {
            /*
             * Give new clients a chance to connect.
             */

            Socket client = null;

            try {
                client = serverSocket.accept();
            } catch (SocketTimeoutException e) {
                /* do nothing, this just means no client tried to connect */
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            /*
             * If we got a new client, add them to the vector
             */

            if (client != null) {
                String username = receiveUsername(client);
                if (username != null) {
                    User user = new User(username, client);
					System.out.println(user.getSocket());
                    clients.add(user);
                    sendMenu(user);
                }
            }

            /*
             * Then give all older clients a chance to do something. Different strategies
             * exist for the most efficient ways of doing this (for example, checking for
             * new clients before processing each old client, rather than before the entire
             * vector).
             */

            int i = 0;
            while (i < clients.size()) {
                if (processClientInput(clients.get(i)))
                    i++;
            }

        } while (true);

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveUsername(Socket client) {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String username = inFromClient.readLine();
            return username;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean processClientInput(User user) {
        /*
         * Returns true if the client connection is still okay
         */

        BufferedReader inFromClient = null;
        PrintWriter outToClient = null;
        String input = null;

        try {
            inFromClient = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
            outToClient = new PrintWriter(user.getSocket().getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            clients.remove(user);
            return false;
        }

        try {
            if (inFromClient.ready()) {
                input = inFromClient.readLine();
                outToClient.println(input.toUpperCase());
            }
        } catch (Exception e) {
            /*
             * If we get an exception when communicating with a client, we should drop the
             * client from the vector of clients, so we don't waste time processing the same
             * client later.
             * 
             */

            clients.remove(user);
            e.printStackTrace();
            return false;
        }

    return true;
  }

private void sendMenu(User user) {
    try {
        PrintWriter outToClient = new PrintWriter(user.getSocket().getOutputStream(), true);
        String menu = "Menu:\n" +
                      "1. Item 1\n" +
                      "2. Item 2\n" +
                      "3. Item 3\n" +
                      "4. Item 4\n";
        outToClient.print(menu);
        outToClient.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {

  ArrayList<User> clients = new ArrayList<User>();
  ArrayList<User> waitlist = new ArrayList<User>();
  ArrayList<TicTacToeBoard> games = new ArrayList<TicTacToeBoard>();

  public static void main(String[] args) {
    Server server = new Server();
    server.go();
  }

  private void go() {
    ServerSocket serverSocket = null;

    /*
     * Create the server socket and set a timeout so that
     * it will not wait forever for a client to connect when
     * we call accept ().
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
        User user = new User(client);
        clients.add(user);
      }
      /*
       * Then give all older clients a chance to do something.
       * Different strategies exist for the most efficient ways
       * of doing this (for example, checking for new clients
       * before processing each old client, rather than before
       * the entire vector).
       */

      int i = 0;
      while (i < clients.size()) {
        if (processClientInput((Socket) clients.get(i).getSocket())) i++;
      }
    } while (true);

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean processClientInput(Socket client) {
    /*
     * Returns true if the client connection is still okay
     */

    BufferedReader inFromClient = null;
    PrintWriter outToClient = null;
    String input = null;

    try {
      inFromClient =
        new BufferedReader(new InputStreamReader(client.getInputStream()));
      outToClient = new PrintWriter(client.getOutputStream(), true);
    } catch (Exception e) {
      // Find the User object associated with the disconnected client and remove it
      for (int i = 0; i < clients.size(); i++) {
        User u = clients.get(i);
        if (u.getSocket() == client) {
          clients.remove(i);
          break;
        }
      }
      return false;
    }

    try {
      if (inFromClient.ready()) {
        input = inFromClient.readLine();
        String inputArray[] = input.split(" ", 2);
		String output = "";

        switch (inputArray[0]) {
          case "IAM":
            for (User user : clients) {
              if (user.getSocket() == client) {
                user.setUsername(inputArray[1]);
				output = "NAMEOK";
				outToClient.println(output);
                break;
              }
            }
            break;
			case "USERS":
			String userList = "USERS ";
			for(User user : clients){
				userList = userList + user.getUsername() + "\n";
			}
			userList += "--end--\n";
			outToClient.println(userList);
			break; 
        }
      }
    } catch (Exception e) {
      /*
       * If we get an exception when communicating with a client,
       * we should drop the client from the vector of clients, so
       * we don't waste time processing the same client later.
       *
       */

      for (int i = 0; i < clients.size(); i++) {
        User u = clients.get(i);
        if (u.getSocket() == client) {
          clients.remove(i);
          break;
        }
      }
      return false;
    }

    return true;
  }
}

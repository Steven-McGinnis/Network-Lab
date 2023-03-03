import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Server {

  private ArrayList<User> clients = new ArrayList<User>();
  private Queue<User> waitList = new LinkedList<User>();

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
      e.printStackTrace();
      clients.remove(client);
      return false;
    }

    try {
      if (inFromClient.ready()) {
        User user = getUserBySocket(client);
        if (user.getUsername() == null) {
          user.setUsername(inFromClient.readLine());
          outToClient.println(
            "Successfully Set Username to " + user.getUsername()
          );
        }

        input = inFromClient.readLine();
     
        if (input.equals("cmdStartGame")) {
        // Add the user to the waitlist
        waitList.add(user);

        // Check if there are at least two players in the waitlist
        if (waitList.size() >= 2) {
          // Create a new TicTacToeGame with the first two players in the waitlist
          User player1 = waitList.remove();
          User player2 = waitList.remove();
          //TicTacToeGame game = new TicTacToeGame(player1, player2);

          // Send "Game Start" message to both players
          PrintWriter outToPlayer1 = new PrintWriter(player1.getSocket().getOutputStream(), true);
          outToPlayer1.println("Game Start");
          outToPlayer1.println("You are now in a game.");

          PrintWriter outToPlayer2 = new PrintWriter(player2.getSocket().getOutputStream(), true);
          outToPlayer2.println("Game Start");
          outToPlayer2.println("You are now in a game.");
        } else {
          outToClient.println("You are now waiting for another player to join the game.");
        }
      }


        if (input.equals("cmdGetUsers")) {
          if (clients.size() > 0) {
            String usernames = "Logged in users: ";
            for (User loggedInUser : clients) {
              usernames += loggedInUser.getUsername() + ", ";
            }
            outToClient.println(usernames);
          } else {
            String noUsers = "There are no users logged in.";
            outToClient.println(noUsers.toUpperCase());
          }
        }

        if (input.equals("cmdExitServer")) {
          outToClient.println("Goodbye!");
          clients.remove(user);
          waitList.remove(user);
          return false;
        }

        outToClient.println(input.toUpperCase());
      }
    } catch (Exception e) {
      /*
       * If we get an exception when communicating with a client,
       * we should drop the client from the vector of clients, so
       * we don't waste time processing the same client later.
       *
       */

      clients.removeIf(user -> user.getSocket().equals(client));
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private User getUserBySocket(Socket socket) {
    for (User user : clients) {
      if (user.getSocket() == socket) {
        return user;
      }
    }
    return null;
  }
}

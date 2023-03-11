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
            Boolean name = isNameTaken(inputArray[1]);
            for (User user : clients) {
              if (name == false) {
                if (user.getSocket() == client) {
                  user.setUsername(inputArray[1]);
                  output = "NAMEOK";
                  outToClient.println(output);
                  break;
                }
              } else {
                output = "NAMEERROR";
                outToClient.println(output);
              }
            }
            break;
          case "USERS":
            String userList = "USERS\n";
            for (User user : clients) {
              String username = user.getUsername();
              if (username != null) {
                userList += username + "\n";
              }
            }
            userList += "--end--\n";
            outToClient.println(userList);
            break;
          case "IAMGONE":
            for (int i = 0; i < clients.size(); i++) {
              User u = clients.get(i);
              if (u.getSocket() == client) {
                clients.remove(i);
                break;
              }
            }
            break;
          case "FINDMATCH":
            User user = null;
            for (User u : clients) {
              if (u.getSocket() == client) {
                user = u;
                break;
              }
            }
            if (user != null) {
              if (waitlist.size() > 0) {
                // TODO: start game
                User opponent = waitlist.get(0);
                waitlist.remove(0);
                user.setPiece('O');
                opponent.setPiece('X');

                // TicTacToeBoard game = new TicTacToeBoard(user, opponent);
                String gameResponse1 =
                  "MATCHFOUND " +
                  opponent.getUsername() +
                  " " +
                  user.getPiece();

                String gameResponse2 =
                  "MATCHFOUND " +
                  user.getUsername() +
                  " " +
                  opponent.getPiece();

                // send the message to the current client
                outToClient.println(gameResponse1);

                // send the message to the opponent
                PrintWriter opponentOut = new PrintWriter(
                  opponent.getSocket().getOutputStream(),
                  true
                );
                opponentOut.println(gameResponse2);
                TicTacToeBoard game = new TicTacToeBoard(user, opponent);
                games.add(game);
                runGame(game);
              } else {
                waitlist.add(user);
              }
            }
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

  Boolean isNameTaken(String name) {
    Boolean isTaken = false;
    if (clients.isEmpty()) {
      return false;
    }
    for (User user : clients) {
      if (user.getUsername() != null && user.getUsername().equals(name)) {
        isTaken = true;
        break;
      }
    }
    return isTaken;
  }

  private void runGame(TicTacToeBoard game) {
    User player1 = game.getPlayer1();
    User player2 = game.getPlayer2();
    BufferedReader player1Input = null;
    BufferedReader player2Input = null;
    PrintWriter player1out = null;
    PrintWriter player2out = null;
    String input;
    Boolean valid = false;

    // try {
    //   player1Input =
    //     new BufferedReader(
    //       new InputStreamReader(player1.getSocket().getInputStream())
    //     );
    //   player2Input =
    //     new BufferedReader(
    //       new InputStreamReader(player2.getSocket().getInputStream())
    //     );
    //   player1out = new PrintWriter(player1.getSocket().getOutputStream(), true);
    //   player2out = new PrintWriter(player2.getSocket().getOutputStream(), true);
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }

    // try {
    //   player1out.println("MOVE");
    //   input = player1Input.readLine();
    //   //valid = game.isMoveValid(input);
    // } catch (Exception e) {
    //   // TODO: handle exception
    // }


  }
}

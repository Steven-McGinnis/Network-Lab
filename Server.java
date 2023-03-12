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
        if (unNamedState(clients.get(i)));
        i++;
      }

      i = 0;
      while (i < clients.size()) {
        if (namedState(clients.get(i))) i++;
      }

      i = 0;
      while (i < games.size()) {
        if (runGame(games.get(i))) i++;
      }
    } while (true);

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean namedState(User user) {
    int state = user.getStatus();
    String input;
    if (state == 1) {
      BufferedReader inFromClient = getBufferedReader(user);
      PrintWriter outToClient = getPrintWriter(user);
      try {
        if (inFromClient.ready()) {
          input = inFromClient.readLine();
          String inputArray[] = input.split(" ", 2);
          switch (inputArray[0]) {
            case "USERS":
              String userList = "USERS\n";
              for (User users : clients) {
                String username = users.getUsername();
                if (username != null) {
                  userList += username + "\n";
                }
              }
              userList += "--end--\n";
              outToClient.println(userList);
              break;
            /*
             *
             */
            case "IAMGONE":
              clients.remove(user);
              waitlist.remove(user);
              break;
            /*
             *
             */
            case "FINDMATCH":
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
                opponent.setStatus(3);
                user.setStatus(3);
              } else {
                waitlist.add(user);
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
        removeClient(user.getSocket());
        return false;
      }
    } else {
      return true;
    }
    return true;
  }

  private Boolean isNameTaken(String name) {
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

  private Boolean runGame(TicTacToeBoard game) {
    User player1 = game.getPlayer1();
    User player2 = game.getPlayer2();
    int gameState = game.getGameState();
    User currentPlayer;
    boolean isWinner = false;
    boolean isDraw = false;
    BufferedReader inFromClientPlayer1 = getBufferedReader(player1);
    PrintWriter outToClientPlayer1 = getPrintWriter(player1);
    BufferedReader inFromClientPlayer2 = getBufferedReader(player2);
    PrintWriter outToClientPlayer2 = getPrintWriter(player2);

    if (player1.getStatus() == 3 && player2.getStatus() == 3) {

      try {
        char piece = game.getActivePiece();
        String input = null;
        String output;

        if (piece == player1.getPiece()) {
          currentPlayer = player1;
        } else {
          currentPlayer = player2;
        }

        if (gameState == 0) {
          if (piece == player1.getPiece()) {
            currentPlayer = player1;
            output = "MOVE";
            outToClientPlayer1.println(output);
            game.setGameState(1);
          } else {
            currentPlayer = player2;
            output = "MOVE";
            outToClientPlayer2.println(output);
            game.setGameState(1);
          }
        }

        if (gameState == 1) {
          if (inFromClientPlayer1.ready()) {
            input = inFromClientPlayer1.readLine();
          }
          if (inFromClientPlayer2.ready()) {
            input = inFromClientPlayer2.readLine();
          }

          if (input != null) {
            String resultedInput[] = input.split(" ", 2);
            switch (resultedInput[0]) {
              case "MOVE":
                boolean valid = game.isMoveValid(resultedInput[1]);
                if (valid == true) {
                  game.makeMove(resultedInput[1]);
                  isWinner = game.checkWinner();
                  isDraw = game.checkDraw();
                  if (isWinner == true) {
                    output = "DONE " + currentPlayer.getUsername();
                    outToClientPlayer1.println(output);
                    outToClientPlayer2.println(output);
                    player1.setStatus(1);
                    player2.setStatus(1);
                    games.remove(game);
                  } else if (isDraw == true) {
                    output = "DONE draw";
                    outToClientPlayer1.println(output);
                    outToClientPlayer2.println(output);
                    player1.setStatus(1);
                    player2.setStatus(1);
                  } else {
                    game.changePlayer();
                    output = "UPDATE " + game.updateBoard();
                    outToClientPlayer1.println(output);
                    outToClientPlayer2.println(output);
                    game.setGameState(0);
                  }
                } else {
                  output = "ERROR That is not a valid move.";
                  if (piece == player1.getPiece()) {
                    outToClientPlayer1.println(output);
                  } else {
                    outToClientPlayer2.println(output);
                  }
                }
                break;
            }
          }
        }
      } catch (Exception e) {
        /*
         * If we get an exception when communicating with a client,
         * we should drop the client from the vector of clients, so
         * we don't waste time processing the same client later.
         *
         */
        // If an exception is thrown during gameplay, remove the game
        games.remove(game);
        player1.setStatus(1);
        player2.setStatus(1);
        String output = "MATCHABORTED";
        outToClientPlayer1.println(output);
        outToClientPlayer2.println(output);
        System.err.println("Error during game: " + e.getMessage());
        return false;
      }
    } else {
      return true;
    }
    return true;
  }

  private Boolean unNamedState(User user) {
    int state = user.getStatus();
    String input;
    if (state == 0) {
      BufferedReader inFromClient = getBufferedReader(user);
      PrintWriter outToClient = getPrintWriter(user);

      try {
        if (inFromClient.ready()) {
          input = inFromClient.readLine();
          String inputArray[] = input.split(" ", 2);
          String output = "";

          switch (inputArray[0]) {
            case "IAM":
              Boolean nameAllowed = isNameAllowed(inputArray[1]);
              Boolean nameTaken = isNameTaken(inputArray[1]);
              if (nameTaken == false && nameAllowed == true) {
                user.setUsername(inputArray[1]);
                output = "NAMEOK";
                user.setStatus(1);
                outToClient.println(output);
                break;
              } else {
                output = "NAMEERROR";
                outToClient.println(output);
              }
              break;
            case "USERS":
              String userList = "USERS\n";
              for (User users : clients) {
                String username = users.getUsername();
                if (username != null) {
                  userList += username + "\n";
                }
              }
              userList += "--end--\n";
              outToClient.println(userList);
              break;
            case "IAMGONE":
              clients.remove(user);
              waitlist.remove(user);
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
        removeClient(user.getSocket());
        return false;
      }
    } else {
      return true;
    }
    return true;
  }

  private Boolean isNameAllowed(String string) {
    if(string.equals("") || string.equals("--end--") || string.equals("draw")){
      return false;
    }
    return true;
  }

  private BufferedReader getBufferedReader(User user) {
    BufferedReader inFromClient = null;
    try {
      inFromClient =
        new BufferedReader(
          new InputStreamReader(user.getSocket().getInputStream())
        );
    } catch (Exception e) {
      // Find the User object associated with the disconnected client and remove it
      removeClient(user.getSocket());
    }
    return inFromClient;
  }

  private PrintWriter getPrintWriter(User user) {
    PrintWriter outToClient = null;
    try {
      outToClient = new PrintWriter(user.getSocket().getOutputStream(), true);
    } catch (Exception e) {
      // Find the User object associated with the disconnected client and remove it
      removeClient(user.getSocket());
    }
    return outToClient;
  }

  public void removeClient(Socket socket) {
    for (int i = 0; i < clients.size(); i++) {
      User user = clients.get(i);
      if (user.getSocket() == socket) {
        clients.remove(i);
        break;
      }
    }
    for (int i = 0; i < waitlist.size(); i++) {
      User u = waitlist.get(i);
      if (u.getSocket() == socket) {
        waitlist.remove(i);
        break;
      }
    }
  }
}

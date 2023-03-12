import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  public static void main(String[] args) throws IOException {
    Client cl = new Client();
    Socket server = null;
    PrintWriter outToServer = null;
    BufferedReader inFromServer = null;
    int status = 0;
    boolean inGame = false;

    try {
      server = new Socket("localhost", 4444);
      outToServer = new PrintWriter(server.getOutputStream(), true);
      inFromServer =
        new BufferedReader(new InputStreamReader(server.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Can't find localhost.");
      return;
    } catch (IOException e) {
      System.err.println(
        "IO Error on connection to localhost. Is server running?"
      );
      return;
    }

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String userInput;

    do {
      String returned;
      /**
       *
       * If your not a user yet do this.
       *
       */
      if (status == 0) {
        System.out.println("What is your name?");
        userInput = stdIn.readLine();
        userInput = "IAM " + userInput;
        outToServer.println(userInput);
        String result = inFromServer.readLine();
        if (result.equals("NAMEERROR")) {
          System.out.println("Im Sorry that name is not available.");
        } else if (result.equals("NAMEOK")) {
          status = 1;
          System.out.println("Name Accepted");
        }
      }

      userInput = "";
      //Main Menu
      if (status == 1) {
        /**
         *
         *
         * First Menu Where you Can choose what to do.
         *
         */
        System.out.println("What would you like to do?");
        System.out.println("Please Enter the Number for your selection.");
        System.out.println("1: Search for a Game");
        System.out.println("2: See List of Active Users");
        System.out.println("3: Quit");
        userInput = stdIn.readLine();
        switch (userInput) {
          /**
           *
           *
           * This is the Find Match Command
           *
           *
           */
          case "1":
            userInput = "FINDMATCH";
            outToServer.println(userInput);
            System.out.println("You are in the wait queue");

            returned = "";
            while (!returned.endsWith("X") && !returned.endsWith("O")) {
              returned += inFromServer.readLine();
            }

            String result[] = returned.split(" ");
            System.out.println("Match Found,  Your Opponent is " + result[1]);
            System.out.println("You are playing the " + result[2] + "'s");
            status = 2;
            break;
          /**
           *
           *
           * This is the List Users Command
           *
           *
           */
          case "2":
            userInput = "USERS";
            outToServer.println(userInput);
            returned = "";
            String line = "";
            while ((line = inFromServer.readLine()) != null) {
              if (line.equals("--end--")) {
                break;
              }
              returned += line + "\n";
            }
            System.out.println(returned);
            break;
          /**
           *
           *
           * This is the Leave Server Command
           *
           *
           */
          case "3":
            userInput = "IAMGONE";
            outToServer.println(userInput);
            inFromServer.readLine();
            userInput = "Bye";
            break;
        }
        /**
         *
         * Game Logic
         *
         */
      } else if (status == 2) {
        inGame = true;
        cl.drawBoard(null);

        while (inGame == true) {
          returned = null;
          while (returned == null) {
            returned = inFromServer.readLine();
          }

          String returnedCommand[] = returned.split(" ", 2);
          switch (returnedCommand[0]) {
            case "MOVE":
              System.out.println("Please make your move 1-9");
              userInput = stdIn.readLine();
              String move = "MOVE " + userInput;
              outToServer.println(move);
              break;
            case "ERROR":
              System.out.println(returnedCommand[1]);
              System.out.println("Please try again 1-9 on an empty space");
              userInput = stdIn.readLine();
              move = "MOVE " + userInput;
              outToServer.println(move);
              break;
            case "UPDATE":
              cl.drawBoard(returnedCommand[1]);
              break;
            case "DONE":
              if (returnedCommand[1].equals("draw")) {
                System.out.println("The Game Ended in a Tie.");
              } else {
                System.out.println("The Winner is " + returnedCommand[1]);
              }
              inGame = false;
              status = 1;
              break;
            case "MATCHABORTED":
              System.out.println("Your Opponent has forfeit you win.");
              inGame = false;
              status = 1;
              break;
          }
        }
      }
      /**
       *
       *
       *
       * Continue Loop
       *
       *
       *
       */
    } while (!userInput.equals("Bye"));

    outToServer.close();
    inFromServer.close();
    server.close();
  }

  void drawBoard(String move) {
    if (move == null) {
      String emptyBoard =
        "  1 | 2 | 3 \n" +
        "  ---------\n" +
        "  4 | 5 | 6 \n" +
        "  ---------\n" +
        "  7 | 8 | 9 \n";
      System.out.println(emptyBoard);
    } else {
      char spaces[] = move.toCharArray();
      int index = 0;
      String board =
        "  " +
        String.valueOf(spaces[index++]).replace('-', '1') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '2') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '3') +
        " \n" +
        "  ---------\n" +
        "  " +
        String.valueOf(spaces[index++]).replace('-', '4') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '5') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '6') +
        " \n" +
        "  ---------\n" +
        "  " +
        String.valueOf(spaces[index++]).replace('-', '7') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '8') +
        " | " +
        String.valueOf(spaces[index++]).replace('-', '9') +
        " \n";
      System.out.println(board);
    }
  }
}

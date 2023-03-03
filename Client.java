import java.io.*;
import java.net.*;

public class Client {

  public static void main(String[] args) throws IOException {
    String Name = null;
    Boolean waitlist = false;
    Boolean ingame = false;
    Socket server = null;
    String userInput = "";
    PrintWriter outToServer = null;
    BufferedReader inFromServer = null;

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
   // String userInput;

    do {
      if (Name == null) {
        System.out.println("Please Enter your Name.");
        userInput = stdIn.readLine();
        Name = userInput;
        outToServer.println(userInput);
        System.out.println("Hello " + Name);
        inFromServer.readLine();
      }
      
      do {
        if (waitlist == true) {
          System.out.println(
           "Waiting for Game..."
          );
          String serverResponse = inFromServer.readLine();
          System.out.println("Server: " + serverResponse);
        
          if (serverResponse.equals("cmdGameStarted")) {
            waitlist = false; // Exit the wait loop
            ingame = true; // Set the game status to true
          }
        }
      } while (waitlist == true);

      if (!ingame) {
        System.out.println("What would you like to do?");
        System.out.println("1: Start a Game");
        System.out.println("2: See all players online.");
        System.out.println("3: Quit");

        userInput = stdIn.readLine();

        if (userInput.equals("1")) {
          outToServer.println("cmdStartGame");
          waitlist = true; // Set waitlist to true after sending cmdStartGame
          System.out.println("Waiting for other player to join...");
        } else if (userInput.equals("2")) {
          outToServer.println("cmdGetUsers");
          System.out.println("Server: " + inFromServer.readLine());
        } else if (userInput.equals("3")) {
          outToServer.println("cmdExitServer");
          //outToServer.println(userInput);
          userInput = "Bye";
        }
      } else {
        // Game logic goes here
        // ...
        // When the game is over, set ingame to false
        ingame = false;
      }
    } while (!userInput.equals("Bye"));

    outToServer.close();
    inFromServer.close();
    server.close();
  }
}

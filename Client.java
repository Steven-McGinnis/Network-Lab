import java.io.*;
import java.net.*;

public class Client {

  public static void main(String[] args) throws IOException {
    String Name = null;
    Socket server = null;
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
    String userInput;

    do {
      if (Name == null) {
        System.out.println("Please Enter your Name.");
        userInput = stdIn.readLine();
        Name = userInput;
        outToServer.println(userInput);
        System.out.println("Hello " + Name);
        inFromServer.readLine();
      }

      System.out.println("What would you like to do?");
      System.out.println("1: Start a Game");
      System.out.println("2: See all players online.");
      System.out.println("3: Quit");
      
      userInput = stdIn.readLine();

      if (userInput.equals("1")) {
        outToServer.println("cmdStartGame");
        System.out.println ("Server: " + inFromServer.readLine ());
      } else if (userInput.equals("2")) {
        outToServer.println("cmdGetUsers");
        System.out.println ("Server: " + inFromServer.readLine ());
      } else if (userInput.equals("3")) {
        outToServer.println(userInput);
      }

      outToServer.println(userInput);
      System.out.println("Server: " + inFromServer.readLine());
    } while (!userInput.equals("Bye"));

    outToServer.close();
    inFromServer.close();
    server.close();
  }
}

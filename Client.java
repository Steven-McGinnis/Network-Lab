import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  public static void main(String[] args) throws IOException {
    Socket server = null;
    PrintWriter outToServer = null;
    BufferedReader inFromServer = null;
    int status = 0;

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
      //Initial Connection
      if (status == 0) {
        System.out.println("What is your name?");
        userInput = stdIn.readLine();

        if (userInput.equals("--end--") || userInput.equals("draw")) {
          break;
        }

        userInput = "IAM " + userInput;
        outToServer.println(userInput);
        String result = inFromServer.readLine();
        if (result.equals("NAMEERROR")) {
          break;
        } else if (result.equals("NAMEOK")) {
          status = 1;
          System.out.println("Name Accepted");
        }
      }

      //Main Menu
      if (status == 1) {
        System.out.println("What would you like to do?");
        System.out.println("Please Enter the Number for your selection.");
        System.out.println("1: Search for a Game");
        System.out.println("2: See List of Active Users");
        System.out.println("3: Quit");
        userInput = stdIn.readLine();
        switch (userInput) {
          case "1":
            break;
          case "2":
            userInput = "USERS";
            outToServer.println(userInput);
            String returned = "";
            String line = "";
            while ((line = inFromServer.readLine()) != null) {
              if (line.equals("--end--")) {
                break;
              }
              returned += line + "\n";
            }
            System.out.println(returned);
            System.out.println("--end--");
            inFromServer.readLine();
            break;
          case "3":
            userInput = "IAMGONE";
            outToServer.println(userInput);
            inFromServer.readLine();
            userInput = "Bye";
            break;
        }
      }

      userInput = stdIn.readLine();
      outToServer.println(userInput);
      System.out.println("Server: " + inFromServer.readLine());
    } while (!userInput.equals("Bye"));

    outToServer.close();
    inFromServer.close();
    server.close();
  }
}

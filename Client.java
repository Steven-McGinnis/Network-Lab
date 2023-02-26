import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) throws IOException {
    Socket server = null;
    PrintWriter outToServer = null;
    BufferedReader inFromServer = null;

    try {
      server = new Socket("localhost", 4444);
      outToServer = new PrintWriter(server.getOutputStream(), true);
      inFromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Can't find localhost.");
      return;
    } catch (IOException e) {
      System.err.println("IO Error on connection to localhost. Is server running?");
      return;
    }

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String userInput;
    String serverResponse;

    do {
      // Wait for a response from the server
      serverResponse = inFromServer.readLine();
      System.out.println("Server: " + serverResponse);

      // Send input to the server
      userInput = stdIn.readLine();
      outToServer.println(userInput);

    } while (!userInput.equals("Bye"));

    outToServer.close();
    inFromServer.close();
    server.close();
  }
}

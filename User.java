import java.net.Socket;

public class User {
    private String username;
    private Socket socket;
    private int status = 0;
    private char piece;
   
    
    public User(Socket socket) {
        this.socket = socket;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public Socket getSocket() {
        return this.socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setPiece(char piece){
        this.piece = piece;
    }

    public char getPiece() {
        return this.piece;
    }
}

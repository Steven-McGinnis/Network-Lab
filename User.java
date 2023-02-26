import java.net.Socket;

public class User {
    private String username;
    private Socket socket;
    
    public User(Socket socket) {
        this.socket = socket;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Socket getSocket() {
        return socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

import java.net.Socket;

public class User {
    private String username;
    private Socket socket;
    private Boolean inQueue = false;
    private Boolean inGame = false;
    
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

    public boolean getInQueue() {
        return this.inQueue;
    }

    public void setInQueue(){
        if (this.inQueue == false){
            this.inQueue = true;
        }else if (this.inQueue == true){
            this.inQueue = false;
        }
        else{
            return;
        }

    }

    public boolean getInGame() {
        return this.inGame;
    }

    public void setInGame(){
        if (this.inGame == false){
            this.inGame = true;
        }else if (this.inGame == true){
            this.inGame = false;
        }
        else{
            return;
        }

    }
}

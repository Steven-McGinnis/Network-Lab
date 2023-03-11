public class TicTacToeBoard {
    private static final int BOARD_SIZE = 3;
  
    private User player1;
    private User player2;
    private char[][] board;
    private User winner;
  
    public TicTacToeBoard(User player1, User player2) {
      this.player1 = player1;
      this.player2 = player2;
      this.board = new char[BOARD_SIZE][BOARD_SIZE];
      this.winner = null;
  
      // Initialize the board with empty spaces
      for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
          board[i][j] = ' ';
        }
      }
    }
  
    public User getPlayer1(){
        return player1;
    }

    public User getPlayer2(){
        return player2;
    }

    public String getBoardState(){
        String boardState = "MOVE ";
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardState = boardState + board[i][j];
            }
        }
        return boardState;
    }

    public Boolean isMoveValid(int move){
        int row = (move - 1) / BOARD_SIZE;
        int col = (move - 1) % BOARD_SIZE;
        if (move < 1 || move > BOARD_SIZE * BOARD_SIZE || board[row][col] != ' ') {
            return false;
        }    
        return true;
    }
}
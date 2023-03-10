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
  
    public boolean isGameOver() {
      return getWinner() != null || isBoardFull();
    }
  
    public boolean isBoardFull() {
      for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (board[i][j] == ' ') {
            return false;
          }
        }
      }
      return true;
    }
  
    public User getWinner() {
      return winner;
    }
  
    public void setWinner(User winner) {
      this.winner = winner;
    }
  
    public boolean isValidMove(int row, int col) {
      return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == ' ';
    }
  
    public boolean makeMove(User player, int row, int col) {
      if (!isValidMove(row, col)) {
        return false;
      }
  
      char symbol = getSymbolForPlayer(player);
      board[row][col] = symbol;
  
      if (hasPlayerWon(player)) {
        setWinner(player);
      }
  
      return true;
    }
  
    public boolean hasPlayerWon(User player) {
      char symbol = getSymbolForPlayer(player);
  
      // Check rows
      for (int i = 0; i < BOARD_SIZE; i++) {
        boolean hasWon = true;
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (board[i][j] != symbol) {
            hasWon = false;
            break;
          }
        }
        if (hasWon) {
          return true;
        }
      }
  
      // Check columns
      for (int i = 0; i < BOARD_SIZE; i++) {
        boolean hasWon = true;
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (board[j][i] != symbol) {
            hasWon = false;
            break;
          }
        }
        if (hasWon) {
          return true;
        }
      }
  
      // Check diagonals
      boolean hasWon = true;
      for (int i = 0; i < BOARD_SIZE; i++) {
        if (board[i][i] != symbol) {
          hasWon = false;
          break;
        }
      }
      if (hasWon) {
        return true;
      }
  
      hasWon = true;
      for (int i = 0; i < BOARD_SIZE; i++) {
        if (board[i][BOARD_SIZE - i - 1] != symbol) {
          hasWon = false;
          break;
        }
      }
      if (hasWon) {
        return true;
      }
  
      return false;
    }
  
}
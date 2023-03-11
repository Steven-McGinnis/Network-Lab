public class TicTacToeBoard {

  private static final int BOARD_SIZE = 3;
  private User player1;
  private User player2;
  private char[][] board;
  private User winner;
  private char currentPiece = 'X';

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

  public User getPlayer1() {
    return player1;
  }

  public User getPlayer2() {
    return player2;
  }

  public char getActivePiece() {
    return currentPiece;
  }

  public String getBoardState() {
    String boardState = "MOVE ";
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        boardState = boardState + board[i][j];
      }
    }
    return boardState;
  }

  public Boolean isMoveValid(String move) {
    int moveInt = Integer.parseInt(move);
    int row = (moveInt - 1) / BOARD_SIZE;
    int col = (moveInt - 1) % BOARD_SIZE;
    if (
      moveInt < 1 || moveInt > BOARD_SIZE * BOARD_SIZE || board[row][col] != ' '
    ) {
      return false;
    }
    return true;
  }

  public void makeMove(String move) {
    int moveInt = Integer.parseInt(move);
    int row = (moveInt - 1) / BOARD_SIZE;
    int col = (moveInt - 1) % BOARD_SIZE;
    board[row][col] = currentPiece;
  }

  public boolean checkWinner() {
    char activePiece = this.currentPiece;
    boolean isWinnerRow = false;
    boolean isWinnerColumn = false;
    boolean isWinnerDiagonal = false;
    isWinnerRow = checkRows(activePiece);
    isWinnerColumn = checkColumns(activePiece);
    isWinnerDiagonal = checkDiagonal(activePiece);
    if (
      isWinnerColumn == true || isWinnerRow == true || isWinnerDiagonal == true
    ) {
      return true;
    }
    return false;
  }

  private boolean checkColumns(char activePiece) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      if (
        board[0][i] == activePiece &&
        board[1][i] == activePiece &&
        board[2][i] == activePiece
      ) {
        return true;
      }
    }
    return false;
  }

  private boolean checkDiagonal(char activePiece) {
    if (
      board[0][0] == activePiece &&
      board[1][1] == activePiece &&
      board[2][2] == activePiece
    ) {
      return true;
    } else if (
      board[0][2] == activePiece &&
      board[1][1] == activePiece &&
      board[2][0] == activePiece
    ) {
      return true;
    }
    return false;
  }

  private boolean checkRows(char activePiece) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      if (
        board[i][0] == activePiece &&
        board[i][1] == activePiece &&
        board[i][2] == activePiece
      ) {
        return true;
      }
    }
    return false;
  }

  public boolean checkDraw() {
    // Check if there are any empty spaces left on the board
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (board[i][j] == ' ') {
          // There is at least one empty space, game is not a draw
          return false;
        }
      }
    }
    // All spaces are filled, game is a draw
    return true;
  }
}

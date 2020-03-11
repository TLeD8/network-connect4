import java.util.Random;

/**
 * 
 * 
 * @author Ping Hsu Tyler LeDuc
 * The purpose of this file is to update the model board and make
 * the moves of each player. This will allow the user at present to make
 * changes to the other files.
 *
 */

public class Connect4Controller {
	private Connect4Model model;
	/** Constructor that gets the model so it can make calls to it
	*/
	public Connect4Controller(Connect4Model model){
		this.model = model;
	}
	/** calls the model to update the board
	*/
	public void updateBoard(int[][] board){
		model.updateBoard(board);
	}
	
	/*
	 * The purpose is to set the player by taking an integer and 
	 * then changing the model class
	 */
	public void setPlayer(int player){
		model.setPlayer(player);
	}
	
	/*
	 * This represents the human turn
	 */
	public int humanTurn(int column){
		return model.dropDisk(column);
	}

	/*
	 * This represents computer turn
	 */
	public int computerTurn(){
		Random rand = new Random();
		int column = rand.nextInt((6 - 0) + 1) + 0;
		return model.dropDisk(column);
	}
	
	/*
	 * Testing purposes
	 */
	public void computerTurn(int column){
		System.out.println("test2");
	}

	/** Checks if 4 pieces are in a row
	*	@Param player number that represents which players disks to check
	*	@Return boolean true if the player won, false otherwise
	*/
	public boolean checkWin(int player){
		// model.printBoard();
	    // horizontalCheck 
		int[][] board = model.getBoard();
		for (int i = 0; i<6 ; i++){
	        for (int j = 0; j<4; j++){ // 7-3 for j
	            if (board[j][i] == player && board[j+1][i]== player && board[j+2][i] == player && board[j+3][i] == player){
	                return true;
	            }           
	        }
	    }
		
		//vertical
	    for (int i = 0; i<3 ; i++){ // 6-4 for i
	        for (int j = 0; j<6; j++){ 
	            if (board[j][i] == player && board[j][i+1] == player && board[j][i+2] == player && board[j][i+3] == player){
	                return true;
	            }           
	        }
	    }
	    
	    // first diagonal
	    for (int i = 0; i<3 ; i++){ // 6-4 for i 3 5
	        for (int j = 0; j<3; j++){
	        	// System.out.println("i = " + (i + 3));
	            if (board[j][i] == player && board[j+1][i+1] == player && board[j+2][i+2] == player && board[j+3][i+3] == player){
	                return true;
	            }           
	        }
	    }
	    
	    // second diagonal checks!
	    for (int i = 3; i<6 ; i++){ // 6-4 for i
	        for (int j = 3; j<7; j++){
	            if (board[j][i] == player && board[j-1][i-1] == player && board[j-2][i-2] == player && board[j-3][i-3] == player){
	                return true;
	            }           
	        }
	    }
	    return false;
	}
}

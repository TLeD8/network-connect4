/** Contains the array that represents the board thats being played on.
*   Methods interact with the board. Places a new disk and then calls the observer to update
*   the board with the new disk that was placed.
*/
import java.util.Random;
import java.util.Observable;
import java.util.*;
import java.util.Observer;
public class Connect4Model extends Observable{
    private int[][] board;
    private int player;
    public Connect4Model() { 
        ;
    }
    
    public void setPlayer(int player){
        this.player = player;
    }
    public void updateBoard(int[][] board){
        this.board = board;
    }

    public int[][] getBoard(){
        return board;

    }
    /** 
    *   @Return int that represents whether the disk was placed successfully or not.
    */
    public int dropDisk(int column){
        int row = 0;
        for (int i = 0; i < 5; i++){
            if (board[column][i+1] != 0){
                board[column][i] = player;
                setChanged();
                // System.out.println("+row: " + i + " column: " + column);
                notifyObservers(new Connect4MoveMessage(i,column, player)); // left off here: dropping a disk, updating the color and model
                return 1;
            }
            row = i;
        }
        if (board[column][5] != 0){
            // System.out.println("Illegal move");
            return 0;
        }
        board[column][5] = player;
        // System.out.println(board[column][5]);
        setChanged();
        notifyObservers(new Connect4MoveMessage(5,column, player));
        return 1;
    }

    public void printBoard(){
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 5; j++){
                System.out.print(board[j][i]);
            }
            System.out.println();
        }
    }


   }



/** This program plays a game of connect4 with two people on a server. Players
*	can choose to play manually or a computer. The game is played on a javafx application
*	and moves are sent over a server to the other players client.
*
*	@author tylerleduc+pinghsu520
*
*/
import javafx.application.Application;
public class Connect4{

	public static void main(String[] args) {		
		Application.launch(Connect4View.class, args);
	}

}
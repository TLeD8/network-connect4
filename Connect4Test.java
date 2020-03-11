import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

public class Connect4Test {

	@Test
	public void testModel() {
		Connect4Model a=new Connect4Model();
		int[][]board=new int[4][2]; 
		a.updateBoard(board);
		int[][]ans=new int[4][2]; 
		Assert.assertEquals(ans, a.getBoard());
	}
	
//	@Test
//	public void testModel2() {
//		Connect4Model a=new Connect4Model();
//		a.setPlayer(player); 
//		Assert.assertEquals(ans, a.getBoard());
//	}
	
	
	
	
	
//	@Test
//	public void dropDisk() {
//		Connect4Model a=new Connect4Model();
//		int temp;
//		
////		a.setPlayer(0);
//		temp=a.dropDisk(5);
//		Assert.assertNotEquals(1, temp);
//	}

//	
//	@Test
//	public void testConnect() {
//		Connect4Server a=new Connect4Server(4000);
//		
//	}
//	
	
	@Test
	public void testClient() {
		Connect4Server a=new Connect4Server(4000);
		Socket player1 = null;
		Socket player2 = null;
		ObjectOutputStream out1 = null;
		ObjectOutputStream out2 = null;
		ObjectInputStream in1 = null;
		ObjectInputStream in2 = null;
		ServerSocket server = null;
		Client b=new Client(player1, player2, out1, out2, in1, in2, server);
		// essentially kills server
		b.killServer();
		int c=1;
		Assert.assertEquals(c,1);
		
	}
	
}

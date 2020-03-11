/** Creates the server that player connects and send messages to both players
*	to update their board
*/
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
public class Connect4Server{
	private ServerSocket server;
	public Connect4Server(int port){
		new Thread(() -> {
			Socket player1 = null;
			Socket player2;
			ObjectInputStream in1 = null;
			ObjectInputStream in2;
			ObjectOutputStream out1 = null;
			ObjectOutputStream out2; 


			boolean player1Connected = false;
			try{
				server = new ServerSocket(port);
				while(true){
					// System.out.println("waiting for connection...");
					Socket player = server.accept();
					OutputStream OutputStream = player.getOutputStream();
					InputStream InputStream = player.getInputStream();
					ObjectOutputStream toPlayer = new ObjectOutputStream(OutputStream);
					ObjectInputStream fromPlayer = new ObjectInputStream(InputStream);

					if (player1Connected == false){
						player1 = player;
						// System.out.println("player1 connected");
						in1 = fromPlayer;
						out1 = toPlayer;
						// out1.writeObject(new Connect4MoveMessage(0,0,1));
						player1Connected = true;
					}
					else if (player1Connected){
						player2 = player;
						// System.out.println("player2 connected");
						in2 = fromPlayer;
						out2 = toPlayer;
						Client client = new Client(player1, player2, out1, out2, in1, in2, server);
						new Thread(client).start();
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}).start();
	}

}

class Client implements Runnable{
	Socket player1;
	Socket player2;
	private ObjectOutputStream out1;
	private ObjectOutputStream out2;
	private ObjectInputStream in1;
	private ObjectInputStream in2;
	private ServerSocket server;

	public Client(ObjectOutputStream out1){
		this.out1 = out1;
	}
	public Client(Socket player1, Socket player2, ObjectOutputStream out1, ObjectOutputStream out2, ObjectInputStream in1, ObjectInputStream in2, ServerSocket server){
		this.player1 = player1;
		this.player2 = player2;
		this.out1 = out1;
		this.out2 = out2;
		this.in1 = in1;
		this.in2 = in2;
		this.server = server;
	}
	public void killServer(){
		try{
			server.close();
			System.exit(0);
		}
		catch( Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		while(true){
			try{
				Connect4MoveMessage message;
				message = (Connect4MoveMessage)in1.readObject();
				if (message.getColor() == 3){
					message = new Connect4MoveMessage(0,1,3);
					out2.writeObject(message);
					Thread.sleep(300);
					killServer();
					System.exit(0);
				}

				out2.writeObject(message);
				// System.out.println(message.getColumn());
				message = (Connect4MoveMessage)in2.readObject();
				if (message.getColor() == 3){
					message = new Connect4MoveMessage(0,2,3);
					out1.writeObject(message);
					Thread.sleep(300);
					killServer();
					System.exit(0);
				}
				out1.writeObject(message);
				// out.close();
				// in.close();
				// player.close();
				// killServer();

			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
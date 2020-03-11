/** Displays the game board and calls the controller to make changes when a new disk is placed
*	by another player. The game board is a gridpane that has 6 columns and 7 rows with a pane
*	in each cell that holds a disk. Players can click to place their disk, or the computer does it for
*	them.
*/
import javafx.application.Application;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label; 
import javafx.scene.control.*; 
import javafx.geometry.*; 
import java.util.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.text.Text; 
import javafx.scene.shape.Circle;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler; 
import javafx.scene.input.MouseEvent; 
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
//platform.runlater
public class Connect4View extends Application implements Observer{
	private Connect4Controller controller;
	private Socket socket;
	private Socket newSocket;
	private String serverName1;
	private int port;
	private Connect4Model model;
	private int[][] board = new int[7][6];
	private Pane[][] objectBoard = new Pane[7][6];
	private int playerNumber;
	private boolean waiting = false;
	private boolean comp = false;
	private boolean compFirst = false;
	private boolean compDone = false;
	GridPane gridPane;
	ObjectInputStream fromServer;
	ObjectOutputStream toServer;
	/** Displays the view for the game board. Pane object with circles represent
	*	game pieces and are clickable to place a new disk.
	*/
	@Override
	public void start(Stage stage) {
		model = new Connect4Model();
		this.controller = new Connect4Controller(model);
		model.addObserver(this);
		BorderPane window = new BorderPane();
		gridPane = new GridPane();
		int cols = 7;
		int rows = 6;
		for (int i = 0; i < cols; i++){
			ColumnConstraints column = new ColumnConstraints(56);
			gridPane.getColumnConstraints().add(column);
		}

		for (int i = 0; i < rows; i++){
			RowConstraints row1 = new RowConstraints(56);
			gridPane.getRowConstraints().add(row1);
		}
		gridPane.setVgap(8);
		gridPane.setHgap(8);
		gridPane.setPadding(new Insets(8));

		for (int i = 0; i < cols; i++){
			for (int j = 0; j < rows; j++){
				final int x = i;
				Pane pane = new Pane();
				pane.setStyle("-fx-background-color: blue;");
				pane.setMaxSize(60,60);
				pane.setOnMouseClicked(new EventHandler<MouseEvent>(){
					@Override
					public void handle(MouseEvent event){
						if (!waiting && !comp){
							Connect4MoveMessage message = new Connect4MoveMessage(0, x, playerNumber);
							// System.out.println("message created");
							if(controller.humanTurn(x) == 0){
								waiting = false;
							}
							else{
								waiting = true;
							}
						}
					}
				});

				Circle circle = new Circle(20, Color.WHITE);
				circle.relocate(8,8);
				pane.getChildren().addAll(circle);
				gridPane.add(pane, i, j);
				board[i][j] = 0; // 0 = empty
				objectBoard[i][j] = pane;

			}
		}
		Menu menu = new Menu("File");
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(e -> {
			connect4Menu(stage);
		});
		menu.getItems().add(newGame);
        MenuBar menuBar = new MenuBar();

        menuBar.getMenus().add(menu);

        VBox vBox = new VBox(menuBar);
        window.setTop(vBox);
		model.updateBoard(board);
		window.setCenter(gridPane);

		Scene scene = new Scene(window);
		stage.sizeToScene();
		gridPane.setStyle("-fx-background-color: blue;");
		stage.setScene(scene);
		stage.show();
	}
	/** Menu display for new game. Allows users to create a server or connect as a client
	*	and choose the server and port. Users can choose to play as a human or computer.
	*/
	public void connect4Menu(Stage stage){
	    GridPane gridpane = new GridPane();
	    gridpane.setPadding(new Insets(10, 10, 10 ,10));
	    final Stage dialog = new Stage();

	    final ToggleGroup group1 = new ToggleGroup();
	    final ToggleGroup group2 = new ToggleGroup();

	    RadioButton serverBtn = new RadioButton("Server");
	    RadioButton clientBtn = new RadioButton("Client");
	    serverBtn.setToggleGroup(group1);
	    clientBtn.setToggleGroup(group1);
	    VBox create = new VBox(new Label("Create:"), serverBtn, clientBtn);
	    gridpane.add(create, 0 ,0);

	    RadioButton humanBtn = new RadioButton("Human");
	    RadioButton compBtn = new RadioButton("Computer");
	    humanBtn.setToggleGroup(group2);
	    compBtn.setToggleGroup(group2);
	    VBox playAs = new VBox(new Label("Play as:"), humanBtn, compBtn);
	    gridpane.add(playAs, 0 ,100);

	    VBox vbox3 = new VBox();
	    TextField serverName = new TextField();
	    serverName.setText("localhost");
	    vbox3.getChildren().addAll(new Label("Server"), serverName);
	    gridpane.add(vbox3, 0, 150);

	    VBox vbox4 = new VBox();
	    TextField portNumber = new TextField();
	    portNumber.setText("4000");
	    vbox4.getChildren().addAll(new Label("Port"), portNumber);
	    gridpane.add(vbox4, 0, 200);

	    Button ok = new Button("OK");
	    ok.setOnAction(e -> {
	      if (serverBtn.isSelected() && humanBtn.isSelected()){
	      	port = Integer.parseInt(portNumber.getText());
	      	Connect4Server server = new Connect4Server(port);
	      	playerNumber = 1;
	      	controller.setPlayer(playerNumber);

	      	serverName1 = serverName.getText();
	      	connectToServerAsPlayer(serverName.getText(), port);
	      	dialog.close();
	      }
	      else if (clientBtn.isSelected() && humanBtn.isSelected()){
	      	port = Integer.parseInt(portNumber.getText());
	      	playerNumber = 2;
	      	controller.setPlayer(playerNumber);

	      	connectToServerAsPlayer(serverName.getText(), port);
	      	dialog.close();
	      }
	      else if (serverBtn.isSelected() && compBtn.isSelected()){
	      	port = Integer.parseInt(portNumber.getText());
	      	Connect4Server server = new Connect4Server(port);
	      	playerNumber = 1;
	      	comp = true;
	      	compFirst = true;
	      	controller.setPlayer(playerNumber);
	      	connectToServerAsComp(serverName.getText(), port);
	      	dialog.close();
	      }
	      else if (clientBtn.isSelected() && compBtn.isSelected()){
	      	port = Integer.parseInt(portNumber.getText());
	      	playerNumber = 2;
	      	comp = true;
	      	controller.setPlayer(playerNumber);
	      	connectToServerAsComp(serverName.getText(), port);
	      	dialog.close();
	      }
	    });
	    gridpane.add(ok, 0, 300);


	    Button cancel = new Button("Cancel");
	    cancel.setOnAction(e -> {
	    	dialog.close();
	    });
	    gridpane.add(cancel, 1, 300);

	    BorderPane window = new BorderPane();
	    window.setCenter(gridpane);
	    Scene scene = new Scene(window);
	    // Stage dialog = new Stage();
	    dialog.setScene(scene);
	    dialog.setTitle("Network Setup");
	    dialog.initOwner(stage);
	    dialog.initModality(Modality.APPLICATION_MODAL);
	    dialog.showAndWait();		
	}
	/**	Connects to the server and runs a thread that takes connect4MoveMessage's from the server
	*	that represents moves made by the other user. If the connect4MoveMessage has 3
	*	as its player number that means the other player won and ends the game. Calls runComp
	*	which starts a thread for a computer to play and make moves.
	*/
	public void connectToServerAsComp(final String serverName, final int port){
		// final Socket socket;
		try {
			// System.out.println(serverName + " : " + port);

			socket = new Socket(serverName, port);
			// System.out.println("connected");

			fromServer = new ObjectInputStream(socket.getInputStream());

			toServer = new ObjectOutputStream(socket.getOutputStream());
			}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		runComp();
		new Thread(() -> {
			while(true){
			try{
				if (!socket.isConnected()){
					try{
						fromServer.close();
						toServer.close();
						Stage stage2 = new Stage();
						stage2.setTitle("The other player won!");
						stage2.showAndWait();
						System.exit(0);							
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				Connect4MoveMessage message;
				if (!waiting){
					message = (Connect4MoveMessage)fromServer.readObject();
				}
				else{
					message = null;
				}
				// Connect4MoveMessage message;
				// message = (Connect4MoveMessage)fromServer.readObject();
				// System.out.println("received: " + message.getColumn());
				compFirst = true;
				Platform.runLater(() -> {
					if (message.getColor() == 3){
						compDone = true;
						compFirst = false;
						try{
							fromServer.close();
							toServer.close();
							// socket.close();							
						} catch (Exception e){
							e.printStackTrace();
						}
						// fromServer.close();
						// toServer.close();
						// socket.close();
						Stage stage2 = new Stage();
						stage2.setTitle(message.getColumn() + " won!");
						stage2.showAndWait();
						System.exit(0);
					}
					board[message.getColumn()][message.getRow()] = message.getColor();
					controller.updateBoard(board);
					insertCircle(message.getRow(), message.getColumn(), message.getColor());
				});
				waiting = false;

			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		}).start();			
	}
	/** Connects to the server and starts a thread that takes connect4MoveMessages from the server.
	*	If a message has the player number as 3 then it ends the game.
	*/
	public void connectToServerAsPlayer(final String serverName, final int port){
		try {
			System.out.println(serverName + " : " + port);

			Socket socket = new Socket(serverName, port);
			System.out.println("connected");

			fromServer = new ObjectInputStream(socket.getInputStream());

			toServer = new ObjectOutputStream(socket.getOutputStream());
			}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		new Thread(() -> {
			while(true){
			try{
				if (!waiting){
					Connect4MoveMessage message;
					message = (Connect4MoveMessage)fromServer.readObject();
					// System.out.println("received: " + message.getColumn());
					Platform.runLater(() -> {
						if (message.getColor() == 3){
							Stage stage2 = new Stage();
							stage2.setTitle(message.getColumn() + " won!");
							stage2.showAndWait();
							System.exit(0);
						}
						board[message.getColumn()][message.getRow()] = message.getColor();
						controller.updateBoard(board);
						insertCircle(message.getRow(), message.getColumn(), message.getColor());
					});
					waiting = false;
				}

			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		}).start();		
	}
	/** Runs a thread that represents the computer and the moves it makes.
	*	The computer picks a random column to place its disk.
	*/
	public void runComp(){
		new Thread(() -> {
			while(true){
				while  (waiting || !compFirst){
		            try{
		                Thread.sleep(100);
		            } catch (Exception e){
		                e.printStackTrace();
		            }
				}
				while(compDone){
		            try{
		                Thread.sleep(100);
		            } catch (Exception e){
		                e.printStackTrace();
		            }					
				}
				waiting = true;
				Random rand = new Random();
	    		// int row = rand.nextInt((6 - 0) + 1) + 0;
	    		int column = rand.nextInt((5 - 0) + 1) + 0;
				// Connect4MoveMessage message = new Connect4MoveMessage(row, column, playerNumber);
				Platform.runLater(() -> {
					if(controller.computerTurn() == 0){
						waiting = false;
					}
					else{
						waiting = true;
					}
				});	
			}
		}).start();

	}

	/** @Return a Pane object in the place where x and y point to.
	*/
	public Pane findPoint(int x, int y){
		return objectBoard[x][y];
	}
	/** Places a new circle in the place that x and y point to. Then it sends
	*	a message to the server to send to the other player in order to update its board
	*	with the new piece. Displays a message saying the player won if they put a piece
	*	in a game winning position.
	*/
	public void insertCircle(int x, int y, int color){
		Pane newPane = findPoint(y, x);
		Circle circle;
		if (color == 1){
			circle = new Circle(20, Color.YELLOW);
		}
		else{
			circle = new Circle(20, Color.RED);
		}
		circle.relocate(8,8);
		newPane.getChildren().addAll(circle);
		gridPane.getChildren().remove(objectBoard[y][x]);
		gridPane.add(newPane, y, x);
		Platform.runLater(() -> {
			if(controller.checkWin(playerNumber)){
				Stage stage2 = new Stage();
				stage2.setTitle(playerNumber + " won!");
				compDone = true;
				stage2.showAndWait();
				Connect4MoveMessage message = new Connect4MoveMessage(0,0,3);
				try{
					toServer.writeObject(message);
					System.exit(0);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}
	/** Updates the view by calling methods to place the new disk.
	*	@Param arg a Connect4MoveMessage that tells where to put the new disk
	*/
	public void update(Observable o, Object arg){
		if (waiting){
			waiting = false;;
		}
		Connect4MoveMessage message = (Connect4MoveMessage)arg;
		insertCircle(message.getRow(), message.getColumn(), message.getColor());
		try{
			int color2 = 0;
			if (message.getColor() == 1){
				color2 = 2;
			}
			else if (message.getColor() == 2){
				color2 = 1;
			}
			Connect4MoveMessage message2 = new Connect4MoveMessage(message.getRow(), message.getColumn(), playerNumber);
			toServer.writeObject(message2);
		} catch(Exception e){
			e.printStackTrace();
		}


	}

}

package controller;

//import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;

import java.util.List;

import java.util.Optional;
import java.util.Timer;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Room;
import model.User;
import network.ServerConnector;
import network.ConnectLoadWaitRoom;
import network.ConnectLoadingRoom;

public class ViewManager {
	private static int WIDTH = 982;
	private static int HEIGHT = 552;
	private ServerConnector connect;
    private User user;
	private Pane mainPane;
	private Scene mainScene;
	private Stage mainStage;
	private List<ViewRoom> listroommap=new ArrayList<ViewRoom>();
	//private Timer timer;
	public ViewManager(User user, Stage primaryStage) {
		try {
			this.user=user;
			this.connect = ServerConnector.getConn();
			this.mainStage=primaryStage;
			//this.timer=new Timer();
			initScenes();
			ConnectLoadingRoom newConnect=new ConnectLoadingRoom(connect);
			newConnect.setListRoom();
			for(Room readyRoom:newConnect.getRoom()){
				this.listroommap.add(new ViewRoom(readyRoom));
			}
			createButtonCreate(this.listroommap);
            CreateLoadingbtn(this.listroommap);
			Loadingwaitroom();
			mainStage = new Stage();
			mainStage.setScene(mainScene);
			mainStage.setTitle("BoomIT 7");
			mainStage.getIcons().add(new Image("file:src/images/bomb.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initScenes() {
		try {
			URL url = new File("src/view/Scene_5.fxml").toURI().toURL();
			mainPane = FXMLLoader.load(url);
			mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// tao button 
	private List<ViewRoom> createButtonCreate(List<ViewRoom> listrooMap) {
		AnchorPane createRoomBtn = (AnchorPane) mainScene.lookup("#create-room");
		VBox listRoomFrame=(VBox) mainScene.lookup("#room-list1");
		//List<ViewRoom> listrooMap=new ArrayList<ViewRoom>();
		createRoomBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			int roomnumber=1;
			@Override
			public void handle(MouseEvent event) {
				try {
					ConnectLoadingRoom newConnect=new ConnectLoadingRoom(connect);
					newConnect.setListRoom();
					for(Room readyRoom:newConnect.getRoom()){
						if(hasRoom(new ViewRoom(readyRoom))==null){
						listrooMap.add(new ViewRoom(readyRoom));
						}
					}
				//Create a dialog to input room's name
				TextInputDialog dialog = new TextInputDialog();
			    dialog.setTitle("Input Dialog");
			    dialog.setHeaderText("Enter room's name:");
			    dialog.setContentText("Name: ");
			    Image image = new Image("file:src/images/bomb.png");
			    ImageView imageView = new ImageView(image);
			    dialog.setGraphic(imageView);
			    Optional<String> result = dialog.showAndWait();
			    if (result.isPresent()) {
			      String roomName=result.get();
			      String inputString = "#c004#&"+roomName+"$$";
				  //send and receive data from client to server
				  String[] resul=connect.SendAndRecvData(inputString);
				  
				  if(!resul[1].equals("serr")){
					//add room
					List<User> listUser=new ArrayList<User>();
					
					Room newRoom=new Room(""+(listrooMap.size()+1),roomName,user.getId(),listUser,1);
				    ViewRoom viewRoom=new ViewRoom(newRoom);
                    viewRoom.setViewWaitRoom(mainStage, mainScene);
					listRoomFrame.getChildren().addAll(viewRoom.getJoinbtn());
					VBox.setMargin(viewRoom.getJoinbtn(),new Insets(15, 0, 0, 0));
					listrooMap.add(viewRoom);
					user.setIdScene(1);
					viewRoom.addUser(user);
					
					    
						mainStage.setScene(viewRoom.getViewWaitRoom());
						
						Loadingwaitroom();
						MyTask mytask=new MyTask(connect, listroommap,mainStage);
						mytask.setUser(user);
						Timer timer=new Timer();
						timer.scheduleAtFixedRate(mytask, 0, 1000);
						//StartRoom startroom=new StartRoom(listrooMap, user.getId());
						//startroom.start();
						//Thread.sleep(500);
						
					
				  }

				  for(ViewRoom room:listrooMap){
					ConnectLoadWaitRoom connectLoadWaitRoom=new ConnectLoadWaitRoom(connect);
					connectLoadWaitRoom.setRoom(room.getRoom().getId());
					Room updateRoom=connectLoadWaitRoom.getRoom();
					room=new ViewRoom(updateRoom);
				  }
				  for(ViewRoom room:listrooMap){
					// join room
					room.getJoinbtn().setOnMouseClicked(new EventHandler<MouseEvent>(){
                         
						@Override
						public void handle(MouseEvent arg0) {
							
                       Loadingwaitroom();
					   MyTask mytask=new MyTask(connect, listrooMap,mainStage);
					   mytask.setUser(user);
					   Timer timer=new Timer();
					   timer.scheduleAtFixedRate(mytask, 0, 500);
							/*try {
							/* 	String[] response=connect.SendAndRecvData("#c005#&"+room.getRoom().getId()+"$$", 5500);
								if(response[3].equals("success")){
									System.out.println("asd");
							mainStage.setScene(room.getViewWaitRoom());
							//System.out.println("asd");
							user.setIdScene(room.getRoom().getUser_List().size());
							room.addUser(user);
							
								}
							
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/ 
						}
							
						

							
							
							
					
						
					}
				 );
				}
			
				  
				   
				
				}
			}catch(Exception e){

			}
		}
	});
	return listrooMap;		
	}



	public void CreateLoadingbtn(List<ViewRoom> listroom){
           ImageView loadingbtn=(ImageView) mainScene.lookup("#loading");
		   VBox listRoomFrame=(VBox) mainScene.lookup("#room-list1");

		   
		   loadingbtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
           
			@Override
			public void handle(MouseEvent arg0) {
				
				try {
					
					ConnectLoadingRoom joinRoomConnect=new ConnectLoadingRoom(connect);
					joinRoomConnect.setListRoom();
					List<Room> ListRoom=joinRoomConnect.getRoom();
					for(Room newRoom: ListRoom){
						
						ViewRoom viewRoom=new ViewRoom(newRoom);
						if(hasRoom(viewRoom)!=null){
						  ViewRoom oldRoom=hasRoom(viewRoom);
                          listRoomFrame.getChildren().remove(oldRoom.getJoinbtn());
						  listroom.remove(oldRoom);
						}
						viewRoom.setViewWaitRoom(mainStage, mainScene);
						listRoomFrame.getChildren().addAll(viewRoom.getJoinbtn());
						VBox.setMargin(viewRoom.getJoinbtn(),new Insets(15, 0, 0, 0));
						listroom.add(viewRoom);
						for(ViewRoom room:listroom){
							ConnectLoadWaitRoom connectLoadWaitRoom=new ConnectLoadWaitRoom(connect);
											connectLoadWaitRoom.setRoom(room.getRoom().getId());
											Room updateRoom=connectLoadWaitRoom.getRoom();
											room=new ViewRoom(updateRoom);
						  }
					    for(ViewRoom room:listroom){
							// join room
							room.getJoinbtn().setOnMouseClicked(new EventHandler<MouseEvent>(){
		
								@Override
								public void handle(MouseEvent arg0) {
									
									//if(room.getRoom().getStatus()!=1){
									Loadingwaitroom();
									MyTask mytask=new MyTask(connect, listroommap,mainStage);
									mytask.setUser(user);
									Timer timer=new Timer();
						
						        timer.scheduleAtFixedRate(mytask, 0, 500);
								if(room.getRoom().getStatus()!=1){
									try {
										String[] response=connect.SendAndRecvData("#c005#&"+room.getRoom().getId()+"$$");
										if(response[3].equals("success")){
									
									//User user=new User("id1","Nhat Sang");
									user.setIdScene(room.getRoom().getUser_List().size());
									room.addUser(user);
									mainStage.setScene(room.getViewWaitRoom());
									System.out.println("dsa");
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}else{
									System.out.println("Room started");
								} 
									
									
								}
							
								
							});
						  }
						
					}
					
				} catch (IOException e) {

					e.printStackTrace();
				}
				
			}

		   } );
	}
	public void Loadingwaitroom(){
		for(ViewRoom view:listroommap){
			view.loadingWaitroom(connect,mainStage);
		}
		
	}

	public ViewRoom hasRoom(ViewRoom room){
      for(ViewRoom view:this.listroommap){
		if(view.getRoom().getName().equals(room.getRoom().getName())){
			return view;
		}
	  }
	  return null;
	}
	public void StartGame(){
		for(ViewRoom view:this.listroommap){
			
		  }  
	}
	
	
	
	
	public Stage getMainStage() {
		return mainStage;
	}
}
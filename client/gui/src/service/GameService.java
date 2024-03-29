package service;

import model.common.Direction;
import network.ServerConnector;

import java.util.Scanner;
import java.util.regex.Pattern;

import exception.InvalidResponseException;
import model.bomb.Boom;
import model.character.Character;

public class GameService {
	private static final String NEW_USER = "#c000#";
	private static final String GET_USER_INFOR = "#c001#";
	private static final String CHANGE_USER_NAME = "#c002#";
	private static final String GET_ROOM_LIST = "#c003#";
	private static final String GET_ROOM_DETAIL = "#c007#";
	private static final String ADD_ROOM = "#c004#";
	private static final String JOIN_ROOOM = "#c005#";
	private static final String START_GAME = "#c006#";
	private static final String PLAY_GAME = "#c008#";
	
	private static final String SERVER_ERROR = "#serr#";
	private static final String NEW_USER_RES = "#s000#";
	private static final String GET_USER_INFOR_RES = "#s001#";
	private static final String CHANGE_USER_NAME_RES = "#s002#";
	private static final String GET_ROOM_LIST_RES = "#s003#";
	private static final String GET_ROOM_DETAIL_RES = "#s007#";
	private static final String ADD_ROOM_RES = "#s004#";
	private static final String JOIN_ROOOM_RES = "#s005#";
	private static final String START_GAME_RES = "#s006#";
	private static final String PLAY_GAME_RES = "#s008#";
	
	ServerConnector connector;
	Converter converter;
	
	public GameService() {
		this.connector = ServerConnector.getConn();
		this.converter = new Converter();
	}
	
	public GameResponse sendPlayerAction(int numPlayer, boolean platingBomb, Direction direction) throws InvalidResponseException{
		String req = converter.paramToRequest(PLAY_GAME, platingBomb? "1" : "0", direction.toString());
		String res = connector.sendData(req);

		try {
			String[] resList = res.split("&");
			
			GameResponse gameResponse = new GameResponse();
			
			if (!resList[0].equals(PLAY_GAME_RES)) {
				throw new InvalidResponseException();
			}
			
			setMap(resList[1], gameResponse);
			setPlayerList(resList[2], gameResponse, numPlayer);
			setBoomList(resList[3], gameResponse);
			setTimeLeft(resList[4], gameResponse);
			
			return gameResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidResponseException(res);
		}
	}
	
	public void quitGame() {
		
	}
	
	private void setMap(String str, GameResponse gameResponse) throws Exception {
		try {
			int index = 0;
			for (int i=0; i<17; i++)
				for (int j=0; j<17; j++) {
					if ('0' <= str.charAt(index) && str.charAt(index) <= '9')
						gameResponse.setMap(i, j, str.charAt(index) - '0');
					else if ('a' <= str.charAt(index) && str.charAt(index) <= 'd') {
						gameResponse.setMap(i, j, 'a' - str.charAt(index) - 1);
					}
					index++;
				}
		} catch (Exception e) {
			System.out.println("Error in get map");
			throw new Exception();
		}
	}
	
	private void setPlayerList(String str, GameResponse gameResponse, int numPlayer) throws Exception{
		try {
			String[] cellList = str.split(Pattern.quote("|"));
			
			for (int i=0; i<numPlayer; i++) {
				Character character = new Character (
						Integer.parseInt(cellList[8*i]),
						Integer.parseInt(cellList[8*i + 1]),
						Integer.parseInt(cellList[8*i + 2]),
						Integer.parseInt(cellList[8*i + 3]),
						Double.parseDouble(cellList[8*i + 4]),
						Double.parseDouble(cellList[8*i + 5]),
						Direction.parseDirection(Integer.parseInt(cellList[8*i + 6])),
						Integer.parseInt(cellList[8*i + 7])
					);
				gameResponse.setPlayerInfor(i, character);
			}
		} catch (Exception e) {
			System.out.println("Error in get player list");
			System.out.println("player: "+ str);
			throw new Exception();
		}
	}
	
	private void setBoomList(String str, GameResponse gameResponse) throws Exception {
		try {
			String[] cellList = str.split(Pattern.quote("|"));
			
			int numBoom = cellList.length - cellList.length % 3;
			for (int i=0; i<numBoom; i+= 3) {
				Boom boom = new Boom(
						Integer.parseInt(cellList[i]),
						Integer.parseInt(cellList[i + 1]),
						Integer.parseInt(cellList[i + 2])
					);
				gameResponse.addBoom(boom);
			}
		} catch (Exception e) {
			System.out.println("Error in get bomb list");
			throw new Exception();
		}
	}
	
	private void setTimeLeft(String str, GameResponse gameResponse) throws Exception {
		try {
			String strTime = str.split("[$]")[0];
			gameResponse.setTimeLeft(Double.parseDouble(strTime));
		} catch (Exception e) {
			System.out.println("Error in get time left");
			throw new Exception();
		}
	}
}

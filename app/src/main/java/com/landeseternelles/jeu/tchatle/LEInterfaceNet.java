package com.landeseternelles.jeu.tchatle;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LEInterfaceNet {

	private Socket socket;
	private DataInputStream in;
	private BufferedOutputStream out;
	private Timer heartBeatTimer;
/*	private TabMap callBackCheckInvasion;
	private TabMap callBackCheckPlayers;*/

	//Server => Client
	final static private byte LOG_IN_TYPE = (byte) 140;
	final static private byte HEART_BEAT = 14;
	final static private byte PING_REQUEST = 60;
	final static private byte LOG_IN_OK = (byte) 250;
	final static private byte LOG_IN_NOT_OK = (byte) 251;
	final static private byte NEW_MINUTE = 5;
	final static private byte GET_ACTIVE_SPELL_LIST = 45;
	final static private byte SYNC_CLOCK = 4;
	final static private byte YOU_ARE = 3;
	final static private byte CHANGE_MAP = 7;
	final static private byte HERE_YOUR_INVENTORY = 19;
	final static private byte RAW_TEXT = 0;
	final static private byte ADD_NEW_ACTOR = 1;
	final static private byte ADD_ACTOR_COMMAND = 2;
	final static private byte REMOVE_ACTOR = 6;
	final static private byte ADD_NEW_ENHANCED_ACTOR = 51;
	final static private byte HERE_YOUR_STATS = 18;

	//Client => Server
	final static private byte MOVE_TO = 1;
	final static private byte ATTACK_SOMEONE = 40;
	
//	private TabGame gameTab;
	
	private boolean isInvasionChecking = false;
	private ArrayList<String[]> res_check_order = new ArrayList<String[]>();
	
	private boolean isPlayersChecking = false;
	private ArrayList<String[]> res_check_players_order = new ArrayList<String[]>();
	
	public LEInterfaceNet(String pseudo, String password, String serverAdress, int serverPort) {
		//this.open(serverAdress, serverPort, pseudo, password);
	}

	public void open(String serverAdr, int port, String pseudo, String password) {
		try {
			InetAddress ServeurAdresse = InetAddress.getByName(serverAdr);
			System.out.println("L'adresse du serveur est : " + ServeurAdresse
					+ " ; Port " + port);
			System.out.println("Demande de connexion");
			socket = new Socket(ServeurAdresse, port);

			in = new DataInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());

			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Byte type = 0;
							while (true) {
								try {
									type = in.readByte();
									if (type != null)
										break;
								} catch (EOFException e) {
								}
							}
							int length = in.readUnsignedByte()
									+ in.readUnsignedByte() * 256;
							int dataLength = length - 1;
							byte[] data = new byte[dataLength];
							for (int i = 0; i < dataLength; i++) {
								data[i] = in.readByte();
							}
							reception(type, data);
						} catch (Exception e) {
							e.printStackTrace();
							if ("Socket closed".equals(e.getMessage()))
								break;
						}
					}
				}
			});
			thread.start();

			this.startHeart_Beat();
			this.login(pseudo, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		System.out.println("Close Socket");
        try {
        	if (socket != null && !socket.isClosed()) {
        		in.close();
        		out.close();
        		socket.close();
        		this.stopHeart_Beat();
        	}
        	else
        		System.out.println("Unable to close socket");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/*	public void gameAdd(TabGame gameTab)
	{
		this.gameTab = gameTab;
	}*/
	
	public void sendRawText(String message)
	{
		System.out.println("Message : " + message);
		
		byte[] data = new byte[Math.min(message.length(), 255)];
		for (int i=0; i<data.length ; i++)
		{
			data[i] = (byte)message.charAt(i);
		}
		
		send(RAW_TEXT, data);
	}

	
	public void moveTo(int x, int y) {
		byte[] message = new byte[4];
		message[0] = (byte) (x % 256);
		message[1] = (byte) (x / 256);
		message[2] = (byte) (y % 256);
		message[3] = (byte) (y / 256);
		
		send(MOVE_TO, message);
	}
	
	public void attackActor(int actorId) {
		// TODO Auto-generated method stub
		byte[] message = new byte[2];
		message[0] = (byte) (actorId % 256);
		message[1] = (byte) (actorId / 256);
		
		send(ATTACK_SOMEONE, message);
	}
	
	private void login(String pseudo, String pwd)
	{
		String stringData = pseudo + " " + pwd;
		System.out.println("Login : " + stringData);
		
		byte[] data = new byte[255];
		for (int i=0; i<stringData.length() && i<254 ; i++)
		{
			data[i] = (byte)stringData.charAt(i);
		}
		data[stringData.length()] = 0;
			
		send(LOG_IN_TYPE, data);
	}
	
	private void startHeart_Beat()
	{
		System.out.println("Start Heart Beat");
		TimerTask task = new TimerTask()
		{
			@Override
			public void run() 
			{
				send(HEART_BEAT, null);
			}	
		};
		
		heartBeatTimer = new Timer();
		heartBeatTimer.scheduleAtFixedRate(task, 0, 25000);
	}
	
	private void stopHeart_Beat()
	{
		System.out.println("Stop Heart Beat");
		heartBeatTimer.cancel();
	}
	
	private void send(byte type, byte[] data)
	{
		int dataLength = 0;
		if (data != null)
			dataLength = data.length;
		byte[] message = new byte[dataLength+3];

		message[0] = type;
		message[1] = (byte) ((dataLength+1) % 256);
		message[2] = (byte) ((dataLength+1) / 256);
		
		if(data != null) {
			for (int i=0; i<data.length ; i++)
			{
				message[i+3] = data[i];
			}
		}
		
		System.out.println("Sent : " + new String(message) + " (");
		for(int i = 0; i<message.length;i++)
		{
			System.out.print(";" + message[i]);
		}
		System.out.println(")\n\n");
		
	    try {
	    	out.write(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reception(byte type, byte[] data)
	{
		System.out.println("RECEIVED");
		System.out.println("Type: " + type);
		
		if (type==PING_REQUEST)
		{
			System.out.println("PING_REQUEST");
			send(PING_REQUEST, data);
		} else if (type == LOG_IN_OK)
			System.out.println("LOGIN OK");
		else if (type == LOG_IN_NOT_OK)
			System.out.println("LOGIN fail");
		else if (type == NEW_MINUTE)
			System.out.println("NEW_MINUTE");
		else if (type == GET_ACTIVE_SPELL_LIST)
			System.out.println("GET_ACTIVE_SPELL_LIST");
		else if (type == SYNC_CLOCK)
			System.out.println("SYNC_CLOCK");
		else if (type == YOU_ARE)
		{
			System.out.println("YOU_ARE");
			//gameTab.myId((data[0]&0xFF)+(data[1]&0xFF)*255);
		}
		else if (type == CHANGE_MAP)
		{
			System.out.println("CHANGE_MAP");
			//gameTab.changeMap(new String(data));
		}
		else if (type == HERE_YOUR_INVENTORY)
			System.out.println("HERE_YOUR_INVENTORY");
		else if (type == RAW_TEXT)
		{
			System.out.println("RAW_TEXT");
		}else if (type == ADD_NEW_ACTOR)
		{
			System.out.println("ADD_NEW_ACTOR");
			//gameTab.newActor(data);
		}else if (type == ADD_ACTOR_COMMAND)
		{
			System.out.println("ADD_ACTOR_COMMAND");
			//gameTab.actorCommand(data);
		}
		else if (type == REMOVE_ACTOR)
		{
			System.out.println("REMOVE_ACTOR");
			//gameTab.removeActor(data);
		}
		else if (type == ADD_NEW_ENHANCED_ACTOR)
		{
			System.out.println("ADD_NEW_ENHANCED_ACTOR");
			//gameTab.newEnhancedActor(data);
		}
		else if (type == HERE_YOUR_STATS)
			System.out.println("HERE_YOUR_STATS");
		
		System.out.print("ByteData: ");
		for(int i = 0; i<data.length;i++)
		{
			System.out.print(";" + (int)data[i]);
		}
		System.out.println();
		System.out.print("Textdata: ");
		for(int i = 0; i<data.length;i++)
		{
			System.out.print((char)data[i]);
		}
		System.out.println("\n\n");
	}

}

package com.landeseternelles.jeu.tchatle;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Levleyth on 19/03/2015.
 */
public class LEInterfaceNetv2 {

    private Socket socket;
    BufferedOutputStream out;
    DataInputStream in;
    private Timer heartBeatTimer;

    private int servPort = 0;
    private String servAddr = "";
    private String pseudo = "";
    private String password = "";

    CallbackRecept reception;

    //Client => Server
    final static private byte RAW_TEXT = 0;
    final static private byte LOG_IN_TYPE = (byte) 140;
    final static private byte HEART_BEAT = 14;
    final static private byte MOVE_TO = 1;
    final static private byte ATTACK_SOMEONE = 40;

    public Boolean chatOnlyMod = false;

    public LEInterfaceNetv2()
    {

    }

    public void open(String pseudo, String password, String serverAdress, int serverPort)
    {
        this.pseudo = pseudo;
        this.password = password;
        servAddr = serverAdress;
        servPort = serverPort;

        new Thread(new ClientThread()).start();
    }

    public void connected()
    {
        System.out.println("connected: connected");

        this.startHeart_Beat();
        this.login(pseudo, password);
    }

    public void send(byte type, byte[] data)
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

        //PROV
        try {
            out.write(message);
            //out.println(message);
            //out.write("test");
            out.flush();
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

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(servAddr);

                socket = new Socket(serverAddr, servPort);

                try {
                    out = new BufferedOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connected();

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            String message = null;

            while(true){
                try {
                    byte type = 0;
                    while (true) {
                        try {
                            type = in.readByte();
                            //System.out.println("Val : " + type);
                            //if (type != null)
                                break;
                        } catch (EOFException e) {
                            //System.out.println("Val : Except");
                        }
                    }
                    int length = in.readUnsignedByte()
                            + in.readUnsignedByte() * 256;
                    int dataLength = length - 1;
                    byte[] data = new byte[dataLength];
                    for (int i = 0; i < dataLength; i++) {
                        data[i] = in.readByte();
                    }
                    reception.callbackFunc(type, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    if ("Socket closed".equals(e.getMessage()))
                        break;
                }
            }

        }

    }

}

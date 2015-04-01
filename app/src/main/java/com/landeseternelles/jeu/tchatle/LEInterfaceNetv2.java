package com.landeseternelles.jeu.tchatle;

import android.app.Activity;

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
    final static private byte GET_PLAYER_INFO = 5;
    final static private byte TRADE_WITH = 32;
    final static private byte ACCEPT_TRADE = 33;
    final static private byte REJECT_TRADE = 34;
    final static private byte EXIT_TRADE = 35;
    final static private byte PUT_OBJECT_ON_TRADE = 36;
    final static private byte REMOVE_OBJECT_FROM_TRADE = 37;
    final static private byte LOOK_AT_TRADE_ITEM = 38;
    final static private byte MOVE_INVENTORY_ITEM = 20;
    final static private byte USE_INVENTORY_ITEM = 31;
    final static private byte TOUCH_PLAYER = 28;
    final static private byte RESPOND_TO_NPC = 29;
    final static private byte CAST_SPELL = 39;
    final static private byte DROP_ITEM = 22;
    final static private byte PICK_UP_ITEM = 23;
    final static private byte LOOK_AT_GROUND_ITEM = 24;
    final static private byte INSPECT_BAG = 25;
    final static private byte S_CLOSE_BAG = 26;

    public static Activity ui;

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
        //byte[] message = new byte[2];
        byte[] message = new byte[4];
        message[3] = (byte) (0);
        message[2] = (byte) (0);
        message[0] = (byte) (actorId % 256);
        message[1] = (byte) (actorId / 256);

        send(ATTACK_SOMEONE, message);
    }

    public void getInfosActor(int actorId) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[4];
        message[3] = (byte) (0);
        message[2] = (byte) (0);
        message[0] = (byte) (actorId % 256);
        message[1] = (byte) (actorId / 256);

        send(GET_PLAYER_INFO , message);
    }

    public void tradeActor(int actorId) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[4];
        message[0] = (byte) (actorId % 256);
        message[1] = (byte) ((actorId/256)%256);
        message[2] = (byte) ((actorId/256/256)%256);
        message[3] = (byte) ((actorId/256/256/256));

        send(TRADE_WITH, message);
    }

    public void tradeAccept() {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[1];
        message[0] = (byte) (1);

        send(ACCEPT_TRADE, message);
    }

    public void tradeReject() {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[1];
        message[0] = (byte) (1);

        send(REJECT_TRADE, message);
    }

    public void tradeExit() {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[1];
        message[0] = (byte) (1);

        send(EXIT_TRADE, message);
    }

    public void moveInventoryItem(int posIni, int posEnd) {
        // TODO Auto-generated method stub
        byte[] message = new byte[2];
        message[0] = (byte) (posIni);
        message[1] = (byte) (posEnd);

        send(MOVE_INVENTORY_ITEM, message);
    }

    public void putObjectOnTrade(int where, int pos, int quant) {
        // TODO Auto-generated method stub
        byte[] message = new byte[7];
        message[0] = (byte) (where);
        message[1] = (byte) (pos);
        message[2] = (byte) (pos/256);
        message[3] = (byte) (quant%256);
        message[4] = (byte) ((quant/256)%256);
        message[5] = (byte) ((quant/256/256)%256);
        message[6] = (byte) ((quant/256/256/256));

        send(PUT_OBJECT_ON_TRADE, message);
    }

    public void touchPlayer(int actorId) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[4];
        message[0] = (byte) (actorId % 256);
        message[1] = (byte) ((actorId/256)%256);
        message[2] = (byte) ((actorId/256/256)%256);
        message[3] = (byte) ((actorId/256/256/256));

        send(TOUCH_PLAYER, message);
    }

    public void castSpell(byte[] sigils) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[sigils.length + 1];
        message[0] = (byte) (sigils.length);
        for (int i = 1 ; i <= message[0] ; i++)
            message[i] = (byte) (sigils[i-1]);

        send(CAST_SPELL, message);
    }

    public void inspectBag(int i) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[1];
        message[0] = (byte) (i);

        send(INSPECT_BAG, message);
    }

    public void closeBag() {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[1];
        message[0] = (byte) (1);

        send(S_CLOSE_BAG, message);
    }

    public void dropItem(int pos, int quant) {
        // TODO Auto-generated method stub
        byte[] message = new byte[6];
        message[0] = (byte) (pos);
        message[1] = (byte) (quant%256);
        message[2] = (byte) ((quant/256)%256);
        message[3] = (byte) ((quant/256/256)%256);
        message[4] = (byte) ((quant/256/256/256));

        send(DROP_ITEM, message);
    }

    public void pickUpItem(int pos, int quant) {
        // TODO Auto-generated method stub
        byte[] message = new byte[6];
        message[0] = (byte) (pos);
        message[1] = (byte) (quant % 256);
        message[2] = (byte) ((quant / 256) % 256);
        message[3] = (byte) ((quant / 256 / 256) % 256);
        message[4] = (byte) ((quant / 256 / 256 / 256));

        send(PICK_UP_ITEM , message);
    }

    public void respondNpc(int actorId, int repId) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[4];
        message[0] = (byte) (actorId % 256);
        message[1] = (byte) (actorId / 256);
        message[2] = (byte) (repId % 256);
        message[3] = (byte) (repId / 256);

        send(RESPOND_TO_NPC , message);
    }

    public void useInventoryItem(int pos) {
        // TODO Auto-generated method stub
        //byte[] message = new byte[2];
        byte[] message = new byte[2];
        message[0] = (byte) (pos % 256);
        message[1] = (byte) (pos / 256);

        send(USE_INVENTORY_ITEM , message);
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
                    final byte type2 = type;
                    final byte[] data2 = data;
                    ui.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reception.callbackFunc(type2, data2);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                    if ("Socket closed".equals(e.getMessage()))
                        break;
                }
            }

        }

    }

}

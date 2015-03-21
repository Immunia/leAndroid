package com.landeseternelles.jeu.tchatle;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.landeseternelles.jeu.tchatle.LEInterfaceNetv2;


public class Login extends ActionBarActivity implements OnClickListener {

    private Button loginB = null;
    private LEInterfaceNetv2 leInterface;

    //private int servPort = 3001;
    //private String servAddr = "192.168.56.1";
    private int servPort = 3000;
    private String servAddr = "jeu.landes-eternelles.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginB = (Button)findViewById(R.id.login_button);
        loginB.setOnClickListener(this);

        leInterface = new LEInterfaceNetv2();

        EditText pse = (EditText)findViewById(R.id.login);
        pse.setText("test_interf");
        EditText mdp = (EditText)findViewById(R.id.pwd);
        mdp.setText("azerty");

        /*loginB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leInterface != null)
                    TabMain.this.leInterface.close();
                TabMain.this.leInterface = new LEInterfaceNet(
                    pseudoNet.getText(),
                    password.getText(),
                    serverAddress.getText(),
                    Integer.parseInt(serverPort.getText()));
                TabMain.this.parentWindow.createGame(leInterface);
                leInterface.open(serverAddress.getText(),
                    Integer.parseInt(serverPort.getText()),
                    pseudoNet.getText(),
                    password.getText());
                updateConfigFile();
                TabMain.this.parentWindow.updateLEInterface(leInterface);
            }
        });*/
    }

    @Override
    public void onClick(View v) {

        EditText loginN;
        loginN = (EditText)findViewById(R.id.login);
        EditText pwd;
        pwd = (EditText)findViewById(R.id.pwd);

        /*if (leInterface != null)
            leInterface.close();
        leInterface = new LEInterfaceNet(
                loginN.getText().toString(),
                pwd.getText().toString(),
                "jeu.landes-eternelles.com",
                3000);
        //TabMain.this.parentWindow.createGame(leInterface);
        leInterface.open("jeu.landes-eternelles.com",
                3000,
                loginN.getText().toString(),
                pwd.getText().toString());*/

        Switch port = (Switch)findViewById(R.id.switch1);
        Switch mod = (Switch)findViewById(R.id.switch2);

        System.out.println("port:" + port.isChecked() + " mod:" + mod.isChecked());

        if(port.isChecked())
            servPort = 3001;

        leInterface.open(loginN.getText().toString(), pwd.getText().toString(), servAddr, servPort);

        if(mod.isChecked()) {
            Game.leInterface = leInterface;

            Intent intent = new Intent(Login.this, Game.class);
            startActivity(intent);

        }
        else {
            chatOnly.leInterface = leInterface;
            Intent intent = new Intent(Login.this, chatOnly.class);
            startActivity(intent);
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

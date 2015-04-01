package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
     * Created by Levleyth on 26/03/2015.
     */
    public class DialogWindow extends FrameLayout {

        FrameLayout framelay;

        EditText tit, tex, quit;

        int npcId = 0;
        int posRep = -1;

        Spinner listRep;

        ArrayList<Integer> listIdRep = new ArrayList<Integer>();

        public DialogWindow(Context context, FrameLayout frame) {
            super(context);

            framelay = frame;

            LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            LayoutParams lparamsL = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            tit = new EditText(context);
            tex = new EditText(context);
            quit = new EditText(context);
            tit.setGravity(Gravity.CENTER_HORIZONTAL);
            tex.setGravity(Gravity.CENTER);
            lparams.gravity = Gravity.RIGHT | Gravity.TOP;
            quit.setLayoutParams(lparams);

            framelay.addView(tit);
            framelay.addView(tex);
            framelay.addView(quit);

            tit.setTextColor(Color.WHITE);
            tex.setTextColor(Color.WHITE);
            quit.setTextColor(Color.WHITE);

            tit.setEnabled(false);
            tex.setEnabled(false);
            quit.setFocusable(false);
            quit.setClickable(true);

            quit.setText("Quitter");
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeWindow();
                }
            });

            listRep = new Spinner(getContext());
            String[] state= {"Rep 1", "Rep 2"};
            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_item, state);
            listRep.setAdapter(adapter_state);
            lparamsL.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            listRep.setLayoutParams(lparamsL);
            listRep.setBackgroundColor(Color.WHITE);
            listRep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position != posRep && position != 0) {
                        Game.leInterface.respondNpc(npcId, listIdRep.get(position-1));
                        System.out.println("Respond to NPC : " + npcId + " " + (listIdRep.get(position-1)));
                        posRep = position;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            framelay.addView(listRep);
        }

        public void openWindow ()
        {
            framelay.setVisibility(View.VISIBLE);
        }

        public void closeWindow ()
        {
            framelay.setVisibility(View.GONE);
            tit.setText("");
            tex.setText("");
        }

        public void setInfos (byte[] data)
        {
            int i = 0;
            String name = "";

            while(data[i] != '\0') {
                name += (char) data[i];
                i++;
            }

            tit.setText(name);

            openWindow();
        }

        public void setTex (byte[] data)
        {
            tex.setText("" + new String(data));

            openWindow();
        }

        public void optionList(byte[] data)
        {
            int cursor = 0;
            int leng = 0;

            int i = 0;
            String texte = "";

            ArrayList<String> state = new ArrayList<String>();
            listIdRep = new ArrayList<Integer>();
            state.add("[ Répondre ]");

            while(cursor < data.length)
            {
                leng = (data[cursor]&0xFF)+(data[cursor+1]&0xFF)*256;
                for(i=0;i<leng-1;i++)
                {
                    texte = texte + (char)(data[cursor+i+2]);
                }

                listIdRep.add((data[cursor + 1 + leng + 1]&0xFF)+(data[cursor + 1 + leng + 2]&0xFF)*256+(data[cursor + 1 + leng + 3]&0xFF)*65536+(data[cursor + 1 + leng + 4]&0xFF)*16777216);

                npcId = (data[cursor + 1 + leng + 5]&0xFF)+(data[cursor + 1 + leng + 6]&0xFF)*256;

                state.add(texte);
                texte = "";
                cursor += 1 + leng + 7;
            }
            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_item, state);
            posRep = -1;
            listRep.setAdapter(adapter_state);

        }

}

package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Levleyth on 21/03/2015.
 */
public class BagsPlan extends FrameLayout {

    private static LinkedList<Bag> bagsList = new LinkedList<Bag>();
    private FrameLayout frameLay;

    public BagsPlan(Context context, FrameLayout frame) {
        super(context);

        this.frameLay = frame;
    }

    public void addBag(byte[] data)
    {
        int x = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int y = (data[2]&0xFF)+(data[3]&0xFF)*256;
        final int id = data[4];

        Bag act = new Bag(getContext());
        //PROV
        int posX = (int)(x*MapPlan.UNIT_MAP+MapPlan.posMapX);
        int posY = (MapPlan.sizeBigMap-(int)(y*MapPlan.UNIT_MAP) + MapPlan.posMapY - Game.actorSize);

        System.out.println("New Act : x=" + x + " y=" + y + " posX=" + posX + " posY=" + posY + " Screen=" + Game.heightScreen);
        act.init(posX, posY, Game.actorSize, id, x, y);
        bagsList.add(act);
        frameLay.addView(act);

        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicBag(id);
            }
        });
    }

    public void addBags(byte[] data)
    {
        int nbr = data[0];
        byte[] prov = new byte[5];

        for(int i = 0 ; i < nbr ; i++)
        {
            prov[0]= data[i*5+1];
            prov[1] = data[i*5+2];
            prov[2] = data[i*5+3];
            prov[3] = data[i*5+4];
            prov[4] = data[i*5+5];

            addBag(prov);
        }

    }

    private void clicBag(int id)
    {
        System.out.println("Clic bag:" + id + " mod:" + Game.clicMod);
        switch (Game.clicMod){
            case Game.WALK_MOD:
                System.out.println("Touch bag : " + id + " : Ok");
                Game.leInterface.inspectBag(id);
                break;
            default:
        }
    }



    public void removeBag(byte[] data)
    {
        int actorId = data[0]&0xFF;
        for(int i = 0; i < bagsList.size(); i++)
            if(bagsList.get(i).getId()==actorId)
            {
                frameLay.removeView(bagsList.get(i));
                bagsList.remove(bagsList.get(i));
            }
        invalidate();
    }

    public void removeBags()
    {
        for(int i = 0; i < bagsList.size(); i++)
        {
            frameLay.removeView(bagsList.get(i));
            bagsList.remove(bagsList.get(i));
        }
        invalidate();
    }

    public static Bag getBagById(int actorId)
    {
        for (int i = 0; i < bagsList.size(); i++)
            if (bagsList.get(i).getId() == actorId)
                return bagsList.get(i);
        return null;
    }

    public void centerBagsOnPlayer()
    {
        for(int i = 0; i < bagsList.size(); i++)
        {
            Bag act = bagsList.get(i);
            //act.setLocation(actorList.get(i).getX() + moveX, actorList.get(i).getY() + moveY);
            refreshPosBag(act);
        }
    }

    public void refreshPosBag(Bag act)
    {
        act.setX(Game.perso.getX() + (act.getPosX()-Game.perso.getPosX())*MapPlan.UNIT_MAP);
        act.setY(Game.perso.getY() - (act.getPosY()-Game.perso.getPosY())*MapPlan.UNIT_MAP);
        act.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawBitmap(map, posMapX, posMapY, null);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        // On récupère les dimensions de l'écran
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        // Sa largeur…
        int widthScreen = metrics.widthPixels;
        int heightScreen = metrics.heightPixels;

        // Comme on veut un carré, on n'aura qu'une taille pour les deux axes, la plus petite possible
        int retour = Math.min(widthScreen, widthScreen);

        setMeasuredDimension(retour, retour);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

               /*
     * Récupère l'action effectuée et sa position
     */
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN)
        {
            System.out.println("MotionEvent.ACTION_DOWN");
        }

        return super.onTouchEvent(event);
    }
}

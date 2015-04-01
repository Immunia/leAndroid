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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Levleyth on 21/03/2015.
 */
public class ActorsPlan extends FrameLayout {

    private static LinkedList<Actor2> actorList = new LinkedList<Actor2>();
    private FrameLayout frameLay;

    public ActorsPlan(Context context, FrameLayout frame) {
        super(context);

        this.frameLay = frame;

    }

    public void addActor(final int actorId, int x, int y, int rot, int type, int maxHealth, int curHealth)
    {
        int imgId;

        imgId = getContext().getResources().getIdentifier("monster" + Utility.getNumberLetters(type), "drawable", getContext().getPackageName());
        if(imgId == 0) {
            System.out.println("Image not found");
            imgId = R.drawable.other;
        }

        //PROV
        int posX = (int)(x*MapPlan.UNIT_MAP+MapPlan.posMapX);
        int posY = (MapPlan.sizeBigMap-(int)(y*MapPlan.UNIT_MAP) + MapPlan.posMapY - Game.actorSize);

        System.out.println("New Act : x=" + x + " y=" + y + " posX=" + posX + " posY=" + posY + " Screen=" + Game.heightScreen);
        /*Actor2 act = new Actor2(getContext(), imgId, posX, posY, Game.actorSize, actorId, x, y, maxHealth, curHealth);
        //act.init(imgId, posX, posY, Game.actorSize, actorId, x, y, maxHealth, curHealth);
        actorList.add(act);
        frameLay.addView(act);*/

        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        Actor2 test = new Actor2(getContext(), imgId, posX, posY, Game.actorSize, actorId, x, y, maxHealth, curHealth);
        test.setLayoutParams(lparams);
        System.out.println("New Act : x=" + posX + " y=" + posY + " posX=" + posX + " posY=" + posY + " mapX=" + MapPlan.posMapX + " mapY=" + MapPlan.posMapY + " Screen=" + Game.heightScreen);
        test.setX(Game.perso.getX() + (x-Game.perso.getPosX())*MapPlan.UNIT_MAP);
        test.setY(Game.perso.getY() - (y-Game.perso.getPosY())*MapPlan.UNIT_MAP);
        test.getLayoutParams().height = Game.actorSize;
        test.getLayoutParams().width = Game.actorSize;
        actorList.add(test);
        frameLay.addView(test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicActor(actorId);
            }
        });
    }

    public void addEnhancedActor(final int actorId, int x, int y, int rot, int type, int maxHealth, int curHealth) {
        int imgId = R.drawable.guy;
        //PROV
        int posX = (int) (x * MapPlan.UNIT_MAP + MapPlan.posMapX);
        int posY = (MapPlan.sizeBigMap - (int) (y * MapPlan.UNIT_MAP) + MapPlan.posMapY - Game.actorSize);

        System.out.println("New Act : x=" + x + " y=" + y + " posX=" + posX + " posY=" + posY + " Screen=" + Game.heightScreen);
        /*Actor act = new Actor(getContext());
        act.init(imgId, posX, posY, Game.actorSize, actorId, x, y, maxHealth, curHealth);
        actorList.add(act);
        frameLay.addView(act);*/

        /*act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicActor(actorId);
            }
        });*/

        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        Actor2 test = new Actor2(getContext(), imgId,posX,posY, Game.actorSize,0,x,y,maxHealth,curHealth);
        test.setLayoutParams(lparams);
        System.out.println("New Act : x=" + posX + " y=" + posY + " posX=" + posX + " posY=" + posY + " mapX=" + MapPlan.posMapX + " mapY=" + MapPlan.posMapY + " Screen=" + Game.heightScreen);
        test.setX(Game.perso.getX() + (x-Game.perso.getPosX())*MapPlan.UNIT_MAP);
        test.setY(Game.perso.getY() - (y-Game.perso.getPosY())*MapPlan.UNIT_MAP);
        test.getLayoutParams().height = Game.actorSize;
        test.getLayoutParams().width = Game.actorSize;
        actorList.add(test);
        frameLay.addView(test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicActor(actorId);
            }
        });
    }

    private void clicActor(int actorId)
    {
        System.out.println("Clic actor:" + actorId + " mod:" + Game.clicMod);
        switch (Game.clicMod){
            case Game.WALK_MOD:
                System.out.println("Touch actor : " + actorId + " : Ok");
                Game.leInterface.touchPlayer(actorId);
                break;
            case Game.ATTACK_MOD :
                System.out.println("Attack actor : " + actorId + " : Ok");
                Game.leInterface.attackActor(actorId);
                break;
            case Game.EYE_MOD :
                System.out.println("Infos actor : " + actorId + " : Ok");
                Game.leInterface.getInfosActor(actorId);
                break;
            case Game.TRADE_MOD :
                System.out.println("Trade actor : " + actorId + " : Ok");
                Game.leInterface.tradeActor(actorId);
                break;
            default:
        }
    }



    public void removeActor(byte[] data)
    {
        int actorId = (data[0]&0xFF)+(data[1]&0xFF)*256;
        for(int i = 0; i < actorList.size(); i++)
            if(actorList.get(i).getActorId()==actorId)
            {
                frameLay.removeView(actorList.get(i));
                actorList.remove(actorList.get(i));
            }
        invalidate();
    }

    public void removeActors()
    {
        for(int i = 0; i < actorList.size(); i++)
        {
            frameLay.removeView(actorList.get(i));
            actorList.remove(actorList.get(i));
        }
        invalidate();
    }

    public static Actor2 getActorById(int actorId)
    {
        for (int i = 0; i < actorList.size(); i++)
            if (actorList.get(i).getActorId() == actorId)
                return actorList.get(i);
        return null;
    }

    public void centerActorsOnPlayer()
    {
        for(int i = 0; i < actorList.size(); i++)
        {
            Actor2 act = actorList.get(i);
            //act.setLocation(actorList.get(i).getX() + moveX, actorList.get(i).getY() + moveY);
            refreshPosActor(act);
        }
    }

    public void refreshPosActor(Actor2 act)
    {
        act.setX(Game.perso.getX() + (act.getPosX()-Game.perso.getPosX())*MapPlan.UNIT_MAP);
        act.setY(Game.perso.getY() - (act.getPosY()-Game.perso.getPosY())*MapPlan.UNIT_MAP);
        act.invalidate();
        act.test2.invalidate();
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

    public void actorDamage(byte[] data) {
        int id = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int val = (data[2]&0xFF)+(data[3]&0xFF)*256;
        int hpVal = (data[4]&0xFF)+(data[5]&0xFF)*256;

        if(id != Game.perso.getActorId())
            getActorById(id).setCurHealth(hpVal);

    }

    public void actorHeal(byte[] data) {
        int id = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int val = (data[2]&0xFF)+(data[3]&0xFF)*256;
        int hpVal = (data[4]&0xFF)+(data[5]&0xFF)*256;

        if(id != Game.perso.getActorId())
            getActorById(id).setCurHealth(hpVal);
    }
}

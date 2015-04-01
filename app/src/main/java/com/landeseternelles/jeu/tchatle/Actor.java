package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.widget.ImageView;

public class Actor extends ImageView{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public int offsetX = 0, offsetY = 0, size = 0;

    private int actorId, x, y, rot, type, frame, maxHealth, curHealth, kindOfActor, scale, attachmentType;
    private String actorName;


    public Actor(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
    }

    public void init(int image, int offsetX, int offsetY, int size, int actorId, int x, int y, int maxHealth, int curHealth) {
        this.setImageResource(image);
        this.setActorId(actorId);
        this.x = x;
        this.y = y;
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.offsetX = offsetX-size/2;
        this.offsetY = offsetY-size/2;
        this.size = size;
        setX(offsetX);
        setY(offsetY);
    }

    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(size, size);
    }

    public int getActorId() {
        return actorId;
    }


    public void setActorId(int actorId) {
        this.actorId = actorId;
    }


    public int getPosX() {
        return x;
    }


    public void setPosX(int x) {
        this.x = x;
    }


    public int getPosY() {
        return y;
    }


    public void setPosY(int y) {
        this.y = y;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public void setCurHealth(int curHealth) {
        this.curHealth = curHealth;
    }
}

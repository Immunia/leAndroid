package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Levleyth on 30/03/2015.
 */
public class Actor2 extends FrameLayout {

    public int offsetX = 0, offsetY = 0, size = 0;

    private int actorId, x, y, rot, type, frame, maxHealth, curHealth, kindOfActor, scale, attachmentType;
    private String actorName;

    ImageView test2;
    ProgressBar hp;

    public Actor2(Context context, int image, int offsetX, int offsetY, int size, int actorId, int x, int y, int maxHealth, int curHealth) {
        super(context);

        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        this.setActorId(actorId);
        this.x = x;
        this.y = y;
        this.maxHealth = maxHealth;
        if(maxHealth == 0)
            this.maxHealth = curHealth;
        this.curHealth = curHealth;

        test2 = new ImageView(getContext());
        test2.setImageResource(image);
        this.addView(test2);

        this.offsetX = offsetX-size/2;
        this.offsetY = offsetY-size/2;
        this.size = size;
        setX(offsetX);
        setY(offsetY);

        if(this.maxHealth>0) {
            hp = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            lparams.gravity = Gravity.TOP;
            hp.setLayoutParams(lparams);
            hp.setProgress(100 * curHealth / this.maxHealth);
            this.addView(hp);
            hp.getLayoutParams().width = Game.actorSize;
            Drawable drawable = hp.getProgressDrawable();
            drawable.setColorFilter(new LightingColorFilter(0xFF000000, 0xFF00FF00));
            hp.setY(-15);
        }
        /*this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hp.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        });*/
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
        System.out.println("Health Modif " + curHealth);
        this.curHealth = curHealth;
        if (hp != null) {
            hp.setProgress(100 * curHealth / this.maxHealth);
            this.invalidate();
            hp.invalidate();
        }
    }
}

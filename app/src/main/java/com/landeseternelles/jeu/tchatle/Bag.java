package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.widget.ImageView;

public class Bag extends ImageView{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int id, x, y, size;


    public Bag(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
    }

    public void init(int offsetX, int offsetY, int size, int id, int x, int y) {
        this.setImageResource(R.drawable.bag);
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        setX(offsetX-size/2);
        setY(offsetY-size/2);
    }

    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(size, size);
    }

    public int getId() {
        return id;
    }


    public int getPosX() {
        return x;
    }


    public int getPosY() {
        return y;
    }

}

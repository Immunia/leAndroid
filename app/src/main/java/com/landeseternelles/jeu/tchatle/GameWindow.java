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
import android.widget.TextView;

/**
 * Created by Levleyth on 21/03/2015.
 */
public class GameWindow extends View {

    private Context context;

    public int widthScreen = 400;
    public int posMapX = 0;
    public int posMapY = 0;

    private Bitmap map;
    private static Canvas mapCanvas;

    public GameWindow(Context context) {
        super(context);
        this.context = context;

        widthScreen = Game.imageSize;

        map = prepareBitmap(ResourcesCompat.getDrawable(getResources(), context.getResources().getIdentifier("trepont", "drawable", context.getPackageName()), null), widthScreen*4, widthScreen*4);

        //PROV
        setY(Game.heightScreen-widthScreen-225);

    }



    private static Bitmap prepareBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, width, height);
        mapCanvas = new Canvas(bitmap);
        drawable.draw(mapCanvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(map, posMapX, posMapY, null);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        // On récupère les dimensions de l'écran
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        // Sa largeur…
        widthScreen = metrics.widthPixels;
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

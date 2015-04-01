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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Levleyth on 21/03/2015.
 */
public class MapPlan extends View {

    public int widthScreen = 400;
    public static int posMapX = 0;
    public static int posMapY = 0;
    static int mapId = 0;
    static int mapSize = 192;
    static int sizeBigMap = 400;
    static final int CASE_AFFICHEES = 40;
    static float UNIT_MAP = 1;

    private Bitmap map;
    private static Canvas mapCanvas;

    public MapPlan(Context context) {
        super(context);

        widthScreen = Game.imageSize;

        changeMap("maps/1_trepont.elm");

        //PROV
        //Game.heightScreen-=R.dimen.abc_action_bar_default_height_material;
        //setY(Game.heightScreen - widthScreen - 245);

    }

    public void centerMapOnPlayer()
    {
        //setY(widthScreen - sizeBigMap);
        //invalidate();
        //PROV
        //setX(widthScreen/2 + getRelativeSquarreX(0));
        //setY(widthScreen/2 + getRelativeSquarreY(192));
        posMapX = widthScreen/2 - getRelativeSquarreX(Game.perso.getPosX()) + Game.actorSize/2;
        posMapY = widthScreen/2 + getRelativeSquarreY(Game.perso.getPosY()) + Game.actorSize/2;

        invalidate();
    }

    public int getRelativeSquarreX(int x)
    {
        return (int)(UNIT_MAP*x) + (int)(UNIT_MAP/2);
    }

    public int getRelativeSquarreY(int y)
    {
        return (int)(UNIT_MAP*(y)) - sizeBigMap + (int)(UNIT_MAP/2);
    }

    public void changeMap(String data) {
        System.out.println("TabGame : changeMap");
        Pattern pattern = Pattern.compile("maps/._(.*).elm");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            String mapName = matcher.group(1);
            mapName = mapName.replaceAll("_","");
            System.out.println("Parse : " + mapName);

            if (map != null)
                map.recycle();

            mapSize = 1 + getContext().getResources().getInteger(getContext().getResources().getIdentifier(mapName+"size", "integer", getContext().getPackageName()));
            mapId = getContext().getResources().getInteger(getContext().getResources().getIdentifier(mapName+"id", "integer", getContext().getPackageName()));

            sizeBigMap = widthScreen*mapSize/CASE_AFFICHEES;

            UNIT_MAP = widthScreen / CASE_AFFICHEES;

            prepareBitmap(ResourcesCompat.getDrawable(getResources(), getContext().getResources().getIdentifier(mapName, "drawable", getContext().getPackageName()), null), sizeBigMap, sizeBigMap);

            System.out.println("Map : " + mapName);
        }
    }

    private void prepareBitmap(Drawable drawable, int width, int height) {
        mapCanvas = new Canvas();
        //map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        map = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        drawable.setBounds(0, 0, width, height);
        mapCanvas = new Canvas(map);
        drawable.draw(mapCanvas);
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
        Game.heightScreen = metrics.heightPixels;
        Game.imageSize = widthScreen;

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

        int xPos = (int)((x-posMapX)/UNIT_MAP);
        int yPos = mapSize-(int)((y-posMapY)/UNIT_MAP);

        if (action == MotionEvent.ACTION_DOWN)
        {

            System.out.println("MotionEvent.ACTION_DOWN " + x + " " + y + " Pos: " + xPos + " " + yPos);
            switch (Game.clicMod) {
                case Game.WALK_MOD:
                    Game.leInterface.moveTo(xPos, yPos);
                    break;
                case Game.USE_MOD:

                    break;
                default:
            }
        }

        return super.onTouchEvent(event);
    }
}

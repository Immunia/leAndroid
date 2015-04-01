package com.landeseternelles.jeu.tchatle;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

/**
 * Created by Levleyth on 20/03/2015.
 */
public class Game extends Activity
{
    //Clic mod
    static byte clicMod = 0;
    final static byte WALK_MOD= 0;
    final static byte ATTACK_MOD = 1;
    final static byte USE_MOD = 2;
    final static byte EYE_MOD = 3;
    final static byte TRADE_MOD = 4;

    private boolean isInventoryOpen = false;

    //Server => Client
    final static private byte LOG_IN_TYPE = (byte) 140;
    final static private byte HEART_BEAT = 14;
    final static private byte PING_REQUEST = 60;
    final static private byte LOG_IN_OK = (byte) 250;
    final static private byte LOG_IN_NOT_OK = (byte) 251;
    final static private byte NEW_MINUTE = 5;
    final static private byte GET_ACTIVE_SPELL_LIST = 45;
    final static private byte SYNC_CLOCK = 4;
    final static private byte YOU_ARE = 3;
    final static private byte CHANGE_MAP = 7;
    final static private byte HERE_YOUR_INVENTORY = 19;
    final static private byte GET_NEW_INVENTORY_ITEM = 21;
    final static private byte REMOVE_ITEM_FROM_INVENTORY = 22;
    final static private byte RAW_TEXT = 0;
    final static private byte ADD_NEW_ACTOR = 1;
    final static private byte ADD_ACTOR_COMMAND = 2;
    final static private byte REMOVE_ACTOR = 6;
    final static private byte ADD_NEW_ENHANCED_ACTOR = 51;
    final static private byte HERE_YOUR_STATS = 18;
    final static private byte GET_TRADE_INFO = 34;
    final static private byte GET_TRADE_OBJECT = 35;
    final static private byte GET_TRADE_ACCEPT = 36;
    final static private byte GET_TRADE_REJECT = 37;
    final static private byte GET_TRADE_EXIT = 38;
    final static private byte REMOVE_TRADE_OBJECT = 39;
    final static private byte GET_YOUR_TRADEOBJECTS = 40;
    final static private byte GET_TRADE_PARTNER_NAME = 41;
    final static private byte SEND_SPECIAL_EFFECT = 79;
    final static private byte NPC_TEXT = 30;
    final static private byte NPC_OPTIONS_LIST = 31;
    final static private byte CLOSE_NPC_MENU = 32;
    final static private byte SEND_NPC_INFO = 33;
    final static private byte HERE_YOUR_GROUND_ITEMS = 23;
    final static private byte GET_NEW_GROUND_ITEM = 24;
    final static private byte REMOVE_ITEM_FROM_GROUND = 25;
    final static private byte CLOSE_BAG = 26;
    final static private byte GET_NEW_BAG = 27;
    final static private byte GET_BAGS_LIST = 28;
    final static private byte DESTROY_BAG = 29;
    final static private byte GET_ACTOR_DAMAGE = 47;
    final static private byte GET_ACTOR_HEAL = 48;
    final static private byte SEND_PARTIAL_STAT = 49;

    //Client => Server
    final static private byte MOVE_TO = 1;
    final static private byte ATTACK_SOMEONE = 40;

    final static private int actorRatio = 25; // *unit/10
    static int actorSize = 1;

    static LEInterfaceNetv2 leInterface;

    static MapPlan imageZone;
    static int imageSize;
    static int heightScreen;

    static ActorsPlan actorsZone;
    static BagsPlan bagsZone;
    static BagWindow bagWindow;

    static Inventory inventory;
    static Trade trade;
    static DialogWindow diagWin;

    static Actor2 perso;

    private EditText tchatG;
    private Button sendGameB;

    static int inventoryItemSelected=-1;
    static int quantObj=1;

    int capacityCur = 0;
    int capacityBase = 0;
    int hpCur = 0;
    int hpBase = 0;
    int manaCur = 0;
    int manaBase = 0;
    int foodCur = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LEInterfaceNetv2.ui = this;

        CreatecallbackRecept();

        //setContentView(new GameWindow(this));
        setContentView(R.layout.activity_game);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        imageSize = metrics.widthPixels;
        heightScreen = metrics.heightPixels;
        //PROV

        TextView pse = (TextView)findViewById(R.id.textView5);
        pse.getLayoutParams().height = imageSize;

        tchatG = (EditText)findViewById(R.id.tchatG);

        //RelativeLayout rl = (RelativeLayout)findViewById(R.id.main_frame);
        FrameLayout r2 = (FrameLayout)findViewById(R.id.layoutGame);
        //imageZone.getLayoutParams().height = imageSize;
        imageZone = new MapPlan(this);
        r2.addView(imageZone);

        actorSize = (int)(actorRatio* MapPlan.UNIT_MAP/10);

        FrameLayout lBag = (FrameLayout)findViewById(R.id.layoutBags);
        bagsZone = new BagsPlan(this, lBag);

        FrameLayout lAct = (FrameLayout)findViewById(R.id.layoutActors);
        actorsZone = new ActorsPlan(this, lAct);
        //r2.addView(actorsZone);

        perso = new Actor2(this, R.drawable.guy,imageSize/2,imageSize/2, actorSize,0,31,66,0,0);
        //perso.init(R.drawable.guy,imageSize/2,imageSize/2, actorSize,0,31,66,0,0);
        //perso.setImageResource(R.drawable.guy);
        lAct.addView(perso);
        perso.getLayoutParams().height = Game.actorSize;
        perso.getLayoutParams().width = Game.actorSize;

        centerMapOnPlayer();

        FrameLayout lInv = (FrameLayout)findViewById(R.id.layoutInventory);
        inventory = new Inventory(this, lInv);
        lInv.setVisibility(LinearLayout.GONE);

        FrameLayout lTrade = (FrameLayout)findViewById(R.id.tradeLayout);
        trade = new Trade(this, lTrade);
        lTrade.setVisibility(LinearLayout.GONE);

        sendGameB = (Button)findViewById(R.id.sendGameB);
        sendGameB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText senten = (EditText)findViewById(R.id.sentenceGame);
                System.out.print(senten.getText().toString());
                leInterface.sendRawText(senten.getText().toString());
                senten.setText("");
            }
        });

        selectMod(WALK_MOD);

        ImageView walkMod = (ImageView)findViewById(R.id.walkButton);
        walkMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicMod != WALK_MOD)
                    selectMod(WALK_MOD);
            }
        });
        ImageView attackMod = (ImageView)findViewById(R.id.attackButton);
        attackMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicMod != ATTACK_MOD)
                    selectMod(ATTACK_MOD);
            }
        });
        ImageView useMod = (ImageView)findViewById(R.id.useButton);
        useMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicMod != USE_MOD)
                    selectMod(USE_MOD);
            }
        });
        ImageView eyeMod = (ImageView)findViewById(R.id.eyeButton);
        eyeMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicMod != EYE_MOD)
                    selectMod(EYE_MOD);
            }
        });
        ImageView tradeMod = (ImageView)findViewById(R.id.tradeButton);
        tradeMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicMod != TRADE_MOD)
                    selectMod(TRADE_MOD);
            }
        });

        ImageView inventBut = (ImageView)findViewById(R.id.inventoryButton);
        inventBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicInventory();
            }
        });

        ImageView spell1 = (ImageView)findViewById(R.id.spell1);
        spell1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("Spell 1");
                byte[] ar = {(byte) 3, (byte) 23};
                leInterface.castSpell(ar);
            }
        });
        ImageView spell2 = (ImageView)findViewById(R.id.spell2);
        spell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("Spell 2");
                byte[] ar = {(byte) 9, (byte) 3, (byte) 23};
                leInterface.castSpell(ar);
            }
        });

        FrameLayout fight = (FrameLayout)findViewById(R.id.layoutFight);
        fight.setVisibility(View.GONE);

        FrameLayout diag = (FrameLayout)findViewById(R.id.layoutDialog);
        diagWin = new DialogWindow(this, diag);
        diag.setVisibility(View.GONE);

        FrameLayout lBagW = (FrameLayout)findViewById(R.id.layoutBagWindow);
        bagWindow = new BagWindow(this, lBagW);
        lBagW.setVisibility(LinearLayout.GONE);

        ProgressBar r3 = (ProgressBar)findViewById(R.id.myCapacity);
        r3.getProgressDrawable().setColorFilter(Color.parseColor("#B15006"), PorterDuff.Mode.SRC_IN);
        r3.setRotation(180);
        ProgressBar r4 = (ProgressBar)findViewById(R.id.myHp);
        r4.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        r4.setRotation(180);
        ProgressBar r5 = (ProgressBar)findViewById(R.id.myMana);
        r5.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        r5.setRotation(180);
        ProgressBar r6 = (ProgressBar)findViewById(R.id.myFood);
        r6.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        r6.setRotation(180);
    }

    private void clicInventory()
    {
        ImageView inventBut = (ImageView)findViewById(R.id.inventoryButton);
        if(isInventoryOpen)
        {
            inventBut.setImageResource(R.drawable.bagbutton);
            isInventoryOpen = false;
            inventory.hideInv();
        }
        else
        {
            inventBut.setImageResource(R.drawable.bagbuttonon);
            isInventoryOpen = true;
            inventory.showInv();
        }
    }

    private void selectMod(byte mod)
    {
        System.out.println("Mod : " + clicMod + " " + mod);
        switch (clicMod){
            case WALK_MOD :
                ImageView walkMod = (ImageView)findViewById(R.id.walkButton);
                walkMod.setImageResource(R.drawable.walkbutton);
                break;
            case ATTACK_MOD :
                ImageView attackMod = (ImageView)findViewById(R.id.attackButton);
                attackMod.setImageResource(R.drawable.attackbutton);
                break;
            case USE_MOD :
                ImageView useMod = (ImageView)findViewById(R.id.useButton);
                useMod.setImageResource(R.drawable.usebutton);
                break;
            case EYE_MOD :
                ImageView eyeMod = (ImageView)findViewById(R.id.eyeButton);
                eyeMod.setImageResource(R.drawable.eyebutton);
                break;
            case TRADE_MOD :
                ImageView tradeMod = (ImageView)findViewById(R.id.tradeButton);
                tradeMod.setImageResource(R.drawable.tradebutton);
                break;
        }
        switch (mod){
            case WALK_MOD :
                ImageView walkMod = (ImageView)findViewById(R.id.walkButton);
                walkMod.setImageResource(R.drawable.walkbuttonon);
                break;
            case ATTACK_MOD :
                ImageView attackMod = (ImageView)findViewById(R.id.attackButton);
                attackMod.setImageResource(R.drawable.attackbuttonon);
                break;
            case USE_MOD :
                ImageView useMod = (ImageView)findViewById(R.id.useButton);
                useMod.setImageResource(R.drawable.usebuttonon);
                break;
            case EYE_MOD :
                ImageView eyeMod = (ImageView)findViewById(R.id.eyeButton);
                eyeMod.setImageResource(R.drawable.eyebuttonon);
                break;
            case TRADE_MOD :
                ImageView tradeMod = (ImageView)findViewById(R.id.tradeButton);
                tradeMod.setImageResource(R.drawable.tradebuttonon);
                break;
        }
        clicMod = mod;
    }

    private void centerMapOnPlayer()
    {
        imageZone.centerMapOnPlayer();
        actorsZone.centerActorsOnPlayer();
        bagsZone.centerBagsOnPlayer();
    }

    public void CreatecallbackRecept()
    {
        leInterface.reception = new CallbackRecept() {
            @Override
            public void callbackFunc(byte type, final byte[] data) {
                System.out.println("RECEIVED");
                System.out.println("Type: " + type);

                if (type==PING_REQUEST)
                {
                    System.out.println("PING_REQUEST");
                    leInterface.send(PING_REQUEST, data);
                } else if (type == LOG_IN_OK)
                    System.out.println("LOGIN OK");
                else if (type == LOG_IN_NOT_OK)
                    System.out.println("LOGIN fail");
                else if (type == NEW_MINUTE)
                    System.out.println("NEW_MINUTE");
                else if (type == GET_ACTIVE_SPELL_LIST)
                    System.out.println("GET_ACTIVE_SPELL_LIST");
                else if (type == SYNC_CLOCK)
                    System.out.println("SYNC_CLOCK");
                else if (type == YOU_ARE)
                {
                    System.out.println("YOU_ARE");
                    perso.setActorId((data[0]&0xFF)+(data[1]&0xFF)*255);
                }
                else if (type == CHANGE_MAP)
                {
                    System.out.println("CHANGE_MAP");
                    actorsZone.removeActors();
                    bagsZone.removeBags();
                    imageZone.changeMap(new String(data));
                }
                else if (type == HERE_YOUR_INVENTORY)
                {
                    System.out.println("HERE_YOUR_INVENTORY");
                    inventory.setInv(data);
                }
                else if (type == RAW_TEXT)
                {
                    ///System.out.println("RAW_TEXT");
                    // tchatG.append("\n" + new String(data));
                    tchatText( data );

                }else if (type == ADD_NEW_ACTOR)
                {
                    System.out.println("ADD_NEW_ACTOR");
                    newActor(data);
                }else if (type == ADD_ACTOR_COMMAND)
                {
                    System.out.println("ADD_ACTOR_COMMAND");
                    actorCommand(data);
                }
                else if (type == REMOVE_ACTOR)
                {
                    System.out.println("REMOVE_ACTOR");
                    actorsZone.removeActor(data);
                }
                else if (type == ADD_NEW_ENHANCED_ACTOR)
                {
                    System.out.println("ADD_NEW_ENHANCED_ACTOR");
                    newEnhancedActor(data);
                }
                else if (type == HERE_YOUR_STATS) {
                    System.out.println("HERE_YOUR_STATS");
                    hereYourStats(data);
                }
                else if (type == GET_TRADE_INFO)
                    System.out.println("GET_TRADE_INFO");
                else if (type == GET_TRADE_OBJECT) {
                    System.out.println("GET_TRADE_OBJECT");
                    trade.addItemFromServ(data);
                }
                else if (type == GET_TRADE_ACCEPT) {
                    System.out.println("GET_TRADE_ACCEPT");
                    trade.acceptTrade(data);
                }
                else if (type == GET_TRADE_REJECT) {
                    System.out.println("GET_TRADE_REJECT");
                    trade.rejectTrade(data);
                }
                else if (type == GET_TRADE_EXIT) {
                    System.out.println("GET_TRADE_EXIT");
                    trade.hideTrade();
                }
                else if (type == REMOVE_TRADE_OBJECT) {
                    System.out.println("REMOVE_TRADE_OBJECT");
                    trade.delItemFromServ(data);
                }
                else if (type == GET_YOUR_TRADEOBJECTS)
                    System.out.println("GET_YOUR_TRADEOBJECTS");
                else if (type == GET_TRADE_PARTNER_NAME)
                {
                    System.out.println("GET_TRADE_PARTNER_NAME");
                    trade.showTrade(new String(data));
                }
                else if (type == SEND_SPECIAL_EFFECT)
                {
                    System.out.println("SEND_SPECIAL_EFFECT");
                    addSpellEffect(data);
                }
                else if (type == NPC_TEXT)
                {
                    System.out.println("NPC_TEXT");
                    diagWin.setTex(data);
                }
                else if (type == NPC_OPTIONS_LIST)
                {
                    System.out.println("NPC_OPTIONS_LIST");
                    diagWin.optionList(data);
                }
                else if (type == CLOSE_NPC_MENU)
                {
                    System.out.println("CLOSE_NPC_MENU");
                    diagWin.closeWindow();
                }
                else if (type == SEND_NPC_INFO)
                {
                    System.out.println("SEND_NPC_INFO");
                    diagWin.setInfos(data);
                }
                else if (type == HERE_YOUR_GROUND_ITEMS)
                {
                    System.out.println("HERE_YOUR_GROUND_ITEMS");
                    bagWindow.addItemsFromServ(data);
                    bagWindow.showBagWindow();
                }
                else if (type == GET_NEW_GROUND_ITEM)
                {
                    System.out.println("GET_NEW_GROUND_ITEM");
                    bagWindow.addItemFromServ(data);
                    bagWindow.showBagWindow();
                }
                else if (type == REMOVE_ITEM_FROM_GROUND )
                {
                    System.out.println("REMOVE_ITEM_FROM_GROUND ");
                    bagWindow.delItemFromServ(data);
                }
                else if (type == CLOSE_BAG )
                {
                    System.out.println("CLOSE_BAG ");
                    bagWindow.hideBagWindow();
                }
                else if (type == GET_NEW_BAG )
                {
                    System.out.println("GET_NEW_BAG ");
                    bagsZone.addBag(data);
                }
                else if (type == GET_BAGS_LIST )
                {
                    System.out.println("GET_BAGS_LIST ");
                    bagsZone.addBags(data);
                }
                else if (type == DESTROY_BAG )
                {
                    System.out.println("DESTROY_BAG ");
                    bagsZone.removeBag(data);
                }
                else if (type == SEND_PARTIAL_STAT )
                {
                    System.out.println("SEND_PARTIAL_STAT ");
                    partialStat(data);
                }
                else if (type == GET_NEW_INVENTORY_ITEM )
                {
                    System.out.println("GET_NEW_INVENTORY_ITEM ");
                    inventory.addItemFromServ(data);
                }
                else if (type == REMOVE_ITEM_FROM_INVENTORY )
                {
                    System.out.println("REMOVE_ITEM_FROM_INVENTORY ");
                    inventory.removeItemFromServ(data);
                }
                else if (type == GET_ACTOR_DAMAGE )
                {
                    System.out.println("GET_ACTOR_DAMAGE ");
                    actorsZone.actorDamage(data);
                }
                else if (type == GET_ACTOR_HEAL )
                {
                    System.out.println("GET_ACTOR_HEAL ");
                    actorsZone.actorHeal(data);
                }

                System.out.print("ByteData: ");
                for(int i = 0; i<data.length;i++)
                {
                    System.out.print(";" + (int)data[i]);
                }
                System.out.println();
                System.out.print("Textdata: ");
                for(int i = 0; i<data.length;i++)
                {
                    System.out.print((char)data[i]);
                }
                //System.out.println("reception : callbackFunc raw : " + new String(data));

            }
        };
    }

    private void partialStat(byte[] data) {
        int type = data[0]&0xFF;
        int val = (data[1]&0xFF)+(data[2]&0xFF)*256;
        int act = (data[3]&0xFF)+(data[4]&0xFF)*256;

        switch (type)
        {
            case 40:
                setCapacityCur(val);
                break;
            case 41:
                capacityBase = val;
                setCapacityCur(capacityCur);
                break;
            case 42:
                if(act == Game.perso.getActorId())
                setHpCur(val);
                break;
            case 43:
                if(act == Game.perso.getActorId()) {
                    hpBase = val;
                    setHpCur(hpCur);
                }
                break;
            case 44:
                setManaCur(val);
                break;
            case 45:
                manaBase = val;
                setManaCur(manaCur);
                break;
            case 46:
                setFoodCur(val);
                break;
        }
    }

    private ImageView spellEff;
    private void addSpellEffect(byte[] data) {
        FrameLayout r2 = (FrameLayout)findViewById(R.id.layoutGame);
        spellEff = new ImageView(this);
        spellEff.setImageResource(R.drawable.particle);
        r2.addView(spellEff);
        Handler mHandler = new Handler();
        mHandler.postDelayed(myTask, 1000);
    }

    Runnable myTask = new Runnable() {
        @Override
        public void run() {
            //do work
            FrameLayout r2 = (FrameLayout)findViewById(R.id.layoutGame);
            r2.removeView(spellEff);

        }
    };

    public void hereYourStats(byte[] data)
    {
        int psyCur = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int psyBase = (data[2]&0xFF)+(data[3]&0xFF)*256;
        int cooCur = (data[4]&0xFF)+(data[5]&0xFF)*256;
        int cooBase = (data[6]&0xFF)+(data[7]&0xFF)*256;
        int reaCur = (data[8]&0xFF)+(data[9]&0xFF)*256;
        int reaBase = (data[10]&0xFF)+(data[11]&0xFF)*256;
        int wilCur = (data[12]&0xFF)+(data[13]&0xFF)*256;
        int wilBase = (data[14]&0xFF)+(data[15]&0xFF)*256;
        int insCur = (data[16]&0xFF)+(data[17]&0xFF)*256;
        int insBase = (data[18]&0xFF)+(data[19]&0xFF)*256;
        int vitCur = (data[20]&0xFF)+(data[21]&0xFF)*256;
        int vitBase = (data[22]&0xFF)+(data[23]&0xFF)*256;

        int sangfCur = (data[198]&0xFF)+(data[199]&0xFF)*256;
        int sangfBase = (data[200]&0xFF)+(data[201]&0xFF)*256;

        int defNexusCur = (data[24]&0xFF)+(data[25]&0xFF)*256;
        int defNexusBase = (data[26]&0xFF)+(data[27]&0xFF)*256;
        int necroNexusCur = (data[28]&0xFF)+(data[29]&0xFF)*256;
        int necroNexusBase = (data[30]&0xFF)+(data[31]&0xFF)*256;
        int potionNexusCur = (data[32]&0xFF)+(data[33]&0xFF)*256;
        int potionNexusBase = (data[34]&0xFF)+(data[35]&0xFF)*256;
        int recolteNexusCur = (data[36]&0xFF)+(data[37]&0xFF)*256;
        int recolteNexusBase = (data[38]&0xFF)+(data[39]&0xFF)*256;
        int fabNexusCur = (data[40]&0xFF)+(data[41]&0xFF)*256;
        int fabNexusBase = (data[42]&0xFF)+(data[43]&0xFF)*256;
        int artiNexusCur = (data[44]&0xFF)+(data[45]&0xFF)*256;
        int artiNexusBase = (data[46]&0xFF)+(data[47]&0xFF)*256;

        capacityBase = (data[82]&0xFF)+(data[83]&0xFF)*256;
        hpBase = (data[86]&0xFF)+(data[87]&0xFF)*256;
        manaBase = (data[90]&0xFF)+(data[91]&0xFF)*256;

        setCapacityCur((data[80]&0xFF)+(data[81]&0xFF)*256);
        setHpCur((data[84]&0xFF)+(data[85]&0xFF)*256);
        setManaCur((data[88]&0xFF)+(data[89]&0xFF)*256);
        setFoodCur(data[92]&0xFF);
    }

    private void setCapacityCur(int val)
    {
        System.out.println("Set Capacity : " + val);
        capacityCur = val;
        ProgressBar r2 = (ProgressBar)findViewById(R.id.myCapacity);
        r2.setProgress(100*capacityCur/capacityBase);
    }

    private void setHpCur(int val)
    {
        System.out.println("Set Hp : " + val);
        hpCur = val;
        ProgressBar r2 = (ProgressBar)findViewById(R.id.myHp);
        r2.setProgress(100*hpCur/hpBase);
    }

    private void setManaCur(int val)
    {
        System.out.println("Set Mana : " + val);
        manaCur = val;
        ProgressBar r2 = (ProgressBar)findViewById(R.id.myMana);
        r2.setProgress(100*manaCur/manaBase);
    }

    private void setFoodCur(int val)
    {
        System.out.println("Set Food : " + val);
        foodCur = val;
        ProgressBar r2 = (ProgressBar)findViewById(R.id.myFood);
        r2.setProgress(100*foodCur/45);
    }

    public void newActor(byte[] data)
    {
        int actorId = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int x = (data[2]&0xFF)+(data[3]&0x07)*256;
        int y = (data[4]&0xFF)+(data[5]&0x07)*256;
        int rot = (data[8]&0xFF)+(data[9]&0xFF)*256;
        int type = data[10]&0xFF;
        int frame = data[11]&0xFF;
        int maxHealth = (data[12]&0xFF)+(data[13]&0xFF)*256;
        int curHealth = (data[14]&0xFF)+(data[15]&0xFF)*256;
        int kindOfActor = data[16]&0xFF;
        String actorName = "";
        int i=17;
        for (i=17;i<data.length-1;i++)
            actorName+=(char)data[i];
        //int ACTOR_SCALE_BASE = 1;
        //int scale = (data[i+1]&0xFF);

        System.out.println("New Actor : id=" + actorId + " x=" + x + " y=" + y + " rot=" + rot + " type=" + type + " frame=" + frame + " maxHealth=" + maxHealth + " curHealth=" + curHealth + " kingOfActor=" + kindOfActor + " actorName=" + actorName);

        if(actorId == perso.getActorId())
        {
            perso.setPosX(x);
            perso.setPosY(y);
            //System.out.println("Loca : " + avatar.getLocation());
            centerMapOnPlayer();

            System.out.println("X=" + x + " Y=" + y + "XPer=" + perso.getPosX() + " YPer=" + perso.getPosY());

        }
        else
        {
            actorsZone.addActor(actorId, x, y, rot, type, maxHealth, curHealth);
        }
    }

    public void newEnhancedActor(byte[] data)
    {
        final int actorId = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int x = (data[2]&0xFF)+(data[3]&0x07)*256;
        int y = (data[4]&0xFF)+(data[5]&0x07)*256;
        int rot = (data[8]&0xFF)+(data[9]&0xFF)*256;
        int type = data[10]&0xFF;
        int frame = data[11]&0xFF;

        int maxHealth = (data[23]&0xFF)+(data[24]&0xFF)*256;
        int curHealth = (data[25]&0xFF)+(data[26]&0xFF)*256;

        System.out.println("New Actor : id=" + actorId + " x=" + x + " y=" + y + " rot=" + rot + " type=" + type + " frame=" + frame + " maxHealth=" + maxHealth + " curHealth=" + curHealth);

        if(actorId == perso.getActorId())
        {
            perso.setPosX(x);
            perso.setPosY(y);
            perso.setMaxHealth(maxHealth);
            perso.setCurHealth(curHealth);
            //System.out.println("Loca : " + avatar.getLocation());
            centerMapOnPlayer();

            System.out.println("X=" + x + " Y=" + y + "XPer=" + perso.getPosX() + " YPer=" + perso.getPosY());

        }
        else
        {
            actorsZone.addEnhancedActor(actorId, x, y, rot, type, maxHealth, curHealth);
        }


    }

    public void actorCommand(byte[] data)
    {
        int actorId = (data[0]&0xFF)+(data[1]&0xFF)*256;
        int command = data[2]&0xFF;

        Actor2 act=null;
        if(actorId==perso.getActorId())
            act=perso;
        else
        {
            act = actorsZone.getActorById(actorId);
        }

        if(command >= 20 && command <= 27)
            move(actorId, act, command);
        else if(command >= 3 && command <= 4) {
            if(actorId!=perso.getActorId()) {
                Actor2 ac = ActorsPlan.getActorById(actorId);
                ac.test2.setImageResource(R.drawable.dead);
                ac.setClickable(false);
            }
            else
            {
                FrameLayout fight = (FrameLayout)findViewById(R.id.layoutFight);
                fight.setVisibility(View.GONE);
            }

        }
        else if(command == 18 && actorId==perso.getActorId())
        {
            FrameLayout fight = (FrameLayout)findViewById(R.id.layoutFight);
            fight.setVisibility(View.VISIBLE);
        }
        else if(command == 19 && actorId==perso.getActorId())
        {
            FrameLayout fight = (FrameLayout)findViewById(R.id.layoutFight);
            fight.setVisibility(View.GONE);
        }
        else
            System.out.println("actorCommand : id=" + actorId + " command=" + command);

    }

    public void move(int actorId, Actor2 act, int command)
    {
        int moveX = 0, moveY = 0;
        switch (command)
        {
            case 20:
                //move_n	20
                moveY++;
                break;
            case 21:
                //move_ne	21
                moveX++;
                moveY++;
                break;
            case 22:
                //move_e	22
                moveX++;
                break;
            case 23:
                //move_se	23
                moveX++;
                moveY--;
                break;
            case 24:
                //move_s	24
                moveY--;
                break;
            case 25:
                //move_sw	25
                moveX--;
                moveY--;
                break;
            case 26:
                //move_w	26
                moveX--;
                break;
            case 27:
                //move_nw	27
                moveX--;
                moveY++;
                break;
            default:
			  /*Action*/;
        }

        /*act.setPosX(act.getPosX()+moveX);
        act.setPosY(act.getPosY()+moveY);*/

        if(actorId==perso.getActorId())
        {
            perso.setPosX(perso.getPosX()+moveX);
            perso.setPosY(perso.getPosY()+moveY);
            System.out.println("Perso moveX=" + moveX + " moveY=" + moveY + " X=" + perso.getPosX() + " Y=" + Game.perso.getPosY());
            centerMapOnPlayer();
        }
        else
        {
            if(act != null) {
                act.setPosX(act.getPosX() + moveX);
                act.setPosY(act.getPosY() + moveY);
                actorsZone.refreshPosActor(act);
            }
            else
                System.out.println("Move: Actor Null");
        }
        //System.out.println("moveX=" + moveX + " moveY=" + moveY + " act=" + act.getLocation());

    }

    private String printText( byte[] bufferText ){
        Charset iso = Charset.forName("ISO-8859-1");
        CharsetDecoder isodecoder = iso.newDecoder();
        ByteBuffer bbuf = ByteBuffer.wrap( bufferText );
        CharBuffer cbuf = null;
        try {
            cbuf = isodecoder.decode(bbuf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        String sData = new String( cbuf.array() );

        return sData;
        // System.out.println("reception : callbackFunc raw ////// : " + sData.substring( 2 ) );
    }

    public void tchatText( byte[] bufferText ){

        String sText = printText( bufferText );

        tchatG.append("\n" + sText.substring(2) );
    }
}

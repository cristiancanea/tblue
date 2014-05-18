package tetris.simple;

import tetris.simple.game.Game;
import tetris.simple.graphics.TetrisView;
import tetris.simple.sound.SoundManager;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TetrisActivity extends Activity {
	
	private static final int MENU_START = 1;
	private static final int MENU_RESUME = 2;
	private static final int MENU_PAUSE = 3; 
    private static final int MENU_EXIT = 4;
    
    public static final int SOUND_MOVE = 1;
    public static final int SOUND_ROTATE = 2;
    public static final int SOUND_RAPIDDOWN = 3;
    
    public static int DEFAULT_SCREEN_HEIGHT  = 800;
    public static int DEFAULT_SCREEN_WIDTH   = 480;
    public static int DEFAULT_SCREEN_DENSITY = 233;
    
    public static double SCREEN_HEIGHT = 0;
    public static double SCREEN_WIDTH = 0;
    public static double SCREEN_DENSITY = 0;
    
    public static double SCREEN_HEIGHT_RATIO  = 0;
    public static double SCREEN_WIDTH_RATIO   = 0;
    public static double SCREEN_DENSITY_RATIO = 0;
    
    public static int VIEW_MENU = 0;
    public static int VIEW_GAME = 1;
    public static int VIEW_ABOUT = 2;
    
    public int crt_view = 0;
    	
	private TetrisView mTetrisView;
	//sounds
    public static SoundManager mSoundManager;
    
    public Game my_game;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             
        
        //init /////////////////////////////////////////////////////////////
        
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        // Set the hardware buttons to control the music  
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC); 
        
        //load sounds//////////////////////////////////////////////
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(SOUND_MOVE, R.raw.sound_move);
        mSoundManager.addSound(SOUND_ROTATE, R.raw.sound_rotate);
        mSoundManager.addSound(SOUND_RAPIDDOWN, R.raw.sound_rapiddown);
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);        
        TetrisActivity.SCREEN_WIDTH   = metrics.widthPixels;
        TetrisActivity.SCREEN_HEIGHT  = metrics.heightPixels;
        TetrisActivity.SCREEN_DENSITY = metrics.densityDpi;                
        
        TetrisActivity.SCREEN_WIDTH_RATIO   = TetrisActivity.SCREEN_WIDTH   / TetrisActivity.DEFAULT_SCREEN_WIDTH;
        TetrisActivity.SCREEN_HEIGHT_RATIO  = TetrisActivity.SCREEN_HEIGHT  / TetrisActivity.DEFAULT_SCREEN_HEIGHT;
        TetrisActivity.SCREEN_DENSITY_RATIO = TetrisActivity.SCREEN_DENSITY / TetrisActivity.DEFAULT_SCREEN_DENSITY;
        
        Log.v( "screen_size", TetrisActivity.SCREEN_DENSITY + " zzz " + TetrisActivity.DEFAULT_SCREEN_DENSITY + " zzz " + TetrisActivity.SCREEN_DENSITY_RATIO );        
        
        startNewGame();
        my_game.setMode( Game.NEW );
        
        //start first view /////////////////////////////////////////////////////////////        
        
        viewMenu();                
        
    }
    
    public void viewMenu(){
    	//butoane view meniu
        setContentView(R.layout.main);
        crt_view = VIEW_MENU;
        
        final Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//start new game
            	startNewGame();
            	crt_view = VIEW_GAME;
            }
        });        
        
        final Button btn_resume = (Button) findViewById(R.id.btn_resume);
        if( my_game.getMode() == Game.PAUSE ) { 
        	btn_resume.setVisibility( View.VISIBLE );
        }else{
        	btn_resume.setVisibility( View.INVISIBLE );
        }
        btn_resume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//resume state
            	setContentView(mTetrisView);
            	if( my_game.getMode() == Game.PAUSE ) my_game.resume();
            	crt_view = VIEW_GAME;
            }
        });        
        
        final Button btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//about
            	setContentView(R.layout.about);
            	crt_view = VIEW_ABOUT;
            }
        });
        
        final Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        //////////////////////////////////////////////////////////////////////
    }
    
    public void startNewGame(){
    	//my_game = null;
    	//mTetrisView = null;
		my_game = new Game();
		mTetrisView = new TetrisView(this, my_game);
		setContentView(mTetrisView);
		//add background
		//mTetrisView.setBackgroundResource(R.drawable.blue_fractal2);
    }
    
        
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
        	if( my_game.getMode() == Game.RUNNING ) onPause();
        	//Log.v("screen_size", "MENU PRESSED");
        	viewMenu();      	
        }else if ( keyCode == KeyEvent.KEYCODE_BACK ){
        	//Log.v("screen_size", "key back");
        	if( crt_view != VIEW_MENU ){
		    	if( my_game.getMode() == Game.RUNNING ) onPause();
		    	viewMenu();
        	}else{
        		return super.onKeyDown(keyCode, event);
        	}
        }else{
        	return super.onKeyDown(keyCode, event);
        }
                
        return true;
    }    
    
    protected void onPause() {
        super.onPause();
        //Log.v("screen_size", "Pause");
        my_game.pause();                
    }
    
    protected void onResume(){
    	super.onResume();
    	if( my_game.getMode() == Game.PAUSE ) my_game.resume();
    }
    
    public void onStop(){
    	super.onStop();
    	//Log.v("screen_size", "tetrisActivity onStop");    	
    }
    
    public void onDestroy(){    	    	
    	super.onDestroy();
    	//Log.v("screen_size", "tetrisActivity destroyed");
    }
    
    public void onSaveInstanceState(Bundle outState) {    	
    	super.onSaveInstanceState(outState);
    	//Log.v("screen_size", "tetrisActivity onSaveInstanceState");
    }
}
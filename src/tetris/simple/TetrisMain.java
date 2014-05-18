package tetris.simple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TetrisMain extends Activity {
	public static final int ACTIVITY_GAME = 0;
	public static final int ACTIVITY_ABOUT = 1;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        setContentView(R.layout.main);
        
        final Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//start new game
            	Intent myIntent = new Intent(v.getContext(), TetrisActivity.class);
            	myIntent.putExtra("gamestate", "new");
                startActivityForResult(myIntent, 0);
            }
        });
        
        
        final Button btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_resume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//resume state
            	Intent myIntent = new Intent(v.getContext(), TetrisActivity.class);
            	myIntent.putExtra("gamestate", "resume");
            	startActivityForResult(myIntent, ACTIVITY_GAME);                
            }
        });
        
        
        final Button btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//about
            	Intent myIntent = new Intent(v.getContext(), TetrisAbout.class);
                startActivityForResult(myIntent, 0);                
            }
        });
        
        final Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.exit(0);
            }
        });
	}
	
}

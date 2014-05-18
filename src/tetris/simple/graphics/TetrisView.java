package tetris.simple.graphics;

import java.util.Random;

import tetris.simple.R;
import tetris.simple.TetrisActivity;
import tetris.simple.game.ElementColision;
import tetris.simple.game.Game;
import tetris.simple.game.TElement;
import tetris.simple.game.Table;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint("DrawAllocation")
public class TetrisView extends View implements OnTouchListener {
	Random rn = new Random();
	
    public static final int TABLE_X = 10;
    public static final int TABLE_Y = 10;
    
    public static final int DEFAULT_TABLE_EL_W = 36;
    public static final int DEFAULT_TABLE_EL_H = 36;
    public static int TABLE_EL_W = 0;
    public static int TABLE_EL_H = 0;
        
    public static final int TABLE_W = 8;
    public static final int TABLE_H = 17;   
    
    public static final int TABLE_EL_MARGIN = 1;
    public static final int BTN_MOVE_DELAY = 150;
    public static final int BTN_MOVE_DECREMENTATION = 20;
    
    private static double screen_width=0, screen_height=0;
	
	private int move_delay;
	
	public Game my_game;

    public Table table;
    
    private int next_shape;
    private int next_color;
    private int next_rotate;
    
    public static final int[] a_colors = { 0xffE0FFFF, 0xffAFEEEE, 0xff7FFFD4, 0xff00CED1, 0xff5F9EA0, 0xff4682B4, 0xffB0C4DE, 0xffB0E0E6, 0xff1E90FF, 0xff6495ED, 0xff4169E1 };
    
    private static MyButtonFromSprite btn_left, btn_right, btn_rotate_left, btn_fast_down, btn_start_new, btn_resume;
    private Bitmap pic_crl_buttons;
    
    private Handler mHandler = new Handler();
    private Runnable task_move_left;
    private Runnable task_move_right;
    
    
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {
        public void handleMessage(Message msg) {
            TetrisView.this.update();
            TetrisView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    /////////

    
    
    public TetrisView(Context context, Game my_game ) {
        super(context);
        
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        
        //game////////////////////////////////////////////////
        this.my_game = my_game;
        this.move_delay = this.my_game.calculateMoveDelay();
        
        TetrisView.screen_width  = TetrisActivity.SCREEN_WIDTH;
        TetrisView.screen_height = TetrisActivity.SCREEN_HEIGHT;
        
        TetrisView.TABLE_EL_W = (int) Math.floor( TetrisActivity.SCREEN_WIDTH_RATIO * TetrisView.DEFAULT_TABLE_EL_W );
    	TetrisView.TABLE_EL_H = TetrisView.TABLE_EL_W;
    	
    	Log.v("screen_size", "element: "+ TetrisView.TABLE_EL_W + " : " + TetrisView.TABLE_EL_H + " " + " sr: " + TetrisActivity.SCREEN_WIDTH_RATIO + " : " + TetrisActivity.SCREEN_DENSITY_RATIO );
    	
        table = new Table(TABLE_X, TABLE_Y, TABLE_W, TABLE_H, TABLE_EL_W, TABLE_EL_H, TABLE_EL_MARGIN);
        
        try{            
            int new_shape = rn.nextInt(TElement.a_shape_matrix.length);
            int new_x = rn.nextInt( TABLE_W - Math.max(TElement.a_shape_matrix[new_shape].length, TElement.a_shape_matrix[new_shape][0].length) );
            int new_color = rn.nextInt( TetrisView.a_colors.length );           
            TElement new_el = new TElement(new_x, 0, TetrisView.a_colors[new_color], new_shape );            
            int new_rotate = rn.nextInt(4);//if gt 0 rotate left number of times
            //Log.v("screen_size", "new_roate "+new_rotate);
            if(new_rotate>0){
            	for(int i=0; i<new_rotate; i++ ){
            		new_el.rotateLeft();
            	}
            }       
        	table.addElement( new_el );
        	
        	//next
        	this.next_shape = rn.nextInt(TElement.a_shape_matrix.length);
            this.next_color = rn.nextInt( TetrisView.a_colors.length );
            this.next_rotate = rn.nextInt(4);
            invalidate();
        }catch(ElementColision e){        	
    	   my_game.setMode( Game.LOSE );
        }
        
        pic_crl_buttons = BitmapFactory.decodeResource( context.getResources(), R.drawable.my_ctrl_buttons );
        //Log.v("screen_size", "bitmap " + pic_crl_buttons.getWidth() + " : " + pic_crl_buttons.getHeight() + " : " + pic_crl_buttons.getDensity());
        
        int bw = pic_crl_buttons.getWidth()  / 4;
        int bh = pic_crl_buttons.getHeight() / 2;
        
        //Log.v("tuch_point", "bitmap: "+pic_crl_buttons.getWidth()+"x"+pic_crl_buttons.getHeight());        
	    btn_left        = new MyButtonFromSprite( new Rect(sw(0)  , sh(680), sw(100), sh(780)), new Rect(0,    0, bw,   bh), new Rect(0,    bh, bw,   bh*2) );
	    btn_right       = new MyButtonFromSprite( new Rect(sw(110), sh(680), sw(220), sh(780)), new Rect(bw,   0, bw*2, bh), new Rect(bw,   bh, bw*2, bh*2) );        
	    btn_rotate_left = new MyButtonFromSprite( new Rect(sw(230), sh(680), sw(330), sh(780)), new Rect(bw*2, 0, bw*3, bh), new Rect(bw*2, bh, bw*3, bh*2) );
	    btn_fast_down   = new MyButtonFromSprite( new Rect(sw(350), sh(680), sw(450), sh(780)), new Rect(bw*3, 0, bw*4, bh), new Rect(bw*3, bh, bw*4, bh*2) );
	    btn_start_new   = new MyButtonFromSprite( new Rect(sw(140), sh(360), sw(360), sh(420)), null, null);
	    btn_resume      = new MyButtonFromSprite( new Rect(sw(140), sh(300), sw(360), sh(380)), null, null);
    
        my_game.setMode( Game.RUNNING );
        mRedrawHandler.sleep(move_delay);      
    }
    
    public int sh(int h) {
    	return (int) Math.floor( TetrisActivity.SCREEN_HEIGHT_RATIO * h);
    }
    
    public int sw(int w) {
    	return (int) Math.floor( TetrisActivity.SCREEN_WIDTH_RATIO * w);   	
    }
    
    public void update(){
    	
    	if ( my_game.getMode() == Game.LOSE ){
    		my_game.reset();
    		this.move_delay = this.my_game.calculateMoveDelay();
    	}
    	
    	if ( my_game.getMode() == Game.RUNNING && btn_left.state == 0 && btn_right.state == 0 ) {    		    			    	
	    	try{
	    		table.moveElementDown( table.getLastElement() );
	    	}catch(ElementColision e){
	    		//colision
	    		//Log.v("colision", "on down : make new element "+el.getX() + "x"+el.getY());
	    		int nr_linii_complete = table.verifyCompleteLine();
	    		my_game.incrementScore(nr_linii_complete);
	    		this.move_delay = my_game.calculateMoveDelay();
	    		
	    		//make new element
	    		try{
	    			int new_shape = this.next_shape;
	    			int new_color = this.next_color;
	    			int new_rotate = this.next_rotate;
	    			
	    			this.next_shape = rn.nextInt(TElement.a_shape_matrix.length);//generate new shape
	    			this.next_color = rn.nextInt( TetrisView.a_colors.length );
	    			this.next_rotate = rn.nextInt(4); //if gt 0 rotate left number of times
	    			
	    			int new_x = rn.nextInt( TABLE_W - Math.max(TElement.a_shape_matrix[new_shape].length, TElement.a_shape_matrix[new_shape][0].length) );	    			
	                
	    			TElement new_el = new TElement(new_x, 0, TetrisView.a_colors[new_color], new_shape);
	                if(new_rotate>0){
	                	for(int i=0; i<new_rotate; i++ ){
	                		new_el.rotateLeft();
	                	}
	                }            
	            	table.addElement( new_el );
	            	invalidate();
	            }catch(ElementColision e2){
	            	my_game.setMode( Game.LOSE );
	            }   
	    	}		    	
    	}
    	//delay
    	mRedrawHandler.sleep(move_delay);
    	   
    }
    
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		TElement el;
		
		float tx = event.getX();
		float ty = event.getY();
		//Log.v("tuch_point",  tx + " x " + ty);
		
		if ( my_game.getMode() == Game.RUNNING) { 
			
			//move left
			if( btn_left.rect.contains( (int)tx, (int)ty) ){						
				if( event.getAction() == MotionEvent.ACTION_DOWN && btn_left.state == 0 ){
					//Log.v("button_tuch_point", "button_left DOWN");					
					TetrisActivity.mSoundManager.playSound( TetrisActivity.SOUND_MOVE );				
					task_move_left = new Runnable() {
					   private int nr_times = 0;
					   public void run() {
						    btn_left.state = 1;					    
						    this.nr_times++;
						    try {						
								table.moveElementLeft( table.getLastElement() );																		
								invalidate();//	
								mHandler.postDelayed(this, BTN_MOVE_DELAY - nr_times*BTN_MOVE_DECREMENTATION);
							} catch (ElementColision e) {
								mHandler.removeCallbacks(this);
								btn_left.state = 0;
								invalidate();
							}
						    						
					   }
					};
					
					mHandler.removeCallbacks(task_move_left);	            
		            mHandler.post(task_move_left);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					//Log.v("button_tuch_point", "button_left UP");
					mHandler.removeCallbacks(task_move_left);//stop Handler
					btn_left.state = 0;				
					invalidate();								
				}			
				return true;
			}else if(event.getAction() == MotionEvent.ACTION_UP && btn_left.state == 1){
				mHandler.removeCallbacks(task_move_left);//stop Handler
				btn_left.state = 0;				
				invalidate();				
			}//
			
			//move right		
			if( btn_right.rect.contains( (int)tx, (int)ty) ){			
				if( event.getAction() == MotionEvent.ACTION_DOWN  && btn_right.state == 0){
					//Log.v("button_tuch_point", "button_right DOWN");					
					TetrisActivity.mSoundManager.playSound( TetrisActivity.SOUND_MOVE );				
					task_move_right = new Runnable() {
						private int nr_times = 0;
						public void run() {
						    btn_right.state = 1;
						    this.nr_times++;
					    	try {
								table.moveElementRight( table.getLastElement() );							
								invalidate();
								mHandler.postDelayed(this, BTN_MOVE_DELAY - nr_times*BTN_MOVE_DECREMENTATION);
							} catch (ElementColision e) {
								// TODO Auto-generated catch block
								mHandler.removeCallbacks(this);
								btn_right.state = 0;
								invalidate();
							}					
					   }
					};
					
					mHandler.removeCallbacks(task_move_right);
		            mHandler.post(task_move_right);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					//Log.v("button_tuch_point", "button_left UP");
					mHandler.removeCallbacks(task_move_right);//stop Handler
					btn_right.state = 0;				
					invalidate();								
				}
							
				return true;
			}else if(event.getAction() == MotionEvent.ACTION_UP && btn_right.state == 1){
				//Log.v("button_tuch_point", "button_left UP");
				mHandler.removeCallbacks(task_move_right);//stop Handler
				btn_right.state = 0;				
				invalidate();		
			}//
			
			//rotate left
			if( btn_rotate_left.rect.contains( (int)tx, (int)ty) ){			
				if(event.getAction() == MotionEvent.ACTION_DOWN ){
					btn_rotate_left.state = 1;
					el = table.getLastElement();
					try {
						//try to rotate left
						table.rotateElementLeft(el);
						TetrisActivity.mSoundManager.playSound( TetrisActivity.SOUND_ROTATE );
						invalidate();					
					} catch (ElementColision e) {		
						//		
					}
									
				}else if(event.getAction() == MotionEvent.ACTION_UP){										
					btn_rotate_left.state = 0;
					invalidate();
				}
				return true;
			}else if(event.getAction() == MotionEvent.ACTION_UP && btn_rotate_left.state == 1){
				btn_rotate_left.state = 0;
				invalidate();
			}//
			
			//move fast down
			if( btn_fast_down.rect.contains( (int)tx, (int)ty) ){
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					TetrisActivity.mSoundManager.playSound( TetrisActivity.SOUND_RAPIDDOWN );
					btn_fast_down.state = 1;
					el = table.getLastElement();
					try {
						table.moveElementFastDown(el);					
						invalidate();					
					} catch (ElementColision e) {
						// TODO Auto-generated catch block
					}
					
					//Log.v("button_tuch_point", "btn_fast_down DOWN");
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					btn_fast_down.state = 0;
					invalidate();
				}
				return true;
			}else if(event.getAction() == MotionEvent.ACTION_UP && btn_fast_down.state == 1){		
				btn_fast_down.state = 0;
				invalidate();
			}//
		}
		
		if( my_game.getMode() == Game.LOSE && btn_start_new.rect.contains( (int)tx, (int)ty)){
			table.reset();
	    	my_game.startNew();	    	
		}
		
		if( my_game.getMode() == Game.PAUSE && btn_resume.rect.contains( (int)tx, (int)ty)){
			my_game.resume();
		}
		
		return false;
	}
    
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	//right side
    	Paint mTextPaint_title = new Paint();
    	mTextPaint_title.setAntiAlias(true);    	
    	mTextPaint_title.setTextSize(26);
    	
    	mTextPaint_title.setColor(0xffb3cfdc);
        
        Paint mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(36);
        mTextPaint.setColor(0xffb3cfdc);
    	
        Paint paint_rect= new Paint();
        paint_rect.setColor(0xff090960);
                
    	canvas.drawText("Score:", sw(340), sh(260), mTextPaint_title );
    	canvas.drawText( String.valueOf( my_game.getScore() ), sw(340), sh(300), mTextPaint );
    	
    	canvas.drawRect(sw(340), sh(320), sw(460), sh(325), paint_rect);
    	
    	canvas.drawText("Speed:", sw(340), sh(360), mTextPaint_title );
    	canvas.drawText( String.valueOf( my_game.getSpeed() ), sw(340), sh(400), mTextPaint );
    	
    	canvas.drawRect(sw(340), sh(420), sw(460), sh(425), paint_rect);
    	
    	canvas.drawText("Lines:", sw(340), sh(470), mTextPaint_title );
    	canvas.drawText( String.valueOf( my_game.getTotalLines() ), sw(340), sh(510), mTextPaint );    	
    	
    	/////////////////////////////////////////
    	if(btn_left.state == 0) canvas.drawBitmap( pic_crl_buttons, btn_left.rect_img_normal, btn_left.rect, null );
			else canvas.drawBitmap( pic_crl_buttons, btn_left.rect_img_clicked, btn_left.rect, null );
    	
		if(btn_right.state == 0) canvas.drawBitmap( pic_crl_buttons, btn_right.rect_img_normal, btn_right.rect, null );
			else canvas.drawBitmap( pic_crl_buttons, btn_right.rect_img_clicked, btn_right.rect, null );		
		
		if(btn_rotate_left.state == 0) canvas.drawBitmap( pic_crl_buttons, btn_rotate_left.rect_img_normal, btn_rotate_left.rect, null );
			else canvas.drawBitmap( pic_crl_buttons, btn_rotate_left.rect_img_clicked, btn_rotate_left.rect, null );		
		
		if(btn_fast_down.state == 0) canvas.drawBitmap( pic_crl_buttons, btn_fast_down.rect_img_normal, btn_fast_down.rect, null );
			else canvas.drawBitmap( pic_crl_buttons, btn_fast_down.rect_img_clicked, btn_fast_down.rect, null );
    	
		
		//next element draw (top-right corner) ////////////////////////////////////////////////////////////				
		int[][] shape_matrix_tmp = TElement.a_shape_matrix[this.next_shape];
		int urm_x = sw(340);
		int urm_y = sh(40);
		for(int i=0; i<shape_matrix_tmp.length; i++){
			for(int j=0; j<shape_matrix_tmp[i].length; j++){
				ShapeDrawable tmp_shape_urm = new ShapeDrawable();
				
				if( shape_matrix_tmp[i][j] == 1 ) tmp_shape_urm.getPaint().setColor( TetrisView.a_colors[this.next_color] );
					else tmp_shape_urm.getPaint().setColor( 0xff000000 );
				
				int tmp_shape_x = urm_x + TetrisView.TABLE_EL_MARGIN + TetrisView.TABLE_EL_W*j;
				int tmp_shape_y = urm_y + TetrisView.TABLE_EL_MARGIN + TetrisView.TABLE_EL_H*i;
				int tmp_shape_w = urm_x + TetrisView.TABLE_EL_W*(j+1);
				int tmp_shape_h = urm_y + TetrisView.TABLE_EL_H*(i+1);
				tmp_shape_urm.setBounds(tmp_shape_x, tmp_shape_y, tmp_shape_w, tmp_shape_h );
				tmp_shape_urm.draw(canvas);
			}
		}
		
    	//table draw ////////////////////////////////////////////////////////////		 
    	TElement[][] table_matrix = table.getMatrix();    	
    	int matrix_shape_w = TetrisView.TABLE_X + TetrisView.TABLE_EL_W * TetrisView.TABLE_W + TetrisView.TABLE_EL_MARGIN;
		int matrix_shape_h = TetrisView.TABLE_Y + TetrisView.TABLE_EL_H * TetrisView.TABLE_H + TetrisView.TABLE_EL_MARGIN;
		
		//shape bg table
		ShapeDrawable matrix_shape = new ShapeDrawable();
		matrix_shape.getPaint().setColor( 0xff0000AA );
		matrix_shape.setBounds(TetrisView.TABLE_X, TetrisView.TABLE_Y, matrix_shape_w, matrix_shape_h);
		matrix_shape.draw(canvas);
		
		
		for(int i=0; i<table_matrix.length; i++){
			for(int j=0; j<table_matrix[i].length; j++){
				ShapeDrawable tmp_shape = new ShapeDrawable();
				
				if( table_matrix[i][j] != null ) tmp_shape.getPaint().setColor( table_matrix[i][j].getColor() );
					else tmp_shape.getPaint().setColor( 0xff000000 );
				
				int tmp_shape_x = TetrisView.TABLE_X + TetrisView.TABLE_EL_MARGIN + TetrisView.TABLE_EL_W*j;
				int tmp_shape_y = TetrisView.TABLE_Y + TetrisView.TABLE_EL_MARGIN + TetrisView.TABLE_EL_H*i;
				int tmp_shape_w = TetrisView.TABLE_X + TetrisView.TABLE_EL_W*(j+1);
				int tmp_shape_h = TetrisView.TABLE_Y + TetrisView.TABLE_EL_H*(i+1);
				tmp_shape.setBounds(tmp_shape_x, tmp_shape_y, tmp_shape_w, tmp_shape_h );
				tmp_shape.draw(canvas);
				
			}
		}
		
        if( my_game.getMode() == Game.LOSE ){
        	//Log.v("screen_size", "lose");
        	
        	//transparent bg layer
        	Paint mShapePaint_bg = new Paint();
        	mShapePaint_bg.setColor(0xff000000);
        	mShapePaint_bg.setAlpha(150);
        	canvas.drawRect(0, 0, (int)TetrisView.screen_width, (int)TetrisView.screen_height, mShapePaint_bg);
        	
        	Paint mShapePaint = new Paint();
        	mShapePaint.setColor(0xff000000);        	
        	canvas.drawRect(sw(100), sh(200), sw(400), sh(450), mShapePaint);
    		
    		mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextSize(28);
            mTextPaint.setColor(0xffFF3333);
    		canvas.drawText( "Game over!" , sw(160), sh(250), mTextPaint );
    		canvas.drawText( "Score:" , sw(160), sh(290), mTextPaint );
    		canvas.drawText( ""+my_game.getScore() , sw(160), sh(330), mTextPaint );
    		
    		mShapePaint.setColor(0xff0033AA);
    		canvas.drawRect(btn_start_new.rect, mShapePaint);
    		mTextPaint.setColor(0xffFFFFFF);
    		canvas.drawText( "Start New Game" , sw(150), sh(400), mTextPaint );
        }
        
        if( my_game.getMode() == Game.PAUSE ){
        	//transparent bg layer
        	Paint mShapePaint_bg = new Paint();
        	mShapePaint_bg.setColor(0xff000000);
        	mShapePaint_bg.setAlpha(150);
        	canvas.drawRect(0, 0, (int)TetrisView.screen_width, (int)TetrisView.screen_height, mShapePaint_bg);
        	
        	Paint mShapePaint = new Paint();
        	mShapePaint.setColor(0xff000000);
        	canvas.drawRect(sw(100), sh(200), sw(400), sh(400), mShapePaint);
    		
    		mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextSize(28);
            mTextPaint.setColor(0xff2222FF);
    		canvas.drawText( "Pause", sw(160), sh(250), mTextPaint );
    		
    		mShapePaint.setColor(0xff0033AA);
    		canvas.drawRect(btn_resume.rect, mShapePaint);
    		mTextPaint.setColor(0xffFFFFFF);
    		canvas.drawText( "Resume" , sw(160), sh(350), mTextPaint );
        }
    }

}

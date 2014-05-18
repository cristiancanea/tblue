package tetris.simple.game;

public class Game {
	public static final int NEW = 0;
	public static final int PAUSE = 1;
    public static final int READY = 2;
    public static final int RUNNING = 3;
    public static final int LOSE = 4;	
    
    private int mMode;
	
	private int score;
	private int speed;
	private int total_linii_complete;
	
	public Game(){
		this.score = 0;
		this.speed = 1;
		this.total_linii_complete = 0;
	}
	
	public void incrementScore(int nr_linii_complete){
		this.total_linii_complete = this.total_linii_complete + nr_linii_complete; 
		this.score = this.score + nr_linii_complete*this.speed*10;
		incrementSpeed();
	}
	
	public int getScore(){
		return this.score;
	}
	
	public void reset(){
		this.score = 0;
		this.speed = 1;
		this.total_linii_complete = 0;
	}
	
	public int getTotalLines(){
		return this.total_linii_complete;
	}
	
	public void incrementSpeed(){		
		if (this.total_linii_complete <= 0){
  		  this.speed = 1;
  		}else if ((this.total_linii_complete >= 1) && (this.total_linii_complete <= 90)){
  		  this.speed = 1 + ((this.total_linii_complete - 1) / 10);
  		}else{
  			this.speed = 10;
  		}
	}
	
	public int getSpeed(){
		return this.speed;
	}
	
	public int calculateMoveDelay(){
		return (13 - getSpeed()) * 70;
	}
	
	public void setMode(int newMode) {
        this.mMode = newMode;
    }
	
	public int getMode() {
        return this.mMode;
    }
	
	public void pause(){
		setMode( Game.PAUSE );
	}
	
	public void startNew(){
		reset();
    	setMode( Game.RUNNING);
	}
	
	public void resume(){
		setMode( Game.RUNNING );
	}
	
	
	
}

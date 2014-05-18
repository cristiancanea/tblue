package tetris.simple.game;

import android.util.Log;

public class ElementColision extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String s = "";
	
	public ElementColision( ) {
		this.s = "";
		Log.v("colision", "Colision!");
	}
	
	public ElementColision( String s ) {
		this.s = s;
		Log.v("colision", "Colision! "+s);
	}
	
	public String getColisionCase(){
		return s;
	}

}

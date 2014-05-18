package tetris.simple.graphics;

import android.graphics.Rect;

public class MyButtonFromSprite {
	public int state;
	public Rect rect;
	public Rect rect_img_normal;
	public Rect rect_img_clicked;
	
	public MyButtonFromSprite( Rect rect, Rect rect_img_normal, Rect rect_img_clicked ){
		this.rect = rect;
		this.state = 0;
		this.rect_img_normal  = rect_img_normal;
		this.rect_img_clicked = rect_img_clicked;
	}
}

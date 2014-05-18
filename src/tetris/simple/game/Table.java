package tetris.simple.game;

import java.util.Arrays;

import android.graphics.Canvas;

public class Table {
	private int matrix_x=0, matrix_y=0, matrix_w=10, matrix_h=20, element_w=10, element_h=10, element_margin=2;
	private TElement[][] table_matrix;
	private TElement last_element = null;	
	
	public Table(int matrix_x, int matrix_y, int matrix_w, int matrix_h, int element_w, int element_h, int element_margin){
		this.matrix_x = matrix_x;
		this.matrix_y = matrix_y;
		this.element_w = element_w;
		this.element_h = element_h;
		this.matrix_w = matrix_w;
		this.matrix_h = matrix_h;
		this.element_margin = element_margin;
				
		reset();	
	}
	
	public void reset(){
		//init table matrix
		table_matrix = new TElement[matrix_h][matrix_w];
		for(int i=0; i<table_matrix.length; i++){
			for(int j=0; j<table_matrix[i].length; j++){
				this.table_matrix[i][j] = null;
			}
		}		
	}
	
	public void addElement(TElement el) throws ElementColision{
		try{
			verifyColision(el);
			this.last_element = el;
		}catch ( ElementColision  e) {
			//
			throw new ElementColision();
		}
	}
	
	private void verifyColision( TElement el ) throws ElementColision{
		int x = el.getX();	
		int y = el.getY();
		int[][] crt_matrix = el.getShapeMatrix();
		
		//verify if colision exist on margin
		if(y<0 || y >matrix_h - el.getShapeMatrix().length ) throw new ElementColision();
		if(x < 0) throw new ElementColision("s");
		if(x > matrix_w - el.getShapeMatrix()[0].length) throw new ElementColision("d");
		
		//verify if colision exist with other element
		for(int i=0; i < crt_matrix.length; i++){
			for(int j=0; j< crt_matrix[i].length; j++){				
				try{
					if( crt_matrix[i][j] == 1 && table_matrix[y+i][x+j] != null && table_matrix[y+i][x+j] != el ){
						//colision detected						
						if(j == 0) throw new ElementColision("s");
						if(j == crt_matrix[i].length-1) throw new ElementColision("d");
						throw new ElementColision();//down
					}
				}catch(IndexOutOfBoundsException e){
					//					
				}
			}
		}
		
		
	}
	
	private void addShapeToShapeMatrix( TElement el ) throws ElementColision{
		int x = el.getX();	
		int y = el.getY();
		int[][] crt_matrix = el.getShapeMatrix();
		
		//update matrix
		for(int i=0; i < crt_matrix.length; i++){
			for(int j=0; j< crt_matrix[i].length; j++){
				if(crt_matrix[i][j] > 0) table_matrix[y+i][x+j] = el;
			}
		}			
	}
	
	private void removeShapeMatrix( TElement el ){
		int x = el.getX();
		int y = el.getY();
		int[][] crt_matrix = el.getShapeMatrix();
		
		//update matrix
		for(int i=0; i < crt_matrix.length; i++){
			for(int j=0; j< crt_matrix[i].length; j++){
				if(crt_matrix[i][j] > 0) table_matrix[y+i][x+j] = null;
			}
		}
	}
	
	public TElement getLastElement(){
		return this.last_element;
	}
	
	public void moveElementDown(TElement el) throws ElementColision{
		el.moveDown();
		
		try{			
			verifyColision(el);
			
			el.moveUp();
			removeShapeMatrix(el);
			el.moveDown();
			addShapeToShapeMatrix(el);						
		}catch(ElementColision e){
			//
			el.moveUp();				
			throw new ElementColision();
		}			
		
	}
	
	public void moveElementFastDown(TElement el) throws ElementColision{

		try{			
			for(;;) moveElementDown(el);//do until exception					
		}catch(ElementColision e){
			//				
			throw new ElementColision();
		}			
		
	}
	
	public void moveElementUp(TElement el) throws ElementColision{
		el.moveUp();
		
		try{
			verifyColision(el);
			
			el.moveDown();
			removeShapeMatrix(el);
			el.moveUp();
			addShapeToShapeMatrix(el);				
		}catch(ElementColision e){
			//
			el.moveDown();				
			throw new ElementColision();
		}			
		
	}
	
	public void moveElementLeft(TElement el) throws ElementColision{					
		el.moveLeft();		
		try{			
			verifyColision(el);
			
			el.moveRight();
			removeShapeMatrix(el);
			el.moveLeft();
			addShapeToShapeMatrix(el);				
			
		}catch(ElementColision e){
			//
			el.moveRight();				
			throw new ElementColision();
		}	
	}
	
	public void moveElementRight(TElement el) throws ElementColision{
		//verify for colision					
		el.moveRight();
		
		try{			
			verifyColision(el);
			
			el.moveLeft();
			removeShapeMatrix(el);
			el.moveRight();
			addShapeToShapeMatrix(el);				
			
		}catch(ElementColision e){
			//
			el.moveLeft();				
			throw new ElementColision();
		}	
	}
	
	public void rotateElementLeft(TElement el) throws ElementColision{
		
		int[][] shape_matrix_old = el.getShapeMatrix();
		el.rotateLeft();	
		int[][] shape_matrix_rotated = el.getShapeMatrix();
		
		try{			
			verifyColision(el);			
			el.setShapeMatrix(shape_matrix_old);
			removeShapeMatrix(el);
			el.setShapeMatrix(shape_matrix_rotated);
			addShapeToShapeMatrix(el);				
		}catch(ElementColision e){
			if(e.getColisionCase() == "d"){
				el.setShapeMatrix(shape_matrix_old);
				try{
					moveElementLeft(el);
					rotateElementLeft(el);
				}catch(ElementColision e2){					
					el.setShapeMatrix(shape_matrix_old);			
					//throw new ElementColision("rleft");
				}
			}else{
				el.setShapeMatrix(shape_matrix_old);
			}
		}
		
	}	
	
	public int verifyCompleteLine(){
		int complete_line = 0;		
		
		int nr_linii_complete = 0;
		int[] pos_linii_complete = new int[10];
		
		for( int i=this.table_matrix.length-1; i>0; i-- ){
			complete_line = 0;
			for(int j=0; j<this.table_matrix[i].length; j++ ){
				if(table_matrix[i][j] !=null ) complete_line++;				
			}
			//if complte line found on line i
			if( complete_line == this.table_matrix[i].length ){ 
				//linie ok
				pos_linii_complete[nr_linii_complete] = i;
				nr_linii_complete++;
			}
		}
		
		if( nr_linii_complete > 0 ) {
			//Log.v("screen_size", "Linii: "+Arrays.toString(pos_linii_complete) );
			
			TElement[][] table_matrix_tmp = new TElement[this.matrix_h][this.matrix_w];
			int new_i = this.table_matrix.length-1;
			
			Arrays.sort(pos_linii_complete);//sort the array
			
			for( int i=this.table_matrix.length-1; i>0; i-- ){
				int is_linie = Arrays.binarySearch(pos_linii_complete, i);							
				if( is_linie < 0 ){
					for(int j=0; j<this.table_matrix[i].length; j++ ){					
						try{
							table_matrix_tmp[new_i][j] = table_matrix[i][j];													
						}catch(IndexOutOfBoundsException e){
							//
						}
					}//
					new_i--;
				}else{
					
				}
			}
			this.table_matrix = table_matrix_tmp;
		}
		
		return nr_linii_complete;
	}
	
	public TElement[][] getMatrix(){
		return this.table_matrix;
	}
		
	public void draw(Canvas canvas){
		/*
		int matrix_shape_w = matrix_x + element_w * matrix_w + element_margin;
		int matrix_shape_h = matrix_y + element_h * matrix_h + element_margin;
		ShapeDrawable matrix_shape = new ShapeDrawable();
		matrix_shape.getPaint().setColor( 0xff0000AA );
		matrix_shape.setBounds(this.matrix_x, this.matrix_y, matrix_shape_w, matrix_shape_h);
		//matrix_shape.setAlpha(90);
		matrix_shape.draw(canvas);
		
		
		for(int i=0; i<table_matrix.length; i++){
			for(int j=0; j<table_matrix[i].length; j++){
				ShapeDrawable tmp_shape = new ShapeDrawable();
				
				if( table_matrix[i][j] != null ){
					tmp_shape.getPaint().setColor( table_matrix[i][j].getColor() );
				}else{
					tmp_shape.getPaint().setColor( 0xff000000 );
					//tmp_shape.setAlpha(100);
				}
				
				int tmp_shape_x = matrix_x + element_margin + element_w*j;
				int tmp_shape_y = matrix_y + element_margin + element_h*i;
				int tmp_shape_w = matrix_x + element_w*(j+1);
				int tmp_shape_h = matrix_y + element_h*(i+1);
				tmp_shape.setBounds(tmp_shape_x, tmp_shape_y, tmp_shape_w, tmp_shape_h );
				tmp_shape.draw(canvas);
				
			}
		}
		*/
		
	}
	
}

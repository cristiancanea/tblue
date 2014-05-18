package tetris.simple.game;

public class TElement {
	private int[][] shape_matrix = { {0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0} };
	private int x=0, y=0;
	private int gx=0, gy=0;//G center
	private int color;
	private static int id =0;//de la inceput
	
	public static final int[][][] a_shape_matrix = { 
		{{1,1,1},{0,1,0},{0,1,0}},// T    									
		{{1,0},{1,0},{1,1}},// L
		{{0,1},{0,1},{1,1}},// L intors
		{{1,0},{1,1}},// L mic
		{{1,1,1},{0,1,0}},// 3
		{{1,1,0},{0,1,1}},// z
		{{0,1,1},{1,1,0}},// z intors
		{{1}},//punct
		{{1,1},{1,1}},//patrat mic								
		{{1},{1}},// bara mica
		{{1},{1},{1}},//bara mijlocie
		{{1},{1},{1},{1}},//bara mare			    									
	  };
	
	public TElement(int x, int y, int color, int shape_matrix_id){
		TElement.id = TElement.id + 1;
		this.x = x;
		this.y = y;
		calcGCenter();
		this.color = color;
		this.shape_matrix = a_shape_matrix[shape_matrix_id];		
	}
	
	private void calcGCenter(){
		this.gx = (int)Math.floor( shape_matrix[0].length / 2 );
		this.gy = (int)Math.floor( shape_matrix.length / 2 );
	}
	
	public int getId(){
		return this.id;
	}
	
	public void moveDown(){
		this.y = this.y + 1;
	}
	
	public void moveUp(){
		this.y = this.y - 1;
	}
	
	public void moveLeft(){
		this.x = this.x - 1;
	}
	
	public void moveRight(){
		this.x = this.x + 1;
	}
	
	public void rotateRight(){
		int n = shape_matrix.length;
		int m = shape_matrix[0].length;
		int[][] shape_tmp = new int[m][n];		
		//calcGCenter();
		
		for(int i=0; i<n; i++){
			for(int j=0; j<m; j++){				
				try{
					shape_tmp[j][n-1-i] = shape_matrix[i][j];
				}catch(IndexOutOfBoundsException e){
					//
				}
			}
		}
		
		this.shape_matrix = shape_tmp;
	}
	
	public void rotateLeft(){
		int n = shape_matrix.length;
		int m = shape_matrix[0].length;
		int[][] shape_tmp = new int[m][n];
		calcGCenter();		
		
		for(int i=0; i<n; i++){
			for(int j=0; j<m; j++){
				try{
					shape_tmp[m-1-j][i] = shape_matrix[i][j];
				}catch(IndexOutOfBoundsException e){
					//
				}
			}
		}
		
		this.shape_matrix = shape_tmp;
	}
	
	public int[][] getShapeMatrix(){
		return this.shape_matrix;
	}
	
	public void setShapeMatrix( int[][] shape_matrix ){
		this.shape_matrix = shape_matrix;
	}
	
	public int getX(){ return this.x; }	
	public int getY(){ return this.y; }
	
	public void setXY(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getGX(){ return this.gx; }	
	public int getGY(){ return this.gy; }		
	
	
	public void setColor(int color){
		this.color = color;	
	}
	
	public int getColor(){
		return this.color;
	}
	
}

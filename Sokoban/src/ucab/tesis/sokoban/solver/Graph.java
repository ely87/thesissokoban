package ucab.tesis.sokoban.solver;

import static java.lang.System.out;

import java.lang.reflect.Array;

import ucab.tesis.sokoban.logic.RectExt;

public class Graph {

	private Node Matrix; 
	private int height,width;
	
	public Graph(int h,int w,String lvl) {
		this.height = h;
		this.width = w;
		createMatrix();
		
	}

	private void createMatrix() {
		Matrix = (Node) Array.newInstance(Node.class,width);
		for (int x = 0; x < width; x++) {
			Array.set(Matrix, x, Array.newInstance(Node.class, height));
			for (int y = 0; y < height; y++) {
				Array e = (Array) Array.get(Matrix, x);
				Array.set(e, y, new Node(RectExt.EMPTY,0,0));
			}
		}
		
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++){
            	Array e = (Array) Array.get(Matrix, i);
            	out.format("%d", i, j, Array.get(e, j));

            }		
	}
}

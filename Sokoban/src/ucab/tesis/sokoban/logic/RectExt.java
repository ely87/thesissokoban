package ucab.tesis.sokoban.logic;

import android.graphics.Rect;
import android.graphics.RectF;

public class RectExt extends RectF implements Comparable<RectExt> {

	private int mType;
	public static final int EMPTY=0,WALL=1,WHOLE=2,BOX=3,PLAYER=4,BOX_ON_WHOLE=5;
	
	
	public RectExt(float left ,float top ,float right ,float bottom,int type) {
		super(left, top, right, bottom);
		mType = type;	
	}
	
	public int getType(){
		return mType;
	}
	
	public Rect getRect(){
			return new Rect((int)super.left,(int)super.top,(int)super.right,(int)super.bottom);
	}

	@Override
	public int compareTo(RectExt rect) {
		int type = rect.getType();
		return type < mType ? 1 : type > mType ? -1 : 0;
	}
	
}


    
   
    
    
    
    
    
    
    
    
    
    
    
    
    

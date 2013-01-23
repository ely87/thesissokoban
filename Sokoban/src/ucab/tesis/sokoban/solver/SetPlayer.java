package ucab.tesis.sokoban.solver;

import ucab.tesis.sokoban.logic.RectExt;
import android.view.KeyEvent;

public class SetPlayer {

	//private List<RectExt> mLevel;
	private int mMoveX,mMoveY,mPaddingWidth,mPaddingHeight;
	
	public SetPlayer(int move_x,int move_y,int padding_w,int padding_h){
		//mLevel = level;
		mMoveX = move_x;
		mMoveY = move_y;
		mPaddingWidth = padding_w;
		mPaddingHeight = padding_h;
	}

	public RectExt set(RectExt player,int direction,Position pos){
		
		player.left = pos.x*mMoveX + mPaddingWidth;
		player.top = pos.y*mMoveY + mPaddingHeight;
		player.right = (pos.x+1)*mMoveX +mPaddingWidth; 
		player.bottom = (pos.y+1)*mMoveY + mPaddingHeight;

		switch(direction){
		
		case KeyEvent.KEYCODE_DPAD_DOWN:
			player.top -= mMoveY;
			player.bottom -= mMoveY;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			player.top += mMoveY;
			player.bottom += mMoveY;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			player.left += mMoveX;
			player.right += mMoveX;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			player.left -= mMoveX;
			player.right -= mMoveX;
			break;
		default:
			break;
		}
		
		return player;
	}
	
}

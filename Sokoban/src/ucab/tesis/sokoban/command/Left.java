package ucab.tesis.sokoban.command;

import ucab.tesis.sokoban.logic.RectExt;

public class Left implements SokobanCommand {

	private int mMove;
	private RectExt mRect; 
	
	public Left(int move,RectExt rect) {
		this.mMove = move;
		this.mRect = rect;
	}
	
	@Override
	public RectExt execute() {
		mRect.left -= mMove;
		mRect.right -= mMove;
		return mRect;
	}

	@Override
	public RectExt undo() {
		mRect.left += mMove;
		mRect.right += mMove;
		return mRect;
	}

	@Override
	public RectExt getRect(){
		return mRect;
	}

}

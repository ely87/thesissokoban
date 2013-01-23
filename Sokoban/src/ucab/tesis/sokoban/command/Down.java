package ucab.tesis.sokoban.command;

import ucab.tesis.sokoban.logic.RectExt;

public class Down implements SokobanCommand {

	private int mMove;
	private RectExt mRect; 
	
	public Down(int move,RectExt rect) {
		this.mMove = move;
		this.mRect = rect;
	}
	
	@Override
	public RectExt execute() {
		mRect.top += mMove;
		mRect.bottom += mMove;
		return mRect;
	}

	@Override
	public RectExt undo() {
		mRect.top -= mMove;
		mRect.bottom -= mMove;
		return mRect;
	}

	@Override
	public RectExt getRect(){
		return mRect;
	}
	
}

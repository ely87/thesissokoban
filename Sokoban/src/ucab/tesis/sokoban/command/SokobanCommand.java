package ucab.tesis.sokoban.command;

import ucab.tesis.sokoban.logic.RectExt;

public interface SokobanCommand {
	
	public RectExt execute();
	public RectExt undo();
	public RectExt getRect();
	
}

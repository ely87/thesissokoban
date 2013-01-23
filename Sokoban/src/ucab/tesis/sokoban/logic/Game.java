package ucab.tesis.sokoban.logic;

import java.util.List;

import ucab.tesis.sokoban.solver.Position;
import android.view.KeyEvent;

public class Game {

	public static final int GAME_NOT_START = 0;
	public static final int GAME_START = 1;
	public static final int GAME_WON = 2;

	public static int boxesOnWholes(List<RectExt> boxes, List<RectExt> wholes) {
		int boxOnWhole = 0;
		for (RectExt box : boxes) {
			for (RectExt whole : wholes) {
				if (box.contains(whole)) {
					boxOnWhole++;
					break;
				}
			}
		}

		return boxOnWhole;
	}

	public static int checkGame(int state, List<RectExt> boxes,
			List<RectExt> wholes) {

		int boxOnWhole = Game.boxesOnWholes(boxes, wholes);
		int whole_size = wholes.size();
		if (boxOnWhole == whole_size) {
			return GAME_WON;
			// mientras hasta que se implemente el seleccionador de mundos segun
			// puntaje
			// java.lang.System.exit(-1);
		} else if (state == GAME_NOT_START)
			return GAME_START;
		return GAME_NOT_START;
	}

	public static int getTypeDirection(Position current, Position past) {
			
		if (past != null) {
			if (past.x == current.x - 1)
				return KeyEvent.KEYCODE_DPAD_RIGHT;
			else if (past.x == current.x + 1)
				return KeyEvent.KEYCODE_DPAD_LEFT;
			else if (past.y == current.y - 1)
				return KeyEvent.KEYCODE_DPAD_DOWN;
			else
				return KeyEvent.KEYCODE_DPAD_UP;
		}
		return 0;
	}
	
	public static int getDirection(int dir){
		switch (dir) {
		case 0:
			return KeyEvent.KEYCODE_DPAD_DOWN;
		case 1:
			return KeyEvent.KEYCODE_DPAD_UP;
		case 2:
			return KeyEvent.KEYCODE_DPAD_RIGHT;
		case 3:
			return KeyEvent.KEYCODE_DPAD_LEFT;
		}
		return -1;
	}

}

package ucab.tesis.sokoban.command;

import java.util.ArrayList;
import java.util.List;

import ucab.tesis.sokoban.logic.RectExt;
import android.view.KeyEvent;

public class SokobanInvoker {

	private ArrayList<SokobanCommand> mCommands;
	private int mMoveY, mMoveX, mLevelSize;
	private ArrayList<RectExt> mLevel;
	public int mPushes = 0;
	public String level_id="";
	public String user_id="";

	public SokobanInvoker(int moveY, int moveX, ArrayList<RectExt> level,String user_id, String level_id) {
		mCommands = new ArrayList<SokobanCommand>();
		mMoveY = moveY;
		mMoveX = moveX;
		this.mLevel = level;
		mLevelSize = mLevel.size();
		this.level_id=level_id;
		this.user_id=user_id;
	}

	public SokobanCommand getLastCommand() {
		if (!mCommands.isEmpty())
			return mCommands.get(mCommands.size() - 1);
		return null;
	}

	public int getLastMove(SokobanCommand cmd) {
		try {
			if (cmd instanceof Up)
				return KeyEvent.KEYCODE_DPAD_UP;
			else if (cmd instanceof Down)
				return KeyEvent.KEYCODE_DPAD_DOWN;
			else if (cmd instanceof Left)
				return KeyEvent.KEYCODE_DPAD_LEFT;
			else if (cmd instanceof Right)
				return KeyEvent.KEYCODE_DPAD_RIGHT;
		} catch (Exception e) {
		}

		return -1;
	}

	// Aqui ejecutamos la logica del juego
	public List<RectExt> Cmd(SokobanCommand cmd, RectExt rect) {

		RectExt affected;
		int CommandSize = mCommands.size();

		cmd.execute();
		for (int i = 0; i < mLevelSize; i++) {
			affected = mLevel.get(i);
			// Verificamos que el objecto contenga a otro y que no sea el mismo
			if (cmd.getRect().contains(affected)
					&& !mLevel.get(i).equals(cmd.getRect())) {
				switch (affected.getType()) {
				case RectExt.WALL:
					System.out.println(" Choca con pared !!!!!!!!!!!!!!!!!!!! ");
					cmd.undo();
					return mLevel;
				case RectExt.BOX:
					if (rect.getType() == RectExt.BOX) {
						cmd.undo();
						System.out.println(" Choca con BOX????!!!!!!!!!!!!!!!!!!!! ");
						return mLevel;
					}
					

					// chequeamos que debemos ejecutar para la caja
				//	new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME>>1).startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 2000);
					switch (getLastMove(cmd)) {
					case KeyEvent.KEYCODE_DPAD_UP:
						this.up(affected);
						mPushes++;
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						this.down(affected);
						mPushes++;
						break;
					case KeyEvent.KEYCODE_DPAD_LEFT:
						this.left(affected);
						mPushes++;
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						this.right(affected);
						mPushes++;
						break;
					default:
						break;
					}

					if (CommandSize == mCommands.size()) {
						cmd.undo();
						mPushes--;
						return mLevel;
					}

					break;
				default:
					break;
				}
			}
		}
		mCommands.add(cmd);
		return mLevel;
	}

	public List<RectExt> up(RectExt rect) {
		return Cmd(new Up(mMoveY, rect), rect);
	}

	public List<RectExt> down(RectExt rect) {
		return Cmd(new Down(mMoveY, rect), rect);
	}

	public List<RectExt> left(RectExt rect) {
		return Cmd(new Left(mMoveX, rect), rect);
	}

	public List<RectExt> right(RectExt rect) {
		return Cmd(new Right(mMoveX, rect), rect);
	}

	public RectExt undo() {
		if (!mCommands.isEmpty()) {
			SokobanCommand cmd = mCommands.get(mCommands.size() - 1);
			mCommands.remove(cmd);
			if (!mCommands.isEmpty()
					&& mCommands.get(mCommands.size() - 1).getRect().getType() == RectExt.BOX) {
				this.undo();
				mPushes--;
			}
			return cmd.undo();
		}
		return null;
	}
}

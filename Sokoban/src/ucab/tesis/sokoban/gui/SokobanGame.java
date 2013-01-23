package ucab.tesis.sokoban.gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.sokoban;
import ucab.tesis.sokoban.command.SokobanInvoker;
import ucab.tesis.sokoban.db.LevelColumns;
import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.db.UserColumns;
import ucab.tesis.sokoban.gui.SokobanView.SokobanThread;
import ucab.tesis.sokoban.level.loadLevel;
import ucab.tesis.sokoban.logic.Game;
import ucab.tesis.sokoban.logic.RectExt;
import ucab.tesis.sokoban.solver.Position;
import ucab.tesis.sokoban.user.Info;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class SokobanGame extends Activity {

	private static final int RESTART_ID = Menu.FIRST;
	private static int mScore = 10, mTime = 10;
	private int mMoveX, mMoveY, mWidth, mHeight;
	private List<RectExt> mBoxes, mWholes;
	private int mState;
	private List<RectExt> mLevel;
	private loadLevel mLoad;
	private Thread AutoResolve = null;
	private SokobanView mSV;
	private SokobanThread mSokobanThread;
	private long lvl_id;
	private long userID;
	private boolean mShutDown = false;
	private boolean mSystemDefined;
	private int lastNumOfThreads = 3;
	private static final int MENU_UNDO = 4;
	private static final int MENU_RESUME = 5;
	private static final int MENU_SKIP = 7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // Esto le quita el
		// titulo a la app
		setContentView(R.layout.sokoban_game);
		mSV = (SokobanView) findViewById(R.id.VistaSokoban);
		mSokobanThread = mSV.getThread();
		mSV.setTextView((TextView) findViewById(R.id.text));
		Intent i = getIntent();
		this.mSystemDefined = i.getBooleanExtra(sokoban.SYSTEM_DEFINED,
				Boolean.FALSE);
		lvl_id = 0;
		Log.i("LVL ID", "" + lvl_id);
		try {
			FileInputStream fileSaveState = openFileInput("playedLevel");
			InputStreamReader inputreader = new InputStreamReader(fileSaveState);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line = "";
			line = buffreader.readLine();
			line = buffreader.readLine();
			this.lvl_id = Long.valueOf(line);
			line = buffreader.readLine();
			mSokobanThread.setLevel_numFormat(line);
			line = buffreader.readLine();
			mSokobanThread.setLevel_name(line);
			line = buffreader.readLine();
			mSokobanThread.setLvl_id(this.lvl_id);
			fileSaveState.close();
			String FILENAME = "playedLevel";
			FileOutputStream fos;
			mSokobanThread.setState(6);

			try {
				fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(" - NULL -  \n");
				oos.close();
			} catch (Exception e) {

			}
			mSokobanThread.doStartForPG(String.valueOf(lvl_id));

		} catch (Exception e) {
			this.lvl_id = i.getLongExtra(LevelColumns.LEVEL_ID, 0);
			mSokobanThread.setLevel_numFormat("");
			mSokobanThread.setLevel_name("");
			mSokobanThread.setLvl_id(0);
			System.out.println("IMprimiendo la condicion del IF: "
					+ lastNumOfThreads + ">=" + Thread.activeCount());
			// if (lastNumOfThreads >= mSokobanThread.activeCount()) {
			// we were just launched: set up a new game
			mSokobanThread.setState(3);// READY
			mSokobanThread.setmMode(3);
			lastNumOfThreads = Thread.activeCount();
			Log.w(this.getClass().getName(),
					"///////////////////////////////////SISTEM is null");
			mSokobanThread.doStart();
			// }
			// else {
			// // we are being restored: resume a previous game
			// Log
			// .w(this.getClass().getName(),
			// "+++++++++++++SISTEM is nonnull. MAY BE RETURNING FROM PAUSE STATE");
			// mSokobanThread.restoreState(savedInstanceState);
			//
			// }
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, RESTART_ID, 0, R.string.restart_game);
		menu.add(0, MENU_SKIP, 0, R.string.menu_skip);
		menu.add(0, MENU_UNDO, 0, R.string.menu_undo);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mSokobanThread.getmMode() != 6 || mSokobanThread.getmMode() != 7) {
			switch (item.getItemId()) {
			case MENU_SKIP: {
				mSokobanThread.skipLvl();
			}
				return true;
			case MENU_UNDO:
				mSokobanThread.doKeyDown(KeyEvent.KEYCODE_DPAD_CENTER,
						new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_DPAD_CENTER));
				return true;
			case MENU_RESUME:
				mSokobanThread.unpause();
				return true;
			case RESTART_ID: {
				mSokobanThread.setState(3);// READY
				mSokobanThread.setmMode(3);
				mSokobanThread.restart();

				return true;
			}

			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	protected boolean checkGame(Integer pushes, List<RectExt> boxes,
			List<RectExt> wholes, Context context, String levelNumFormat,
			String levelName, String levelID, String userID) {
		boolean state = false;
		this.mBoxes = boxes;
		this.mWholes = wholes;
		mState = Game.checkGame(mState, mBoxes, mWholes);
		if (mState == Game.GAME_WON) {
			SokobanDbAdapter mSA = new SokobanDbAdapter(context);

			try {

				System.out
						.println("LEYENDO EL USUARIO PARA CREAR SU TABLA!!! .......>>>>>"
								+ userID);
				mSA.open();
				if (mSA.createUserLevel(levelID, userID, " ") == -1) {
					System.out.println(System.err);
				} else {
					System.out.println("TODO BIEN, Ya gano.");
					mSA.close();
				}
				mSA.open();
				if (mSA.createLevel(levelNumFormat, levelName, pushes, levelID,
						userID) == -1) {
					System.out.println(System.err);
				} else {
					System.out.println("TODO BIEN, Ya gano.");
					mSA.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			state = true;
		}
		return state;
	}

	protected void winGame(Integer pushes) {
		// llamar a algo que diga que ganaste con tu puntaje respectivo.
		Intent i = new Intent(this, Info.class);
		i.putExtra(UserColumns.SCORE, "" + mScore);
		i.putExtra(UserColumns.TIME, "" + mTime);
		i.putExtra(UserColumns.BOX_PUSHES, pushes.toString());
		startActivity(i);
		this.finish();

	}

	protected void loadLvl(int X, int Y, int H, int W) {
		mMoveX = X;
		mMoveY = Y;
		mHeight = H;
		mWidth = W;
		loadLvl();

		if (mSystemDefined) {
			// resolveLvl();
		}

	}

	private void loadLvl() {
		if (mMoveX != 0 && mMoveY != 0 && mHeight != 0 && mWidth != 0) {

			mLoad = new loadLevel(mMoveX, mMoveY, mHeight, mWidth);
			mLevel = mLoad.load(lvl_id, "");
			mSokobanThread.setmLevel(mLevel);

			mBoxes = new ArrayList<RectExt>();
			mWholes = new ArrayList<RectExt>();

			for (RectExt item : mLevel) {
				if (item.getType() == RectExt.BOX)
					mBoxes.add(item);
				else if (item.getType() == RectExt.WHOLE)
					mWholes.add(item);
			}
		}

		mSokobanThread.setmSi(new SokobanInvoker(mMoveY, mMoveX,
				(ArrayList<RectExt>) mLevel, String.valueOf(userID), String
						.valueOf(lvl_id)));
	}

	protected List<RectExt> getLevel() {
		return getmLevel();
	}

	protected Position getPlayerPosition(Position pos, int dir) {
		Log.i("pos", pos.toString());
		Log.i("posdir", "" + dir);
		switch (dir) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			pos.y -= 1;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			pos.y += 1;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			pos.x += 1;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			pos.x -= 1;
			break;
		}
		Log.i("pos", pos.toString());
		return pos;
	}

	// Resuelvo el sokoban automaticamente si es posible.
	// Esto debe ser solo para admin, los jugadores jamas deberian poseer esta
	// opcion

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_MENU:
	// return true;
	// case KeyEvent.KEYCODE_DPAD_UP:
	// return true;
	// case KeyEvent.KEYCODE_DPAD_DOWN:
	// return true;
	// case KeyEvent.KEYCODE_DPAD_LEFT:
	// return true;
	// case KeyEvent.KEYCODE_DPAD_RIGHT:
	// return true;
	// case KeyEvent.KEYCODE_DPAD_CENTER:
	// return true;
	// case KeyEvent.KEYCODE_BACK:
	// return true;
	//
	// default:
	// break;
	// }
	//
	// return super.onKeyDown(keyCode, event);
	// }

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mSV.getThread().pause(); // pause game when Activity pauses
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSV.getThread().unpause(); // pause game when Activity pauses

	}

	/**
	 * Notification that something is about to happen, to give the Activity a
	 * chance to save state.
	 * 
	 * @param outState
	 *            a Bundle into which this Activity should save its state
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// just have the View's thread save its state into our Bundle
		super.onSaveInstanceState(outState);
		mSokobanThread.saveState(outState);
		Log.w(this.getClass().getName(), "SIS called");
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		// just have the View's thread save its state into our Bundle
		super.onRestoreInstanceState(outState);
		mSokobanThread.restoreState(outState);
		Log.w(this.getClass().getName(), "SIS called");
	}

	public void setmLevel(List<RectExt> mLevel) {
		this.mLevel = mLevel;
	}

	public List<RectExt> getmLevel() {
		return mLevel;
	}

	public String readFilePlayer() {
		String userID = "";
		try {
			FileInputStream fileSaveState = openFileInput("player");
			InputStreamReader inputreader = new InputStreamReader(fileSaveState);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line = "";
			line = buffreader.readLine();
			line = buffreader.readLine();
			userID = line;
			fileSaveState.close();
		} catch (Exception e) {

		}
		return userID;
	}

}

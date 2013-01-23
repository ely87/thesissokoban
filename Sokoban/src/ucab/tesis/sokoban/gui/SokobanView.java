package ucab.tesis.sokoban.gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.command.SokobanInvoker;
import ucab.tesis.sokoban.db.LevelColumns;
import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.level.loadLevel;
import ucab.tesis.sokoban.logic.RectExt;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback {
	class SokobanThread extends Thread {
		/*
		 * State-tracking constants
		 */
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;
		public static final int STATE_REPLAY = 6;
		public static final int STATE_REPLAY_WON = 7;
		public final Canvas surface = new Canvas();
		private long userID;
		private Bitmap mBackgroundImage;
		private int mCanvasHeight = 1;
		private int mCanvasWidth = 1;
		private int mDifficulty;
		private double skillPoints;
		private String level_name;
		private String level_numFormat;
		/** score */
		private double score;

		/** Message handler used by thread to interact with TextView */
		private Handler mHandler;

		private Drawable mPlayer, mBox, mWhole, mWall, mBoxOnWhole, mFloor,
				mPlayerUp, mPlayerRight, mPlayerDown, mPlayerLeft, upB, downB,
				leftB, rightB;

		/** Used to figure out elapsed time between frames */
		private long mLastTime;

		/** Paint to draw the lines on screen. */
		private Paint mLinePaint;

		/** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
		private int mMode;

		/** Indicate whether the surface has been created & is ready to draw */
		private boolean mRun = false;
		private RectF mScratchRect;
		private SurfaceHolder mSurfaceHolder;
		private RectExt mPlayerRect;
		private SokobanGame mSokobanGame = new SokobanGame();
		private List<RectExt> mLevel;
		private SokobanInvoker mSi;
		private int mMoveX = 0;
		private int mMoveY = 0;
		private int mWidth = 0;
		private int mHeight = 0;
		private List<RectExt> mBoxes, mWholes;
		private long lvl_id = 0;
		private static final String KEY_DIFFICULTY = "mDifficulty";
		private static final String KEY_userID = "USER_ID";

		public int getmDifficulty() {
			return mDifficulty;
		}

		public void setmDifficulty(int mDifficulty) {
			this.mDifficulty = mDifficulty;
		}

		public double getSkillPoints() {
			return skillPoints;
		}

		public void setSkillPoints(double skillPoints) {
			this.skillPoints = skillPoints;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public int getmWidth() {
			return mWidth;
		}

		public void setmWidth(int mWidth) {
			this.mWidth = mWidth;
		}

		public int getmHeight() {
			return mHeight;
		}

		public void setmHeight(int mHeight) {
			this.mHeight = mHeight;
		}

		public long getLvl_id() {
			return lvl_id;
		}

		public void setLvl_id(long lvlId) {
			lvl_id = lvlId;
		}

		public int getmMode() {
			return mMode;
		}

		public void setmMode(int mMode) {
			this.mMode = mMode;
		}

		public int getmMoveX() {
			return mMoveX;
		}

		public void setmMoveX(int mMoveX) {
			this.mMoveX = mMoveX;
		}

		public int getmMoveY() {
			return mMoveY;
		}

		public void setmMoveY(int mMoveY) {
			this.mMoveY = mMoveY;
		}

		public String getLevel_numFormat() {
			return level_numFormat;
		}

		public void setLevel_numFormat(String levelNumFormat) {
			level_numFormat = levelNumFormat;
		}

		public String getLevel_name() {
			return level_name;
		}

		public void setLevel_name(String levelName) {
			level_name = levelName;
		}

		public RectExt getmPlayerRect() {
			return this.mPlayerRect;
		}

		public void setmPlayerRect(RectExt mPlayerRect) {
			this.mPlayerRect = mPlayerRect;
		}

		public void setmSi(SokobanInvoker mSi) {
			this.mSi = mSi;
		}

		public void setmLevel(List<RectExt> level) {
			this.mLevel = level;
		}

		public List<RectExt> getmLevel() {
			return this.mLevel;
		}

		public long getUserID() {
			return userID;
		}

		public void setUserID(long userID) {
			this.userID = userID;
		}

		public SokobanThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;
			Resources res = context.getResources();
			// cache handles to our key sprites & other drawables
			mPlayerUp = context.getResources()
					.getDrawable(R.drawable.bogado_up);
			mPlayerDown = context.getResources().getDrawable(
					R.drawable.bogado_down);
			mPlayerRight = context.getResources().getDrawable(
					R.drawable.bogado_right);
			mPlayerLeft = context.getResources().getDrawable(
					R.drawable.bogado_left);
			upB = context.getResources().getDrawable(R.drawable.up);
			downB = context.getResources().getDrawable(R.drawable.down);
			leftB = context.getResources().getDrawable(R.drawable.left);
			rightB = context.getResources().getDrawable(R.drawable.right);
			mPlayer = mPlayerUp;
			mBox = context.getResources().getDrawable(R.drawable.inter_fire);
			mBoxOnWhole = context.getResources().getDrawable(
					R.drawable.inter_done);
			mFloor = context.getResources().getDrawable(R.drawable.inter_floor);
			mWall = context.getResources().getDrawable(R.drawable.inter_wall);
			mWhole = context.getResources().getDrawable(R.drawable.inter_water);
			mBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.grass);
			mDifficulty = 0;
			setFocusable(true);
			requestFocus();
			// setState(STATE_RUNNING);
		}

		public void skipLvl() {
			// int box_pushes, String user_id, String level_id
			System.out.println("EL ESTADO ACTUAL DEL JUEGO ES:    ---------> "
					+ mMode);
			if (mMode != STATE_WIN) {
				int Width = Integer.parseInt(getResources().getString(
						R.string.width));
				int Height = Integer.parseInt(getResources().getString(
						R.string.height));
				//

				SokobanDbAdapter sda = new SokobanDbAdapter(mContext);

				try {

					setUserID(Long.valueOf(readFilePlayer()));
					// c.getLong(c.getColumnIndex(UserColumns.USER_ID));
					System.out.println("EL ID DEL USUARIO EN EL CURSOR ES: "
							+ userID);

				} catch (Exception e) {

				}

				ArrayList<String> result = new ArrayList<String>();

				mCanvasHeight = Height;
				mCanvasWidth = Width;
				mWidth = (mCanvasWidth);
				mHeight = (mCanvasHeight);
				mMoveX = mWidth;
				mMoveY = mHeight;
				loadLevel mLoad = new loadLevel(mMoveX, mMoveY, mHeight, mWidth);

				try {
					sda.open();
					System.out.println("No ha hecho el cursor");
					Cursor c = sda.getAll("level", LevelColumns.Columns);
					c.moveToLast();
					System.out.println("CUANTAS ROWS TIENE? " + c.getCount());
					int pushes = 99999; // Maximo valor para bajar eficiencia
					if (c.getCount() == 0) {
						System.out.println("Entro en el if");
						result = mLoad.getLevel(String.valueOf(userID));
						this.lvl_id = Long.valueOf(result.get(4));
					} else {
						System.out.println("Entro en el else");
						System.out
								.println("AHORA VOY A HACER EL  >>>>> GETLEVEL ");
						result = mLoad.getLevel(pushes, String.valueOf(userID),
								String.valueOf(this.lvl_id));
						this.skillPoints = Double.valueOf(result.get(5));

					}
					mLoad.setmGameHeight(Integer.valueOf(result.get(2)));
					mLoad.setmGameWidth(Integer.valueOf(result.get(3)));
					// mLoad.setmHeight(Height-mLoad.getmGameHeight());
					// mLoad.setmWidth(Width-mLoad.getmGameWidth());
					mLoad.setmHeight(Height + mLoad.getmGameHeight() / 2);
					mLoad.setmWidth(Width + mLoad.getmGameWidth() / 2);
					// mLoad.setmMoveX(Height);
					// mLoad.setmMoveY(Width);
					c.close();
					sda.close();
				} catch (Exception f) {

				}

				int maxSizeI = Integer.parseInt(result.get(2));
				int maxSizeJ = Integer.parseInt(result.get(3));
				this.level_name = result.get(1);
				String world = result.get(0);
				String sizes = "";

				if (String.valueOf(maxSizeI).length() == 1) {
					sizes = "0" + String.valueOf(maxSizeI);
				} else {
					sizes = String.valueOf(maxSizeI);
				}

				if (String.valueOf(maxSizeJ).length() == 1) {
					sizes = sizes + "0" + String.valueOf(maxSizeJ);
				} else {
					sizes = sizes + String.valueOf(maxSizeJ);
				}

				world = sizes + world;
				this.setLevel_numFormat(world);
				this.setmLevel(mLoad.load(world));
				this.setLevel_name(result.get(1));
				this.setLvl_id(Long.valueOf(result.get(4)));
				mSokobanGame = new SokobanGame();
				try {
					mSokobanGame.setmLevel(this.getmLevel());
				} catch (Exception e) {
					System.out
							.println("LoadLevel --> mSokobanGame.setMLevel es NULL");
				}
				mBoxes = new ArrayList<RectExt>();
				mWholes = new ArrayList<RectExt>();

				for (RectExt item : mLevel) {
					if (item.getType() == RectExt.BOX)
						mBoxes.add(item);
					else if (item.getType() == RectExt.WHOLE)
						mWholes.add(item);
				}

				this.setmSi(new SokobanInvoker(mMoveY, mMoveX,
						(ArrayList<RectExt>) mLevel, String.valueOf(userID),
						result.get(4)));
				setState(STATE_RUNNING);
			}
		}
		
		public String readFilePlayer() {
			String userID="";
			try {
				FileInputStream fileSaveState = mContext.openFileInput("player");
				InputStreamReader inputreader = new InputStreamReader(fileSaveState);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line = "";
				line = buffreader.readLine();
				line = buffreader.readLine();
				userID=line;
				setUserID(Long.valueOf(userID));
				fileSaveState.close();
			} catch (Exception e) {

			}
			return userID;
		}

		private void loadLvl() {
			System.out.println("EL ESTADO ACTUAL DEL JUEGO ES:    --------->  "
					+ mMode);
			if (mMode == STATE_WIN || mMode == STATE_READY) {
				int Width = Integer.parseInt(getResources().getString(
						R.string.width));
				int Height = Integer.parseInt(getResources().getString(
						R.string.height));
				//

				SokobanDbAdapter sda = new SokobanDbAdapter(mContext);

				try {
					setUserID(Long.valueOf(readFilePlayer()));
					// c.getLong(c.getColumnIndex(UserColumns.USER_ID));
					System.out.println("EL ID DEL USUARIO EN EL CURSOR ES: "
							+ userID);
				} catch (Exception e) {
					System.out.println(e);
				}

				ArrayList<String> result = new ArrayList<String>();

				mCanvasHeight = Height;
				mCanvasWidth = Width;
				mWidth = (mCanvasWidth);
				mHeight = (mCanvasHeight);
				mMoveX = mWidth;
				mMoveY = mHeight;
				loadLevel mLoad = new loadLevel(mMoveX, mMoveY, mHeight, mWidth);

				try {
					sda.open();
					System.out.println("No ha hecho el cursor");
					String q="SELECT * FROM LEVEL WHERE USER_ID='" + getUserID()+"'";
					Cursor  c = sda.getSpecific(q);
				//	Cursor c = sda.get(LevelColumns.TABLE_LEVELS, getUserID(), LevelColumns.Columns);
					c.moveToLast();
					System.out.println("CUANTAS ROWS TIENE? " + c.getCount());
					if (c.getCount() == 0) {
						System.out.println("Entro en el if");
						result = mLoad.getLevel(String.valueOf(userID));
						this.lvl_id = Long.valueOf(result.get(4));
					} else {
						System.out.println("Entro en el else");
						int pushes = 0;
						// int box_pushes, String user_id, String level_id

						if (mSi == null) {
							pushes = c
									.getInt(c
											.getColumnIndexOrThrow(LevelColumns.PUSHES));
							this.lvl_id = c
									.getLong(c
											.getColumnIndexOrThrow(LevelColumns.LEVEL_ID));
						} else {
							if (mSi.mPushes == 0) {
								pushes = c
										.getInt(c
												.getColumnIndexOrThrow(LevelColumns.PUSHES));
								this.lvl_id = c
										.getLong(c
												.getColumnIndexOrThrow(LevelColumns.LEVEL_ID));
								System.out.println("ENTRO EN EL IF DEL ELSSE");
							} else {
								System.out.println("Pushes, userid, level_id"
										+ mSi.mPushes + ", " + userID + " , "
										+ mSi.level_id);
								pushes = mSi.mPushes;
								this.lvl_id = Long.valueOf(mSi.level_id);
							}

						}
						System.out
								.println("AHORA VOY A HACER EL  >>>>> GETLEVEL ");
						result = mLoad.getLevel(pushes, String.valueOf(userID),
								String.valueOf(this.lvl_id));
						this.skillPoints = Double.valueOf(result.get(5));

					}
					
	//				mLoad.setmHeight(Height);
	//				mLoad.setmWidth(Width);
	//				mLoad.setmHeight(Height + mLoad.getmGameHeight()/2);
	//				mLoad.setmWidth(Width + mLoad.getmGameWidth()/2);
					// mLoad.setmMoveX(Height);
					// mLoad.setmMoveY(Width);
					c.close();
					sda.close();
				} catch (Exception f) {

				}

				int maxSizeI = Integer.parseInt(result.get(2));
				int maxSizeJ = Integer.parseInt(result.get(3));
				this.level_name = result.get(1);
				String world = result.get(0);
				String sizes = "";

				if (String.valueOf(maxSizeI).length() == 1) {
					sizes = "0" + String.valueOf(maxSizeI);
				} else {
					sizes = String.valueOf(maxSizeI);
				}

				if (String.valueOf(maxSizeJ).length() == 1) {
					sizes = sizes + "0" + String.valueOf(maxSizeJ);
				} else {
					sizes = sizes + String.valueOf(maxSizeJ);
				}
				System.out.println("EL HEIGHT DEL JUEGO ES: " + Integer.valueOf(result.get(2)));
				System.out.println("EL Width DEL JUEGO ES: " + Integer.valueOf(result.get(3)));
				mLoad.setmGameHeight(Integer.valueOf(result.get(2)));
				mLoad.setmGameWidth(Integer.valueOf(result.get(3)));
				world = sizes + world;
				this.setLevel_numFormat(world);
				this.setmLevel(mLoad.load(world));
				this.setLevel_numFormat(world);
				this.setLevel_name(result.get(1));
				this.setLvl_id(Long.valueOf(result.get(4)));
				mSokobanGame = new SokobanGame();
				try {
					mSokobanGame.setmLevel(this.getmLevel());
				} catch (Exception e) {
					System.out
							.println("LoadLevel --> mSokobanGame.setMLevel es NULL");
				}
				mBoxes = new ArrayList<RectExt>();
				mWholes = new ArrayList<RectExt>();

				for (RectExt item : mLevel) {
					if (item.getType() == RectExt.BOX)
						mBoxes.add(item);
					else if (item.getType() == RectExt.WHOLE)
						mWholes.add(item);
				}

				this.setmSi(new SokobanInvoker(mMoveY, mMoveX,
						(ArrayList<RectExt>) mLevel, String.valueOf(userID),
						result.get(4)));
				setState(STATE_RUNNING);
			}
		}

		private void loadLvlPG(String lvl_id) {
			System.out.println("EL ESTADO ACTUAL DEL JUEGO ES:    ---------> "
					+ mMode + "..............(PG)");
			int Width = Integer.parseInt(getResources().getString(
					R.string.width));
			int Height = Integer.parseInt(getResources().getString(
					R.string.height));
			//

			mCanvasHeight = Height;
			mCanvasWidth = Width;
			mWidth = (mCanvasWidth);
			mHeight = (mCanvasHeight);
			mMoveX = mWidth;
			mMoveY = mHeight;
			loadLevel mLoad = new loadLevel(mMoveX, mMoveY, mHeight, mWidth);
			System.out
					.println("AHORA VOY A HACER EL  >>>>> GETLEVEL....... (PARA PG)....... ");
			setmLevel(mLoad.load(this.lvl_id, getLevel_numFormat()));

			mSokobanGame = new SokobanGame();

			try {
				mSokobanGame.setmLevel(this.getmLevel());
			} catch (Exception e) {
				System.out
						.println("LoadLevel --> mSokobanGame.setMLevel es NULL");
			}
			mBoxes = new ArrayList<RectExt>();
			mWholes = new ArrayList<RectExt>();

			for (RectExt item : mLevel) {
				if (item.getType() == RectExt.BOX)
					mBoxes.add(item);
				else if (item.getType() == RectExt.WHOLE)
					mWholes.add(item);
			}

			this
					.setmSi(new SokobanInvoker(mMoveY, mMoveX,
							(ArrayList<RectExt>) mLevel,
							String.valueOf(userID), lvl_id));
			setmMode(STATE_REPLAY);
			setState(STATE_REPLAY);

		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void doStart() {
			synchronized (mSurfaceHolder) {
				int demoDelay = 1000;
				final ProgressDialog pg;
				pg = new ProgressDialog(mContext);
				pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pg.setMessage("Loading...");
				pg.setCancelable(false);
				pg.setProgress(100);
				pg.show();
				final Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {

						pg.dismiss(); // clearing progress dialog
						pg.cancel();

					}
				}, demoDelay);
				// final Handler handler = new Handler() {
				//
				// @Override
				// public void handleMessage(Message msg) {
				// pg.dismiss();
				// }
				//
				// };
				System.out.println("ENTRO EN DOSTART");
				loadLvl();
				// Adjust difficulty params for EASY/HARD
				if (mDifficulty == 3) {
					// Desbloquear las otras pantallas
				} else if (mDifficulty == 5) {
					// Desbloquear las otras pantallas
				}
				mLastTime = System.currentTimeMillis() + 100;
				setState(STATE_RUNNING);
				mMode = STATE_RUNNING;
			}
		}

		public void restart() {
			int demoDelay = 1000;
			final ProgressDialog pg;
			pg = new ProgressDialog(mContext);
			pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pg.setMessage("Restarting level...");
			pg.setCancelable(false);
			pg.setProgress(100);
			pg.show();
			final Handler handler = new Handler();

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {

					pg.dismiss(); // clearing progress dialog
					pg.cancel();

				}
			}, demoDelay);
			int Width = Integer.parseInt(getResources().getString(
					R.string.width));
			int Height = Integer.parseInt(getResources().getString(
					R.string.height));
			//

			SokobanDbAdapter sda = new SokobanDbAdapter(mContext);

			ArrayList<String> result = new ArrayList<String>();

			mCanvasHeight = Height;
			mCanvasWidth = Width;
			mWidth = (mCanvasWidth);
			mHeight = (mCanvasHeight);
			mMoveX = mWidth;
			mMoveY = mHeight;
			loadLevel mLoad = new loadLevel(mMoveX, mMoveY, mHeight, mWidth);
			result = mLoad.getLevel(String.valueOf(userID));
			mLoad.setmGameHeight(Integer.valueOf(result.get(2)));
			mLoad.setmGameWidth(Integer.valueOf(result.get(3)));
			// mLoad.setmHeight(Height-mLoad.getmGameHeight());
			// mLoad.setmWidth(Width-mLoad.getmGameWidth());
			mLoad.setmHeight(Height + mLoad.getmGameHeight()/2);
			mLoad.setmWidth(Width + mLoad.getmGameWidth()/2);
			// mLoad.setmMoveX(Height);
			// mLoad.setmMoveY(Width);
			this.lvl_id = Long.valueOf(result.get(4));

			int maxSizeI = Integer.parseInt(result.get(2));
			int maxSizeJ = Integer.parseInt(result.get(3));
			this.level_name = result.get(1);
			String world = result.get(0);
			String sizes = "";

			if (String.valueOf(maxSizeI).length() == 1) {
				sizes = "0" + String.valueOf(maxSizeI);
			} else {
				sizes = String.valueOf(maxSizeI);
			}

			if (String.valueOf(maxSizeJ).length() == 1) {
				sizes = sizes + "0" + String.valueOf(maxSizeJ);
			} else {
				sizes = sizes + String.valueOf(maxSizeJ);
			}

			world = sizes + world;
			this.setLevel_numFormat(world);
			this.setmLevel(mLoad.load(world));
			this.setLevel_numFormat(world);
			this.setLevel_name(result.get(1));
			this.setLvl_id(Long.valueOf(result.get(4)));
			mSokobanGame = new SokobanGame();
			try {
				mSokobanGame.setmLevel(this.getmLevel());
			} catch (Exception e) {
				System.out
						.println("LoadLevel --> mSokobanGame.setMLevel es NULL");
			}
			mBoxes = new ArrayList<RectExt>();
			mWholes = new ArrayList<RectExt>();

			for (RectExt item : mLevel) {
				if (item.getType() == RectExt.BOX)
					mBoxes.add(item);
				else if (item.getType() == RectExt.WHOLE)
					mWholes.add(item);
			}

			this.setmSi(new SokobanInvoker(mMoveY, mMoveX,
					(ArrayList<RectExt>) mLevel, String.valueOf(userID), result
							.get(4)));
			setState(STATE_RUNNING);

		}

		public void doStartForPG(String lvl_id) {
			synchronized (mSurfaceHolder) {
				int demoDelay = 1000;
				final ProgressDialog pg;
				pg = new ProgressDialog(mContext);
				pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pg.setMessage("Loading...");
				pg.setCancelable(false);
				pg.setProgress(100);
				pg.show();
				final Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {

						pg.dismiss(); // clearing progress dialog
						pg.cancel();

					}
				}, demoDelay);
				System.out.println("ENTRO EN DOSTART  (PG) ..........");
				loadLvlPG(lvl_id);
				mLastTime = System.currentTimeMillis() + 100;
				setState(STATE_REPLAY);
				setmMode(STATE_REPLAY);
			}
		}

		/**
		 * Pauses the physics update & animation.
		 */
		public void pause() {
			synchronized (mSurfaceHolder) {
				setState(STATE_PAUSE);
				// saveState(new Bundle());
				// mSurfaceHolder.getSurface().freeze();
			}
		}

		/**
		 * Restores game state from the indicated Bundle. Typically called when
		 * the Activity is being restored after having been previously
		 * destroyed.
		 * 
		 * @param savedState
		 *            Bundle containing the game state
		 */

		public synchronized void restoreState(Bundle savedState) {
			synchronized (mSurfaceHolder) {
				// System.out
				// .println(" '''''''''''''' ENTRO EN RESTORE STATE ''''''''''''''''");
				// setState(STATE_PAUSE);
				// // mDifficulty = savedState.getInt(KEY_DIFFICULTY);
				// setUserID(savedState.getLong(KEY_userID));
				// try {
				// FileInputStream fileSaveState = mContext
				// .openFileInput("saveState");
				// InputStreamReader inputreader = new InputStreamReader(
				// fileSaveState);
				// BufferedReader buffreader = new BufferedReader(inputreader);
				// String line = "";
				// char firstCharacter = '*';
				// int location = 0;
				//				
				// // read every line of the file into the line-variable, on
				// // line at the time
				// line = buffreader.readLine();
				// while (line != null) {
				//				
				// if (line.charAt(0) == firstCharacter) {
				// switch (location) {
				// case 0: {
				// this.setUserID(Long.valueOf(line.substring(1,
				// line.length())));
				// line = buffreader.readLine();
				// }
				// case 1: // mPlayer
				// {
				// while (line.charAt(0) != firstCharacter) {
				// line = line + buffreader.readLine();
				// line = buffreader.readLine();
				// int left, top, right, bottom;
				// String auxLine = line;
				// for (int i = 0; i < auxLine.length(); i++) {
				// auxLine=line.substring(7, 11);
				// }
				// // Rect r = new Rect(left, top, right, bottom);
				// // mPlayerRect.set(r);
				// }
				//				
				// }
				// case 2: // mLevel
				// {
				// this.setUserID(Long.valueOf(line.substring(1,
				// line.length())));
				// }
				// case 3: // mBoxes
				// {
				// this.setUserID(Long.valueOf(line.substring(1,
				// line.length())));
				// }
				// case 4: // mWholes
				// {
				// this.setUserID(Long.valueOf(line.substring(1,
				// line.length())));
				// }
				// }
				//				
				// location++;
				// }
				//				
				// }
				// fileSaveState.close();
				// } catch (Exception e) {
				//				
				// }
				// doDraw(new Canvas());

				setState(STATE_RUNNING);
			}
		}

		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						if (mMode == STATE_RUNNING) {
							if (updatePhysics()) {
								System.out.println("STATE WIN");
								mMode = STATE_WIN;
								doDraw(c);
							} else {
								mMode = STATE_RUNNING;
								// System.out.println("STATE RUNNING");
								// doStart();
								doDraw(c);
							}
						} else if (mMode == STATE_WIN) {
							// System.out.println("STATE WIN 1rst, draw, then ready");
							doDraw(c);
							mMode = STATE_READY;
							System.out.println("STATE READY");

						} else if (mMode == STATE_READY) {
							// System.out.println("STATE READY");
							// doStart();
							// doDraw(c);
						} else if (mMode == STATE_REPLAY) {
							if (updatePhysics()) {
								// System.out.println("STATE WIN_REPLAY");
								mMode = STATE_REPLAY_WON;
								doDraw(c);
							} else {
								mMode = STATE_REPLAY;
								// System.out.println("STATE REPLAY RUN");
								// doStart();
								doDraw(c);
							}
							doDraw(c);
						} else if (mMode == STATE_REPLAY_WON) {
							doDraw(c);
							break;
						}

					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		/**
		 * Dump game state to the provided Bundle. Typically called when the
		 * Activity is being suspended.
		 * 
		 * @return Bundle with this view's state
		 */

		public void saveState(Bundle bundle) {
			System.out
					.println(" '''''''''''''' ENTRO EN PAUSE STATE ''''''''''''''''");
			synchronized (mSurfaceHolder) {
				// Bundle map = new Bundle();
				// map = bundle;
				// // mSurfaceHolder.getSurface().freeze();
				// // map.putInt(KEY_DIFFICULTY, Integer.valueOf(mDifficulty));
				// map.putString(KEY_userID, String.valueOf(this.getUserID()));
				// private static final RectExt KEY_mPlayerRect;
				// private static final List<RectExt> KEY_mLevel;
				// private static final List<RectExt> KEY_mBoxes,
				// KEY_mWholes;
				// private static final String KEY_userID;
				/*
				 * String FILENAME = "saveState"; FileOutputStream fos;
				 * 
				 * try { fos = mContext.openFileOutput(FILENAME,
				 * Context.MODE_PRIVATE); ObjectOutputStream oos = new
				 * ObjectOutputStream(fos); oos.writeObject("*" +
				 * String.valueOf(userID) + "\n*" + mPlayerRect.bottom +","+
				 * mPlayerRect.left +","+ mPlayerRect.right +","+
				 * mPlayerRect.top +"," + "\n*"); for (int
				 * i=0;i<mLevel.size();i++){ oos.writeObject(mLevel.get(i)); }
				 * oos.writeObject("\n*");
				 * 
				 * // + mLevel + "\n*" + mBoxes // + "\n*" + mWholes);
				 * oos.close(); } catch (Exception e) { // TODO Auto-generated
				 * catch block e.printStackTrace(); }
				 */

				// return map;
			}

		}

		/**
		 * Sets the current difficulty.
		 * 
		 * @param difficulty
		 */
		public void setDifficulty(int difficulty) {
			synchronized (mSurfaceHolder) {
				mDifficulty = difficulty;
			}
		}

		/**
		 * Used to signal the thread whether it should be running or not.
		 * Passing true allows the thread to run; passing false will shut it
		 * down if it's already running. Calling start() after this was most
		 * recently called with false will result in an immediate shutdown.
		 * 
		 * @param b
		 *            true to run, false to shut down
		 */
		public void setRunning(boolean b) {
			mRun = b;
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @see #setState(int, CharSequence)
		 * @param mode
		 *            one of the STATE_* constants
		 */
		public void setState(int mode) {
			synchronized (mSurfaceHolder) {
				setState(mode, null);
			}
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @param mode
		 *            one of the STATE_* constants
		 * @param message
		 *            string to add to screen or null
		 */
		public void setState(int mode, CharSequence message) {
			/*
			 * This method optionally can cause a text message to be displayed
			 * to the user when the mode changes. Since the View that actually
			 * renders that text is part of the main View hierarchy and not
			 * owned by this thread, we can't touch the state of that View.
			 * Instead we use a Message + Handler to relay commands to the main
			 * thread, which updates the user-text View.
			 */
			synchronized (mSurfaceHolder) {
				mMode = mode;

				if (mMode == STATE_RUNNING || mMode == STATE_REPLAY) {
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", "");
					b.putInt("viz", View.INVISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				} else {
					Resources res = mContext.getResources();
					CharSequence str = "";
					if (mMode == STATE_READY) {
						str = res.getText(R.string.mode_ready);
					} else if (mMode == STATE_PAUSE) {
						str = res.getText(R.string.mode_pause);
					} else if (mMode == STATE_WIN || mMode == STATE_REPLAY_WON) {
						str = res.getString(R.string.mode_win);
					}

					if (message != null) {
						str = message + "\n" + str;
					}

					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", str.toString());
					b.putInt("viz", View.VISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;

				// don't forget to resize the background image
				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
						width, height, true);
			}
		}

		/**
		 * Resumes from a pause.
		 */
		public void unpause() {
			// Move the real time clock up to now
			synchronized (mSurfaceHolder) {
				mLastTime = System.currentTimeMillis() + 100;
			}
			if (mMode == STATE_REPLAY) {

			} else {
				// System.out
				// .println(",,,,,,,,,,,ESTA EN UNPAUSE Y HACE STATE RUNNING PORQUE SI,,,,,,");
				// setState(STATE_RUNNING);
				// mSurfaceHolder.getSurface().unfreeze();
				// restoreState(new Bundle());
			}
		}

		/**
		 * Handles a key-down event.
		 * 
		 * @param keyCode
		 *            the key that was pressed
		 * @param msg
		 *            the original event object
		 * @return true
		 */
		boolean doKeyDown(int keyCode, KeyEvent msg) {
			synchronized (mSurfaceHolder) {
				boolean okStart = false;

				if (mMode == STATE_READY || mMode == STATE_WIN) {
					// ready-to-start -> start
					System.out
							.println(" ///...... Estamos en que esta ready o win (doKeyDowd).......////");
					doStart();
					return true;
				} else if (mMode == STATE_PAUSE) {
					// paused -> running
					unpause();
					return true;
				} else if (mMode == STATE_RUNNING || mMode == STATE_REPLAY) {
					// center/space -> fire
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_UP:
						mPlayer = mPlayerUp;
						mLevel = mSi.up(mPlayerRect);
						okStart = true;
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						mPlayer = mPlayerDown;
						mLevel = mSi.down(mPlayerRect);
						okStart = true;
						break;
					case KeyEvent.KEYCODE_DPAD_LEFT:
						mPlayer = mPlayerLeft;
						mLevel = mSi.left(mPlayerRect);
						okStart = true;
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						mPlayer = mPlayerRight;
						mLevel = mSi.right(mPlayerRect);
						okStart = true;
						break;
					case KeyEvent.KEYCODE_DPAD_CENTER: // UNDO MOVE
						RectExt rect = mSi.undo();
						if (null != rect) {
							switch (mSi.getLastMove(mSi.getLastCommand())) {
							case KeyEvent.KEYCODE_DPAD_UP:
								mPlayer = mPlayerUp;
								okStart = true;
								break;
							case KeyEvent.KEYCODE_DPAD_DOWN:
								mPlayer = mPlayerDown;
								okStart = true;
								break;
							case KeyEvent.KEYCODE_DPAD_LEFT:
								mPlayer = mPlayerLeft;
								okStart = true;
								break;
							case KeyEvent.KEYCODE_DPAD_RIGHT:
								mPlayer = mPlayerRight;
								okStart = true;
								break;
							default:
								break;
							}
							mPlayerRect = rect;
						}

					}

				}
				// if (keyCode == KeyEvent.KEYCODE_BACK) {
				// okStart = true;
				// pause();
				// }

				return false;
			}
		}

		/**
		 * Draws the ship, fuel/speed bars, and background to the provided
		 * Canvas.
		 */
		private void doDraw(Canvas canvas) {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.

			if (mMode != STATE_RUNNING && mMode != STATE_WIN
					&& mMode != STATE_PAUSE && mMode != STATE_REPLAY
					&& mMode != STATE_REPLAY_WON) {
				setState(STATE_RUNNING);
				mMode = STATE_RUNNING;
				System.out
						.println("Esta entrando en la asignacion de DODRAW y POR ESO DA RUNNING");

			}
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);

			for (RectExt rect : mLevel) {

				switch (rect.getType()) {
				case RectExt.WALL:
					mWall.setBounds(rect.getRect());
					mWall.draw(canvas);
					break;
				case RectExt.WHOLE:
					mWhole.setBounds(rect.getRect());
					mWhole.draw(canvas);
					break;
				case RectExt.BOX:
					mBox.setBounds(rect.getRect());
					mBox.draw(canvas);
					break;
				case RectExt.BOX_ON_WHOLE:
					mBoxOnWhole.setBounds(rect.getRect());
					mBoxOnWhole.draw(canvas);
					break;
				case RectExt.PLAYER:
					mPlayer.setBounds(rect.getRect());
					mPlayer.draw(canvas);
					mPlayerRect = rect;
					break;
				case RectExt.EMPTY:
					mFloor.setBounds(rect.getRect());
					mFloor.draw(canvas);
				}

				// upB.setBounds(30,30,40,40);
				// upB.setAlpha(245);
				// upB.draw(canvas);
				// downB.setBounds(upB.getBounds().);
				// downB.setAlpha(100);
				// downB.draw(canvas);
				// leftB.setBounds(rect.getRect());
				// leftB.setAlpha(100);
				// leftB.draw(canvas);
				// rightB.setBounds(rect.getRect());
				// rightB.setAlpha(100);
				// rightB.draw(canvas);
				canvas.save();

			}

			// Draw the ship with its current rotation

			canvas.restore();
		}

		/**
		 * Figures the lander state (x, y, fuel, ...) based on the passage of
		 * realtime. Does not invalidate(). Called at the start of draw().
		 * Detects the end-of-game and sets the UI to the next state.
		 */
		private boolean updatePhysics() {
			boolean win = false;

			// Evaluate if we have landed ... stop the game
			int result = 0;
			CharSequence message = "";
			boolean gameWon = false;
			try {

				if (mSi.mPushes != 0) {
					gameWon = mSokobanGame.checkGame(mSi.mPushes, mBoxes,
							mWholes, mContext, this.level_numFormat,
							this.level_name, String.valueOf(this.lvl_id), String.valueOf(this.getUserID()));
					if (gameWon) {
						if (mMode == STATE_REPLAY) {
							mMode = STATE_REPLAY_WON;
							setState(STATE_REPLAY_WON);
							win = true;
						} else {
							result = STATE_WIN;
							mMode = STATE_WIN;
							win = true;
							// setState(result, message);
							setState(STATE_WIN);
							// mSokobanGame.winGame(mSi.mPushes);
							/*
							 * Aqui vamos a pasarle los datos de mi juego al
							 * servidor y nos va a regresar cuantos puntos gane,
							 * mis skillpoints y mi proximo nivel.
							 */
							// doStart();
						}
					} else {
						if (mMode == STATE_REPLAY) {

						} else {
							setState(STATE_RUNNING);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}

			return win;

		}

	}

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */

	// ------------- BIS HIER

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;

	/** Pointer to the text view to display "Paused.." etc. */
	private TextView mStatusText;

	/** The thread that actually draws the animation */
	private SokobanThread thread;

	public SokobanView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new SokobanThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setVisibility(m.getData().getInt("viz"));
				mStatusText.setText(m.getData().getString("text"));
			}
		});

		setFocusable(true); // make sure we get key events
	}

	/**
	 * Fetches the animation thread corresponding to this LunarView.
	 * 
	 * @return the animation thread
	 */
	public SokobanThread getThread() {
		return thread;
	}

	/**
	 * Standard override to get key-press events.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		super.onKeyDown(keyCode, msg);
		if (thread.mMode == 5) { // WIN
			System.out.println("El programa reconoce que gano.");
			thread.mMode = 3;
			return thread.doKeyDown(keyCode, msg);
		} else if (thread.mMode == 7) {
			return thread.doKeyDown(keyCode, msg); 
		} else {
			return thread.doKeyDown(keyCode, msg);
		}

	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;

	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	// @Override
	// public void onWindowFocusChanged(boolean hasWindowFocus) {
	// if (!hasWindowFocus)
	// thread.pause();
	// }

	/**
	 * Installs a pointer to the text view used for messages.
	 */
	public void setTextView(TextView textView) {
		mStatusText = textView;
	}

	/* Callback invoked when the surface dimensions change. */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		thread.setRunning(true);
		thread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		if (thread.mMode == 2) {
			// Pause
			System.out.println("NO DESTRUYE EL THREAD :D ESTA EN PAUSA!");

		} else {
			boolean retry = true;
			thread.setRunning(false);
			while (retry) {
				try {
					System.out
							.println("COMENZO A DESTRUIR EL THREAD con Thread.join()  en SokobanView");
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Una vez que conocemos nuestro ancho y alto.
		// le asignamos el tamano del cuadro para tratarlo como una matriz
		int Width = Integer.parseInt(this.getResources().getString(
				R.string.width));
		int Height = Integer.parseInt(this.getResources().getString(
				R.string.height));
		thread.setmMoveX(w / Width);
		thread.setmMoveY(h / Height);
		thread.setmHeight(Height);
		thread.setmWidth(Width);

	}

}

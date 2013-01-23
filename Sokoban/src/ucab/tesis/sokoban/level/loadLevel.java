package ucab.tesis.sokoban.level;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONObject;

import ucab.tesis.sokoban.logic.RectExt;
import android.app.Activity;
import android.database.Cursor;

public class loadLevel extends Activity {

	private int mGameWidth, mGameHeight, mMoveY, mMoveX, mHeight, mWidth,
			mPaddingWidth, mPaddingHeight;
	private String mGame;
	public int getmGameWidth() {
		return mGameWidth;
	}

	public void setmGameWidth(int mGameWidth) {
		this.mGameWidth = mGameWidth;
	}

	public int getmGameHeight() {
		return mGameHeight;
	}

	public void setmGameHeight(int mGameHeight) {
		this.mGameHeight = mGameHeight;
	}

	public int getmMoveY() {
		return mMoveY;
	}

	public void setmMoveY(int mMoveY) {
		this.mMoveY = mMoveY;
	}

	public int getmMoveX() {
		return mMoveX;
	}

	public void setmMoveX(int mMoveX) {
		this.mMoveX = mMoveX;
	}

	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	private Cursor mCursor;
	private ArrayList<RectExt> mLevel;

	public ArrayList<RectExt> getLevelInstance() {
		return mLevel;
	}

	public loadLevel(int moveX, int moveY, int height, int width) {
		// public loadLevel(int moveX, int moveY,int height,int width) {
		// mDbHelper = new SokobanDbAdapter(this);
		// mDbHelper.open();
		mMoveY = moveY;
		mMoveX = moveX;
		mHeight = height;
		mWidth = width;
	}

	public String getGame() {
		return mGame;
	}

	public int getGameWidth() {
		return mGameWidth;
	}

	public int getGameHeight() {
		return mGameHeight;
	}

	public int getPaddingWidth() {
		return mPaddingWidth;
	}

	public int getPaddingHeight() {
		return mPaddingHeight;
	}

	public ArrayList<RectExt> load() {
		//return load(-1);
		return load("");
	}

	public ArrayList<RectExt> load(long lvl_id, String level) {
		//mGame = getLevel(lvl_id);
		mGame = level;
		// TODO chequear que Height siempre sea mayor que Width
		mGameHeight = Integer.parseInt(mGame.substring(0, 2));
		mGameWidth = Integer.parseInt(mGame.substring(2, 4));
		mGame = mGame.substring(4, mGame.length());
		mLevel = new ArrayList<RectExt>();

		System.out.println("LA UBICACION HACE LA SIGUIENTE RESTA:  ((mWidth ["+mWidth+"] - mGameWidth  ["+mGameWidth+"]) / 2) * mMoveX  ["+mMoveX+"]");
		mPaddingWidth = ((mWidth - mGameWidth) / 2)*mMoveX;
		System.out.println("LA UBICACION HACE LA SIGUIENTE RESTA:  ((mheighh ["+mHeight+"] - mGameHeighth  ["+mGameHeight+"]) / 2) * mMoveX  ["+mMoveY+"]");
		mPaddingHeight = ((mHeight - mGameHeight) / 2)*mMoveY;
		System.out.println("Los resultados son: (widht y heoight)" + mPaddingWidth +"  y   "+ mPaddingHeight);

		for (int lastY = 0; lastY < mGameHeight; lastY++) {
			for (int lastX = 0; lastX < mGameWidth; lastX++)
				if (mGame.charAt((lastY * mGameWidth) + lastX) != '0')
					if (mGame.charAt((lastY * mGameWidth) + lastX) != '5'
							&& mGame.charAt((lastY * mGameWidth) + lastX) != '6')
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								Integer.parseInt(""
										+ mGame.charAt((lastY * mGameWidth)
												+ lastX))));
					else if (mGame.charAt((lastY * mGameWidth) + lastX) == '5') {
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.BOX));
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.WHOLE));
					} else if (mGame.charAt((lastY * mGameWidth) + lastX) == '6') {
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.PLAYER));
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.WHOLE));
					}
		}
		Collections.sort(mLevel);
		return mLevel;
	}

	public int getmHeight() {
		return mHeight;
	}

	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public ArrayList<RectExt> load(String lvl) {
		mGame = lvl;

		// TODO chequear que Height siempre sea mayor que Width
		mGameHeight = Integer.parseInt(mGame.substring(0, 2));
		mGameWidth = Integer.parseInt(mGame.substring(2, 4));
		mGame = mGame.substring(4, mGame.length());
		mLevel = new ArrayList<RectExt>();
		System.out.println("LA UBICACION HACE LA SIGUIENTE RESTA:  ((mWidth ["+mWidth+"] - mGameWidth  ["+mGameWidth+"]) / 2) * mMoveX  ["+mMoveX+"]");
		mPaddingWidth = ((mWidth - mGameWidth) / 2)*mMoveX;
		System.out.println("LA UBICACION HACE LA SIGUIENTE RESTA:  ((mheighh ["+mHeight+"] - mGameHeighth  ["+mGameHeight+"]) / 2) * mMoveX  ["+mMoveY+"]");
		mPaddingHeight = ((mHeight - mGameHeight) / 2)*mMoveY;
		System.out.println("Los resultados son: (widht y heoight)" + mPaddingWidth +"  y   "+ mPaddingHeight);
		
		
		for (int lastY = 0; lastY < mGameHeight; lastY++) {
			for (int lastX = 0; lastX < mGameWidth; lastX++)
				if (mGame.charAt((lastY * mGameWidth) + lastX) != '0')
					if (mGame.charAt((lastY * mGameWidth) + lastX) != '5'
							&& mGame.charAt((lastY * mGameWidth) + lastX) != '6')
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								Integer.parseInt(""
										+ mGame.charAt((lastY * mGameWidth)
												+ lastX))));
					else if (mGame.charAt((lastY * mGameWidth) + lastX) == '5') {
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.BOX));
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.WHOLE));
					} else if (mGame.charAt((lastY * mGameWidth) + lastX) == '6') {
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.PLAYER));
						mLevel.add(new RectExt(
								(lastX * mMoveX) + mPaddingWidth,
								(lastY * mMoveY) + mPaddingHeight,
								((lastX + 1) * mMoveX) + mPaddingWidth,
								((lastY + 1) * mMoveY) + mPaddingHeight,
								RectExt.WHOLE));
					}
		}
		Collections.sort(mLevel);
		return mLevel;
	}
	public ArrayList<String> getLevel(String userID) {
		// AQUI PEDIMOS LVL CON ID USUARIO
		String lev = "";
		String name;
		Long height;
		Long width;
		Long id;
		ArrayList<String> result = new ArrayList<String>();

		
		try {
			URL url = new URL(
					"http://sokofighter.appspot.com/sokoban/json/android/level/?user_id="
							+ userID);
			URLConnection urlc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlc
					.getInputStream()));
			String inputLine= in.readLine();
			JSONObject response = new JSONObject(inputLine);
			lev = response.getString("level");
			name = response.getString("name");
			height = Long.valueOf(response.getString("height"));
			width = Long.valueOf(response.getString("width"));
			id = Long.valueOf(response.getString("id"));
			result.add(lev);
			result.add(name);
			result.add(height.toString());
			result.add(width.toString());
			result.add(id.toString());
			this.mGameHeight=Integer.valueOf(height.toString());
			this.mGameWidth=Integer.valueOf(width.toString());
			in.close();
		} catch (Exception e) {
			System.out.println("ERRRRRRRRRRRROOOORRRRR en GETLEVEL!!");
		}

		return result;
	}

	public ArrayList<String> getLevel(int box_pushes, String user_id, String level_id) {
		ArrayList<String> result=new ArrayList<String>();
		String lev = "";
		String name;
		Long height;
		Long width;
		Long id;
		Long skillpoints;
		URL url;
		String commit;
//	
		try {			
			URL url_post = new URL(
					"http://sokofighter.appspot.com/sokoban/json/android/level/?box_pushes="+box_pushes+
					"&user_id="+user_id+"&level_id="+level_id);
			
			commit = "box_pushes=" + box_pushes 
			+ "&user_id=" + user_id
			+ "&level_id=" + level_id;

			//Sending data
			try {
				HttpURLConnection conn = (HttpURLConnection) url_post
						.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				OutputStreamWriter wr = new OutputStreamWriter(conn
						.getOutputStream());
		
	//			wr.write(commit);
	//			wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine= rd.readLine();
				JSONObject response = new JSONObject(inputLine);
				lev = response.getString("level");
				name = response.getString("name");
				height = Long.valueOf(response.getString("height"));
				width = Long.valueOf(response.getString("width"));
				id = Long.valueOf(response.getString("id"));
				skillpoints = Long.valueOf(response.getString("skill_points"));
				result.add(lev);
				result.add(name);
				result.add(height.toString());
				result.add(width.toString());
				result.add(id.toString());
				result.add(skillpoints.toString());
				this.mGameHeight=Integer.valueOf(height.toString());
				this.mGameWidth=Integer.valueOf(width.toString());
				rd.close();
			} catch (Exception e1) {
				System.out.println("....PROBLEMAS CARGANDO/ POSTEANDO NUEVO NIVEL...................... T_______T ");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			userID="";
		}		
		

		return result;
	}

}

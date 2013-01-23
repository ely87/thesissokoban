package ucab.tesis.sokoban.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class SokobanDbAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	private Cursor mCursor;
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "sokoban.db";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String TAG = "DatabaseHelper";

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + UserColumns.TABLE_USER + " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY,"  + UserColumns.NAME + " Text NOT NULL,"
					+ UserColumns.TIME + " INTEGER," + UserColumns.LAST_PLAYED
					+ " INTEGER," + UserColumns.BOX_PUSHES + " INTEGER," 
					+ UserColumns.SCORE + " INTEGER, " + UserColumns.USER_ID+" INTEGER" + ");");

			db.execSQL("CREATE TABLE " + LevelColumns.TABLE_LEVELS + " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY," + LevelColumns.LEVEL + " TEXT,"
					+ LevelColumns.NAME + " TEXT, "
					+ LevelColumns.PUSHES+ " INTEGER, "
					+ LevelColumns.LEVEL_ID+ " TEXT, "
					+ LevelColumns.USER_ID+ " TEXT"+ ");");
			
			db.execSQL("CREATE TABLE " + UserLevelColumns.TABLE_USERLEVEL + " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY, " + UserLevelColumns.USER_ID + " INTEGER, "
					+ UserLevelColumns.LEVEL_ID + " INTEGER, "
					+ UserLevelColumns.SKILL_POINTS+ " TEXT"+ ");");


			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ".");
			db.execSQL("DROP TABLE IF EXISTS "+UserColumns.TABLE_USER);
			db.execSQL("DROP TABLE IF EXISTS "+LevelColumns.TABLE_LEVELS);

		}		

	}

	public SokobanDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public SokobanDbAdapter open()  {
		try{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		}catch(Exception e)
		{
			System.out.println("Issue opening DB on SokobanDbAdapter");
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/*
	 * Creamos el CRUD de la base de datos
	 */

	public long createUser(String userName, String userID) {
		long now = System.currentTimeMillis();
		this.open();
		ContentValues values = new ContentValues();
		values.put(UserColumns.NAME, userName);
		values.put(UserColumns.SCORE, 0);
		values.put(UserColumns.TIME, now);
		values.put(UserColumns.LAST_PLAYED, 0);
		values.put(UserColumns.USER_ID,userID );
		return mDb.insert(UserColumns.TABLE_USER, null, values);
	}

	public long createLevel(String level,String name, int pushes, String level_id , String user_id) {
		ContentValues values = new ContentValues();
		this.open();
		values.put(LevelColumns.LEVEL, level);
		values.put(LevelColumns.NAME, name);
		values.put(LevelColumns.PUSHES, pushes);
		values.put(LevelColumns.LEVEL_ID, level_id);	
		values.put(LevelColumns.USER_ID, user_id);
		return mDb.insert(LevelColumns.TABLE_LEVELS, null, values);

	}
	
	public long createUserLevel(String level_id, String user_id, String skillpoints) {
		this.open();
		ContentValues values = new ContentValues();
		values.put(UserLevelColumns.USER_ID, user_id);
		values.put(UserLevelColumns.LEVEL_ID, level_id);
		values.put(UserLevelColumns.SKILL_POINTS, skillpoints);	
		return mDb.insert(UserLevelColumns.TABLE_USERLEVEL, null, values);

	}

	public boolean delete(String table, long rowId) {
		return mDb.delete(table, "_id =" + rowId, null) > 0;
	}

	/*
	 * Aqui llamo al mundo sokoban a usar el campo columns debe ser algo como:
	 * new String[] {LevelColumns._ID, LevelColumns.DIFICULTY,
	 * LevelColumns.LEVEL }
	 */

	public Cursor get(String table,long rowId,  String[] columns) throws SQLException{

		mCursor = mDb.query(true, table, columns, "_id =" + rowId, null,
				null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	public boolean isEmpty(String table, String[] columns){
		
		if (0 == mDb.query(false, table, columns, null, null, null, null, null, "1").getCount())
				return true;
			return false;
			
	}
	
	public Cursor getAll(String table, String[] columns) throws SQLException{

		return mDb.query(table, columns, null, null, null, null, null);
	}
	
	public Cursor getSpecific(String query) throws SQLException{

		return mDb.rawQuery(query, null);
	}

	public boolean update(String table, long rowId, ContentValues data) {

		return mDb.update(table, data, "_id = " + rowId, null) > 0;
	}

}

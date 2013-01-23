package ucab.tesis.sokoban.db;

import android.provider.BaseColumns;

public class LevelColumns implements BaseColumns {
	

	public static final String TABLE_LEVELS = "level";
	public static final String Columns[] = new String[] {LevelColumns._ID, "level", "name", "pushes", "level_id","user_id"};
	public static final String LEVEL = Columns[1];
	public static final String NAME = Columns[2];
	public static final String PUSHES = Columns[3];
	public static final String LEVEL_ID = Columns[4];
	public static final String USER_ID = Columns[5];
}

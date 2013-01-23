package ucab.tesis.sokoban.db;

import android.provider.BaseColumns;

public class UserColumns implements BaseColumns{
	
	public static final String TABLE_USER = "user";
	public static final String Columns[] = new String[] {UserColumns._ID, "time", "last_played","name","score","boxPushes", "user_id"};
	public static final String TIME = Columns[1];
	public static final String LAST_PLAYED = Columns[2];
	public static final String NAME = Columns[3];
	public static final String SCORE = Columns[4];
	public static final String BOX_PUSHES = Columns[5];
	public static final String USER_ID = Columns[6];

}

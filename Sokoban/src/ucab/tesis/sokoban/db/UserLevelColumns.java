package ucab.tesis.sokoban.db;

import android.provider.BaseColumns;

public class UserLevelColumns implements BaseColumns {
	

	public static final String TABLE_USERLEVEL = "userlevel";
	public static final String Columns[] = new String[] {UserLevelColumns._ID, "user_id", "level_id", "skill_points"};
	public static final String USER_ID = Columns[1];
	public static final String LEVEL_ID = Columns[2];
	public static final String SKILL_POINTS = Columns[3];
}

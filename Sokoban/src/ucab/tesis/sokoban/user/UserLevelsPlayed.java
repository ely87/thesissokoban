package ucab.tesis.sokoban.user;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import ucab.tesis.sokoban.sokoban;
import ucab.tesis.sokoban.db.LevelColumns;
import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.gui.SokobanGame;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class UserLevelsPlayed extends ListActivity {

	private String[] mString;
	private SokobanDbAdapter mDbHelper;
	Cursor mCursor;
	private String userID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("ENTRO EN USERLEVELS PLAYED :P ");
		mDbHelper = new SokobanDbAdapter(this);
		mDbHelper.open();
		// createData();
		fillData();
		registerForContextMenu(getListView());
		mDbHelper.close(); //
	}

	private void fillData() {
		long user_id = 0;
		try {
			FileInputStream fileSaveState = openFileInput("player");
			InputStreamReader inputreader = new InputStreamReader(fileSaveState);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line = "";
			line = buffreader.readLine();
			line = buffreader.readLine();
			user_id = Long.valueOf(line);
			this.userID = line;
			fileSaveState.close();
		} catch (Exception e) {

		}

		// mCursor = mDbHelper.get(UserLevelColumns.TABLE_USERLEVEL,user_id,
		// UserLevelColumns.Columns);
		// mCursor = mDbHelper.get(LevelColumns.TABLE_LEVELS,
		// Long.valueOf(userID), LevelColumns.Columns);
		String q = "SELECT * FROM LEVEL WHERE USER_ID='" + user_id + "'";
		mCursor = mDbHelper.getSpecific(q);
		startManagingCursor(mCursor);

		SimpleCursorAdapter levels_played = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, mCursor,
				new String[] { LevelColumns.LEVEL_ID },
				new int[] { android.R.id.text2 });
		setListAdapter(levels_played);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mCursor.moveToPosition(position);

		String FILENAME = "playedLevel";
		FileOutputStream fos;

		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject("Already played game (to play again) \n"
					+ mCursor.getString(0) + "\n" + mCursor.getString(1) + "\n"
					+ mCursor.getString(2) + "\n" + mCursor.getString(3) + "\n"
					+ mCursor.getString(4) + "\n");
			oos.close();
		} catch (Exception e) {

		}

		Intent i = new Intent(this, SokobanGame.class);
		i.putExtra(LevelColumns.LEVEL_ID, mCursor.getString(4));
		i.putExtra(sokoban.SYSTEM_DEFINED, Boolean.FALSE);
		startActivity(i);


	}

//	private void populateFields() {
//		if (userID.equals("")) {
//
//		} else {
//			String q = "SELECT * FROM LEVEL WHERE USER_ID='" + userID + "'";
//			mCursor = mDbHelper.getSpecific(q);
//			startManagingCursor(mCursor);
//
//			SimpleCursorAdapter levels_played = new SimpleCursorAdapter(this,
//					android.R.layout.simple_list_item_2, mCursor,
//					new String[] { LevelColumns.LEVEL_ID },
//					new int[] { android.R.id.text2 });
//			setListAdapter(levels_played);
//		}
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (userID.equals("")) {
//
//		} else {
//			outState.putString(UserColumns.USER_ID, userID);
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		populateFields();
//	}

}

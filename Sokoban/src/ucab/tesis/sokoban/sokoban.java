package ucab.tesis.sokoban;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.db.UserColumns;
import ucab.tesis.sokoban.gui.welcomeScreen;
import ucab.tesis.sokoban.user.Info;
import ucab.tesis.sokoban.user.UserEdit;
import ucab.tesis.sokoban.user.UserPlay;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class sokoban extends ListActivity {
	/** Called when the activity is first created. */

	public static SokobanDbAdapter mDbHelper;
	private Cursor mCursor;
	private static final int WELCOME = Menu.FIRST;
	private static final int INSERT_ID = Menu.FIRST+1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	private static final int VIEW_ID = Menu.FIRST + 3;
	private static final int EDIT_ID = Menu.FIRST + 4;
	private static final int ACTIVITY_CREATE = 0;
	private static final String TAG = "sokoban";
	private static final String FILE_NAME = "/sdcard/microban_num.txt";
	public static final String SYSTEM_DEFINED = "SystemDefined";
	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		mDbHelper = new SokobanDbAdapter(this);
		mDbHelper.open();
	//	createData();
		fillData();
		registerForContextMenu(getListView());

	}

	private void fillData() {
		System.out.println(SokobanDbAdapter.DATABASE_VERSION);

		mCursor = mDbHelper.getAll(UserColumns.TABLE_USER, UserColumns.Columns);
		startManagingCursor(mCursor);

		SimpleCursorAdapter users = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, mCursor,
				new String[] { UserColumns.NAME },
				new int[] { android.R.id.text1 });
		setListAdapter(users);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.add_user);
		return true;
	}
	
	private void createFilePlayer(String userID) {
		System.out.println("YA CREO EL ARCHIVO PLAYER !!!!!! CON EL ID" + userID);
		String FILENAME = "player";
		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject("User currentyl playing (or last played) \n"
					+ userID + "\n");
			oos.close();
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createUser();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void createUser() {
		Intent i = new Intent(this, UserEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	private void welcome(){
		Intent i = new Intent(this, welcomeScreen.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long) Esto te llevara a la actividad del usuario,
	 * en donde estaran botones de jugar y opciones extra.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mCursor.moveToPosition(position);		
		Intent i = new Intent(this, UserPlay.class);
		i.putExtra(BaseColumns._ID, id);
		i.putExtra(UserColumns.NAME, mCursor.getString(mCursor
				.getColumnIndexOrThrow(UserColumns.NAME)));
		i.putExtra(UserColumns.SCORE, mCursor.getString(mCursor
				.getColumnIndexOrThrow(UserColumns.SCORE)));
		createFilePlayer(String.valueOf(mCursor.getString(mCursor
				.getColumnIndexOrThrow(UserColumns.USER_ID))));
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.setHeaderTitle(mCursor.getString(mCursor
				.getColumnIndexOrThrow(UserColumns.NAME)));
		menu.add(0, VIEW_ID, 0, R.string.user_info);
		menu.add(0, EDIT_ID, 0, R.string.user_edit);
		menu.add(0, DELETE_ID, 0, R.string.user_delete);
	
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info;

		try {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case DELETE_ID: {
			mDbHelper.delete(UserColumns.TABLE_USER, info.id);
			fillData();
			return true;
		}
		case EDIT_ID: {
			Intent i = new Intent(this, UserEdit.class);
			i.putExtra(BaseColumns._ID, info.id);
			startActivity(i);
			return true;
		}
		case VIEW_ID: {
			Intent i = new Intent(this, Info.class);
			Cursor c = mDbHelper.get(UserColumns.TABLE_USER, info.id,
					UserColumns.Columns);
			startManagingCursor(c);
			i.putExtra(UserColumns.NAME,
					c.getString(c.getColumnIndexOrThrow(UserColumns.NAME)));
			i.putExtra(UserColumns.SCORE,
					c.getString(c.getColumnIndexOrThrow(UserColumns.SCORE)));
			i.putExtra(UserColumns.TIME,
					c.getString(c.getColumnIndexOrThrow(UserColumns.TIME)));
			startActivity(i);
			return true;

		}

		}
		return super.onContextItemSelected(item);

	}

}
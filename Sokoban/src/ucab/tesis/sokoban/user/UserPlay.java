package ucab.tesis.sokoban.user;

import ucab.tesis.sokoban.gui.SokobanGame;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserPlay extends ListActivity {

	private static final String[] mString = { "Play", "Options" };
	private static final int PLAY = 0;
	private static final int OPTIONS = 1;
	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mDbHelper = new SokobanDbAdapter(this);
		// mDbHelper.open();
		// createData();
		fillData();
	}

	private void fillData() {
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mString));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i;
		switch (position) {
		case PLAY: // ///////////////////////////////------------------------------------------------
				i = new Intent(this, SokobanGame.class);
				i.putExtra("SystemDefined", Boolean.FALSE);
				startActivity(i);
			break;
		case OPTIONS:
			i = new Intent(this, UserOptions.class);
			startActivity(i);
			break;
		}

	}

}

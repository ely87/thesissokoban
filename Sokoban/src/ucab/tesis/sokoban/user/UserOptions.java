package ucab.tesis.sokoban.user;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserOptions extends ListActivity {

	private static final String[] mString = { "Check Score", "Games Played" };
	private static final int CHECH_SCORE = 0;
	private static final int GAMES_PLAYED = 1;
	Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		long user_id = 0;
		
		try {
			FileInputStream fileSaveState = openFileInput("player");
			InputStreamReader inputreader = new InputStreamReader(
					fileSaveState);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line = "";
			line = buffreader.readLine();
			line = buffreader.readLine();
			user_id = Long.valueOf(line);
			fileSaveState.close();
		} catch (Exception e) {

		}
		
		System.out.println("opcion de posicion: " + position);
		
		switch (position) {
		
		case CHECH_SCORE:
			////////////
			break;
		case GAMES_PLAYED:
			i = new Intent(this, UserLevelsPlayed.class);
			i.putExtra("SystemDefined", Boolean.FALSE);
			startActivity(i);
			break;

		}
	}
}

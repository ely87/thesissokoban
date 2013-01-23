package ucab.tesis.sokoban.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.db.UserColumns;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserEdit extends Activity {

	private EditText mNameText;
	private Long mUserID;
	private Long mRowId;
	private SokobanDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new SokobanDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.user_edit);
		mNameText = (EditText) findViewById(R.id.user_name);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(BaseColumns._ID) : null;


		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(BaseColumns._ID) : null;
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Bundle bundle = new Bundle();

				bundle.putString(UserColumns.NAME, mNameText.getText()
						.toString());
				if (mRowId != null) {
					bundle.putString(BaseColumns._ID, mRowId.toString());
				}
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}

		});
		mDbHelper.close();
	}

	private String saveUserOnServer(String userName) {
		String userID = "";

		URL url;
		try {
			URL url_post = new URL(
					"http://sokofighter.appspot.com/sokoban/json/android/level/");

			String commit = "user_name=" + userName;

			// Send data
			System.out.println(commit);
			try {
				HttpURLConnection conn = (HttpURLConnection) url_post
						.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				OutputStreamWriter wr = new OutputStreamWriter(conn
						.getOutputStream());
				wr.write(commit);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String answer = rd.readLine();
				JSONObject response = new JSONObject(answer);
				userID = response.getString("user_id");
				rd.close();
				wr.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			userID = "";
		}
		return userID;
	}



	private void populateFields() {
		if (mRowId != null) {
			Cursor name = mDbHelper.get(UserColumns.TABLE_USER, mRowId,
					new String[] { UserColumns.NAME });
			startManagingCursor(name);
			mNameText.setText(name.getString(name
					.getColumnIndexOrThrow(UserColumns.NAME)));
			stopManagingCursor(name);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(BaseColumns._ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String name = mNameText.getText().toString().trim();

		if (mRowId == null) {
			String userID = saveUserOnServer(name);
			long id = mDbHelper.createUser(name, userID);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			ContentValues cv = new ContentValues();
			cv.put(UserColumns.NAME, name.trim());
			mDbHelper.update(UserColumns.TABLE_USER, mRowId, cv);
		}
	}

}

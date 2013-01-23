package ucab.tesis.sokoban.user;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.db.SokobanDbAdapter;
import ucab.tesis.sokoban.db.UserColumns;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class Info extends Activity {

	SokobanDbAdapter mDbHelper;
	private String mName, mScore, mTime,mBoxPushes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.user_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.user_info_title);
		getValues(savedInstanceState);
		
		TextView tview = (TextView)findViewById(R.id.user_info_name);
		TextView tname = (TextView)findViewById(R.id.user_info_title);
		
		tname.setText(mName);
		tview.setText("Score: "+mScore+"\nTime: "+mTime+"\nBox Pushes: "+mBoxPushes);
		
	}

	private void getValues(Bundle bundle) {
		if (null == bundle) {
			Bundle extras = getIntent().getExtras();
			bundle = extras;
		}
		
		if (null != bundle){
			mName = bundle.getString(UserColumns.NAME) != null ? bundle.getString(UserColumns.NAME) : "Info";
			mScore = bundle.getString(UserColumns.SCORE) != null ? bundle.getString(UserColumns.SCORE) : "0";
			mTime = bundle.getString(UserColumns.TIME) != null ? bundle.getString(UserColumns.TIME) : "0";
			mBoxPushes = bundle.getString(UserColumns.BOX_PUSHES) != null ? bundle.getString(UserColumns.BOX_PUSHES) : "0";
		}
		
	}
	
}

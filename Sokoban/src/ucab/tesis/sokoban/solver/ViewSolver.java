package ucab.tesis.sokoban.solver;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.gui.SokobanView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ViewSolver extends Activity {

	private static SokobanView mSokobanView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Esto le quita el titulo a la app
		setContentView(R.layout.sokoban_game);
		mSokobanView = (SokobanView)findViewById(R.id.VistaSokoban);
		
	}


}

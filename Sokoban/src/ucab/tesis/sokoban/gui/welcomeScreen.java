package ucab.tesis.sokoban.gui;

import ucab.tesis.sokoban.R;
import ucab.tesis.sokoban.sokoban;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class welcomeScreen extends Activity {
	private Context mContext;
	private static final int STOPSPLASH = 0;
	// time in milliseconds
	private static final long SPLASHTIME = 3000;
	private ImageView splash;
	final welcomeScreen welcomeScreen = this;
	private Thread splashThread;  
	public  int sonido;
	private MediaPlayer mp;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	//	mp = MediaPlayer.create(getBaseContext(), R.raw.intro_sf); /*Gets your 
	//	soundfile from res/raw/sound.ogg */
	//	mp.start(); //Starts your sound
//		MediaPlayer player = new MediaPlayer();
//		try {
//			player.setDataSource("/data/data/ucab/tesis/sokoban/introSF.mp3");
//			player.prepare();
//			player.start();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		setContentView(R.layout.splash_screen);
		splash = (ImageView) findViewById(R.drawable.splashvertical);
		splashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						// Wait given period of time or exit on touch
						
						wait(5000);
					}
				} catch (InterruptedException ex) {
				}

				finish();
				Intent intent = new Intent();
				intent.setClass(welcomeScreen, sokoban.class);
				startActivity(intent);				
				this.stop();

			}
		};
		splashThread.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		synchronized(splashThread){
            splashThread.notifyAll();
        }
		return true;

	}
	/**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(splashThread){
                splashThread.notifyAll();
            }
        }
        return true;
    } 
    
   

}

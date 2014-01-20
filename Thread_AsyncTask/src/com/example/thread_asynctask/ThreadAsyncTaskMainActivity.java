package com.example.thread_asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ThreadAsyncTaskMainActivity extends Activity {
	private static final String URL_SENSOR = "http://lmi92.cnam.fr/ds2438/ds2438/";
	private Button startButton, stopButton, setButton;
	private ProgressBar progressBar;
	private EditText editText;
	private TextView textView;
	private String url;
	private long top, bot, acquitionTime = 3000; // 3 s en ms
	private WorkAsyncTask task;
	private HumiditySensorAbstract ds2438;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread_async_task_main);
		// on récupère les différentes vues
		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		setButton = (Button) findViewById(R.id.setButton);
		editText = (EditText) findViewById(R.id.editText);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		textView = (TextView) findViewById(R.id.textView1);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		url = ThreadAsyncTaskMainActivity.loadURL_SENSOR(this);

	}

	/*
	 * Méthode appelée pour commencer l'acquisition
	 */
	public void onClickStart(View v) {
		Toast.makeText(this, "Starting...", Toast.LENGTH_SHORT).show();
		task = new WorkAsyncTask();
		task.execute();
		
	}

	/*
	 * Méthode appelée pour arrêter l'acquisition
	 */
	public void onClickStop(View v) {
		Toast.makeText(this, "Stopping...", Toast.LENGTH_SHORT).show();
		task.cancel(true);
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		textView.setText("Acquisition... Appuyez sur start");

	}

	/*
	 * Méthode appelée pour valider la saisie du nouvel url
	 */
	public void onClickSet(View v) {
		url = editText.getText().toString();
		setButton.setVisibility(View.INVISIBLE);
		startButton.setEnabled(true);
		textView.setVisibility(View.VISIBLE);
		editText.setVisibility(View.INVISIBLE);
		ThreadAsyncTaskMainActivity.saveURL_SENSOR(this, url);

	}

	/*
	 * Méthode appelée lorsque l'utilisateur clique sur l'écran pour modifier
	 * l'url
	 */
	public void updateUrl(View v) {
		setButton.setVisibility(View.VISIBLE);
		textView.setVisibility(View.INVISIBLE);
		editText.setVisibility(View.VISIBLE);
		editText.setText(url);
		stopButton.setEnabled(false);
		startButton.setEnabled(false);
	}

	/*
	 * Methode permettant la persistance de l'Url - sauvegarde l'url entré.
	 */

	private static void saveURL_SENSOR(Context context, String url) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString("url_sensor", url);
		edit.commit();
	}

	/*
	 * Charge la préférence sauvegardée si elle existe, sinon retourne la
	 * préférence par défaut.
	 */
	private static String loadURL_SENSOR(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString("url_sensor", URL_SENSOR);
	}

	private class WorkAsyncTask extends AsyncTask<Void, Float, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (!isCancelled()) {

				try {
					ds2438 = new HTTPHumiditySensor(url);
					top = System.currentTimeMillis();
					float taux = ds2438.value();
					bot = System.currentTimeMillis();
					long time = top - bot;
					publishProgress(taux);
					SystemClock.sleep(acquitionTime - time);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			Time currentTime = new Time();
			currentTime.setToNow();
			String date = currentTime.format("%d.%m.%Y %H:%M:%S");
			// String date = currentTime.format("%H:%M:%S");
			textView.setText("[" + date + "] ds2438 : " + values[0]);
			progressBar.setProgress(values[0].intValue());
		}

		@Override
		protected void onPreExecute() {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
		}

		@Override
		protected void onCancelled() {
			progressBar.setProgress(0);

		}
	}
}

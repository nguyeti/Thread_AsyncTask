package com.example.thread_asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Classe de lecture d'un capteur sur le Web
 * 
 * @version de test
 * @author jm Douin
 */
public class HTTPHumiditySensor extends HumiditySensorAbstract {
	// public final static String DEFAULT_HTTP_SENSOR =
	// "http://localhost:8999/ds2438/";
	public final static String DEFAULT_HTTP_SENSOR = "http://10.0.2.2:8999/ds2438/";
	public static final long ONE_MINUTE = 60L * 1000L;

	/** l'URL associ�e au capteur */
	private static String urlSensor;

	/**
	 * Constructeur d'une connexion avec un Capteur, valeur par d�faut
	 * 
	 * @param urlSensor
	 *            l'URL du capteur sur le Web en protocole HTTP
	 */
	private HTTPHumiditySensor() {
		this(DEFAULT_HTTP_SENSOR);
	}

	/**
	 * Constructeur d'une connexion avec un Capteur, syntaxe habituelle
	 * http://site:port/
	 * 
	 * @param urlSensor
	 *            l'URL du capteur sur le Web en protocole HTTP
	 */
	public HTTPHumiditySensor(String urlSensor) {
		this.urlSensor = urlSensor;
	}

	/**
	 * Lecture de la valeur de humidit� relative
	 **/
	public float value() throws Exception {
		StringTokenizer st = new StringTokenizer(request(), "=");
		st.nextToken();
		float f = Float.parseFloat(st.nextToken()) * 10F;
		return ((int) f) / 10F;
	}

	public long minimalPeriod() {
		if (urlSensor.startsWith("http://localhost")
				|| urlSensor.startsWith("http://10.0.2.2"))
			return 200L; // � valider
		else
			return 2000L; // ONE_MINUTE;
	}

	/**
	 * lecture de l'URL
	 * 
	 * @return l'url associ�e � ce capteur
	 */
	public String getUrl() {
		return HTTPHumiditySensor.urlSensor;
	}

	/**
	 * Lecture des informations issues de ce capteur
	 * 
	 * @return la totalit� de la page lue
	 * @throws Exception
	 *             en cas d'erreur
	 */
	private String request() throws Exception {

		/****/
		URL url = new URL(urlSensor);
		URLConnection connection = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()), 128); // 78 caract�res
		String result = new String("");
		String inputLine = in.readLine();
		while (inputLine != null) {
			result = result + inputLine;
			inputLine = in.readLine();
		}
		in.close();
		return result;
		/****/

	}
	
}

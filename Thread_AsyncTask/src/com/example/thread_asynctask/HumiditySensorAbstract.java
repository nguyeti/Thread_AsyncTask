package com.example.thread_asynctask;

public abstract class HumiditySensorAbstract {
	/** valeur du capteur, pr�cision de 0.1 */
	public abstract float value() throws Exception;
	public abstract String getUrl();
		/* p�riode minimale entre deux lectures */
	public abstract long minimalPeriod();
}
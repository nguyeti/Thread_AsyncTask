package com.example.thread_asynctask;

public abstract class HumiditySensorAbstract {
	/** valeur du capteur, précision de 0.1 */
	public abstract float value() throws Exception;
	public abstract String getUrl();
		/* période minimale entre deux lectures */
	public abstract long minimalPeriod();
}
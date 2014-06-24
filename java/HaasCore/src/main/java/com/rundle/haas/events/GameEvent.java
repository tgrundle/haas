package com.rundle.haas.events;

import java.io.Serializable;
import java.lang.reflect.Method;



public abstract class GameEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3923936106189497504L;

	public final void handleEvent(GameEventHandler handler) {
		try {
			String handlerMethod = "on" + this.getClass().getSimpleName();
			Method method = handler.getClass().getMethod(handlerMethod, this.getClass());
			method.invoke(handler, this);
		} catch (RuntimeException re) {
			throw re;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
package com.rundle.haas.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import android.content.Intent;
//import android.support.v4.content.LocalBroadcastManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

//import com.rundle.haas.events.GameEvent;
import com.rundle.haas.events.GameEventHandler;

public class GameEventHandlerAdapter implements InvocationHandler {

	private final HaasActivity context;

	public static GameEventHandler newInstance(HaasActivity context) {
		return (GameEventHandler) java.lang.reflect.Proxy.newProxyInstance(
				context.getClassLoader(),
				new Class[] { GameEventHandler.class },
				new GameEventHandlerAdapter(context));
	}

	private GameEventHandlerAdapter(HaasActivity context) {
		this.context = context;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// Intent eventIntent = new Intent("HAAS_EVENT");
		// eventIntent.putExtra("Method", method.getName());
		// eventIntent.putExtra("Args", ((GameEvent) args[0]));
		// LocalBroadcastManager.getInstance(context).sendBroadcast(eventIntent);
		final Method theMethod = method;
		final Object[] theArgs = args;

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			public void run() {
				try {
					theMethod.invoke(context, theArgs);
				} catch (InvocationTargetException ite) {
					Log.e("HaasActivity", "BroadcastReceiver", ite.getTargetException());
				} catch (Throwable t) {
					Log.e("HaasActivity", "BroadcastReceiver", t);
				}
			}
		});

		return null;

	}

}

package com.rundle.haas.android;

import java.util.LinkedList;
import java.util.Queue;


public abstract class UIAction implements Runnable {
	static final Queue<UIAction> GLOBAL_QUEUE = new LinkedList<UIAction>();
	static final Object SYNC_OBJ = new Object();
	
	static UIAction ACTIVE_ACTION_CHAIN;
	protected final HaasActivity context;

	private UIAction nextAction;

//	public final UIAction getNextAction() {
//		return nextAction;
//	}

	protected UIAction(HaasActivity context) {
		this.context = context;
	}

	final void setNextAction(UIAction nextAction) {
		if (this != nextAction) {
            if (this.nextAction == null) {
                this.nextAction = nextAction;
            } else {
                this.nextAction.setNextAction(nextAction);
            }
        }
	}

	public final void run() {
		boolean doRun;
		
		synchronized (SYNC_OBJ) {
			if (ACTIVE_ACTION_CHAIN != null) {
				GLOBAL_QUEUE.add(this);
				doRun = false;
			} else {
				ACTIVE_ACTION_CHAIN = this;
				doRun = true;
			}
		}

		if (doRun) {
			doRun();
		}
	}

    protected final void doNext() {
        boolean doNextRun = false;
        if (nextAction != null) {
            doNextRun = true;
        } else {
            synchronized (UIAnimationAction.SYNC_OBJ) {
                nextAction = UIAction.GLOBAL_QUEUE.poll();
                if (nextAction != null) {
                    doNextRun = true;
                } else {
                    UIAction.ACTIVE_ACTION_CHAIN = null;
                }
            }
        }

        if(doNextRun) {
            nextAction.doRun();
        }
    }
	abstract void doRun();
}



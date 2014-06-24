package com.rundle.haas.android;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rundle.haas.Card;
import com.rundle.haas.PlayerHandlerLocalInteractive;
import com.rundle.haas.android.holders.CardHolder;
import com.rundle.haas.android.holders.PlayerHolder;
import com.rundle.haas.events.CardSelectionEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Created by timothy on 6/22/14.
 */
public class UIEnableTouchForCardSelectionAction extends UIAction {

    private final PlayerHandlerLocalInteractive phi;
    private final PlayerHolder player;
    private final Class<? extends CardSelectionEvent> eventType;

    public UIEnableTouchForCardSelectionAction(HaasActivity context, PlayerHolder player, PlayerHandlerLocalInteractive phi, Class<? extends CardSelectionEvent> eventType) {
        super(context);
        this.phi = phi;
        this.player=player;
        this.eventType = eventType;
    }

    @Override
    void doRun() {
        final UIAction uiAction = this;

        for (final CardHolder cardHolder : player.hand) {
            Log.d("HaasActivity", "Enabling touch on: " + cardHolder.card.toString());
            cardHolder.getView().setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int [] locations = new int[2];
                    context.mainView.getLocationOnScreen(locations);

                    float x_cord = event.getRawX() - locations[0];
                    float y_cord = event.getRawY() - locations[1];

                    if (x_cord < 0) {
                        x_cord = 0;
                    } else if (x_cord  + context.drawMaster.getCARD_WIDTH() > context.drawMaster.getScreenWidth()) {
                        x_cord = context.drawMaster.getScreenWidth() - context.drawMaster.getCARD_WIDTH();
                    }
                    if (y_cord < 0) {
                        y_cord = 0;
                    } else if (y_cord  + context.drawMaster.getCARD_HEIGHT()> context.drawMaster.getScreenHeight()) {
                        y_cord = context.drawMaster.getScreenHeight() - context.drawMaster.getCARD_HEIGHT();
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            cardHolder.getView().bringToFront();
                            break;
                        case MotionEvent.ACTION_MOVE:

                            cardHolder.getView().setX(x_cord);
                            cardHolder.getView().setY(y_cord);

                            break;
                        case MotionEvent.ACTION_UP:

                            if (x_cord < context.drawMaster.getCARD_WIDTH() && y_cord < context.drawMaster.getCARD_HEIGHT()) {

                                cardHolder.setInHand(false);

                                try {
                                    CardSelectionEvent selectedCardEvent = eventType.getConstructor(Card.class).newInstance(cardHolder.card);
                                    phi.messagesFromClientQueue.offer(selectedCardEvent);
                                    synchronized (phi.SYNC_MSG_FROM_CLIENT) {
                                        phi.SYNC_MSG_FROM_CLIENT.notify();
                                    }

                                } catch (Exception ignored) {
                                }

                                UIAction animation = null;
                                LinkedList<UIAction> actionQueue = context.drawMaster.removeCardFromHand(context, player.hand);
                                for (UIAction action : actionQueue) {
                                    if (animation == null) {
                                        animation = action;
                                    } else {
                                        animation.setNextAction(action);
                                    }
                                }
                                cardHolder.getView().setX(x_cord);
                                cardHolder.getView().setY(y_cord);
                                if (animation != null) {
                                    animation.run();
                                }

                            } else {
                                cardHolder.getView().setX(cardHolder.getX());
                                cardHolder.getView().setY(cardHolder.getY());
                                context.drawMaster.cardReturnedToHand(context, player.hand, cardHolder);
                            }

                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
        uiAction.doNext();
    }
}

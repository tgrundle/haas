/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use context file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.rundle.haas.android;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.rundle.haas.Card;
import com.rundle.haas.R;
import com.rundle.haas.android.animation.FlipCardAnimation;
import com.rundle.haas.android.animation.MoveViewHolderAnimation;
import com.rundle.haas.android.holders.CardHolder;

public class DrawMaster {

    // MDPI - Normal 600 x 800
    private static final float PLAYER_HAND_ADJ_INDP = 125;
    private static final float PLAYER_NAME_WIDTH_INDP = 75;
    private static final float PLAYER_NAME_HEIGHT_INDP = 20;
    private static float[] COORD_Y_NAME_INDP = { 665, 365, 115, 115, 365 };
    private static float[] COORD_Y_HAND_INDP = { 565, 390, 140, 140, 390 };
    private static float[] COORD_Y_BID_INDP = { 540, 405, 155, 155, 405 };
    private static float KIDDIE_SEP_INDP = 10;
    private static float KIDDIE_Y_INDP = 15;

    private float PLAYER_HAND_ADJ;
    private float[] COORD_Y_NAME;
    private float[] COORD_Y_HAND;
    private float[] COORD_Y_BID;
    private float KIDDIE_SEP;
    private float PLAYER_NAME_WIDTH;
    private float PLAYER_NAME_HEIGHT;
    private float KIDDIE_Y;
    // TODO Add Winning Bid location.

    private float CARD_WIDTH;
    private float CARD_HEIGHT;

    private float TRUMP_WIDTH;
    private float TRUMP_HEIGHT;

    // Background
    private float screenWidth;
    private float screenHeight;
    private float kiddiesBaseX = -1;

    // private Paint mBGPaint;

    // Card stuff
    // public final Paint mSuitPaint = new Paint();
    private final Bitmap[] mCardBitmap;
    public final Bitmap[] mSuitBitmap;
    public final Bitmap[] mSuitLargeBitmap;
    public final Bitmap mCardHidden;

    public DrawMaster(Context context) {

        mCardBitmap = new Bitmap[24];
        mSuitBitmap = new Bitmap[4];
        mSuitLargeBitmap = new Bitmap[4];
        mCardHidden = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.deck1);
        mSuitBitmap[0] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.club);
        mSuitBitmap[1] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.diamond);
        mSuitBitmap[2] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.spade);
        mSuitBitmap[3] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.heart);

        mSuitLargeBitmap[0] = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.club_large);
        mSuitLargeBitmap[1] = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.diamond_large);
        mSuitLargeBitmap[2] = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.spade_large);
        mSuitLargeBitmap[3] = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.heart_large);

        mCardBitmap[0] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.c9);
        mCardBitmap[1] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ct);
        mCardBitmap[2] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cj);
        mCardBitmap[3] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cq);
        mCardBitmap[4] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ck);
        mCardBitmap[5] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ca);

        mCardBitmap[6] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.d9);
        mCardBitmap[7] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.dt);
        mCardBitmap[8] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.dj);
        mCardBitmap[9] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.dq);
        mCardBitmap[10] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.dk);
        mCardBitmap[11] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.da);

        mCardBitmap[12] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.s9);
        mCardBitmap[13] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.st);
        mCardBitmap[14] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sj);
        mCardBitmap[15] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sq);
        mCardBitmap[16] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sk);
        mCardBitmap[17] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sa);

        mCardBitmap[18] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.h9);
        mCardBitmap[19] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ht);
        mCardBitmap[20] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.hj);
        mCardBitmap[21] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.hq);
        mCardBitmap[22] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.hk);
        mCardBitmap[23] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ha);

    }

    // public void setScreenSize(int width, int height, HaasActivity context) {
    public void setScreenSize(View mainView) {
        Context context = mainView.getContext();

        Log.d("DrawMaster", "left: " + mainView.getLeft());
        Log.d("DrawMaster", "right: " + mainView.getRight());
        Log.d("DrawMaster", "top: " + mainView.getTop());
        Log.d("DrawMaster", "bottom: " + mainView.getBottom());

        screenWidth = mainView.getRight() - mainView.getLeft();
        screenHeight = mainView.getBottom() - mainView.getTop();

        int[] locations = new int[2];
        mainView.getLocationOnScreen(locations);
        float xScale = (screenWidth + locations[0]) / convertDpToPixel(600.0f, context);
        float yScale = (screenHeight + locations[1]) / convertDpToPixel(800.0f, context);
        PLAYER_HAND_ADJ = convertDpToPixel(PLAYER_HAND_ADJ_INDP, context) * xScale;
        COORD_Y_NAME = new float[] {
                convertDpToPixel(COORD_Y_NAME_INDP[0], context) * yScale,
                convertDpToPixel(COORD_Y_NAME_INDP[1], context) * yScale,
                convertDpToPixel(COORD_Y_NAME_INDP[2], context) * yScale,
                convertDpToPixel(COORD_Y_NAME_INDP[3], context) * yScale,
                convertDpToPixel(COORD_Y_NAME_INDP[4], context) * yScale
        };
        COORD_Y_HAND = new float[] {
                convertDpToPixel(COORD_Y_HAND_INDP[0], context) * yScale,
                convertDpToPixel(COORD_Y_HAND_INDP[1], context) * yScale,
                convertDpToPixel(COORD_Y_HAND_INDP[2], context) * yScale,
                convertDpToPixel(COORD_Y_HAND_INDP[3], context) * yScale,
                convertDpToPixel(COORD_Y_HAND_INDP[4], context) * yScale
        };
        COORD_Y_BID = new float[] {
                convertDpToPixel(COORD_Y_BID_INDP[0], context) * yScale,
                convertDpToPixel(COORD_Y_BID_INDP[1], context) * yScale,
                convertDpToPixel(COORD_Y_BID_INDP[2], context) * yScale,
                convertDpToPixel(COORD_Y_BID_INDP[3], context) * yScale,
                convertDpToPixel(COORD_Y_BID_INDP[4], context) * yScale,
        };
        KIDDIE_SEP = convertDpToPixel(KIDDIE_SEP_INDP, context) * xScale;
        KIDDIE_Y = convertDpToPixel(KIDDIE_Y_INDP, context) * yScale;
        PLAYER_NAME_WIDTH = convertDpToPixel(PLAYER_NAME_WIDTH_INDP, context) * xScale;
        PLAYER_NAME_HEIGHT = convertDpToPixel(PLAYER_NAME_HEIGHT_INDP, context) * yScale;

        CARD_WIDTH = mCardHidden.getWidth() * 1f * xScale;
        CARD_HEIGHT = mCardHidden.getHeight() * 1f * yScale;

        TRUMP_WIDTH = mSuitLargeBitmap[0].getWidth() * xScale;
        TRUMP_HEIGHT = mSuitLargeBitmap[0].getHeight() * yScale;

        Log.d("DrawMaster", "CARD_WIDTH:" + CARD_WIDTH + " CARD_HEIGHT:" + CARD_HEIGHT + " TRUMP_WIDTH:" + TRUMP_WIDTH
                + " TRUMP_HEIGHT:" + TRUMP_HEIGHT);
        kiddiesBaseX = (screenWidth - (3 * CARD_WIDTH + 2 * KIDDIE_SEP)) / 2;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public float getCARD_WIDTH() {
        return CARD_WIDTH;
    }

    public float getCARD_HEIGHT() {
        return CARD_HEIGHT;
    }

    public float getTRUMP_WIDTH() {
        return TRUMP_WIDTH;
    }

    public float getTRUMP_HEIGHT() {
        return TRUMP_HEIGHT;
    }

    public float getPLAYER_NAME_WIDTH() {
        return PLAYER_NAME_WIDTH;
    }

    public float getPLAYER_NAME_HEIGHT() {
        return PLAYER_NAME_HEIGHT;
    }

    public float getKiddieX() {
        if (kiddiesBaseX == -1) {
            return -1;
        } else {
            return kiddiesBaseX
                    + (TableMaster.INSTANCE.kiddie.size() * (CARD_WIDTH + KIDDIE_SEP));
        }
    }

    public float getKiddieY() {
        return KIDDIE_Y;
    }

    public float getPlayerNameX(int position) {
        float x;
        if (position == 0) {
            x = (screenWidth / 2) - (CARD_WIDTH / 2);
        } else {
            float adj = position < 3 ? -1 * (PLAYER_HAND_ADJ + CARD_WIDTH / 2 + PLAYER_NAME_WIDTH)
                    : PLAYER_HAND_ADJ + CARD_WIDTH / 2;
            x = (screenWidth / 2) + adj;
        }
        return x;
    }

    public float getPlayerNameY(int position) {
        return COORD_Y_NAME[position];
    }

    public float getPlayerBidX(int position) {
        float x;
        if (position == 0) {
            x = (screenWidth / 2) - (CARD_WIDTH / 2);
        } else {
            // find middle point
            float adj = position < 3 ? -1 * PLAYER_HAND_ADJ + 10 : PLAYER_HAND_ADJ - 70;
            x = (screenWidth / 2) + adj;
        }
        return x;
    }

    public float getPlayerBidY(int position) {
        return COORD_Y_BID[position];
    }

    // - .5 * card
    // - .75 * card
    // - 1 * Card
    public float getPlayerHandX(int position, int cardsInHand) {
        float x;
        if (position == 0) {

            float adj = ((cardsInHand * CARD_WIDTH / 2) + CARD_WIDTH); // why 4
            // x = (screenWidth - ((cardsInHand * CARD_WIDTH /2) + ((cardsInHand - 1)
            // * getCardSeparater()))) / 2;

            x = (screenWidth - adj) / 2;
        } else {
            // find middle point
            float adj = (position < 3 ? -1 * (PLAYER_HAND_ADJ + CARD_WIDTH) : PLAYER_HAND_ADJ);
            x = (screenWidth / 2) + adj;
        }
        return x;
    }

    public float getPlayerHandY(int position) {
        return COORD_Y_HAND[position];
    }

    public float getPlayerPlayX(int position) {
        float x;
        if (position == 0) {
            x = (screenWidth - CARD_WIDTH) / 2;
        } else {
            // find middle point
            float adj = position < 3 ? -1 * (PLAYER_HAND_ADJ + CARD_WIDTH / 4) : (PLAYER_HAND_ADJ - 3 * CARD_WIDTH / 4);
            x = (screenWidth / 2) + adj;
        }
        return x;
    }

    public float getPlayerPlayY(int position) {
        // TODO Make 30 Variable and convert from DP to PX
        float handY = getPlayerHandY(position);
        float playY;
        if (position == 0) {
            playY = handY - CARD_HEIGHT - (3 * getCardSeparater());
        } else if (position == 1 || position == 4) {
            playY = handY - CARD_HEIGHT / 2;
        } else {
            playY = handY + CARD_HEIGHT / 2;
        }
        return playY;
    }

    public float getCardSeparater() {
        return KIDDIE_SEP / 2;
    }

    public Bitmap bitmapForCard(Card card) {

        int index = -1;

        switch (card.suit) {
            case CLUB:
                index = 0;
                break;
            case DIAMOND:
                index = 6;
                break;
            case SPADE:
                index = 12;
                break;
            case HEART:
                index = 18;
                break;
        }
        switch (card.value) {
            case NINE:
                break;
            case TEN:
                index += 1;
                break;
            case JACK:
                index += 2;
                break;
            case QUEEN:
                index += 3;
                break;
            case KING:
                index += 4;
                break;
            case ACE:
                index += 5;
                break;
        }

        return mCardBitmap[index];
    }

    public LinkedList<UIAction> createInsertCardActionQueue(
            HaasActivity context, CardHolder card, List<CardHolder> hand,
            boolean flip) {
        LinkedList<UIAction> actionQueue = new LinkedList<UIAction>();
        LinkedList<UIAction> postInsertionActionQueue = new LinkedList<UIAction>();
        // re-order cards
        float destX = -1;
        float destY = getPlayerHandY(0);

        float newX = getPlayerHandX(0, hand.size());

        int animationPoint = -1;
        for (CardHolder cardInHand : hand) {
            int sortOrder = cardInHand.compareTo(card);
            if (sortOrder > 0 && destX == -1) {
                destX = newX;
                // newX = newX + getCardSeparater() + context.drawMaster.CARD_WIDTH;
                newX = newX + context.drawMaster.CARD_WIDTH / 2;
                animationPoint = actionQueue.size();
            }
            if (animationPoint == -1) {
                actionQueue.add(new MoveViewHolderAnimation(context, cardInHand,
                        newX, destY, HaasActivity.ANIMATION_CARDINHAND_SPEED));
            } else {
                actionQueue.add(animationPoint, new MoveViewHolderAnimation(context,
                        cardInHand, newX, destY, HaasActivity.ANIMATION_CARDINHAND_SPEED));
                // Forces the card to the front
                postInsertionActionQueue.add(new MoveViewHolderAnimation(context,
                        cardInHand, newX, destY, HaasActivity.ANIMATION_CARDINHAND_SPEED));
            }
            // newX = newX + getCardSeparater() + context.drawMaster.CARD_WIDTH;
            newX = newX + context.drawMaster.CARD_WIDTH / 2;
        }
        if (destX < 0) {
            destX = newX;
        }
        actionQueue
                .add(new MoveViewHolderAnimation(context, card, destX, destY, HaasActivity.ANIMATION_CARDDEALT_SPEED));
        if (flip) {
            actionQueue.add(new FlipCardAnimation(context, card, 5));
        }

        actionQueue.addAll(postInsertionActionQueue);
        return actionQueue;
    }

    public LinkedList<UIAction> removeCardFromHand(
            HaasActivity context, List<CardHolder> hand) {

        LinkedList<UIAction> actionQueue = new LinkedList<UIAction>();
        boolean leftOfPlay = true;
        for (CardHolder cardHolder : hand) {
            if (!cardHolder.isInHand()) {
                leftOfPlay = false;
            } else if (leftOfPlay) {
                actionQueue.add(new MoveViewHolderAnimation(context, cardHolder, cardHolder.getX() + CARD_WIDTH / 4,
                        cardHolder.getY(), HaasActivity.ANIMATION_CARDINHAND_SPEED));
            } else {
                actionQueue.add(new MoveViewHolderAnimation(context, cardHolder, cardHolder.getX() - CARD_WIDTH / 4,
                        cardHolder.getY(), HaasActivity.ANIMATION_CARDINHAND_SPEED));
            }
        }

        return actionQueue;
    }

    public void cardReturnedToHand(HaasActivity context, List<CardHolder> hand, CardHolder cardHolder) {
        LinkedList<UIAction> actionQueue = new LinkedList<UIAction>();
        boolean leftOfPlay = true;
        for (CardHolder cardHolderForCardInHand : hand) {
            if (cardHolder.equals(cardHolderForCardInHand)) {
                leftOfPlay = false;
            } else if (!leftOfPlay) {
                cardHolderForCardInHand.getView().bringToFront();
            }
        }
    }

    public void sortHand(List<CardHolder> hand) {

        // Need to move bars
        CardHolder previousCard = hand.get(0);
        CardHolder currentCard = null;
        for (int i = 1; i < hand.size(); i++) {

            if (i == 0) {
                continue;
            }

            previousCard = hand.get(i - 1);
            currentCard = hand.get(i);

            if (currentCard.compareTo(previousCard) == -1) {
                float x = previousCard.getX();
                float y = previousCard.getY();

                previousCard.setPosition(currentCard.getX(), currentCard.getY());
                previousCard.getView().setX(currentCard.getX());
                previousCard.getView().setY(currentCard.getY());

                currentCard.setPosition(x, y);
                currentCard.getView().setX(x);
                currentCard.getView().setY(y);

                hand.set(i, previousCard);
                hand.set(i - 1, currentCard);
                i = i - 2;
            }
        }

        for (CardHolder cardHolder : hand) {
            cardHolder.getView().bringToFront();
        }
    }

    public boolean isCoordInPlayArea(float x, float y) {
        // need to account that x/y were altered to
        // display card center point under finger

        float playX = getPlayerPlayX(0);
        float playY = getPlayerPlayY(0);
        float minX = playX - CARD_WIDTH / 2;
        float maxX = playX + 1.5f * CARD_WIDTH;
        float minY = playY - CARD_HEIGHT;
        float maxY = (playY + 1 * CARD_HEIGHT);

        Log.d("DrawMaster",
                "\nx: " + x
                        + "\ny: " + y
                        + "\nplayX: " + playX
                        + "\nplayY: " + playY
                        + "\nminX: " + minX
                        + "\nmaxX: " + maxX
                        + "\nminY: " + minY
                        + "\nmaxY: " + maxY);
        return minX < x && x < maxX && minY < y && y < maxY;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device
     *         density
     */
    private float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
    //
    // /**
    // * This method converts device specific pixels to density independent pixels.
    // *
    // * @param px A value in px (pixels) unit. Which we need to convert into db
    // * @param context Context to get resources and device specific display metrics
    // * @return A float value to represent dp equivalent to px value
    // */
    // public static float convertPixelsToDp(float px, Context context){
    // Resources resources = context.getResources();
    // DisplayMetrics metrics = resources.getDisplayMetrics();
    // float dp = px / (metrics.densityDpi / 160f);
    // return dp;
    // }
}

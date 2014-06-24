package com.rundle.haas.android.holders;

import android.view.View;
import android.widget.ImageView;

import com.rundle.haas.Card;
import com.rundle.haas.android.ViewHolder;

public class CardHolder implements Comparable<CardHolder> , ViewHolder {

	public final Card card;
	private ImageView view;

	private boolean show = false;

    private boolean inHand = false;
	private float mX;
	private float mY;

	public CardHolder(Card card, ImageView view) {
		this.card = card;
		this.view = view;
//		mX = DrawMaster.convertDpToPixel(view.getX(), view.getContext());
//		mY = DrawMaster.convertDpToPixel(view.getY(), view.getContext());
        mX = view.getX();
        mY = view.getY();
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	/*
	 * public int getIndex() { return card.index(); }
	 */
	public void setPosition(float x, float y) {
		mX = x;
		mY = y;
	}

    public View getView() {
        return view;
    }

    public void setImageView(ImageView view) {
        this.view = view;
    }

	public void flip() {
        show = !show;
	}

	public boolean isShown() {
		return show;
	}

    public boolean isInHand() {
        return inHand;
    }

    public void setInHand(boolean inHand) {
        this.inHand = inHand;
    }

	@Override
	public int compareTo(CardHolder another) {
		return card.compareTo(another.card);
	}
}

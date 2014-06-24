package com.rundle.haas.android.dialogs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.rundle.haas.Card.CardSuit;
import com.rundle.haas.R;
import com.rundle.haas.Trump;
import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.ImageAdapter;

public class NameTrumpDialog extends Fragment {

	private static final CardSuit[] POS2SUIT = {CardSuit.CLUB, CardSuit.DIAMOND, CardSuit.SPADE, CardSuit.HEART};
	private Trump trump;
	private final NameTrumpDialog self = this;
	
	public Trump getTrump() {
		return trump;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		//HaasView haasView = (HaasView) getActivity().findViewById(R.id.haas_view);
		GridView view = (GridView) inflater.inflate(R.layout.nametrump, container, false);
		view.setPadding(200, 200, 0, 0);
		view.setAdapter(new ImageAdapter(getActivity(), ((HaasActivity)getActivity()).getDrawMaster().mSuitBitmap));
		view.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	trump = new Trump(POS2SUIT[position]);
	           ((HaasActivity) getActivity()).onTrumpSelected(self);
	        }
	    });
		
		return view;
	}
}
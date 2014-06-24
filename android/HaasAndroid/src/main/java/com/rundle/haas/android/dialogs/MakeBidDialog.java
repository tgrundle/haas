package com.rundle.haas.android.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.rundle.haas.R;
import com.rundle.haas.Table;
import com.rundle.haas.Table.Bid;
import com.rundle.haas.android.HaasActivity;

public class MakeBidDialog extends Fragment {

	private final List<Bid> validBids = new ArrayList<Bid>();
	private Bid bid;

	public List<Bid> getValidBids() {
		return validBids;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View bidView = inflater.inflate(R.layout.make_bid, container, false);

		ListView listView = (ListView) bidView.findViewById(R.id.bid_list);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				bid = validBids.get(position);
			}

		});
		ArrayAdapter<Bid> bidList = new ArrayAdapter<Table.Bid>(getActivity(),
				android.R.layout.simple_spinner_item);
		bidList.addAll(validBids);
		listView.setAdapter(bidList);

		Button button = (Button) bidView.findViewById(R.id.bid_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((HaasActivity) getActivity()).onBidSelected(MakeBidDialog.this);
			}
		});

		return bidView;
	}

	public Bid getBid() {
		return bid;
	}

}

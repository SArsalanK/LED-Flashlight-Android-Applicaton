package com.verox.flashlight;

import com.verox.flashlight.R.id;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragBattery extends Fragment{

	View v;
	public static TextView batteryPercentage;
	ImageView batteryBg;
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		v = inflater.inflate(R.layout.battery, container, false);
		context = getActivity().getApplicationContext();

		//		Toast.makeText(context, 
		//				"onCreateView", 
		//				Toast.LENGTH_SHORT).show();

		batteryPercentage = (TextView) v.findViewById(R.id.battery_value);
		batteryBg = (ImageView) v.findViewById(id.battery_bg);

		// -- BATTERY RECIEVER 
		getActivity().registerReceiver(FragBattery.this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		return v;
	}

	public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub

//									Toast.makeText(context, 
//											"BroadcastReceiver", 
//											Toast.LENGTH_SHORT).show();

			int level = intent.getIntExtra("level", 0);
			batteryPercentage.setText("" + String.valueOf(level) + "%");

			if (level <= 100) {
				batteryBg.setBackgroundResource(R.drawable.devicetemp_lower_piece_green);
			}
			if (level <= 50) {
				batteryBg.setBackgroundResource(R.drawable.devicetemp_lower_piece_blue);
			}
			if (level <= 25) {
				batteryBg.setBackgroundResource(R.drawable.devicetemp_lower_piece_red);
			}
		}
	};

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (v != null) {
			ViewGroup parentViewGroup = (ViewGroup) v.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
//			getActivity().unregisterReceiver(mBatInfoReceiver);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(mBatInfoReceiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		getActivity().registerReceiver(FragBattery.this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	public static FragBattery newInstance(CharSequence label) {
		System.out.println("newInstance");
		FragBattery f = new FragBattery();
		Bundle b = new Bundle();
		b.putCharSequence("label", label);
		f.setArguments(b);
		return f;
	}
}

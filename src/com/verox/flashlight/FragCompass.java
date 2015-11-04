package com.verox.flashlight;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class FragCompass extends Fragment implements SensorEventListener{

	private View v;
	private ImageView compass_needle;
	private float currentDegree = 0f;
	private SensorManager mSensorManager;
	private Context context;
	private List<android.hardware.Sensor> sensorList;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.compass, container, false);
		context = getActivity().getApplicationContext();

		compass_needle = (ImageView) v.findViewById(R.id.compass_needle);
		
		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensorList = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

		compass_needle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (sensorList.size() > 0) {
					//Do Nothing
				} else {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

					// set title
					alertDialogBuilder.setTitle("Orientation Sensor");

					// set dialog message
					alertDialogBuilder
					.setMessage("Sorry, your device doesn't have an orientation sensor.")
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}

			}
		});
		return v;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		//		Toast.makeText(context, 
		//				"onSensorChanged", 
		//				Toast.LENGTH_SHORT).show();

//		Log.i("onSensorChanged: Compass", event.toString());

		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(
				currentDegree, 
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF,
				0.5f);

		// how long the animation will take place
		ra.setDuration(210);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		compass_needle.startAnimation(ra);
		currentDegree = -degree;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//		Toast.makeText(context, 
		//				"onResume", 
		//				Toast.LENGTH_SHORT).show();

		// for the system's orientation sensor registered listeners
		this.mSensorManager.registerListener(FragCompass.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

		//		Toast.makeText(context, 
		//				"onPause", 
		//				Toast.LENGTH_SHORT).show();

		super.onPause();
		this.mSensorManager.unregisterListener(FragCompass.this);
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		//		Toast.makeText(context, 
		//				"onDestroyView", 
		//				Toast.LENGTH_SHORT).show();

		if (v != null) {
			ViewGroup parentViewGroup = (ViewGroup) v.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
			mSensorManager.unregisterListener(this);
		}
	}

	public static FragCompass newInstance(CharSequence label) {
		System.out.println("newInstance");
		FragCompass f = new FragCompass();
		Bundle b = new Bundle();
		b.putCharSequence("label", label);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}

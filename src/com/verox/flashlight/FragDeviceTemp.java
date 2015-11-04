package com.verox.flashlight;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragDeviceTemp extends Fragment {

	private View v;
	public static TextView deviceTemp;
	private SensorManager sm;
	private Sensor temperature;
	private ImageView device_temp_bg;
	private Context context;
	FragFlashLight FragFlashLight;
	int temp;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		v = inflater.inflate(R.layout.device_temp, container, false);
		context = getActivity().getApplicationContext();
		
		FragFlashLight = new FragFlashLight();

		deviceTemp = (TextView) v.findViewById(R.id.temperature_value);
		device_temp_bg = (ImageView) v.findViewById(R.id.device_temp_bg);

		sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		temperature = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		
		Log.i("temperature: ", "" + temperature);

		if (temperature == null) {
			//			Toast.makeText(context, 
			//					"Sorry, your device doesn't have an ambient temperature sensor!", 
			//					Toast.LENGTH_SHORT).show();
			deviceTemp.setText("?");
			deviceTemp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

					// set title
					alertDialogBuilder.setTitle("Ambient Temperature Sensor");

					// set dialog message
					alertDialogBuilder
					.setMessage("Sorry, your device doesn't have an ambient temperature sensor.")
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
			});
		} else {
			sm.registerListener(sel, temperature, SensorManager.SENSOR_DELAY_NORMAL);
			device_temp_bg.setVisibility(View.INVISIBLE);
			deviceTemp.setText("...");
			
			deviceTemp.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!FragFlashLight.isInFahrenheit) {
						FragFlashLight.isInFahrenheit = true;
						Double Fahrenheit = 1.8*temp + 32;
						int d_Fahrenheit = (int) Math.round(Fahrenheit); 
						deviceTemp.setText(d_Fahrenheit + "\u2109");
					} else {
						FragFlashLight.isInFahrenheit = false;
						deviceTemp.setText(temp + "\u2103");
					}
				}
			});
		}

		return v;
	}

	SensorEventListener sel = new SensorEventListener(){  

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}  

		public void onSensorChanged(SensorEvent event) {

			temp = Math.round(event.values[0]); 
			device_temp_bg.setVisibility(View.VISIBLE);

			if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
				if (!FragFlashLight.isInFahrenheit) {
					deviceTemp.setText(temp + "\u2103");
				} else {
					Double Fahrenheit = 1.8*temp + 32;
					int d_Fahrenheit = (int) Math.round(Fahrenheit); 
					deviceTemp.setText(d_Fahrenheit + "\u2109");
				}
			}
			if (event.values[0] <= 45) {
				device_temp_bg.setBackgroundResource(R.drawable.devicetemp_lower_piece_red);
			}
			if (event.values[0] <= 35) {
				device_temp_bg.setBackgroundResource(R.drawable.devicetemp_lower_piece_blue);
			}
			if (event.values[0] <= 25) {
				device_temp_bg.setBackgroundResource(R.drawable.devicetemp_lower_piece_green);
			}

		}  
	};

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (temperature != null) {
			sm.unregisterListener(sel, temperature); 
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(sel, temperature, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (v != null) {
			ViewGroup parentViewGroup = (ViewGroup) v.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
			if (temperature != null) {
				sm.unregisterListener(sel, temperature); 
			}
		}
	}

	public static FragDeviceTemp newInstance(CharSequence label) {
		System.out.println("newInstance");
		FragDeviceTemp f = new FragDeviceTemp();
		Bundle b = new Bundle();
		b.putCharSequence("label", label);
		f.setArguments(b);
		return f;
	}
}

package com.verox.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;

import com.verox.flashlight.flip.AnimationFactory;
import com.verox.flashlight.flip.AnimationFactory.FlipDirection;

public class FragFlashLight extends Fragment implements OnKeyListener{

	public static ToggleButton flashLightBtn;
	public static Camera mCamera = null;
	public static Parameters params;
	private Context context;
	private View v;
	private PackageManager pm;
	public static boolean flashSupport;
	public static SeekBar seekbar;
	public static int duration;
	public static Handler handler;
	public static  Boolean isInFahrenheit = false;
	private LinearLayout ll_blinker;
	private ImageView blinker_one, blinker_two, blinker_three, blinker_four, blinker_five;
	private static ImageView blinker_x;
	private ImageButton flip_btn, flip_btn_back;
	private ViewAnimator viewFlipperFlashlight;
	public static RelativeLayout rl_white;
	private ActivitySwipeDetector activitySwipeDetector;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		try {
			v = inflater.inflate(R.layout.flashlight, container, false);
			context =  getActivity().getApplicationContext();
			pm = context.getPackageManager();

			flashLightBtn = (ToggleButton) v.findViewById(R.id.btn_flashlight);
			flashSupport = isFlashSupported(pm);

			flashLightBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {

					params = mCamera.getParameters();

					if (flashSupport) {

						boolean on = ((ToggleButton) view).isChecked();

						stopTorchlightOff();
						stopTorchlightOn();
						seekbar.setProgress(2);
						duration = 0;

						blinker_one.setBackgroundResource(R.drawable.blinker_off);
						blinker_two.setBackgroundResource(R.drawable.blinker_off);
						blinker_three.setBackgroundResource(R.drawable.blinker_off);
						blinker_four.setBackgroundResource(R.drawable.blinker_off);
						blinker_five.setBackgroundResource(R.drawable.blinker_off);

						try {
							if (on) {
								//								Log.i("info", "torch on!");
								blinker_x.setBackgroundResource(R.drawable.torch_indicator_on);
								params.setFlashMode(Parameters.FLASH_MODE_TORCH);
								mCamera.setParameters(params);
								mCamera.startPreview();
							} else {
								//								Log.i("info", "torch off!");
								blinker_x.setBackgroundResource(R.drawable.blinker_off);
								params.setFlashMode(Parameters.FLASH_MODE_OFF);
								mCamera.setParameters(params);
								mCamera.stopPreview();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						//					flashLightBtn.setChecked(false);
						((ToggleButton) view).setChecked(false);
						AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
						.create();
						alertDialog.setTitle("No Camera Flash");
						alertDialog
						.setMessage("The device's Camera doesn't support flash.");
						alertDialog.setButton(Activity.RESULT_OK, "OK",
								new DialogInterface.OnClickListener() {
							public void onClick(
									final DialogInterface dialog,
									final int which) {
								Log.i("err",
										"The device's Camera doesn't support flash.");
							}
						});
						alertDialog.show();
					}

				}
			});

			// -- CAMERA SENSOR 
			if (!isCameraSupported(pm)) {
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.setTitle("No Camera");
				alertDialog.setMessage("The device's doesn't support Camera.");
				alertDialog.setButton(Activity.RESULT_OK, "OK",
						new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int which) {
						Log.e("err", "This device's doesn't support Camera.");
					}
				});
				alertDialog.show();
			}

			if (mCamera == null) {
				mCamera = Camera.open();     
			}

			seekbar = (SeekBar) v.findViewById(R.id.seekBar);
			ll_blinker = (LinearLayout) v.findViewById(R.id.ll_blinker);

			if (!flashSupport) {
				seekbar.setVisibility(View.INVISIBLE);
				ll_blinker.setVisibility(View.INVISIBLE);
			} 

			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					Log.i("onStopTrackingTouch", "onStopTrackingTouch");

					stopTorchlightOn();
					stopTorchlightOff();

					if (duration != 0) {

						if (duration == 3000) {

							blinker_one.setBackgroundResource(R.drawable.blinker_on);
							blinker_two.setBackgroundResource(R.drawable.blinker_off);
							blinker_three.setBackgroundResource(R.drawable.blinker_off);
							blinker_four.setBackgroundResource(R.drawable.blinker_off);
							blinker_five.setBackgroundResource(R.drawable.blinker_off);

						} else if (duration == 2000) {

							blinker_two.setBackgroundResource(R.drawable.blinker_on);
							blinker_one.setBackgroundResource(R.drawable.blinker_off);
							blinker_three.setBackgroundResource(R.drawable.blinker_off);
							blinker_four.setBackgroundResource(R.drawable.blinker_off);
							blinker_five.setBackgroundResource(R.drawable.blinker_off);

						} else if (duration == 1000) {

							blinker_three.setBackgroundResource(R.drawable.blinker_on);
							blinker_one.setBackgroundResource(R.drawable.blinker_off);
							blinker_two.setBackgroundResource(R.drawable.blinker_off);
							blinker_four.setBackgroundResource(R.drawable.blinker_off);
							blinker_five.setBackgroundResource(R.drawable.blinker_off);

						} else if (duration == 500) {

							blinker_four.setBackgroundResource(R.drawable.blinker_on);
							blinker_one.setBackgroundResource(R.drawable.blinker_off);
							blinker_three.setBackgroundResource(R.drawable.blinker_off);
							blinker_two.setBackgroundResource(R.drawable.blinker_off);
							blinker_five.setBackgroundResource(R.drawable.blinker_off);

						} else if (duration == 250) {

							blinker_five.setBackgroundResource(R.drawable.blinker_on);
							blinker_one.setBackgroundResource(R.drawable.blinker_off);
							blinker_three.setBackgroundResource(R.drawable.blinker_off);
							blinker_four.setBackgroundResource(R.drawable.blinker_off);
							blinker_two.setBackgroundResource(R.drawable.blinker_off);

						}

						startTorchlightOn();

						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// Do something after 5s = 5000ms
								startTorchlightOff();
							}
						}, duration/2);

					} else {

						blinker_one.setBackgroundResource(R.drawable.blinker_off);
						blinker_two.setBackgroundResource(R.drawable.blinker_off);
						blinker_three.setBackgroundResource(R.drawable.blinker_off);
						blinker_four.setBackgroundResource(R.drawable.blinker_off);
						blinker_five.setBackgroundResource(R.drawable.blinker_off);
					}

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					//				Log.i("onStartTrackingTouch", "onStartTrackingTouch");

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					//				Log.i("onProgressChanged", "onProgressChanged");

					if (progress < 10) {
						seekBar.setProgress(2);
						duration = 0;

					} else if ((progress >= 10) && (progress < 30)){
						seekBar.setProgress(20);
						duration = 3000;

					} else if ((progress >= 30) && (progress < 50)) {
						seekBar.setProgress(40);
						duration = 2000;

					} else if ((progress >= 50) && (progress < 70)) {
						seekBar.setProgress(60);
						duration = 1000;

					} else if ((progress >= 70) && (progress < 90)) {
						seekBar.setProgress(80);
						duration = 500;

					} else if (progress >= 90) {
						seekBar.setProgress(98);
						duration = 250;
					}				
				}
			});

			blinker_one = (ImageView) v.findViewById(R.id.blinker_one);
			blinker_two = (ImageView) v.findViewById(R.id.blinker_two);
			blinker_three = (ImageView) v.findViewById(R.id.blinker_three);
			blinker_four = (ImageView) v.findViewById(R.id.blinker_four);
			blinker_five = (ImageView) v.findViewById(R.id.blinker_five);
			blinker_x = (ImageView) v.findViewById(R.id.blinker_x);

			handler = new Handler();

			flip_btn = (ImageButton) v.findViewById(R.id.flip_btn);
			flip_btn_back = (ImageButton) v.findViewById(R.id.flip_btn_back);
			viewFlipperFlashlight = (ViewAnimator) v.findViewById(R.id.viewFlipperFlashlight);

			flip_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AnimationFactory.flipTransition(viewFlipperFlashlight,
							FlipDirection.LEFT_RIGHT);
				
				}
			});
			
			flip_btn_back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AnimationFactory.flipTransition(viewFlipperFlashlight,
							FlipDirection.RIGHT_LEFT);
				}
			});
			
			rl_white = (RelativeLayout) v.findViewById(R.id.rl_white);
			activitySwipeDetector = new ActivitySwipeDetector(getActivity());
			rl_white.setOnTouchListener(activitySwipeDetector);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return v;
		
	}
	
	public static Runnable TorchlightOn = new Runnable() {
		@Override 
		public void run() {

			Log.i("info", "torch on!");
			try {
				blinker_x.setBackgroundResource(R.drawable.torch_indicator_on);
				params = mCamera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(params);
				mCamera.startPreview();
				flashLightBtn.setChecked(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			handler.postDelayed(TorchlightOn, duration);
			Log.i("TorchlightOn", "TorchlightOn for duration: " + duration);
		}
	};

	public static void startTorchlightOn() {
		TorchlightOn.run(); 
	}

	public static void stopTorchlightOn() {
		handler.removeCallbacks(TorchlightOn);
	}

	public static Runnable TorchlightOff = new Runnable() {
		@Override 
		public void run() {
			//	      updateStatus(); //this function can change value of mInterval.

			Log.i("info", "torch off!");
			try {
				blinker_x.setBackgroundResource(R.drawable.blinker_off);
				params = mCamera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(params);
				mCamera.stopPreview();
				flashLightBtn.setChecked(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			handler.postDelayed(TorchlightOff, duration);
			Log.i("TorchlightOff", "TorchlightOff for duration: " + duration);
		}
	};

	public static void startTorchlightOff() {
		TorchlightOff.run(); 
	}

	public static void stopTorchlightOff() {
		handler.removeCallbacks(TorchlightOff);
	}

	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support mCamera flash?
		if (packageManager
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	private boolean isCameraSupported(PackageManager packageManager) {
		// if device support mCamera?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		return false;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		//		if (v != null) {
		//			ViewGroup parentViewGroup = (ViewGroup) v.getParent();
		//			if (parentViewGroup != null) {
		//				parentViewGroup.removeAllViews();
		//			}
		//		}
	}

	public static FragFlashLight newInstance(CharSequence label) {
		System.out.println("newInstance");
		FragFlashLight f = new FragFlashLight();
		Bundle b = new Bundle();
		b.putCharSequence("label", label);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//		if (mCamera != null) {
		//			mCamera.setPreviewCallback(null);
		////			mPreview.getHolder().removeCallback(mPreview);
		//			mCamera.release();
		//		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//		if (mCamera != null) {
		//			mCamera.setPreviewCallback(null);
		////			mPreview.getHolder().removeCallback(mPreview);
		//			mCamera.release();
		//		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if(keyCode == KeyEvent.KEYCODE_HOME)
		{
			Toast.makeText(context, "onKey", Toast.LENGTH_SHORT).show();
		}

		return false;
	}


}

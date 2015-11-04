package com.verox.flashlight;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ActivitySwipeDetector implements View.OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	private Activity activity;
	static final int MIN_DISTANCE = 50;
	private float downX, downY, upX, upY;
	FragFlashLight fl;
	double alpha;

	public ActivitySwipeDetector(Activity activity){
		this.activity = activity;
		fl = new FragFlashLight();
		alpha = fl.rl_white.getAlpha();
	}

	public void onRightToLeftSwipe(){
		Log.i(logTag, "RightToLeftSwipe!");
//		    activity.doSomething();
	}

	public void onLeftToRightSwipe(){
		Log.i(logTag, "LeftToRightSwipe!");
		//    activity.doSomething();
	}

	public void onTopToBottomSwipe(){
		Log.i(logTag, "onTopToBottomSwipe!");
		//    activity.doSomething();
	}

	public void onBottomToTopSwipe(){
		Log.i(logTag, "onBottomToTopSwipe!");
		//    activity.doSomething();
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// swipe horizontal?
			if(Math.abs(deltaX) > Math.abs(deltaY))
			{
				if(Math.abs(deltaX) > MIN_DISTANCE){
					// left or right
					if(deltaX < 0) { this.onRightSwipe(); return true; }
					if(deltaX > 0) { this.onLeftSwipe(); return true; }
				}
				else {
					Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
					return false; // We don't consume the event
				}
			}
			// swipe vertical?
			else 
			{
				if(Math.abs(deltaY) > MIN_DISTANCE){
					// top or down
					if(deltaY < 0) { this.onDownSwipe(); return true; }
					if(deltaY > 0) { this.onUpSwipe(); return true; }
				}
				else {
					Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
					return false; // We don't consume the event
				}
			}

			return true;
		}
		}
		return false;
	}

	private void onLeftSwipe() {
		// TODO Auto-generated method stub
		Log.d("logTag", "onLeftSwipe");
	}

	private void onRightSwipe() {
		// TODO Auto-generated method stub
		Log.d("logTag", "onRightSwipe");
	}

	private void onUpSwipe() {
		// TODO Auto-generated method stub
		Log.d("logTag", "onUpSwipe");
		
		if (alpha <= .99 && alpha >= 0) {
			alpha = alpha + 0.1;
			fl.rl_white.setAlpha((float) alpha);
		}
		
	}

	private void onDownSwipe() {
		// TODO Auto-generated method stub
		Log.d("logTag", "onDownSwipe");
		
		if (alpha <= 1.0 && alpha >= 0.01) {
			alpha = alpha - 0.1;
			fl.rl_white.setAlpha((float) alpha);
		}
		
	}

}
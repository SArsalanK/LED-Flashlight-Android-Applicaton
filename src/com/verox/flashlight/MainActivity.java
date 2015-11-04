package com.verox.flashlight;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends FragmentActivity implements OnClickListener , OnTabChangeListener, OnPageChangeListener{

	private TextView title;
	private Context context = this;
	// record the compass picture angle turned
	private ViewPager mViewPager;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private MyPageAdapter pageAdapter;
	private TabHost mTabHost;
	private List<Fragment> fragments;
	private FragFlashLight FragFlashLight;
	private FragDeviceTemp FragDeviceTemp;
	private FragBattery FragBattery;
	private Button handler, rate_us, more_apps;
	private LinearLayout ll_more_options;
	private Uri uriApp, uriProfile;
	private int count = 0;
	private AdView mAdView;
	private InterstitialAd mInterstitial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main);
		context = getApplicationContext();

		float scale = getApplicationContext().getResources().getDisplayMetrics().density;
		Log.i("scale", Float.toString(scale));
		//		0.75 means low density
		//		1.0 means standard (medium) density
		//		1.5 means high (large) density
		//		2.0 means extra high density

		FragFlashLight = new FragFlashLight();
		FragDeviceTemp = new FragDeviceTemp();
		FragBattery = new FragBattery();

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		title = (TextView) findViewById(R.id.title);
		handler = (Button) findViewById(R.id.handler);
		ll_more_options = (LinearLayout) findViewById(R.id.ll_more_options);
		ll_more_options.setVisibility(View.VISIBLE);
		
		
		rate_us = (Button) findViewById(R.id.rate_us);
		more_apps = (Button) findViewById(R.id.more_apps);
		uriApp = Uri.parse("market://details?id=" + context.getPackageName());
		uriProfile = Uri.parse("market://search?q=pub:Verox Games");

		handler.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ll_more_options.isShown()) {
					ll_more_options.setVisibility(View.GONE);
				} else {
					ll_more_options.setVisibility(View.VISIBLE);
				}

			}
		});

		rate_us.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uriApp);
				try {
					ll_more_options.setVisibility(View.GONE);
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
				}

			}
		});

		more_apps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent goToProfile = new Intent(Intent.ACTION_VIEW, uriProfile);
				try {
					ll_more_options.setVisibility(View.GONE);
					startActivity(goToProfile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=Verox+Games")));
				}

			}
		});

		initialiseTabHost();
		fragments = getFragments();
		pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setOnPageChangeListener(MainActivity.this);
		mViewPager.setOffscreenPageLimit(4);

		// ADS INTEGRATION

		// BANNER
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.setAdListener(new ToastAdListener(this));
		mAdView.loadAd(new AdRequest.Builder().build());

		// INTERESTIAL
		mInterstitial = new InterstitialAd(this);
		mInterstitial.setAdUnitId("ca-app-pub-1625519019998985/1144254155");

		mInterstitial.setAdListener(new ToastAdListener(this) {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				showInterstitial();
//				Toast.makeText(context, "onAdLoaded: mInterstitial", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
//				Toast.makeText(context, "onAdFailedToLoad: mInterstitial", Toast.LENGTH_SHORT).show();
			}
		});
		loadInterstitial();

	}

	public void loadInterstitial() {
//		Toast.makeText(context, "loadInterstitial", Toast.LENGTH_SHORT).show();
		mInterstitial.loadAd(new AdRequest.Builder().build());
	}

	public void showInterstitial() {
//		Toast.makeText(context, "showInterstitial", Toast.LENGTH_SHORT).show();
		if (mInterstitial.isLoaded()) {
			mInterstitial.show();
		}
	}

	private void initialiseTabHost() {

		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();	

		MainActivity.AddTab(MainActivity.this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(createTabView(this, R.drawable.selector_flashlight)));
		MainActivity.AddTab(MainActivity.this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(createTabView(this, R.drawable.selector_devicetemp)));
		MainActivity.AddTab(MainActivity.this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(createTabView(this, R.drawable.selector_battery)));
		MainActivity.AddTab(MainActivity.this, this.mTabHost, this.mTabHost.newTabSpec("Tab4").setIndicator(createTabView(this, R.drawable.selector_compass)));

		mTabHost.setOnTabChangedListener(this);
	}

	private static void AddTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
		tabSpec.setContent(new MyTabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	private View createTabView(Context context, int d) {
		View view = LayoutInflater.from(context).inflate(R.layout.customtab_tabhost, null, false);

		CheckBox iv =(CheckBox) view.findViewById(R.id.main_selector);
		iv.setBackgroundResource(d);

		if (d == R.drawable.selector_flashlight) {
			iv.setChecked(true);
			iv.setTag("selector_flashlight");
		} else if (d == R.drawable.selector_devicetemp) {
			iv.setTag("selector_devicetemp");
		} else if (d == R.drawable.selector_battery) {
			iv.setTag("selector_battery");
		} else if (d == R.drawable.selector_compass) {
			iv.setTag("selector_compass");
		}

		return view;
	}

	private List<Fragment> getFragments(){
		List<Fragment> fList = new ArrayList<Fragment>();

		// TODO Put here your Fragments
		Bundle bundle = new Bundle();
		bundle.putString("subcatins_id", "");
		FragFlashLight f1 = FragFlashLight.newInstance("FragFlashLight");
		f1.setArguments(bundle);
		FragDeviceTemp f2 = FragDeviceTemp.newInstance("FragDeviceTemp");
		f2.setArguments(bundle);
		FragBattery f3 = FragBattery.newInstance("FragBattery");
		f3.setArguments(bundle);
		FragCompass f4 = FragCompass.newInstance("FragCompass");
		f4.setArguments(bundle);

		fList.add(f1);
		fList.add(f2);
		fList.add(f3);
		fList.add(f4);

		TabWidget widget = mTabHost.getTabWidget();
		final int tabChildrenCount = widget.getChildCount();

		for (int i = 0; i < tabChildrenCount; i++) {

			View mTabHostView = widget.getChildAt(i);

			CheckBox cb =(CheckBox) mTabHostView.findViewById(R.id.main_selector);
			cb.setOnClickListener((OnClickListener) this);

		}

		return fList;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		//		Toast.makeText(context, "onPageScrollStateChanged", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		//		Toast.makeText(context, "onPageScrolled", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		//		Toast.makeText(context, "onPageSelected", Toast.LENGTH_SHORT).show();
		int pos = this.mViewPager.getCurrentItem();
		this.mTabHost.setCurrentTab(pos);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		//		Toast.makeText(context, "onTabChanged"+tabId, Toast.LENGTH_SHORT).show();
		resetMainListView();
		View v = this.mTabHost.getCurrentTabView();
		CheckBox cb =(CheckBox) v.findViewById(R.id.main_selector);
		cb.setChecked(true);

		FragDeviceTemp.deviceTemp.setVisibility(View.VISIBLE);
		FragBattery.batteryPercentage.setVisibility(View.VISIBLE);
		
		if (tabId == "Tab1") {
			title.setText("FLASHLIGHT");
			FragDeviceTemp.deviceTemp.setVisibility(View.GONE);
			FragBattery.batteryPercentage.setVisibility(View.GONE);
		} else if (tabId == "Tab2") {
			title.setText("DEVICE TEMPERATURE");
			FragBattery.batteryPercentage.setVisibility(View.GONE);
		} else if (tabId == "Tab3") {
			title.setText("DEVICE BATTERY");
			FragDeviceTemp.deviceTemp.setVisibility(View.GONE);
		} else if (tabId == "Tab4") {
			title.setText("COMPASS");
			FragDeviceTemp.deviceTemp.setVisibility(View.GONE);
			FragBattery.batteryPercentage.setVisibility(View.GONE);
		}

	}

	public void resetMainListView() {
		TabWidget widget =mTabHost.getTabWidget();
		final int tabChildrenCount = widget.getChildCount();

		for (int i = 0; i < tabChildrenCount; i++) {

			View mTabHostView = widget.getChildAt(i);
			CheckBox cb =(CheckBox) mTabHostView.findViewById(R.id.main_selector);
			cb.setChecked(false);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		CheckBox cb = (CheckBox) v.findViewById(R.id.main_selector);
		if (!cb.isChecked()) {
			cb.setChecked(true);
		}
		if (v.getTag() == "selector_flashlight") {
			this.mViewPager.setCurrentItem(0);
		} else if (v.getTag() == "selector_devicetemp") {	
			this.mViewPager.setCurrentItem(1);
		} else if (v.getTag() == "selector_battery") {
			this.mViewPager.setCurrentItem(2);
		} else if (v.getTag() == "selector_compass") {
			this.mViewPager.setCurrentItem(3);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAdView.resume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mAdView.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAdView.destroy();
		super.onDestroy();
		
		FragFlashLight.stopTorchlightOff();
		FragFlashLight.stopTorchlightOn();
		
		if (FragFlashLight.flashSupport) {
			if (FragFlashLight.flashLightBtn.isChecked()) {
				Log.i("info", "torch off!");
				FragFlashLight.params.setFlashMode(Parameters.FLASH_MODE_OFF);
				FragFlashLight.mCamera.setParameters(FragFlashLight.params);
				FragFlashLight.mCamera.stopPreview();
			}
		}
	}

	private void startTimer() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Do something after 5s = 5000ms
				count = 0;
			}
		}, 4000);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (count == 0) {
			Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show();
			count = count + 1;
			startTimer();
		} else {
			super.onBackPressed();
		}

	}

}
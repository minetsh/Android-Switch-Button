package com.kareluo.views;

import com.kareluo.views.view.SwitchButton;
import com.kareluo.views.view.SwitchButton.OnCheckedChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	private SwitchButton mSwitchButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSwitchButton2 = (SwitchButton) findViewById(R.id.main_switch_2);
		mSwitchButton2.setOnCheckedListener(this);
	}

	@Override
	public void onCheckedChange(SwitchButton view, boolean isChecked) {
		Toast.makeText(this, "" + isChecked, Toast.LENGTH_SHORT).show();
		findViewById(R.id.main_switch_3).setEnabled(isChecked);
	}
}

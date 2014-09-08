package com.project.taikunserver;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeScreenActivity extends Activity {
	
	Button playButton,helpButton,exitButton;
	TextView welcomeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);
		
		playButton = (Button)findViewById(R.id.buttonStartGame);
		helpButton = (Button)findViewById(R.id.buttonHelp);
		exitButton= (Button)findViewById(R.id.buttonExit);
		welcomeText = (TextView)findViewById(R.id.welcome_textView);
		
		Intent intent = getIntent();
		String username=intent.getStringExtra("username");
		welcomeText.setText("Welcome " + username);
		
		playButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(WelcomeScreenActivity.this,ProposeScreenActivity.class);
				startActivity(i);
			}
		});
		
		
		helpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//go to the help activity 1
				Intent ii=new Intent(WelcomeScreenActivity.this,HelpPageActivityOne.class);
				startActivity(ii);
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		
	}
}

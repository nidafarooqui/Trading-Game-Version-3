package com.project.taikunclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameWonActivity extends Activity {
	Button mainMenuBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_won);
		mainMenuBtn = (Button) findViewById(R.id.buttonMainMenu);
		
		
		mainMenuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(GameWonActivity.this,WelcomeScreenActivity.class);
				startActivity(i);
				
			}
		});
	}
}

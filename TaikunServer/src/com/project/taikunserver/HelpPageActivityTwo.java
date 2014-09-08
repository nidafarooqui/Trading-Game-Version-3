package com.project.taikunserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class HelpPageActivityTwo extends Activity {
	
	ImageButton nextPage, prevPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_page_two);
		
		nextPage = (ImageButton)findViewById(R.id.imageButtonPageThree);
		prevPage = (ImageButton)findViewById(R.id.imageButtonBack);
		
		nextPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent(HelpPageActivityTwo.this,HelpPageActivityFour.class);
				startActivity(i);
				
			}
		});
		
		
		prevPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent ii=new Intent(HelpPageActivityTwo.this,HelpPageActivityOne.class);
				startActivity(ii);
				
			}
		});
	}
}

package com.project.taikunclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends Activity{

	LoginDataBaseAdapter loginDataBaseAdapter;
	EditText password,repassword,securityhint;
	EditText username;
	Button register,cancel,reg_btn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		//View view = View.inflate(this, R.layout.activity_main, null);
		loginDataBaseAdapter = new LoginDataBaseAdapter(this);
		loginDataBaseAdapter=loginDataBaseAdapter.open();
		//reg_btn=(Button)view.findViewById(R.id.reg_btn);
		username= (EditText) findViewById(R.id.usernameRegister);
        password=(EditText)findViewById(R.id.pass_edt);
        repassword=(EditText)findViewById(R.id.repass_edt);
        securityhint=(EditText)findViewById(R.id.security_edt);
        register= (Button)findViewById(R.id.reg_btn);
        cancel=(Button)findViewById(R.id.cancel_btn);
        register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String User= username.getText().toString();
				String Pass=password.getText().toString();
				String Secu=securityhint.getText().toString();
				String Repass=repassword.getText().toString();
				//String confirmPassword=editTextConfirmPassword.getText().toString();
				
				// check if any of the fields are vaccant
				if(Pass.equals("")||Repass.equals("")||Secu.equals(""))
				{
						Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
						return;
				}
				// check if both password matches
				if(!Pass.equals(Repass))
				{
					Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
				    // Save the Data in Database
				    loginDataBaseAdapter.insertEntry(User, Pass, Repass,Secu);
				    Dec.isRegistered=true;
				   // reg_btn.setVisibility(View.GONE);
				    Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
				    Log.d("USERNAME", User);
				    Log.d("PASSWORD",Pass);
				    Log.d("RE PASSWORD",Repass);
				    Log.d("SECURITY HINT",Secu);
				    Intent i=new Intent(Registration.this,MainActivityLogin.class);
				   	startActivity(i);
					
				}
				
				
				
			}
		});
        
	}

}
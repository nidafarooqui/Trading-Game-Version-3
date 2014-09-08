package com.project.taikunclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends Activity {
	
	TextView textResponse;
	EditText editTextAddress, editTextPort;
	Button buttonConnect, buttonClear, buttonStartGame;
	
	EditText welcomeMsg;
	//private MyClientTask myClientTask;
	String response = "";
	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	String wool = "3";
	String rock = "3";
	String wheat = "3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		editTextAddress = (EditText) findViewById(R.id.address);
		editTextPort = (EditText) findViewById(R.id.port);
		buttonConnect = (Button) findViewById(R.id.connect);
		buttonClear = (Button) findViewById(R.id.clear);
		buttonStartGame = (Button) findViewById(R.id.buttonStartGame);
		textResponse = (TextView) findViewById(R.id.response);
		
		Intent intent = getIntent();
		rock=intent.getStringExtra("rock");
		wheat=intent.getStringExtra("wheat");
		wool=intent.getStringExtra("wool");
		
		welcomeMsg = (EditText)findViewById(R.id.welcomemsg);

		buttonConnect.setOnClickListener(buttonConnectOnClickListener);

		buttonClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textResponse.setText("");
			}
		});
		
		buttonStartGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent iii=new Intent(ClientActivity.this,TaikunGameActivity.class);
				iii.putExtra("IP Address", editTextAddress.getText().toString());
				iii.putExtra("Port", editTextPort.getText().toString());
				iii.putExtra("Chat",response);
				iii.putExtra("rock",rock);
				iii.putExtra("wheat",wheat);
				iii.putExtra("wool", wool);
				startActivity(iii);
			}
		});
	}
	
	OnClickListener buttonConnectOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			
			String tMsg = welcomeMsg.getText().toString();
			if(tMsg.equals("")){
				tMsg = null;
				//Toast.makeText(ClientActivity.this, "No Welcome Msg sent", Toast.LENGTH_SHORT).show();
			}
			
			MyClientTask myClientTask = new MyClientTask(editTextAddress
					.getText().toString(), Integer.parseInt(editTextPort
					.getText().toString()),
					tMsg);
			response += "You say: " + tMsg + "\n";
			textResponse.setText(response);
			welcomeMsg.setText("");
			myClientTask.execute();
		}
	};
	

	public class MyClientTask extends AsyncTask<Void, Void, Void> {
		String dstAddress;
		int dstPort;
		String msgToServer;
		
		MyClientTask(String addr, int port, String msgTo) {
			dstAddress = addr;
			dstPort = port;
			msgToServer = msgTo;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			try {
				socket = new Socket(dstAddress, dstPort);
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
				
				if(msgToServer != null){
					dataOutputStream.writeUTF(msgToServer);
				}
				
				response += "Server says: " + dataInputStream.readUTF() + "\n";

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "UnknownHostException: " + e.toString();
				//Toast.makeText(ClientActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "IOException: " + e.toString();
				//Toast.makeText(ClientActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			} 
			return null;
		}	
		@Override
		protected void onPostExecute(Void result) {
			textResponse.setText(response);
			super.onPostExecute(result);
		}
		
		
	}
	public void closeSockets()
	{
		
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(ClientActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		if (dataInputStream != null) {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(ClientActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(ClientActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
			
	}
	protected void onDestroy()
	{
		super.onDestroy();
		closeSockets();
		
	}

}


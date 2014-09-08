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
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class ProposeScreenActivity extends Activity {
	
	Button startTradeButton;
	TextView rockState, wheatState, woolState;
	Socket socket = null;
	DataInputStream dataInputStream = null;
	DataOutputStream dataOutputStream = null;
	ServerSocket serverSocket;
	String wool= "3";
	String rock= "3";
	String wheat="3";
	String roundNum = " ";
	String chatMessage= " ";
	String tradeActions= " ";
	String report= " ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_propose_screen);
		
		startTradeButton = (Button) findViewById(R.id.buttonStartTrade);
		rockState = (TextView) findViewById(R.id.rocksGoalState);
		wheatState = (TextView) findViewById(R.id.wheatGoalState);
		woolState = (TextView) findViewById(R.id.woolGoalState);
		
		//get values of the rock, wheat and wool state to update the text view 
		//and the chat message and trade actions to write to the file
		Intent intent2 = getIntent();
		wool = intent2.getStringExtra("Wool");
		rock = intent2.getStringExtra("Ore");
		wheat = intent2.getStringExtra("Wheat");
		chatMessage= intent2.getStringExtra("Chat");
		tradeActions= intent2.getStringExtra("Trade Actions");
		int roundNumber = intent2.getIntExtra("Round Number", 0);
		roundNum = Integer.toString(roundNumber);
		report+= "\n" + "Round: " + roundNum + "\n" + "Wool: " + wool + "Wheat: " + wheat + "Ore: " + rock + "\n" + 
				tradeActions + "\n";
		if(rock!= null)
		{
			rockState.setText(rock);
		}
		else
		{
			rockState.setText("3");
		}
		if(wool!= null)
		{
			woolState.setText(wool);
		}
		else
		{
			woolState.setText("3");
		}
		if(wheat!= null)
		{
			wheatState.setText(wheat);
		}
		else
		{
			wheatState.setText("3");
		}
		
		
		startTradeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ProposeScreenActivity.this, ServerActivity.class);
					try {
						serverSocket.close();
						closeSockets();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
					}
					
				intent.putExtra("rock", rock);
				intent.putExtra("wheat", wheat);
				intent.putExtra("wool", wool);
				startActivity(intent);
			}
		});
		
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
				closeSockets();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class SocketServerThread extends Thread {

		static final int SocketServerPORT = 8080;
		
		@Override
		public void run() {
			

			try {
				serverSocket = new ServerSocket(SocketServerPORT);

				while (true) {
					socket = serverSocket.accept();
					dataInputStream = new DataInputStream(
							socket.getInputStream());
					dataOutputStream = new DataOutputStream(
							socket.getOutputStream());

					String messageFromClient = "";
					
					//If no message sent from client, this code will block the program
					messageFromClient = dataInputStream.readUTF();
					
					if(messageFromClient.equals("4"))
					{
						ProposeScreenActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Intent ii=new Intent(ProposeScreenActivity.this,GameLostActivity.class);
								try {
									serverSocket.close();
									closeSockets();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
							}
								startActivity(ii);
							}
						});
					}

					if((rockState.getText().toString().equals("4") && woolState.getText().toString().equals("5")) || 
							(rockState.getText().toString().equals("4") && wheatState.getText().toString().equals("5")))
							{
						//write to file if game won
						try { 
							// catches IOException below
  
							FileOutputStream fOut = openFileOutput("taikunfile.txt",
			                                                            MODE_WORLD_READABLE);
							OutputStreamWriter osw = new OutputStreamWriter(fOut); 

							// Write the string to the file
							osw.write(report);

							/* ensure that everything is
							 * really written out and close */
							osw.flush();
							osw.close();
						} 
						catch (IOException ioe) 
						{
							ioe.printStackTrace();
						}
							Intent i=new Intent(ProposeScreenActivity.this,GameWonActivity.class);
							try {
								dataOutputStream.writeUTF("4");
							} catch (IOException e) {
								e.printStackTrace();
								//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								// TODO Auto-generated catch block
								
							}
							
								try {
										serverSocket.close();
										closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
							
							startActivity(i);
							}

					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//
				final String errMsg = e.toString();
				ProposeScreenActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(ProposeScreenActivity.this, "An exception occurred: " +errMsg, Toast.LENGTH_SHORT).show();;
					}
				});
				
			} 			
			
		}
		
		
		

	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip =inetAddress.getHostAddress();
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
			//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
		}

		return ip;
	}
	
	public void closeSockets()
	{
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (dataInputStream != null) {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

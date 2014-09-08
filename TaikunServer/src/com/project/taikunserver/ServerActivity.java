package com.project.taikunserver;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class ServerActivity extends Activity {
	
	TextView info, infoip, msg;
	String message = "";
	ServerSocket serverSocket;
	EditText chatBoxText;
	Button sendChatbtn, startGameBtn;
	String wool = "3";
	String rock = "3";
	String wheat = "3";
	
	Socket socket = null;
	DataInputStream dataInputStream = null;
	DataOutputStream dataOutputStream = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_socket);
		info = (TextView) findViewById(R.id.info);
		infoip = (TextView) findViewById(R.id.infoip);
		msg = (TextView) findViewById(R.id.msg);
		chatBoxText=(EditText) findViewById(R.id.chatBox);
		sendChatbtn=(Button) findViewById(R.id.sendChatButton);
		startGameBtn=(Button) findViewById(R.id.startGamebutton);
		infoip.setText(getIpAddress());
		Intent intent = getIntent();
		rock=intent.getStringExtra("rock");
		wheat=intent.getStringExtra("wheat");
		wool=intent.getStringExtra("wool");
		startGameBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent iii=new Intent(ServerActivity.this,TaikunGameActivity.class);
				iii.putExtra("IP Address", infoip.getText().toString());
				iii.putExtra("Port", 8080);
				iii.putExtra("Chat", message);
				iii.putExtra("rock",rock);
				iii.putExtra("wheat",wheat);
				iii.putExtra("wool", wool);
				
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
				
				
				startActivity(iii);
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
		int count = 0;
		
		String chatMsg = chatBoxText.getText().toString();
		@Override
		public void run() {	
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				ServerActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						info.setText("Port: "
								+ serverSocket.getLocalPort());
					}
				});

				while (true) {
					socket = serverSocket.accept();
					dataInputStream = new DataInputStream(
							socket.getInputStream());
					dataOutputStream = new DataOutputStream(
							socket.getOutputStream());

					String messageFromClient = "";
					
					//If no message sent from client, this code will block the program
					messageFromClient = dataInputStream.readUTF();
					
					count++;
					message += //"#" + count + " from " + socket.getInetAddress()
							//+ ":" + socket.getPort() + "\n" + 
							"Client says: " + messageFromClient + "\n";

					ServerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							msg.setText(message);					
						}
					});
					
					sendChatbtn.setOnClickListener(new OnClickListener() {
						
					@Override
					public void onClick(View v) {
						chatMsg = chatBoxText.getText().toString();
						message += "You say: " + chatMsg + "\n";
						msg.setText(message);
						chatBoxText.setText("");
						
						try {
							dataOutputStream.writeUTF(chatMsg);
						} catch (IOException e) {
							e.printStackTrace();
							//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
							// TODO Auto-generated catch block
							
						}

					}
					
					});
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
				final String errMsg = e.toString();
				ServerActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						msg.setText(errMsg);
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
						ip += "IP Address: "
								+ inetAddress.getHostAddress() + "\n";
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


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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TaikunGameActivity extends Activity {
	String[] resources = {"Wheat", "Ore", "Wool"};
	String wheat, wool, ore;
	int wheatResource, oreResource, woolResource;
	String textResourceFromSpinner;
	String textResourceFromSpinner2;
	TextView textView1,msg, wheatState, woolState, oreState;
	 Button btnOpenPopup;
	String message = "";
	String clientMsg = " ";
	String msgToClient = "";
	String msgToClientTrade="";
	String messageFromClient = "";
	ServerSocket serverSocket;
	Socket socket = null;
	String chatMessage;
	String tradeAction;
	int countRound = 0;
	DataInputStream dataInputStream = null;
	DataOutputStream dataOutputStream = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taikun_game);
		
		Intent intent = getIntent();
		String ipAddress = intent.getStringExtra("IP Address");
		String portNumber= "8080";
		chatMessage=intent.getStringExtra("Chat");
		ore=intent.getStringExtra("rock");
		wheat=intent.getStringExtra("wheat");
		wool=intent.getStringExtra("wool");
	
		//TextView tv = (TextView) findViewById(R.id.textView1);
		//tv.setText(ipAddress);
		
		//TextView tv2 = (TextView) findViewById(R.id.textView2);
		//tv2.setText(portNumber);
		msg = (TextView) findViewById(R.id.tradingProposalView);
		 textView1=(TextView)findViewById(R.id.textView1);
		  //textView2=(TextView)findViewById(R.id.textView2);
		  
		  btnOpenPopup = (Button) findViewById(R.id.buttonDeclineTrade);
		  wheatState=(TextView)findViewById(R.id.wheatGoalState);
		  woolState=(TextView)findViewById(R.id.woolGoalState);
		  oreState=(TextView)findViewById(R.id.rocksGoalState);
		  
		  if(ore!=null)
		  {
			  oreState.setText(ore);
		  }
		  if(wheat!=null)
		  {
			  wheatState.setText(wheat);
		  }
		  if(wool!=null)
		  {
			  woolState.setText(wool);
		  }
		  Thread socketServerThread = new Thread(new SocketServerThread());
			socketServerThread.start();
	}
	
	private class SocketServerThread extends Thread {

		static final int SocketServerPORT = 8080;
		int count = 0;
		
		//String chatMsg = chatBoxText.getText().toString();
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

					
					
					//If no message sent from client, this code will block the program
					messageFromClient = dataInputStream.readUTF();
					
					
					//***************************************make changes if we have accepted client's trade offer*************************************
					if (messageFromClient.equals("1 Wheat for 1 Ore")) {	
						clientMsg=messageFromClient;	
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
			
							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade "+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {
														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												wheat= wheatState.getText().toString();
												wheatResource=Integer.parseInt(wheat);
												wheatResource=wheatResource+1;
												wheat=Integer.toString(wheatResource);
				                				wheatState.setText(wheat);
				                				
				                				ore=oreState.getText().toString();
				        						oreResource=Integer.parseInt(ore);
				        						oreResource=oreResource-1;
				        						ore=Integer.toString(oreResource);
				                				oreState.setText(ore);
				                				
				                				//start activity to go to propose screen
				        						tradeAction += "You accepted 1 Wheat for 1 Ore" + "\n";
				        						countRound++;
				        						Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
				        						intent2.putExtra("Chat", chatMessage);
				        						intent2.putExtra("Wheat", wheat);
				        						intent2.putExtra("Ore", ore);
				        						intent2.putExtra("Wool", wool);
				        						intent2.putExtra("Round Number", countRound);
				        						intent2.putExtra("Trade Actions", tradeAction);
				        						try {
				        							serverSocket.close();
				        							closeSockets();
				        						} catch (IOException e) {
				        							// TODO Auto-generated catch block
				        							e.printStackTrace();
				        							//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
				        						}
				        						startActivity(intent2);
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												
												tradeAction += "You rejected 1 Wheat for 1 Ore" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
				                
							}
						});
						
						
						
					}
					else if (messageFromClient.equals("1 Wheat for 1 Wool")) {
						clientMsg=messageFromClient;
						
						
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade"+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												wheat= wheatState.getText().toString();
												wheatResource=Integer.parseInt(wheat);
												wheatResource=wheatResource+1;
												wheat=Integer.toString(wheatResource);
				                				wheatState.setText(wheat);
				                				
				                				wool=woolState.getText().toString();
				        						woolResource=Integer.parseInt(wool);
				        						woolResource=woolResource-1;
				        						wool=Integer.toString(woolResource);
				        						woolState.setText(wool);
				        						
				        						//start activity to go to propose screen
				        						tradeAction += "You accepted 1 Wheat for 1 Wool" + "\n";
				        						countRound++;
				        						Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
				        						intent2.putExtra("Chat", chatMessage);
				        						intent2.putExtra("Wheat", wheat);
				        						intent2.putExtra("Ore", ore);
				        						intent2.putExtra("Wool", wool);
				        						intent2.putExtra("Round Number", countRound);
				        						intent2.putExtra("Trade Actions", tradeAction);
				        						try {
				        							serverSocket.close();
				        							closeSockets();
				        						} catch (IOException e) {
				        							// TODO Auto-generated catch block
				        							e.printStackTrace();
				        							//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
				        						}
				        						startActivity(intent2);
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												
												tradeAction += "You rejected 1 Wheat for 1 Wool" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
							}
						});
						
					}
					else if (messageFromClient.equals("1 Ore for 1 Wheat")) {
						clientMsg=messageFromClient;
						
						
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade"+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												ore= oreState.getText().toString();
												oreResource=Integer.parseInt(ore);
												oreResource=oreResource+1;
												ore=Integer.toString(oreResource);
												oreState.setText(ore);
				                				
												wheat=wheatState.getText().toString();
												wheatResource=Integer.parseInt(wheat);
												wheatResource=wheatResource-1;
												wheat=Integer.toString(wheatResource);
												wheatState.setText(wheat);
												
												//start activity to go to propose screen
												tradeAction += "You accepted 1 Ore for 1 Wheat" + "\n";
												countRound++;
												Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
												intent2.putExtra("Chat", chatMessage);
												intent2.putExtra("Wheat", wheat);
												intent2.putExtra("Ore", ore);
												intent2.putExtra("Wool", wool);
												intent2.putExtra("Round Number", countRound);
												intent2.putExtra("Trade Actions", tradeAction);
												try {
													serverSocket.close();
													closeSockets();
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
												}
												startActivity(intent2);
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												tradeAction += "You rejected 1 Ore for 1 Wheat" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
							}
						});
					//start activity to go back to propose screen
						
					}
					else if (messageFromClient.equals("1 Ore for 1 Wool")) {
						clientMsg=messageFromClient;
						
						
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade"+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												ore= oreState.getText().toString();
				        						oreResource=Integer.parseInt(ore);
				        						oreResource=oreResource+1;
				        						ore=Integer.toString(oreResource);
				        						oreState.setText(ore);
				        						
				        						wool=woolState.getText().toString();
				        						woolResource=Integer.parseInt(wool);
				        						woolResource=woolResource-1;
				        						wool=Integer.toString(woolResource);
				        						woolState.setText(wool);
				        						
				        						//start activity to go to propose screen
				        						tradeAction += "You accepted 1 Ore for 1 Wool" + "\n";
				        						countRound++;
				        						Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
				        						intent2.putExtra("Chat", chatMessage);
				        						intent2.putExtra("Wheat", wheat);
				        						intent2.putExtra("Ore", ore);
				        						intent2.putExtra("Wool", wool);
				        						intent2.putExtra("Round Number", countRound);
				        						intent2.putExtra("Trade Actions", tradeAction);
				        						try {
				        							serverSocket.close();
				        							closeSockets();
				        						} catch (IOException e) {
				        							// TODO Auto-generated catch block
				        							e.printStackTrace();
				        							//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
				        						}
				        						startActivity(intent2);
				                				
				                				
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												tradeAction += "You rejected 1 Ore for 1 Wool" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
							}
						});
		
					}
					else if (messageFromClient.equals("1 Wool for 1 Wheat")) {
						clientMsg=messageFromClient;
						
						
						
						
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade"+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												wool=woolState.getText().toString();
												woolResource=Integer.parseInt(wool);
												woolResource=woolResource+1;
												wool=Integer.toString(woolResource);
												woolState.setText(wool);
												
				                				
												wheat=wheatState.getText().toString();
												wheatResource=Integer.parseInt(wheat);
												wheatResource=wheatResource-1;
												wheat=Integer.toString(wheatResource);
												wheatState.setText(wheat);
												
												//start activity to go to propose screen
												tradeAction += "You accepted 1 Wool for 1 Wheat" + "\n";
												countRound++;
												Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
												intent2.putExtra("Chat", chatMessage);
												intent2.putExtra("Wheat", wheat);
												intent2.putExtra("Ore", ore);
												intent2.putExtra("Wool", wool);
												intent2.putExtra("Round Number", countRound);
												intent2.putExtra("Trade Actions", tradeAction);
												try {
													serverSocket.close();
													closeSockets();
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
												}
												startActivity(intent2);
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												tradeAction += "You rejected 1 Wool for 1 Wheat" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
							}
						});
		
						
					}
					else if (messageFromClient.equals("1 Wool for 1 Ore")) {
						clientMsg=messageFromClient;
						
						
						TaikunGameActivity.this.runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								//show alert dialog
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				                        TaikunGameActivity.this);
				 
				                alertDialog.setTitle("Trade Offer");
				 
				                alertDialog.setMessage("Would you like to accept " + messageFromClient + "?");
				 
				                alertDialog.setIcon(R.drawable.ic_launcher);
				                
				                //accept trade button
				                alertDialog.setPositiveButton("ACCEPT OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                                // User pressed ACCEPT OFFER button. Write Logic Here
				                                Toast.makeText(getApplicationContext(),
				                                        "Trade has been accepted",
				                                        Toast.LENGTH_SHORT).show();
				                                
				                                message +="You have accepted trade"+ "\n";
				                                
				                              //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade ="trade accepted";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
				                                
				                              //update values
												wool=woolState.getText().toString();
												woolResource=Integer.parseInt(wool);
												woolResource=woolResource+1;
												wool=Integer.toString(woolResource);
												woolState.setText(wool);
												
				                				
												ore=oreState.getText().toString();
												oreResource=Integer.parseInt(ore);
												oreResource=oreResource-1;
												ore=Integer.toString(oreResource);
												oreState.setText(ore);
												
												//start activity to go to propose screen
												tradeAction += "You accepted 1 Wool for 1 Ore" + "\n";
												countRound++;
												Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
												intent2.putExtra("Chat", chatMessage);
												intent2.putExtra("Wheat", wheat);
												intent2.putExtra("Ore", ore);
												intent2.putExtra("Wool", wool);
												intent2.putExtra("Round Number", countRound);
												intent2.putExtra("Trade Actions", tradeAction);
												try {
													serverSocket.close();
													closeSockets();
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
												}
												startActivity(intent2);
												
				                                
				                            }
				                        });
				                    //decline trade button
				                    alertDialog.setNegativeButton("DECLINE OFFER",
				                        new DialogInterface.OnClickListener() {
				 
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            Toast.makeText(getApplicationContext(),
				                                        "Trade has been cancelled",
				                                        Toast.LENGTH_SHORT).show();
				                            message +="You have declined trade"+ "\n";
				                            //show message in the scroll view
												msg.setText(message);
											  //inform server about trade being accepted
												msgToClientTrade="trade declined";
												try {
													dataOutputStream.writeUTF(msgToClientTrade);
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
													final String errMsg = e.toString();
													TaikunGameActivity.this.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															msg.setText(errMsg);
														}
													});
												}
												tradeAction += "You rejected 1 Wool for 1 Ore" + "\n";
				                           
				                            }
				                        });
				                alertDialog.show();
								//wheatState.setText(wheat);
							}
						});
					
					}
					//***********************************make changes if client has accepted our trade offer**********************************************
					if (messageFromClient.equals("trade accepted")){
						message +="Client has accepted your trade" + "\n";
						msg.setText(message);
							if(clientMsg.equals("1 Wheat for 1 Ore"))
							{
								wheat=wheatState.getText().toString();
								wheatResource=Integer.parseInt(wheat);
								wheatResource=wheatResource-1;
								wheat=Integer.toString(wheatResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										wheatState.setText(wheat);		
									}
								});
								ore=oreState.getText().toString();
								oreResource=Integer.parseInt(ore);
								oreResource=oreResource+1;
								ore=Integer.toString(oreResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										oreState.setText(ore);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Wheat for 1 Ore" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
								
							}
							else if(clientMsg.equals("1 Wheat for 1 Wool"))
							{
								wheat=wheatState.getText().toString();
								wheatResource=Integer.parseInt(wheat);
								wheatResource=wheatResource-1;
								wheat=Integer.toString(wheatResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										wheatState.setText(wheat);
										
									}
								});
								
								
								wool=woolState.getText().toString();
								woolResource=Integer.parseInt(wool);
								woolResource=woolResource+1;
								wool=Integer.toString(woolResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										woolState.setText(wool);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Wheat for 1 Wool" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
							}
							else if(clientMsg.equals("1 Wool for 1 Ore"))
							{
								wool=woolState.getText().toString();
								woolResource=Integer.parseInt(wool);
								woolResource=woolResource-1;
								wool=Integer.toString(woolResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										woolState.setText(wool);
										
									}
								});
								
								
								ore=oreState.getText().toString();
								oreResource=Integer.parseInt(ore);
								oreResource=oreResource+1;
								ore=Integer.toString(oreResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										oreState.setText(ore);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Wool for 1 Ore" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
								
							}
							else if(clientMsg.equals("1 Wool for 1 Wheat"))
							{
								wool=woolState.getText().toString();
								woolResource=Integer.parseInt(wool);
								woolResource=woolResource-1;
								wool=Integer.toString(woolResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										woolState.setText(wool);
										
									}
								});
								
								
								wheat=wheatState.getText().toString();
								wheatResource=Integer.parseInt(wheat);
								wheatResource=wheatResource+1;
								wheat=Integer.toString(wheatResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										wheatState.setText(wheat);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Wool for 1 Wheat" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
								
								
							}
							else if(clientMsg.equals("1 Ore for 1 Wool"))
							{
								ore=oreState.getText().toString();
								oreResource=Integer.parseInt(ore);
								oreResource=oreResource-1;
								ore=Integer.toString(oreResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										oreState.setText(ore);
										
									}
								});
								
								
								wool=woolState.getText().toString();
								woolResource=Integer.parseInt(wool);
								woolResource=woolResource+1;
								wool=Integer.toString(woolResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										woolState.setText(wool);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Ore for 1 Wool" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
								
							}
							else if(clientMsg.equals("1 Ore for 1 Wheat"))
							{
								ore=oreState.getText().toString();
								oreResource=Integer.parseInt(ore);
								oreResource=oreResource-1;
								ore=Integer.toString(oreResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										oreState.setText(ore);
										
									}
								});
								
								
								wheat=wheatState.getText().toString();
								wheatResource=Integer.parseInt(wheat);
								wheatResource=wheatResource+1;
								wheat=Integer.toString(wheatResource);
								TaikunGameActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										wheatState.setText(wheat);
										
									}
								});
								//start activity to go to propose screen
								tradeAction += "Client accepted 1 Ore for 1 Wheat" + "\n";
								countRound++;
								Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
								intent2.putExtra("Chat", chatMessage);
								intent2.putExtra("Wheat", wheat);
								intent2.putExtra("Ore", ore);
								intent2.putExtra("Wool", wool);
								intent2.putExtra("Round Number", countRound);
								intent2.putExtra("Trade Actions", tradeAction);
								try {
									serverSocket.close();
									closeSockets();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//Toast.makeText(ServerActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
								}
								startActivity(intent2);
							}
						
					}
					else if (messageFromClient.equals("trade declined")){
						message +="Client has declined trade" + "\n";
						TaikunGameActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								msg.setText(message);
								
							}
						});
						
						//start activity to go to propose screen
						tradeAction += "Client declined trade for"+ clientMsg + "\n";
						countRound++;
					}

					
					
					count++;
					message += //"#" + count + " from " + socket.getInetAddress()
							//+ ":" + socket.getPort() + "\n" + 
							"Client wants to trade " + messageFromClient + "\n";

					TaikunGameActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							msg.setText(message);
							
						}
					});
					btnOpenPopup.setOnClickListener(new OnClickListener() {

						   @Override
						   public void onClick(View arg0) {
						
						    LayoutInflater layoutInflater = 
						      (LayoutInflater)getBaseContext()
						      .getSystemService(LAYOUT_INFLATER_SERVICE);
						    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
						    final PopupWindow popupWindow = new PopupWindow(
						      popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						    
						    final Button btnDismiss = (Button)popupView.findViewById(R.id.dismissButton);
						    final Button btnOfferTrade = (Button)popupView.findViewById(R.id.offerTradeButton);
						    
						    final Spinner popupSpinner = (Spinner)popupView.findViewById(R.id.popupspinner);
						    final Spinner popupSpinner2 = (Spinner)popupView.findViewById(R.id.popupspinner2);
						    
						    ArrayAdapter<String> adapter = 
						      new ArrayAdapter<String>(TaikunGameActivity.this, 
						        android.R.layout.simple_spinner_item, resources);
						    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						    popupSpinner.setAdapter(adapter);
						    
						    ArrayAdapter<String> adapter2 = 
								      new ArrayAdapter<String>(TaikunGameActivity.this, 
								        android.R.layout.simple_spinner_item, resources);
								    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								    popupSpinner2.setAdapter(adapter);
							TaikunGameActivity.this.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											btnDismiss.setOnClickListener(new OnClickListener(){

											     @Override
											     public void onClick(View v) {
											      popupWindow.dismiss();
											     }});
											
										}
									});
						    
						    
						  TaikunGameActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								  btnOfferTrade.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											
											textResourceFromSpinner = popupSpinner.getSelectedItem().toString();
											textResourceFromSpinner2 = popupSpinner2.getSelectedItem().toString();
											message += "You have offered 1 " + textResourceFromSpinner + " for 1 " + textResourceFromSpinner2+ "\n";
											msg.setText(message);
											msgToClient= "1 " + textResourceFromSpinner + " for 1 " + textResourceFromSpinner2;
											clientMsg=msgToClient;
											//textView1.setText(textResourceFromSpinner);
											//textView2.setText(textResourceFromSpinner2);
											
											//send trade offer to client
											try {
												dataOutputStream.writeUTF(msgToClient);
											} catch (IOException e) {
												e.printStackTrace();
												//Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
												// TODO Auto-generated catch block
												
											}
											popupWindow.dismiss();
											
											
										}
									});
							}
						});
						    
						    //popupWindow.showAsDropDown(btnOpenPopup, 50, -30);\
						    popupWindow.showAtLocation(textView1, Gravity.CENTER, 0, 0);
						   }
						   
						   

						  });
					
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
				final String errMsg = e.toString();
				TaikunGameActivity.this.runOnUiThread(new Runnable() {

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
			//Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
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
}


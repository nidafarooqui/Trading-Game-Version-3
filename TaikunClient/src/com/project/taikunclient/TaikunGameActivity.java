package com.project.taikunclient;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;

public class TaikunGameActivity extends Activity {
	
	TextView textView1,textResponse, wheatState, woolState, oreState;
	Button newTradeButton;
	String message = "";
	Socket socket = null;
	String clientMessage = " ";
	String serverMessage = "";
	String serverMessage2 = "";
	String msgToServerTrade = " ";
	String msg= " ";
	String chatMessage;
	String tradeAction;
	int countRound = 0;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	String[] resources = {"Wheat", "Ore", "Wool"};
	String textResourceFromSpinner;
	String textResourceFromSpinner2;
	
	String wheat, wool, ore;
	int wheatResource, oreResource, woolResource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taikun_game);
		
		Intent intent = getIntent();
		final String ipAddress = intent.getStringExtra("IP Address");
		final String portNumber= "8080";
		chatMessage=intent.getStringExtra("Chat");
		ore=intent.getStringExtra("rock");
		wheat=intent.getStringExtra("wheat");
		wool=intent.getStringExtra("wool");
		
		
		textView1=(TextView)findViewById(R.id.textView1);
		//textView2=(TextView)findViewById(R.id.textView2);
		textResponse = (TextView) findViewById(R.id.tradingProposalView);
		
		newTradeButton= (Button)findViewById(R.id.buttonDeclineTrade);
		
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
		
		
		newTradeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				LayoutInflater layoutInflater = 
					      (LayoutInflater)getBaseContext()
					      .getSystemService(LAYOUT_INFLATER_SERVICE);
					    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
					    final PopupWindow popupWindow = new PopupWindow(
					      popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					    
					    Button btnDismiss = (Button)popupView.findViewById(R.id.dismissButton);
					    Button btnOfferTrade = (Button)popupView.findViewById(R.id.offerTradeButton);
					    
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
					    
					    btnDismiss.setOnClickListener(new OnClickListener(){

					     @Override
					     public void onClick(View v) {
					      popupWindow.dismiss();
					     }});
					    
					    btnOfferTrade.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								textResourceFromSpinner = popupSpinner.getSelectedItem().toString();
								textResourceFromSpinner2 = popupSpinner2.getSelectedItem().toString();
								//textView1.setText(textResourceFromSpinner);
								//textView2.setText(textResourceFromSpinner2);
								message += "You have offered 1 " + textResourceFromSpinner + " for 1 " + textResourceFromSpinner2+ "\n";
								clientMessage = "1 " + textResourceFromSpinner + " for 1 " + textResourceFromSpinner2;
								msg="1 " + textResourceFromSpinner + " for 1 " + textResourceFromSpinner2;
								//show message in the scroll view
								textResponse.setText(message);
								//String tMsg ="";
								
								//send message to the server
								MyClientTask myClientTask = new MyClientTask(ipAddress, Integer.parseInt(portNumber),
										clientMessage);
								myClientTask.execute();
								popupWindow.dismiss();
								
								
							}
							
						});
					    
					    //popupWindow.showAsDropDown(btnOpenPopup, 50, -30);\
					    popupWindow.showAtLocation(textView1, Gravity.CENTER, 0, 0);
			}
			
		});
		
		
	}
	
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
				serverMessage = dataInputStream.readUTF();
				
	
				message+= "Server says: " + serverMessage + "\n";

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message = "UnknownHostException: " + e.toString();
				Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message= "IOException: " + e.toString();
				Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			} 
			return null;
		}

		
		@Override
		protected void onPostExecute(Void result) {
			
			//***************************************make changes if we have accepted server's trade offer*************************************
			if (serverMessage.equals("1 Wheat for 1 Ore")) {
				
				msg=serverMessage;
				
				//show alert dialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
                                
                                message +="You have accepted trade" + "\n";
                                
                              //show message in the scroll view
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								
								tradeAction += "You rejected 1 Wheat for 1 Ore" + "\n";
                           
                            }
                        });
                alertDialog.show();
				
				
			}
			else if (serverMessage.equals("1 Wheat for 1 Wool")) {
				msg=serverMessage;
				//show alert dialog
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								tradeAction += "You rejected 1 Wheat for 1 Wool" + "\n";
								
                            }
                        });
                alertDialog.show();
				
				
			}
			else if (serverMessage.equals("1 Ore for 1 Wheat")) {
				msg=serverMessage;
				
				//show alert dialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								tradeAction += "You rejected 1 Ore for 1 Wheat" + "\n";
                           
                            }
                        });
                alertDialog.show();
				
				
				
			}
			else if (serverMessage.equals("1 Ore for 1 Wool")) {
				msg=serverMessage;
				//show alert dialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								tradeAction += "You rejected 1 Ore for 1 Wool" + "\n";
                           
                            }
                        });
                alertDialog.show();
				
			}
			else if (serverMessage.equals("1 Wool for 1 Wheat")) {
				msg=serverMessage;
				//show alert dialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								tradeAction += "You rejected 1 Wool for 1 Wheat" + "\n";
                           
                            }
                        });
                alertDialog.show();
				
				
			}
			else if (serverMessage.equals("1 Wool for 1 Ore")) {
				msg=serverMessage;
				//show alert dialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        TaikunGameActivity.this);
 
                alertDialog.setTitle("Trade Offer");
 
                alertDialog.setMessage("Would you like to accept " + serverMessage + "?");
 
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade accepted";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
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
								textResponse.setText(message);
							  //inform server about trade being accepted
								msgToServerTrade="trade declined";
								try {
									dataOutputStream.writeUTF(msgToServerTrade);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									final String errMsg = e.toString();
									textResponse.setText(errMsg);
								}
								tradeAction += "You rejected 1 Wool for 1 Ore" + "\n";
                           
                            }
                        });
                alertDialog.show();
				
			}
			//***********************************make changes if server has accepted our trade offer**********************************************
			if (serverMessage.equals("trade accepted")){
				message +="Server has accepted your trade" + "\n";
				textResponse.setText(message);
					if(msg.equals("1 Wheat for 1 Ore"))
					{
						wheat=wheatState.getText().toString();
						wheatResource=Integer.parseInt(wheat);
						wheatResource=wheatResource-1;
						wheat=Integer.toString(wheatResource);
						wheatState.setText(wheat);
						
						ore=oreState.getText().toString();
						oreResource=Integer.parseInt(ore);
						oreResource=oreResource+1;
						ore=Integer.toString(oreResource);
						oreState.setText(ore);
						
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
						startActivity(intent2);
						
						
					}
					else if(msg.equals("1 Wheat for 1 Wool"))
					{
						wheat=wheatState.getText().toString();
						wheatResource=Integer.parseInt(wheat);
						wheatResource=wheatResource-1;
						wheat=Integer.toString(wheatResource);
						wheatState.setText(wheat);
						
						wool=woolState.getText().toString();
						woolResource=Integer.parseInt(wool);
						woolResource=woolResource+1;
						wool=Integer.toString(woolResource);
						woolState.setText(wool);
						
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
						startActivity(intent2);
					}
					else if(msg.equals("1 Wool for 1 Ore"))
					{
						wool=woolState.getText().toString();
						woolResource=Integer.parseInt(wool);
						woolResource=woolResource-1;
						wool=Integer.toString(woolResource);
						woolState.setText(wool);
						
						ore=oreState.getText().toString();
						oreResource=Integer.parseInt(ore);
						oreResource=oreResource+1;
						ore=Integer.toString(oreResource);
						oreState.setText(ore);
						
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
						startActivity(intent2);
					}
					else if(msg.equals("1 Wool for 1 Wheat"))
					{
						wool=woolState.getText().toString();
						woolResource=Integer.parseInt(wool);
						woolResource=woolResource-1;
						wool=Integer.toString(woolResource);
						woolState.setText(wool);
						
						wheat=wheatState.getText().toString();
						wheatResource=Integer.parseInt(wheat);
						wheatResource=wheatResource+1;
						wheat=Integer.toString(wheatResource);
						wheatState.setText(wheat);
						
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
						startActivity(intent2);
						
					}
					else if(msg.equals("1 Ore for 1 Wool"))
					{
						ore=oreState.getText().toString();
						oreResource=Integer.parseInt(ore);
						oreResource=oreResource-1;
						ore=Integer.toString(oreResource);
						oreState.setText(ore);
						
						wool=woolState.getText().toString();
						woolResource=Integer.parseInt(wool);
						woolResource=woolResource+1;
						wool=Integer.toString(woolResource);
						woolState.setText(wool);
						
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
						startActivity(intent2);
						
					}
					else if(msg.equals("1 Ore for 1 Wheat"))
					{
						ore=oreState.getText().toString();
						oreResource=Integer.parseInt(ore);
						oreResource=oreResource-1;
						ore=Integer.toString(oreResource);
						oreState.setText(ore);
						
						wheat=wheatState.getText().toString();
						wheatResource=Integer.parseInt(wheat);
						wheatResource=wheatResource+1;
						wheat=Integer.toString(wheatResource);
						wheatState.setText(wheat);
						
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
						startActivity(intent2);
					}
				
			}
			else if (serverMessage.equals("trade declined")){
				message +="Server has declined trade" + "\n";
				textResponse.setText(message);
				//start activity to go to propose screen
				tradeAction += "Client declined trade for"+ msg + "\n";
				countRound++;
				//Intent intent2= new Intent(TaikunGameActivity.this,ProposeScreenActivity.class);
				//intent2.putExtra("Chat", chatMessage);
				//intent2.putExtra("Wheat", wheat);
				//intent2.putExtra("Ore", ore);
				//intent2.putExtra("Wool", wool);
				//intent2.putExtra("Round Number", countRound);
				//intent2.putExtra("Trade Actions", tradeAction);
				//startActivity(intent2);
				
				
			}
			
			textResponse.setText(message);
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
				Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		if (dataInputStream != null) {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}

		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(TaikunGameActivity.this, "An exception occurred: " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
			
	}
	protected void onDestroy()
	{
		super.onDestroy();
		closeSockets();
		
	}
}

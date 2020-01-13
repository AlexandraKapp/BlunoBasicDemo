package com.dfrobot.angelo.blunobasicdemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.Date;

// TODO: proper frequence (once every minute??)
// TODO: keep connection when App is closed
// TODO: send to remote database

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;
	//private AppDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary

		//TODO: dont recreate db with every app start
		// create database
		//db = Room.databaseBuilder(getApplicationContext(),
		//		AppDatabase.class, "temperature-db").build();


		requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

		serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
				if (permissionCheck != PackageManager.PERMISSION_GRANTED){
					boolean requestCheck = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
					if (requestCheck){
						requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
					}else {
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("Permission Required")
								.setMessage("Please enable location permission to use this application.")
								.setNeutralButton("I Understand", null)
								.show();
					}
				}else {
					buttonScanOnClickProcess(); //Alert Dialog for selecting the BLE device
				}
			}
		});
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        //onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		//onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {//Once connection data received, this function will be called
		double tempValue = Float.parseFloat(theString);
		serialReceivedText.append(theString);//append the text into the EditText
        serialReceivedText.append("\n");

		//TODO: save data to DB (change to float)
		long time = System.currentTimeMillis();
		Temperature newTemperature = new Temperature(time, tempValue);

		new AgentAsyncTask(this,newTemperature).execute();


		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}



	private static class AgentAsyncTask extends AsyncTask<Void, Void, Integer> {

		//Prevent leak
		private WeakReference<Activity> weakActivity;
		private Temperature temperature;

		public AgentAsyncTask(Activity activity, Temperature temperature){
			weakActivity = new WeakReference<>(activity);
			this.temperature = temperature;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			TemperatureDao temperatureDao = MyApp.DatabaseSetup.getDatabase().temperatureDao();
			temperatureDao.insertTemperature(temperature);
			//TODO proper return
			return null;
		}
	}

}
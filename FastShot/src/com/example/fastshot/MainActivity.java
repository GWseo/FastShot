package com.example.fastshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private Camera cam;
	private CameraPreview mPreview;
	private Button btn1;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private boolean checkCameraHardware(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			return true;
		}else{
			return false;
		}
	}
	
	public static Camera getCameraInstance(){
		Camera c = null;
		try{
			c= Camera.open();
		}catch(Exception e){
			
		}
		return c;
	}
	private PictureCallback mPicture = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.d("FastShot","checkPoint3");
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			Log.d("FastShot","checkPoint");
			if(pictureFile == null){
				Log.d("FastShot","Error creating media file, check storage permissions:");
				return;
			}
			Log.d("FastShot","checkPoint");
			try{
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			}catch(FileNotFoundException e){
				Log.d("FastShot","File not found");
			}catch(IOException e){
				Log.d("FastShot","Error accessing file:");
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cam = getCameraInstance();
		
		mPreview = new CameraPreview(this, cam);
		
		FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		
		Button captureButton = (Button)findViewById(R.id.button1);
		captureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cam.takePicture(null, null, mPicture);
			}
		});
		
		Log.d("FastShot","Checkpoint 1");
//		mThread t1 = new mThread();
//		t1.start();
	}
	
	private class mThread extends Thread{
		@Override
		public void run(){
			//cam.takePicture(null, null, mPicture);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static File getOutputMediaFile(int type){
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FastShot");

		if(!mediaStorageDir.exists()){
			if(!mediaStorageDir.mkdirs()){
				Log.d("FastShot","Fail to make direcotry");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if(type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getParent()+File.separator+"IMG_"+timeStamp+".jpg");
			Log.d("FastShot",mediaStorageDir.getParent());
		}
		else{
			return null;
		}

		return mediaFile;
	}

}

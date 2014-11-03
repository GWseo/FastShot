package com.example.fastshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder mHolder;
	private Camera mCamera;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private static ReentrantLock mLock;
	
	public CameraPreview(Context context, Camera camera){
		super(context);
		mCamera = camera;
		mLock = new ReentrantLock();
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mLock.lock();
	}
	public void surfaceCreated(SurfaceHolder holder){
		try{
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			mCamera.stopPreview();
		}catch(IOException  e){
			
		}
	}
	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
		
		if(mHolder.getSurface() == null){
			return;
		}
		try{
			mCamera.stopPreview();
		}catch(Exception e){
			
		}
		
		try{
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			
			mCamera.takePicture(null, null, mPicture);
			
			mLock.lock();
			
			mCamera.stopPreview();
			mLock.unlock();
		}catch(Exception e){
			Log.d("FastShot","Error starting camera preview");
		}
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
			mLock.unlock();
		}

	};
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

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}

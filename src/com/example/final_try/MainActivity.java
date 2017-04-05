package com.example.final_try;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();
	
 
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PICK_IMAGE_REQUEST_CODE = 300;

    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_PICK = 3;

 
    private Uri fileUri; // file url to store image/video
    
    private Button btnCapturePicture, btnRecordVideo, btnPickPicture;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        // Changing action bar background color
        // These two lines are not needed
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));
 
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        //btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        btnPickPicture = (Button) findViewById(R.id.btnPickImage);

 
        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
 
        /**
         * Record video button click event
         */

       btnPickPicture.setOnClickListener(new View.OnClickListener() {
        	 
            @Override
            public void onClick(View v) {
                // record video
                PickPicture();
            }
        });
        
 
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }
 
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
 
    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
 
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    /**
     * Launching camera app to record video
     */

    
    private void PickPicture() {
    	try {
    	    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
    	                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	        // Start the Intent
    	         fileUri = getOutputMediaFileUri(MEDIA_TYPE_PICK);//this is m using for camera 
    	         Log.w("ImageAddressOnClick pr", ""+fileUri);
    	        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    	    } catch (Exception e) {
    	        Toast.makeText(getApplicationContext(),
    	        e.getMessage()+"ye show hora h",
    	        Toast.LENGTH_LONG).show();
    	        Log.e(e.getClass().getName(), e.getMessage(), e);
    	    }
        
    }
 
    
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
 
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
 
    
 
    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                
            	// successfully captured the image
                // launching upload activity
            	launchUploadActivity(true);
            	
            	
            } else if (resultCode == RESULT_CANCELED) {
                
            	// user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        
        } 
        
        else if(requestCode == PICK_IMAGE_REQUEST_CODE)
        	{
        	if(resultCode == RESULT_OK  && null != data)
                {
        		    Toast.makeText(getApplicationContext(), "hiii", Toast.LENGTH_SHORT).show();
                    Uri selectedImage = data.getData();         
                    String picturePath = getRealPathFromURI(selectedImage,
                            this);
                   Intent i = new Intent(MainActivity.this, UploadActivity.class);
                   i.putExtra("filePath", picturePath);
                   i.putExtra("isImage", true);
                   startActivity(i);
        		//launchUploadActivity(true);
                }
        	
        		else if (resultCode == RESULT_CANCELED) {
                
        			// user cancelled Image capture
        				Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            
        			} 
        			else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
        			}
        	
        	}
        
        
    }
    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }
   
    private void launchUploadActivity(boolean isImage){
    	Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }
     
    /**
     * ------------ Helper Methods ---------------------- 
     * */
 
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
 
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
 
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);
 
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
 
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } 
        else if (type == MEDIA_TYPE_PICK) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        else {
            return null;
        }
 
        return mediaFile;
    }
}
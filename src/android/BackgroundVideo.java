package io.iclue.backgroundvideo;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.widget.RelativeLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;


public class BackgroundVideo extends CordovaPlugin {
    private static final String TAG = "BACKGROUND_VIDEO";
    private static final String ACTION_START_RECORDING = "start";
    private static final String ACTION_STOP_RECORDING = "stop";
    private static final String FILE_EXTENSION = ".mp4";
    private String FILE_PATH = "";
    private String FILE_NAME = "";

    //private final static float opacity = 0.3f;
    private VideoOverlay videoOverlay;
    private RelativeLayout relativeLayout;
	//private View barTop;
 	private View barBottom;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        FILE_PATH = cordova.getActivity().getFilesDir().toString() + "/";
    }


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        try {
            Log.d(TAG, "ACTION: " + action);

            if(ACTION_START_RECORDING.equals(action)) {
                FILE_NAME = args.getString(0);
                String CAMERA_FACE = args.getString(1);
                final int x = Integer.parseInt(args.getString(2));
                final int y = Integer.parseInt(args.getString(3));
                final int w = Integer.parseInt(args.getString(4));
                final int h = Integer.parseInt(args.getString(5));
                final int rw = Integer.parseInt(args.getString(6));
                final int rh = Integer.parseInt(args.getString(7));
		final int barh = (rh - 360) * (h / rh) / 2;

                if(videoOverlay == null) {
                    videoOverlay = new VideoOverlay(cordova.getActivity(), getFilePath(), rh, rw);
                    videoOverlay.setCameraFacing(CAMERA_FACE);
					videoOverlay.setTranslationX(x);
					videoOverlay.setTranslationY(y);
					//barTop = new View(cordova.getActivity());
					//barTop.setBackgroundColor(0xFF455A64);
					//barTop.setBackgroundColor(0xFFff0000);
					//barTop.setTranslationX(x);
					//barTop.setTranslationY(y);
					barBottom = new View(cordova.getActivity());
					barBottom.setBackgroundColor(0xFF455A64);
					//barBottom.setBackgroundColor(0xFFff0000);
					barBottom.setTranslationX(x);
					barBottom.setTranslationY(y + h - barh);
					//barBottom.setTranslationY(y + h - barh);

                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            webView.setKeepScreenOn(true); //do via another plugin?
                            try {
					cordova.getActivity().addContentView(videoOverlay, new ViewGroup.LayoutParams(w, h));
					//cordova.getActivity().addContentView(barTop, new ViewGroup.LayoutParams(w, barh));
					cordova.getActivity().addContentView(barBottom, new ViewGroup.LayoutParams(w, barh+40));
                            } catch(Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": " + e.getMessage());
                            }
                        }
                    });
                } else {
                    videoOverlay.setCameraFacing(CAMERA_FACE);
                    videoOverlay.setFilePath(getFilePath());
					

                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                videoOverlay.startPreview(true);
				//barTop.setBackgroundColor(0xFF455A64);
				//barTop.setBackgroundColor(0xFFff0000);
				barBottom.setBackgroundColor(0xFF455A64);
				//barBottom.setBackgroundColor(0xFFff0000);
                            } catch(Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": " + e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }

            if(ACTION_STOP_RECORDING.equals(action)) {
                if(videoOverlay != null) {
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(videoOverlay != null)
                                videoOverlay.onPause();
				//if(barTop != null)
				//	barTop.setBackgroundColor(0x00000000);
				if(barBottom != null)
					barBottom.setBackgroundColor(0x00000000);
                        }
                    });
                }
                return true;
            }

            callbackContext.error(TAG + ": INVALID ACTION");
            return false;
        } catch(Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage(), e);
            callbackContext.error(TAG + ": " + e.getMessage());
            return false;
        }
    }

    private String getFilePath(){
        return  FILE_PATH + getNextFileName() + FILE_EXTENSION;
    }

    private String getNextFileName(){
        int i=1;
        String tmpFileName = FILE_NAME;
        while(new File(FILE_PATH + tmpFileName + FILE_EXTENSION).exists()) {
            tmpFileName = FILE_NAME + '_' + i;
            i++;
        }
        return tmpFileName;
    }

    //Plugin Method Overrides
    /*@Override
    public void onPause(boolean multitasking) {
        if(videoOverlay != null)
            videoOverlay.onPause();
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if(videoOverlay != null)
            videoOverlay.onResume();
    }*/

    @Override
    public void onDestroy() {
        if(videoOverlay != null)
            videoOverlay.onDestroy();
        super.onDestroy();
    }

}

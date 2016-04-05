package io.iclue.backgroundvideo;

import android.graphics.Matrix;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class TextureViewPreview implements Preview, TextureView.SurfaceTextureListener {
    private static final String TAG = "BACKGROUND_VID_TEXTURE";
    private final TextureView view;
    private final VideoOverlay overlay;
    private SurfaceTexture surface;
    private float opacity = 0.2f;
    private boolean startRecordingOnCreate = true;

    public TextureViewPreview (VideoOverlay overlay) {
        Log.d(TAG, "Creating Texture Preview");
        this.overlay = overlay;
        view = new TextureView(overlay.getContext());

/**/
    float scaleX = 1.0f;
    float scaleY = 2.0f;
int mVideoWidth = 360;
int mVideoHeight = 720;
int viewWidth = mVideoWidth - 180;
int viewHeight = mVideoHeight - 360;

    /*if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
        scaleX = mVideoWidth / viewWidth;
        scaleY = mVideoHeight / viewHeight;
    } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
        scaleY = viewWidth / mVideoWidth;
        scaleX = viewHeight / mVideoHeight;
    } else if (viewWidth > mVideoWidth) {
        scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
    } else if (viewHeight > mVideoHeight) {
        scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
    }*/

    // Calculate pivot points, in our case crop from center
    int pivotPointX = viewWidth / 2;
    int pivotPointY = viewHeight / 2;

    Matrix matrix = new Matrix();
    matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

    view.setTransform(matrix);
/**/

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setClickable(false);
        view.setSurfaceTextureListener(this);

        //Note done in backgroundvideo on the wrapping view.
        //view.setAlpha(opacity);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(TAG, "Creating Texture Created");
        try {
            this.surface = surfaceTexture;
            overlay.previewAvailable();
            overlay.initPreview(height, width);


            if (startRecordingOnCreate) {
                overlay.startRecording();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error start camera", ex);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "Surface Destroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = opacity;
        view.setAlpha(opacity);
    }

    @Override
    public void startRecordingWhenAvailable(boolean startOnCreate) { startRecordingOnCreate = startOnCreate; }

    @Override
    public void attach(Camera camera) throws IOException {
        camera.setPreviewTexture(surface);
    }

    @Override
    public void attach(MediaRecorder recorder) { }

    @Override
    public View getView() {
        Log.d(TAG, "getView called");
        return view;
    }

    @Override
    public PreviewType getPreviewType(){
        return PreviewType.TEXTURE_VIEW;
    }
}

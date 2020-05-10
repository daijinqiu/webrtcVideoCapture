package com.example.capturecamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.capturecamera.cameras.Camera2Enumerator;
import com.example.capturecamera.cameras.CameraEnumerator;
import com.example.capturecamera.cameras.CapturerObserver;
import com.example.capturecamera.cameras.RendererCommon.ScalingType;
import com.example.capturecamera.cameras.SurfaceTextureHelper;
import com.example.capturecamera.cameras.SurfaceViewRenderer;
import com.example.capturecamera.cameras.VideoCapturer;
import com.example.capturecamera.cameras.VideoFrame;
import com.example.capturecamera.opengl.EglBase;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    final EglBase eglBase = EglBase.create();
    private SurfaceViewRenderer renderer;
    private VideoCapturer videoCapturer;
    private SurfaceTextureHelper surfaceTextureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request();
        renderer = findViewById(R.id.fullscreen_video_view);
        renderer.init(eglBase.getEglBaseContext(), null);
        renderer.setScalingType(ScalingType.SCALE_ASPECT_FILL);
        renderer.setZOrderMediaOverlay(true);
        //renderer.setEnableHardwareScaler(true);
        renderer.setEnableHardwareScaler(false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Log.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Log.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void request() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, " grad");
            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS, 1);
            //videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));

            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
            videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), new CapturerObserver() {
                @Override
                public void onCapturerStarted(boolean success) {

                }

                @Override
                public void onCapturerStopped() {

                }

                @Override
                public void onFrameCaptured(VideoFrame frame) {
                    renderer.onFrame(frame);
                }
            });
            videoCapturer.startCapture(1920, 1080, 30);
        } else {
            Log.d(TAG, " not grad");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));

            surfaceTextureHelper = surfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
            videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), new CapturerObserver() {
                @Override
                public void onCapturerStarted(boolean success) {

                }

                @Override
                public void onCapturerStopped() {

                }

                @Override
                public void onFrameCaptured(VideoFrame frame) {
                    renderer.onFrame(frame);
                }
            });
            videoCapturer.startCapture(1920, 1080, 30);
        }
    }

}

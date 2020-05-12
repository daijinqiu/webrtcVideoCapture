package com.example.capturecamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capturecamera.cameras.CapturerObserver;
import com.example.capturecamera.cameras.RendererCommon.ScalingType;
import com.example.capturecamera.cameras.ScreenCapturerAndroid;
import com.example.capturecamera.cameras.SurfaceTextureHelper;
import com.example.capturecamera.cameras.SurfaceViewRenderer;
import com.example.capturecamera.cameras.VideoCapturer;
import com.example.capturecamera.cameras.VideoFrame;
import com.example.capturecamera.opengl.EglBase;

public class ScreenActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    final EglBase eglBase = EglBase.create();
    private SurfaceViewRenderer renderer;
    private VideoCapturer videoCapturer;
    private SurfaceTextureHelper surfaceTextureHelper;

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
    private static int mediaProjectionPermissionResultCode;
    private static Intent mediaProjectionPermissionResultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen);

        renderer = findViewById(R.id.fullscreen_video_view);
        renderer.init(eglBase.getEglBaseContext(), null);
        renderer.setScalingType(ScalingType.SCALE_ASPECT_FILL);
        renderer.setZOrderMediaOverlay(true);
        renderer.setEnableHardwareScaler(true);
        startScreenCapture();

    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @TargetApi(21)
    private @Nullable
    VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;

        mediaProjectionPermissionResultCode = resultCode;
        mediaProjectionPermissionResultData = data;
        videoCapturer = createScreenCapturer();
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

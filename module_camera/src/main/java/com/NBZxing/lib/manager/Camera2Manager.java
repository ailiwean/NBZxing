package com.NBZxing.lib.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.NBZxing.lib.PermissionActivity;
import com.NBZxing.lib.listener.Callback;
import com.NBZxing.lib.listener.TextureListener;
import com.NBZxing.lib.util.Utils;

import java.util.Collections;

public class Camera2Manager {

    CameraDevice cameraDevice;

    private CameraCaptureSession session;

    public static class Holder {
        static Camera2Manager INSTANCE = new Camera2Manager();
    }

    public static Camera2Manager getInstance() {
        return Holder.INSTANCE;
    }

    public void openCamera() {
        openCamera(null);
    }

    private void openCamera(final Runnable runnable) {

        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    return;
                }

                if (cameraDevice != null)
                    return;

                String mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
                CameraManager cameraManager = (CameraManager) Utils.getContext().getSystemService(Context.CAMERA_SERVICE);
                try {
                    if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        PermissionActivity.request();
                        return;
                    }
                    cameraManager.openCamera(mCameraID, getCameraStateCallback(runnable), getChildHanlder());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /***
     * 创建相机开关回调
     * @param runnable
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback getCameraStateCallback(final Runnable runnable) {
        return new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
                if (runnable != null)
                    runnable.run();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                if (null != cameraDevice) {
                    cameraDevice.close();
                }
                cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Toast.makeText(Utils.getContext(), "摄像头开启失败" + error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /***
     * 创建管道回调
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraCaptureSession.StateCallback getSessionStateCallback(final Callback<CameraCaptureSession> callback) {
        return new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if (callback != null)
                    callback.callback(session);
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Toast.makeText(Utils.getContext(), "配置失败", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /***
     * 若相机未启动则启动并关联Surface
     * @param textureView
     */
    public void startCameraAndPreView(final TextureView textureView) {

        surfaceCreate(textureView, new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    return;
                }

                //如果相机未打开则打开相机回调
                if (cameraDevice == null) {
                    openCamera(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            takePreView(textureView);
                        }
                    });
                    return;
                }
                takePreView(textureView);

            }
        }, null);

    }

    /***
     * 重启相机并管理Surface
     */
    public void restartCameraAndPreView(final TextureView textureView) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        closeCamera();
        startCameraAndPreView(textureView);
    }
    
    /***
     * TextureView生成Surface回调
     * @param textureView
     * @param create
     * @param destory
     */
    private void surfaceCreate(TextureView textureView, final Runnable create, final Runnable destory) {
        textureView.setSurfaceTextureListener(new TextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (create != null)
                    create.run();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (destory != null)
                    destory.run();
                return super.onSurfaceTextureDestroyed(surface);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreView(TextureView textureView) {

        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            configSelfAttr(previewRequestBuilder);
            // 将TextureView的surface作为CaptureRequest.Builder的目标
            Surface surface = new Surface(textureView.getSurfaceTexture());
            previewRequestBuilder.addTarget(surface);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            cameraDevice.createCaptureSession(Collections.singletonList(surface), getSessionStateCallback(new Callback<CameraCaptureSession>() {
                @Override
                public void callback(CameraCaptureSession session) {

                    if (cameraDevice == null)
                        return;
                    Camera2Manager.this.session = session;
                    CaptureRequest previewRequest = previewRequestBuilder.build();
                    try {
                        session.setRepeatingRequest(previewRequest, null, getChildHanlder());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }), getChildHanlder());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 配置相机自有属性
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configSelfAttr(CaptureRequest.Builder builder) {

        // 自动对焦
        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        // 打开闪光灯
        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

    }

    private Handler getChildHanlder() {
        HandlerThread handlerThread = new HandlerThread("CameraHandler");
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    private Handler getMainHanlder() {
        return new Handler(Looper.getMainLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void closeCamera() {

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (session != null) {
            session.close();
            session = null;
        }

    }

}

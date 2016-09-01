package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class Fotografia extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    public Camera camera;

    public Fotografia(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int idCamera = findFrontFacingCamera();
        camera = Camera.open(idCamera);
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(camera!=null) {
            Camera.Parameters parametros = camera.getParameters();
            parametros.setRotation(270);
            camera.setParameters(parametros);
            camera.startPreview();
        }
    }
}

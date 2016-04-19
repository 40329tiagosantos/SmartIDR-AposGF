package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import pt.ul.fc.di.aplicacaosmartphone.detecao.DetetorLigacaoSmartwatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FotografarIntruso extends Service {

    public static Fotografia fotografia;
    public static int numeroSessao;
    private FileOutputStream outStream;
    private int larguraFotografia, alturaFotografia;
    private int numeroMaxCaras;
    private FaceDetector detetorCaras;
    private FaceDetector.Face[] cara;
    private TimerTask tarefa;
    private Timer cronometro;
    private int numeroCarasDetetadas;
    private SharedPreferences numeroSessaoAtual;
    private WindowManager windowManager;
    private WindowManager.LayoutParams parametros;
    private Bitmap bitMapFoto;
    private long tamanhoFoto;
    private boolean tirouFoto = false;
    public static boolean ocorreuIntrusaoFotogIntr;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        ocorreuIntrusaoFotogIntr=true;
        tamanhoFoto = 0;
        numeroCarasDetetadas = 0;
        numeroMaxCaras = 1;
        numeroSessaoAtual = getApplicationContext().getSharedPreferences("numeroSessao", MODE_PRIVATE);
        numeroSessao = numeroSessaoAtual.getInt("numeroSessao", 1);
        fotografia = new Fotografia(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        parametros = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        parametros.width = 1;
        parametros.height = 1;
        parametros.x = 0;
        parametros.y = 0;
        parametros.screenOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        windowManager.addView(fotografia, parametros);
        cara = new FaceDetector.Face[numeroMaxCaras];

        cronometro = new Timer();
        tarefa = new java.util.TimerTask() {
            @Override
            public void run() {
                fotografia.camera.takePicture(null, null, jpegCallback);
            }
        };
        cronometro.schedule(tarefa, 3000, 10000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cronometro.cancel();
        if (numeroCarasDetetadas == 0)
            windowManager.removeView(fotografia);
        if (fotografia.camera != null && numeroCarasDetetadas == 0) {
            fotografia.camera.stopPreview();
            fotografia.camera.setPreviewCallback(null);
            fotografia.camera.release();
            fotografia.camera = null;
        }
        if (tirouFoto) {
            numeroSessao++;
            SharedPreferences.Editor editor = numeroSessaoAtual.edit();
            editor.putInt("numeroSessao", numeroSessao);
            editor.commit();
        }
    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] dados, Camera camera) {
            try {
                String caminhoFotografia = numeroSessao + DetetorLigacaoSmartwatch.estado + "fotoIntrusoNova.jpeg";
                outStream = openFileOutput(caminhoFotografia, Context.MODE_PRIVATE);
                outStream.write(dados);
                outStream.close();
                FileInputStream inStream = openFileInput(caminhoFotografia);
                BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
                BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
                bitMapFoto = BitmapFactory.decodeStream(inStream, null, BitmapFactoryOptionsbfo);
                inStream.close();

                if (bitMapFoto.getWidth() > bitMapFoto.getHeight())
                    bitMapFoto = Bitmap.createScaledBitmap(bitMapFoto, 1280, 800, true);
                else
                    bitMapFoto = Bitmap.createScaledBitmap(bitMapFoto, 800, 1280, true);

                larguraFotografia = bitMapFoto.getWidth();
                alturaFotografia = bitMapFoto.getHeight();
                detetorCaras = new FaceDetector(larguraFotografia, alturaFotografia, numeroMaxCaras);
                numeroCarasDetetadas = 0;
                numeroCarasDetetadas = detetorCaras.findFaces(bitMapFoto, cara);
                if (numeroCarasDetetadas == 1) {
                    cronometro.cancel();
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    windowManager.removeView(fotografia);
                    fotografia.camera = null;
                    File ficheiros[] = getApplicationContext().getFilesDir().listFiles();
                    for (File ficheiroFotoIntruso : ficheiros) {
                        if (ficheiroFotoIntruso.getName().contains(caminhoFotografia)) {
                            String caminhoFotografiaNova = numeroSessao + DetetorLigacaoSmartwatch.estado + "fotoIntruso.jpeg";
                            getApplicationContext().getFileStreamPath(caminhoFotografia).renameTo(new File(ficheiroFotoIntruso.getParent(), caminhoFotografiaNova));
                        }
                    }
                    tirouFoto = true;
                } else {
                    windowManager.removeView(fotografia);
                    windowManager.addView(fotografia, parametros);
                    File ficheiros[] = getApplicationContext().getFilesDir().listFiles();
                    long tamanhoFotoAtual = getApplicationContext().getFileStreamPath(caminhoFotografia).length();
                    for (File ficheiroFotoIntruso : ficheiros) {
                        if (ficheiroFotoIntruso.getName().contains(caminhoFotografia) && tamanhoFoto <= tamanhoFotoAtual) {
                            String caminhoFotografiaNova = numeroSessao + DetetorLigacaoSmartwatch.estado + "fotoIntruso.jpeg";
                            getApplicationContext().getFileStreamPath(caminhoFotografia).renameTo(new File(ficheiroFotoIntruso.getParent(), caminhoFotografiaNova));
                            tamanhoFoto = tamanhoFotoAtual;
                        } else if (ficheiroFotoIntruso.getName().contains(caminhoFotografia) && tamanhoFoto > tamanhoFotoAtual) {
                            ficheiroFotoIntruso.delete();
                        }
                    }
                    tirouFoto = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

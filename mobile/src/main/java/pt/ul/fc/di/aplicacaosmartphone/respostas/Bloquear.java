package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class Bloquear extends Service{

    private boolean ativado;
    private WindowManager windowsManager;
    private RelativeLayout wrapperView;
    private int brilho;
    private int volume;
    private AudioManager volumeSom;
    private String estado;
    public static boolean bloqueado;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        estado = intent.getStringExtra("estado");
        if (!ativado) {
            if (estado.equals("ComLigacaoBT")) {
                Mensagem mensagemPedido = new Mensagem("Bloqueado", getApplication());
                mensagemPedido.enviaMensagem();
            }
            volumeSom = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volume = volumeSom.getStreamVolume(AudioManager.STREAM_SYSTEM);
            volumeSom.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
            try {
                brilho = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 0);
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_SECURE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            this.windowsManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));

            this.wrapperView = new RelativeLayout(getBaseContext());

            this.windowsManager.addView(this.wrapperView, localLayoutParams);
            bloqueado=true;
        } else {
            stopSelf();
            bloqueado=false;
        }
        ativado = !ativado;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (estado.equals("ComLigacaoBT")) {
            Mensagem mensagemPedido = new Mensagem("Desbloqueado", getApplication());
            mensagemPedido.enviaMensagem();
        }
        this.windowsManager.removeView(this.wrapperView);
        this.wrapperView.removeAllViews();
        android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brilho);
        volumeSom.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
        bloqueado=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

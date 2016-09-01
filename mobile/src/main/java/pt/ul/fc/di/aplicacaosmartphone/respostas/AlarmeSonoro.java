package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.tiasa.aplicacaosmartwatch.R;

public class AlarmeSonoro extends Service {

    public static boolean ocorreuIntrusaoAlarSon;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        iniciaAlarmeSonoro(estado);
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void iniciaAlarmeSonoro(String estado) {
        if(estado.equals("SemLigacao"))
        ocorreuIntrusaoAlarSon=true;
        AudioManager volumeSom = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSom.setStreamVolume(AudioManager.STREAM_MUSIC, volumeSom.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarmesonoro);
        mediaPlayer.start();
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("AlarmeSonoro");
        editor.commit();
    }
}


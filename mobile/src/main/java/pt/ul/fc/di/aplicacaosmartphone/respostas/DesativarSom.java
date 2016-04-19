package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class DesativarSom extends BroadcastReceiver {

    private AudioManager volumeSom;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (Bloquear.class.getName().equals(service.service.getClassName())||PedirAutenticacao.class.getName().equals(service.service.getClassName())) {
                    volumeSom = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    volumeSom.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
                }
        }
    }
}}
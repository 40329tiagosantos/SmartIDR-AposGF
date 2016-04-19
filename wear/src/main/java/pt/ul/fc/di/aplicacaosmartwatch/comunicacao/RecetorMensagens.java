package pt.ul.fc.di.aplicacaosmartwatch.comunicacao;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ListaAtividades;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ListaRespostas;

public class RecetorMensagens extends WearableListenerService {

    public static int numeroAtividades;

    @Override
    public void onMessageReceived(MessageEvent eventoMensagem) {
        String mensagem = eventoMensagem.getPath();
        if (mensagem.equals("Intrusao")) {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();
            Notification notificacao = new Notification.Builder(this)
                    .setContentText("Intrusão Detetada!")
                    .setSmallIcon(R.drawable.iconprincipal)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.iconintrusao))
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .build();

            NotificationManager gestorNotificacoes = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            gestorNotificacoes.notify(0, notificacao);
            wakeLock.release();
        } else if (mensagem.contains("Atividade: ")) {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();

            if(ListaAtividades.listaIcons==null){
                ListaAtividades.listaIcons=new ArrayList<>();
                SharedPreferences preferencias = getApplicationContext().getSharedPreferences("ListaAtividades", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                editor.clear();
                editor.commit();
            }
            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("ListaAtividades", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            mensagem = mensagem.replace("Atividade: ", "");
            editor.putString("atividade" + String.valueOf(preferencias.getAll().size()), mensagem);
            editor.commit();

            numeroAtividades++;

            if (ListaAtividades.iniciouAtividade || !ListaRespostas.iniciouAplicacao) {
                Intent listaAtividades = new Intent(getApplicationContext(), ListaAtividades.class);
                listaAtividades.putExtra("Icon",eventoMensagem.getData());
                listaAtividades.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                listaAtividades.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(listaAtividades);
            } else {
                if (numeroAtividades == 1) {
                    apresentaMensagem(String.valueOf(numeroAtividades) + " nova atividade!");
                    Drawable icon = new BitmapDrawable(getApplicationContext().getResources(), BitmapFactory.decodeByteArray(eventoMensagem.getData(), 0, eventoMensagem.getData().length));
                    ListaAtividades.listaIcons.add(0,icon);
                }
                else {
                    apresentaMensagem(String.valueOf(numeroAtividades) + " novas atividades!");
                    Drawable icon = new BitmapDrawable(getApplicationContext().getResources(), BitmapFactory.decodeByteArray(eventoMensagem.getData(), 0, eventoMensagem.getData().length));
                    ListaAtividades.listaIcons.add(0,icon);
                }
            }
            wakeLock.release();
        } else if (mensagem.equals("ModoPartilhaControlada")) {
            long padrao[] = {0, 200, 200, 200};
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(padrao, -1);
        } else {
            apresentaMensagem(mensagem);
            if (ListaRespostas.iniciouAplicacao) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ListaRespostas.iconProgresso.setVisibility(View.INVISIBLE);
                        ListaRespostas.cabecalho.setVisibility(View.VISIBLE);
                        ListaRespostas.listaRespostas.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private void apresentaMensagem(final String mensagem) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
            }
        });
    }
}

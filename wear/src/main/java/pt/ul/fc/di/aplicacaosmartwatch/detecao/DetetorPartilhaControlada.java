package pt.ul.fc.di.aplicacaosmartwatch.detecao;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;

public class DetetorPartilhaControlada extends Service implements SensorEventListener {

    private int mudancasLuminosidade;
    private boolean mudouLuminosidade;
    private String mensagem;
    public static boolean iniciou;
    private PowerManager.WakeLock wl;
    private ArrayList<Timer> listaTimers;
    private ArrayList<TimerTask> listaTask;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (!iniciou) {
            iniciou = true;
            listaTimers = new ArrayList<>();
            listaTask = new ArrayList<>();

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
            mensagem = "AtivarModoPartilha";
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Mantem a executar Thread com smartwatch em Standby");
            return START_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!wl.isHeld())
            wl.acquire();
        float valor = event.values[0];
        if (valor < 5 && !mudouLuminosidade) {
            if (mudancasLuminosidade == 0) {
                TimerTask tarefa = new TimerTask() {
                    @Override
                    public void run() {
                        mudancasLuminosidade = 0;
                        mudouLuminosidade = false;
                    }
                };
                Timer cronometro = new Timer();
                cronometro.schedule(tarefa, 10000);
                listaTimers.add(cronometro);
                listaTask.add(tarefa);
            }
            mudancasLuminosidade++;
            mudouLuminosidade = true;
        } else if (valor >= 15 && mudouLuminosidade) {
            mudancasLuminosidade++;
            mudouLuminosidade = false;
        }
        if (mudancasLuminosidade == 6) {
            enviaMensagem();
            mudancasLuminosidade = 0;
            mudouLuminosidade = false;
            for (int i = 0; i < listaTimers.size(); i++) {
                listaTimers.get(i).cancel();
                listaTask.get(i).cancel();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void enviaMensagem() {
        if (!wl.isHeld())
            wl.acquire();
        Mensagem mensagemPartilha = new Mensagem(mensagem,getApplication());
        mensagemPartilha.enviaMensagem();
    }
}

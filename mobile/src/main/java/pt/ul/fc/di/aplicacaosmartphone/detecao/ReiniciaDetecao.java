package pt.ul.fc.di.aplicacaosmartphone.detecao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ReiniciaDetecao extends BroadcastReceiver {

    public static boolean reiniciou=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        reiniciou=true;
        Intent reinicia = new Intent(context,DetetorLigacaoSmartwatch.class);
        context.startService(reinicia);
    }
}

package pt.ul.fc.di.aplicacaosmartwatch.detecao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ReiniciaDetecao extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent reinicia = new Intent(context, DetetorPartilhaControlada.class);
        context.startService(reinicia);
        reinicia = new Intent(context, DetetorLigacaoSmartphone.class);
        context.startService(reinicia);
    }
}

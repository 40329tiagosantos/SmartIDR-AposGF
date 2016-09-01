package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pt.ul.fc.di.aplicacaosmartphone.respostas.FotografarIntruso;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsFotografias;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsVideos;

public class LimparPreferencias extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent reporAlbunsFotos = new Intent(getApplicationContext(), GestorAlbunsFotografias.class);
        reporAlbunsFotos.putExtra("reporAlbuns", "reporAlbuns");
        startService(reporAlbunsFotos);

        Intent reporAlbunsVideos = new Intent(this, GestorAlbunsVideos.class);
        reporAlbunsVideos.putExtra("reporAlbuns", "reporAlbuns");
        startService(reporAlbunsVideos);

        ConfiguracoesRespostas.numeroRespostas = 0;
        FotografarIntruso.numeroSessao = 0;

        SharedPreferences data = getApplicationContext().getSharedPreferences("DataFicheiroAtividades", MODE_PRIVATE);
        SharedPreferences.Editor editorData = data.edit();
        editorData.clear();
        editorData.apply();

        data = getApplicationContext().getSharedPreferences("DataFicheiroFotos", MODE_PRIVATE);
        editorData = data.edit();
        editorData.clear();
        editorData.apply();

        SharedPreferences.Editor numeroSessaoAtual = getApplicationContext().getSharedPreferences("numeroSessao", MODE_PRIVATE).edit();
        numeroSessaoAtual.clear();
        numeroSessaoAtual.apply();

        stopService(new Intent(getApplicationContext(), FotografarIntruso.class));

        Intent limpaEstados = new Intent(getApplicationContext(), LimparEstados.class);
        startService(limpaEstados);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

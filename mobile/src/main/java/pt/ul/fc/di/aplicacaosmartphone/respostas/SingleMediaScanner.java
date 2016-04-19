package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mediaScanner;
    private File ficheiro;

    public SingleMediaScanner(Context contexto, File ficheiro) {
        this.ficheiro = ficheiro;
        mediaScanner = new MediaScannerConnection(contexto, this);
        mediaScanner.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScanner.scanFile(ficheiro.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mediaScanner.disconnect();
    }

}
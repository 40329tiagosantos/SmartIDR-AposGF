package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class GestorAlbunsFotografias extends Service {

    private boolean escondeuPartilha;
    private boolean escondeuSemLigacao;
    private boolean escondeuComLigacao;
    private boolean escondeuQuickLaunch;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("reporAlbuns") != null) {
            apresentaAlbunsEstado("Partilha");
            apresentaAlbunsEstado("SemLigacao");
            apresentaAlbunsEstado("QuickLaunch");
        } else {
            if (intent.getStringExtra("estado").equals("Partilha")) {
                if (!escondeuPartilha) {
                    escondeAlbunsEstado("Partilha");
                    escondeuPartilha = true;
                } else {
                    apresentaAlbunsEstado("Partilha");
                    escondeuPartilha = false;
                }
            }
            if (intent.getStringExtra("estado").equals("QuickLaunch")) {
                   escondeAlbunsEstado("QuickLaunch");
             }
            if (intent.getStringExtra("estado").equals("SemLigacao")) {
                if (!escondeuSemLigacao) {
                    escondeAlbunsEstado("SemLigacao");
                    escondeuSemLigacao = true;
                } else {
                    apresentaAlbunsEstado("SemLigacao");
                    escondeuSemLigacao = false;
                }
            }
            if (intent.getStringExtra("estado").equals("ComLigacaoBT")) {
                if (!escondeuComLigacao) {
                    escondeAlbunsEstado("SemLigacao");
                    escondeAlbunsEstado("Partilha");
                    Mensagem mensagemPedido = new Mensagem("✓ Álbuns de fotografias ocultados com sucesso!", getApplication());
                    mensagemPedido.enviaMensagem();
                    escondeuComLigacao = true;
                } else {
                    apresentaAlbunsEstado("SemLigacao");
                    apresentaAlbunsEstado("Partilha");
                    Mensagem mensagemPedido = new Mensagem("✓ Álbuns de fotografias apresentados com sucesso!", getApplication());
                    mensagemPedido.enviaMensagem();
                    escondeuComLigacao = false;
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void escondeAlbunsEstado(String estado) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
        Map<String, ?> conjuntoPreferencias = prefs.getAll();
        if (conjuntoPreferencias != null) {
            for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                if (entry.getKey().contains("preferenciaAlbunsFotografias"))
                    escondeAlbuns(entry.getValue().toString());
            }
        }
    }

    private void apresentaAlbunsEstado(String estado) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
        Map<String, ?> conjuntoPreferencias = prefs.getAll();
        if (conjuntoPreferencias != null) {
            for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                if (entry.getKey().contains("preferenciaAlbunsFotografias"))
                    apresentaAlbuns(entry.getValue().toString());
            }
        }
    }

    private void escondeAlbuns(String caminhoAlbum) {
        File ficheiro = new File(caminhoAlbum + File.separator + ".nomedia");
        if (!ficheiro.exists())
            ficheiro.mkdir();
        File pasta = new File(caminhoAlbum + File.separator);
        File ficheiro2[] = pasta.listFiles();
        for (File ficheiro3 : ficheiro2) {
            if (!ficheiro3.isDirectory() && eFotografia(ficheiro3)) {
                ficheiro3.renameTo(new File(caminhoAlbum + File.separator + ".nomedia" + File.separator + "." + ficheiro3.getName()));
                new SingleMediaScanner(getApplicationContext(), ficheiro3);
            }
        }
    }

    private void apresentaAlbuns(String caminhoAlbum) {
        File ficheiro = new File(caminhoAlbum + File.separator + ".nomedia" + File.separator);
        if (ficheiro.exists()) {
            File ficheiro2[] = ficheiro.listFiles();
            for (File ficheiro3 : ficheiro2) {
                if (!ficheiro3.isDirectory() && eFotografia(ficheiro3)) {
                    ficheiro3.renameTo(new File(caminhoAlbum + File.separator + ficheiro3.getName().replaceFirst(".", "")));
                    adicionarImagemGaleria(this.getContentResolver(), devolveExtensaoFicheiro(ficheiro3.getName()), new File(caminhoAlbum + File.separator + ficheiro3.getName().replaceFirst(".", "")));
                }
            }
        }
        ficheiro = new File(caminhoAlbum + File.separator + ".nomedia");
        ficheiro.delete();
    }

    private boolean eFotografia(File ficheiro) {
        return (ficheiro.getName().contains(".jpg") || ficheiro.getName().contains(".jpeg") || ficheiro.getName().contains(".gif") || ficheiro.getName().contains(".png") ||
                ficheiro.getName().contains(".bmp") || ficheiro.getName().contains(".webP"));
    }

    public static String devolveExtensaoFicheiro(String nomeFicheiro) {
        return nomeFicheiro.substring((nomeFicheiro.lastIndexOf(".") + 1), nomeFicheiro.length());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Uri adicionarImagemGaleria(ContentResolver cr, String tipoImagem, File caminhoFicheiro) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image" + File.separator + tipoImagem);
        values.put(MediaStore.Images.Media.DATA, caminhoFicheiro.toString());
        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}

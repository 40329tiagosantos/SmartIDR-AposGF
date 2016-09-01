package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class RegistoAtividades extends Service {

    public static boolean ocorreuIntrusaoRegisAt;
    private boolean estaEscrever;
    private String textoEscrito = "", nomePacoteAtEscri = "", estadoAtEscri = "";
    private FileOutputStream outStream;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        if (estado.equals("SemLigacao"))
            ocorreuIntrusaoRegisAt = true;
        String nomeEvento = intent.getStringExtra("nomeEvento");
        String nomePacote = intent.getStringExtra("nomePacote");
        String descricaoEvento = intent.getStringExtra("descricaoEvento");
        String tipoEvento = intent.getStringExtra("tipoEvento");
        String classeEvento = intent.getStringExtra("classeEvento");
        if (!nomePacote.equals(getPackageName()))
            iniciaRegistarAtividades(estado, nomeEvento, nomePacote, descricaoEvento, tipoEvento, classeEvento);
        return START_NOT_STICKY;
    }

    private void iniciaRegistarAtividades(String estado, String nomeEvento, String nomePacote, String descricaoEvento, String tipoEvento, String classeEvento) {

        Date date = new Date();
        boolean eventoValido = false;
        String nomeFicheiroAtividades = null;

        File ficheiros[] = getApplicationContext().getFilesDir().listFiles();
        for (File ficheiro : ficheiros) {
            if (ficheiro.getName().contains("atividadesIntruso")) {
                nomeFicheiroAtividades = ficheiro.getName();
                try {
                    outStream = openFileOutput(nomeFicheiroAtividades, MODE_APPEND);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        BufferedWriter escritor = null;
        if (outStream != null) {
            escritor = new BufferedWriter(new OutputStreamWriter(outStream));
        }

        StringBuilder linha = new StringBuilder();
        if (!tipoEvento.equals("")) {
            linha.append("Atividade: ");
            try {
                switch (tipoEvento) {

                    case "TYPE_VIEW_CLICKED":
                        estaEscrever = false;
                        if (!textoEscrito.equals("") && !estaEscrever) {
                            enviaMensagemAtividade(textoEscrito, nomePacoteAtEscri, estadoAtEscri);
                            escritor.write(textoEscrito.replace("Atividade: ", ""));
                            escritor.newLine();
                            textoEscrito = "";
                            nomePacoteAtEscri = "";
                            estadoAtEscri = "";
                        }
                        if (!nomeEvento.equals("") && descricaoEvento.equals("") && !nomePacote.equals("com.android.systemui") && classeEvento.contains("EditText")) {
                            if (!devolveUltimaLinha(nomeFicheiroAtividades).contains(nomeEvento)) {
                                linha.append(date.getTime()).append(" Selected: ");
                                linha.append(nomeEvento).append(" ");
                                linha.append(nomePacote);
                                escritor.write(linha.toString().replace("Atividade: ", ""));
                                escritor.newLine();
                            }
                        } else if (!descricaoEvento.equals("") && !nomeEvento.equals("") && !nomePacote.equals("com.android.systemui") && !classeEvento.contains("ImageButton")) {
                            linha.append(date.getTime()).append(" Selected: ");
                            linha.append(nomeEvento).append(" ");
                            linha.append(nomePacote);
                            escritor.write(linha.toString().replace("Atividade: ", ""));
                            escritor.newLine();
                        } else if (!nomeEvento.equals("") && descricaoEvento.equals("") && !nomePacote.equals("com.android.systemui")) {
                            linha.append(date.getTime()).append(" Opened: ");
                            linha.append(nomeEvento).append(" ");
                            linha.append(nomePacote);
                            escritor.write(linha.toString().replace("Atividade: ", ""));
                            escritor.newLine();
                        } else if (!descricaoEvento.equals("") && nomeEvento.equals("") && !nomePacote.equals("com.android.systemui") && !classeEvento.contains("ImageButton")) {
                            if (!devolveUltimaLinha(nomeFicheiroAtividades).contains(descricaoEvento)) {
                                linha.append(date.getTime()).append(" Selected: ");
                                linha.append(nomeEvento).append(" ").append(descricaoEvento).append(" ");
                                linha.append(nomePacote);
                                escritor.write(linha.toString().replace("Atividade: ", ""));
                                escritor.newLine();
                            }
                        }
                        eventoValido = true;
                        break;

                    case "TYPE_VIEW_SELECTED":
                        estaEscrever = false;

                        if (!textoEscrito.equals("") && !estaEscrever) {
                            enviaMensagemAtividade(textoEscrito, nomePacoteAtEscri, estadoAtEscri);
                            escritor.write(textoEscrito.replace("Atividade: ", ""));
                            escritor.newLine();
                            textoEscrito = "";
                            nomePacoteAtEscri = "";
                            estadoAtEscri = "";
                        }
                        if (!descricaoEvento.equals("")) {
                            if (!devolveUltimaLinha(nomeFicheiroAtividades).contains("Closed: " + descricaoEvento)) {
                                linha.append(date.getTime()).append(" Closed: ");
                                linha.append(descricaoEvento).append(" ");
                                linha.append(nomePacote);

                                escritor.write(linha.toString().replace("Atividade: ", ""));
                                escritor.newLine();
                            }
                        } else if (!nomeEvento.equals("")) {
                            if (!devolveUltimaLinha(nomeFicheiroAtividades).contains(nomeEvento) && !classeEvento.contains("Launcher")) {
                                linha.append(date.getTime()).append(" Selected: ");
                                linha.append(nomeEvento).append(" ");
                                linha.append(nomePacote);
                                escritor.write(linha.toString().replace("Atividade: ", ""));
                                escritor.newLine();
                            }
                        }
                        eventoValido = true;
                        break;
                    case "TYPE_VIEW_TEXT_SELECTION_CHANGED":
                        estaEscrever = false;

                        if (!nomeEvento.equals("")) {
                            if (!devolveUltimaLinha(nomeFicheiroAtividades).contains(nomeEvento) && classeEvento.contains("EditText") && URLUtil.isValidUrl(nomeEvento)) {
                                linha.append(date.getTime()).append(" Acceded: ");
                                linha.append(nomeEvento).append(" ");
                                linha.append(nomePacote);
                                escritor.write(linha.toString().replace("Atividade: ", ""));
                                escritor.newLine();
                            }
                        }
                        eventoValido = true;
                        break;

                    case "TYPE_VIEW_TEXT_CHANGED":
                        if (!nomeEvento.equals("")) {
                            linha.append(date.getTime()).append(" Wrote: ");
                            linha.append(nomeEvento).append(" ");
                            linha.append(nomePacote);

                            textoEscrito = linha.toString();
                            nomePacoteAtEscri = nomePacote;
                            estadoAtEscri = estado;
                            estaEscrever = true;
                        }
                        eventoValido = true;
                        break;

                    case "TYPE_WINDOW_STATE_CHANGED":
                        estaEscrever = false;
                        if (!textoEscrito.equals("") && !estaEscrever) {
                            enviaMensagemAtividade(textoEscrito, nomePacoteAtEscri, estadoAtEscri);
                            escritor.write(textoEscrito.replace("Atividade: ", ""));
                            escritor.newLine();
                            textoEscrito = "";
                            nomePacoteAtEscri = "";
                            estadoAtEscri = "";
                        }
                        if (!descricaoEvento.equals("")) {
                            linha.append(date.getTime()).append(" Viewed: ");
                            linha.append(descricaoEvento).append(" ");
                            linha.append(nomePacote);
                            escritor.write(linha.toString().replace("Atividade: ", ""));
                            escritor.newLine();
                        }
                        eventoValido = true;
                        break;
                }
                if (!estaEscrever && eventoValido) {
                    enviaMensagemAtividade(linha.toString(), nomePacote, estado);
                }

                escritor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean enviaMensagemAtividade(String linha, String nomePacote, String estado) {
        Intent aplicacoesLauncher = new Intent(Intent.ACTION_MAIN, null);
        aplicacoesLauncher.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager gestorPacotes = getPackageManager();
        List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(aplicacoesLauncher, 0);

        if (linha.contains("Wrote") && !estado.equals("SemLigacao")) {
            Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconescrita);
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] bitmapdata = stream.toByteArray();
            Mensagem mensagemAtividade = new Mensagem(linha.replace(nomePacote, ""), bitmapdata, getApplication());
            mensagemAtividade.enviaMensagemDados();
            return true;
        }
        for (ResolveInfo ri : availableActivities) {
            if (linha.contains(ri.activityInfo.packageName) && !linha.contains("Closed")) {
                Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, true);
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] bitmapdata = stream.toByteArray();
                Mensagem mensagemAtividade = new Mensagem(linha.replace(nomePacote, ""), bitmapdata, getApplication());
                mensagemAtividade.enviaMensagemDados();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else if (linha.contains(ri.loadLabel(gestorPacotes).toString())) {
                Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, true);
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] bitmapdata = stream.toByteArray();
                Mensagem mensagemAtividade = new Mensagem(linha.replace(nomePacote, ""), bitmapdata, getApplication());
                mensagemAtividade.enviaMensagemDados();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        if (!linha.equals("Atividade: ") && !estado.equals("SemLigacao")) {
            Mensagem mensagemAtividade = new Mensagem(linha.replace(nomePacote, ""), getApplication());
            mensagemAtividade.enviaMensagem();
        }
        return true;
    }

    private String devolveUltimaLinha(String nomeFicheiro) {
        String ultimaLinha = "";
        String linhaActual;
        try {
            FileInputStream ficheiroAtividades = openFileInput(nomeFicheiro);
            BufferedReader leitorFicheiroAtividades = new BufferedReader(new InputStreamReader(ficheiroAtividades));
            while ((linhaActual = leitorFicheiroAtividades.readLine()) != null) {
                ultimaLinha = linhaActual;
            }
            leitorFicheiroAtividades.close();
            ficheiroAtividades.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ultimaLinha;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


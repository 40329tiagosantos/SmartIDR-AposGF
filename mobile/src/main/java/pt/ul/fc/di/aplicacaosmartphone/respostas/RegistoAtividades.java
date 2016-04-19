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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class RegistoAtividades extends Service {

    private String dataAtual;
    public static boolean ocorreuIntrusaoRegisAt;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        if (estado.equals("SemLigacao"))
            ocorreuIntrusaoRegisAt = true;
        String nomeEvento = intent.getStringExtra("nomeEvento");
        String nomePacote = intent.getStringExtra("nomePacote");
        String descricaoEvento = intent.getStringExtra("descricaoEvento");
        String tipoEvento = intent.getStringExtra("tipoEvento");
        String textoEvento = intent.getStringExtra("textoEvento");
        String classeEvento = intent.getStringExtra("classeEvento");
        iniciaRegistarAtividades(estado, nomeEvento, nomePacote, descricaoEvento, tipoEvento, textoEvento, classeEvento);
        return START_NOT_STICKY;
    }

    private void iniciaRegistarAtividades(String estado, String nomeEvento, String nomePacote, String descricaoEvento, String tipoEvento, String textoEvento, String classeEvento) {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        FileOutputStream outStream = null;
        try {
            dataAtual = dateFormat.format(date);
            outStream = openFileOutput(dataAtual + "atividadesIntruso.txt", MODE_APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(outStream));

        dateFormat = new SimpleDateFormat("HH:mm:ss");

        StringBuilder linha = new StringBuilder();
        if (!tipoEvento.equals("")) {
            linha.append("Atividade: ");
            String etiqueta = dateFormat.format(date);
            try {
                if (tipoEvento.equals("TYPE_VIEW_CLICKED")) {
                    if (!nomeEvento.equals("") && !nomePacote.equals("com.android.systemui")) {
                        escritor.write((etiqueta + " "));
                        escritor.write((nomePacote + " "));
                        linha.append(etiqueta + " ");
                        linha.append("Seleccionou: " + nomeEvento + '\n');
                        linha.append(nomePacote);
                        escritor.write(("Seleccionou: " + nomeEvento));
                        escritor.newLine();
                    } else if (!descricaoEvento.equals("") && !nomePacote.equals("com.android.systemui")) {
                        if (!devolveUltimaLinha(dataAtual).contains(descricaoEvento)) {
                            escritor.write((etiqueta + " "));
                            escritor.write((nomePacote + " "));
                            linha.append(etiqueta + " ");
                            linha.append("Seleccionou: " + nomeEvento + " " + descricaoEvento + '\n');
                            linha.append(nomePacote);
                            escritor.write(("Seleccionou: " + nomeEvento + " " + descricaoEvento));
                            escritor.newLine();
                        }
                    }
                }
                if (tipoEvento.equals("TYPE_VIEW_TEXT_CHANGED")) {
                    linha.append(etiqueta + " ");
                    escritor.write((etiqueta + " "));
                    escritor.write((nomePacote + " "));
                    linha.append("Escreveu: " + nomeEvento + '\n');
                    linha.append(nomePacote);
                    escritor.write(("Escreveu: " + nomeEvento));
                    escritor.newLine();
                }
                if (tipoEvento.equals("TYPE_WINDOW_STATE_CHANGED")) {
                    if (!descricaoEvento.equals("")) {
                        escritor.write((etiqueta + " "));
                        escritor.write((nomePacote + " "));
                        linha.append(etiqueta + " ");
                        linha.append("Visualizou: " + descricaoEvento + '\n');
                        linha.append(nomePacote);
                        escritor.write(("Visualizou: " + descricaoEvento));
                        escritor.newLine();
                    }
                }

                if (tipoEvento.equals("TYPE_VIEW_SELECTED")) {
                    if (!descricaoEvento.equals("")) {
                        if (!devolveUltimaLinha(dataAtual).contains("Fechou: " + descricaoEvento)) {
                            linha.append(etiqueta + " ");
                            linha.append("Fechou: " + descricaoEvento + '\n');
                            linha.append(nomePacote);
                            escritor.write((etiqueta + " "));
                            escritor.write((nomePacote + " "));
                            escritor.write(("Fechou: " + descricaoEvento));
                            escritor.newLine();
                        }
                    }
                }
                escritor.close();
                outStream.close();
                enviaMensagemAtividade(linha, nomePacote, estado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean enviaMensagemAtividade(StringBuilder linha, String nomePacote, String estado) {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager gestorPacotes = getPackageManager();
        List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {
            if (linha.toString().contains(ri.loadLabel(gestorPacotes).toString())) {
                Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] bitmapdata = stream.toByteArray();
                Mensagem mensagemAtividade = new Mensagem(linha.toString().replace(nomePacote, ""), bitmapdata, getApplication());
                mensagemAtividade.enviaMensagemDados();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            if (linha.toString().contains(ri.activityInfo.packageName)) {
                Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] bitmapdata = stream.toByteArray();
                Mensagem mensagemAtividade = new Mensagem(linha.toString().replace(nomePacote, ""), bitmapdata, getApplication());
                mensagemAtividade.enviaMensagemDados();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        if (!linha.toString().equals("Atividade: ") && !estado.equals("SemLigacao")) {
            Mensagem mensagemAtividade = new Mensagem(linha.toString().replace(nomePacote, ""), getApplication());
            mensagemAtividade.enviaMensagem();
            return true;
        }
        return true;
    }

    private String devolveUltimaLinha(String dataAtual) {
        String ultimaLinha = "";
        String linhaActual;
        try {
            FileInputStream ficheiroAtividades = openFileInput(dataAtual + "atividadesIntruso.txt");
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


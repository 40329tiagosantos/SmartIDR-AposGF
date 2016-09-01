package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.respostas.ExibirMensagemTexto;

public class ConfiguracoesMensagemTexto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_respostas);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        definirMensagemTexto();
    }

    private void definirMensagemTexto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Define a Text Message:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mensagemInserida = input.getText().toString();

                        FileOutputStream fos = null;
                        try {
                            fos = openFileOutput("Mensagem" + getIntent().getStringExtra("estado"), MODE_PRIVATE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (fos != null) {
                                fos.write(mensagemInserida.getBytes());
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(!getIntent().getStringExtra("estado").equals("QuickLaunch")) {

                            SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                            SharedPreferences.Editor editorPosicoes = posicoes.edit();
                            editorPosicoes.putString("posicao" + ConfiguracoesRespostas.estado + String.valueOf(ConfiguracoesRespostas.numeroRespostas), ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote());
                            editorPosicoes.apply();
                            ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).colocaSeleccionado(true);
                            ConfiguracoesRespostas.numeroRespostas++;
                        }
                        else{
                            Intent a = new Intent(getApplicationContext(), ExibirMensagemTexto.class);
                            a.putExtra("estado","QuickLaunch");
                            startService(a);
                        }
                        Toast.makeText(getApplicationContext(), "Mensagem de Texto definida com sucesso!", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }

        );

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(ConfiguracoesMensagemTexto.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.apply();
                            }
                        }
                        SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                        SharedPreferences.Editor editorPosicoes = posicoes.edit();
                        for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                            if (entrada.getValue().equals(ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote())) {
                                editorPosicoes.remove(entrada.getKey());
                                editorPosicoes.apply();
                            }
                        }
                        dialog.cancel();
                        finish();
                    }
                }

        );
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()

        {
            @Override
            public void onCancel(DialogInterface dialog) {
                SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                    if (entry.getValue().equals(ConfiguracoesMensagemTexto.class.getName())) {
                        editor.remove(entry.getKey());
                        editor.apply();
                    }
                }
                SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                SharedPreferences.Editor editorPosicoes = posicoes.edit();
                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                    if (entrada.getValue().equals(ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote())) {
                        editorPosicoes.remove(entrada.getKey());
                        editorPosicoes.apply();
                    }
                }
                dialog.cancel();
                finish();
            }

        });
        builder.show();
    }
}

package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemListaComparator;
import pt.ul.fc.di.aplicacaosmartphone.respostas.DesativarAplicacoes;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsFotografias;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsVideos;
import pt.ul.fc.di.aplicacaosmartphone.respostas.RegistoAtividades;

public class ConfiguracoesRespostas extends Activity {

    private boolean temConfiguracoes;
    private ListView vistaListaRespostas;
    public static String estado;
    public static int numeroRespostas = 0;
    PackageManager gestorPacotes;
    public static ArrayList<ItemLista> listaRespostas;
    public static int posicao;
    private ArrayAdapter<ItemLista> adaptador;
    public static AdapterView<?> vistaAdaptador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_respostas);

        estado = getIntent().getStringExtra("estado");
        TextView cabecalho = (TextView) findViewById(R.id.cabecalho);
        TextView descricao = (TextView) findViewById(R.id.descricao);

        if (estado.equals("SemLigacao")) {
            cabecalho.append("Modo Sem Ligação");
            SpannableString nomeRespostaEditado = new SpannableString(cabecalho.getText().toString());
            nomeRespostaEditado.setSpan(new RelativeSizeSpan(1.2f), 0, nomeRespostaEditado.length(), 0);
            cabecalho.setText(nomeRespostaEditado);
            descricao.setText("Defina o conjunto de opções de proteção que serão ativadas automaticamente sempre que" +
                    " os dispositivos estejam sem comunicação");
        }

        if (estado.equals("Partilha")) {
            cabecalho.append("Modo de Partilha Controlada");
            SpannableString nomeRespostaEditado = new SpannableString(cabecalho.getText().toString());
            nomeRespostaEditado.setSpan(new RelativeSizeSpan(1.2f), 0, nomeRespostaEditado.length(), 0);
            cabecalho.setText(nomeRespostaEditado);
            descricao.setText("Defina o conjunto de opções de proteção que serão ativadas sempre que" +
                    " partilhar o seu smartphone");
        }

        iniciaListaRespostas();
        criaListaRespostas();
        adicionaClickListener();
    }

    public void iniciaListaRespostas() {
        listaRespostas = new ArrayList<>();
        gestorPacotes = getPackageManager();
        Intent atividadesRespostas = new Intent(Intent.ACTION_MAIN, null);
        atividadesRespostas.addCategory("resposta.PLUGIN");
        List<ResolveInfo> respostas = gestorPacotes.queryIntentActivities(atividadesRespostas, 0);
        for (ResolveInfo ri : respostas) {
            ItemLista resposta = new ItemLista();
            resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
            resposta.atribuiNomePacote(ri.activityInfo.name);
            resposta.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
            resposta.colocaSeleccionado(false);
            resposta.colocaConfiguracao(true);
            listaRespostas.add(resposta);
        }
        Intent servicosRespostas = new Intent(Intent.ACTION_MAIN, null);
        servicosRespostas.addCategory("resposta.PLUGIN");
        respostas = gestorPacotes.queryIntentServices(servicosRespostas, 0);
        for (ResolveInfo ri : respostas) {
            if ((!ri.loadLabel(gestorPacotes).toString().contains("Notificação") && estado.equals("SemLigacao")) || estado.equals("Partilha")) {
                ItemLista resposta = new ItemLista();
                resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
                resposta.atribuiNomePacote(ri.serviceInfo.name);
                resposta.atribuiIcon(ri.serviceInfo.loadIcon(gestorPacotes));
                resposta.colocaSeleccionado(false);
                listaRespostas.add(resposta);
            }
        }
        Collections.sort(listaRespostas, new ItemListaComparator());
        for (int i = 0; i < listaRespostas.size(); i++) {
            String nomeResposta = listaRespostas.get(i).devolveNome();
            listaRespostas.get(i).atribuiNome(nomeResposta.replaceFirst(String.valueOf(nomeResposta.charAt(0)), ""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        criaListaRespostas();
    }

    private void criaListaRespostas() {
        if (vistaListaRespostas == null)
            vistaListaRespostas = (ListView) findViewById(R.id.listaRespostas);
        Parcelable state = vistaListaRespostas.onSaveInstanceState();
        adaptador = new ArrayAdapter<ItemLista>(this, R.layout.item_lista_respostas_registos, listaRespostas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_lista_respostas_registos, null);
                ImageView iconResposta = (ImageView) convertView.findViewById(R.id.icon_resposta);
                iconResposta.setImageDrawable(listaRespostas.get(position).devolveIcon());
                TextView nomeResposta = (TextView) convertView.findViewById(R.id.nome_resposta);

                nomeResposta.setText(listaRespostas.get(position).devolveNome());
                TextView configuracao = (TextView) convertView.findViewById(R.id.configuracao);

                if (listaRespostas.get(position).temConfiguracao())
                    configuracao.setVisibility(convertView.VISIBLE);

                else
                    configuracao.setVisibility(convertView.INVISIBLE);

                SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + estado, MODE_PRIVATE);

                if (listaRespostas.get(position).estaSeleccionado() || posicoes.getAll().containsValue(listaRespostas.get(position).devolveNomePacote())) {
                    nomeResposta.setTextSize(19);
                    nomeResposta.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                } else {
                    nomeResposta.setTextSize(12);
                    nomeResposta.setTextColor(Color.BLACK);
                }
                return convertView;
            }
        };
        vistaListaRespostas.setAdapter(adaptador);
        vistaListaRespostas.onRestoreInstanceState(state);
    }

    private void adicionaClickListener() {
        vistaListaRespostas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + estado, MODE_PRIVATE);
                SharedPreferences.Editor editorPosicoes = posicoes.edit();
                SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                vistaAdaptador = av;
                temConfiguracoes = true;
                posicao = pos;
                Intent resposta = null;
                try {
                    resposta = new Intent(getApplicationContext(), Class.forName(listaRespostas.get(pos).devolveNomePacote()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (listaRespostas.get(pos).devolveNomePacote().contains("AtividadesIntruso")) {
                    if (!preferencias.getAll().containsValue(listaRespostas.get(pos).devolveNomePacote())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), RegistoAtividades.class.getName());
                        editor.commit();
                        editorPosicoes.putString("posicao" + estado + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editorPosicoes.commit();
                        numeroRespostas++;
                        temConfiguracoes = false;
                        listaRespostas.get(pos).colocaSeleccionado(true);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                                temConfiguracoes = true;
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("AlbunsFotografias")) {
                    if (!preferencias.getAll().containsValue(GestorAlbunsFotografias.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), GestorAlbunsFotografias.class.getName());
                        editor.commit();
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        startActivity(resposta);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(GestorAlbunsFotografias.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("AlbunsVideos")) {
                    if (!preferencias.getAll().containsValue(GestorAlbunsVideos.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), GestorAlbunsVideos.class.getName());
                        editor.commit();
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        startActivity(resposta);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(GestorAlbunsVideos.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("Aplicacoes")) {
                    if (!preferencias.getAll().containsValue(DesativarAplicacoes.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), DesativarAplicacoes.class.getName());
                        editor.commit();
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        startActivity(resposta);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(DesativarAplicacoes.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("ConfiguracoesMensagemTexto")) {
                    if (!preferencias.getAll().containsValue(ConfiguracoesMensagemTexto.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), ConfiguracoesMensagemTexto.class.getName());
                        editor.commit();
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        startActivity(resposta);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(ConfiguracoesMensagemTexto.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else {
                    if (!preferencias.getAll().containsValue(listaRespostas.get(pos).devolveNomePacote())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editor.commit();
                        editorPosicoes.putString("posicao" + estado + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editorPosicoes.commit();
                        numeroRespostas++;
                        listaRespostas.get(pos).colocaSeleccionado(true);
                        temConfiguracoes = false;
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                                temConfiguracoes = true;
                            }
                        }
                    }
                }
                if (!temConfiguracoes)
                    Toast.makeText(getApplicationContext(), "✓ Resposta " + listaRespostas.get(pos).devolveNome() + " Definida!", Toast.LENGTH_SHORT).show();
                criaListaRespostas();
            }
        });
    }
}
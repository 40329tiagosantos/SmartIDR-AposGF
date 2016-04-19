package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.respostas.DesativarAplicacoes;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsVideos;

public class ConfiguracoesAplicacoes extends Activity {

    private ArrayList<ItemLista> listaAplicacoes;
    private ArrayList<String> listaAplicacoesSeleccionadas;
    private String estado;
    private boolean selecionarTudo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_aplicacoes);
        criaListaAplicacoes();

        Button botaoConfirmarAplicacoes = (Button) findViewById(R.id.botaoConfirmarAplicacoes);
        listaAplicacoesSeleccionadas = new ArrayList<>();

        TextView cabecalho = (TextView) findViewById(R.id.cabecalho);
        estado = getIntent().getStringExtra("estado");

        if (estado.equals("SemLigacao"))
            cabecalho.append("Modo Sem Ligação");

        if (estado.equals("Partilha"))
            cabecalho.append("Modo de Partilha Controlada");

        SpannableString nomeRespostaEditado = new SpannableString(cabecalho.getText().toString());
        nomeRespostaEditado.setSpan(new RelativeSizeSpan(1.2f), 0, nomeRespostaEditado.length(), 0);
        cabecalho.setText(nomeRespostaEditado);
        cabecalho.append("\nDesativar Aplicações");




        botaoConfirmarAplicacoes.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();

                                                            int numeroAplicacoesEscolhidas = 0;

                                                            for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                                                                if (entry.getKey().toString().contains("preferenciaAplicacoes")) {
                                                                    numeroAplicacoesEscolhidas++;
                                                                }
                                                            }

                                                            for (int i = 0; i < listaAplicacoes.size(); i++) {
                                                                if (!listaAplicacoes.get(i).estaSeleccionado()) {
                                                                    for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                                                                        if (entry.getValue().toString().equals(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote())) {
                                                                            editor.remove(entry.getKey());
                                                                            editor.commit();
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            for (int i = numeroAplicacoesEscolhidas; i < numeroAplicacoesEscolhidas + listaAplicacoesSeleccionadas.size(); i++) {
                                                                for (int j = 0; j < listaAplicacoes.size(); j++) {
                                                                    if (listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas).contains(listaAplicacoes.get(j).devolveNome())) {
                                                                        editor.putString("preferenciaAplicacoes" + j, listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas));
                                                                        editor.commit();
                                                                    }
                                                                }
                                                            }

                                                            SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
                                                            SharedPreferences.Editor editorPosicoes = posicoes.edit();
                                                            editorPosicoes.putString("posicao" + ConfiguracoesRespostas.estado + String.valueOf(ConfiguracoesRespostas.numeroRespostas), ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote());
                                                            editorPosicoes.commit();
                                                            ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).colocaSeleccionado(true);
                                                            ConfiguracoesRespostas.numeroRespostas++;

                                                            Toast.makeText(getApplicationContext(), "✓ Aplicações Definidas!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }

        );
    }

    private void criaListaAplicacoes() {
        ListView mainListView = (ListView) findViewById(R.id.listaAplicacoes);
        if (listaAplicacoes == null)
            listaAplicacoes = new ArrayList<>();

        PackageManager gestorPacotes = getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(i, 0);
        ItemLista aplicacao;
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
        Set<? extends Map.Entry<String, ?>> conjuntoPreferencias = preferencias.getAll().entrySet();

        for (ResolveInfo ri : availableActivities) {
            aplicacao = new ItemLista();
            aplicacao.atribuiNome((String) ri.loadLabel(gestorPacotes));
            aplicacao.atribuiNomePacote(ri.activityInfo.packageName);
            aplicacao.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
            for (Map.Entry<String, ?> entry : conjuntoPreferencias) {
                if (entry.getValue().equals(aplicacao.devolveNome() + aplicacao.devolveNomePacote()))
                    aplicacao.colocaSeleccionado(true);
            }
            listaAplicacoes.add(aplicacao);
        }

        final ArrayAdapter<ItemLista> adaptadorLista = new AdaptadorAplicacoes(this, listaAplicacoes);
        mainListView.setAdapter(adaptadorLista);

        Switch botaoSelecionarTudo = (Switch) findViewById(R.id.botaoSelecionarTudo);
        botaoSelecionarTudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selecionarTudo) {
                    listaAplicacoesSeleccionadas.clear();
                    for (int i = 0; i < listaAplicacoes.size(); i++) {
                        listaAplicacoes.get(i).colocaSeleccionado(true);
                        adaptadorLista.notifyDataSetChanged();
                        listaAplicacoesSeleccionadas.add(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = true;
                }
                else {
                    for (int i = 0; i < listaAplicacoes.size(); i++) {
                        listaAplicacoes.get(i).colocaSeleccionado(false);
                        adaptadorLista.notifyDataSetChanged();
                        listaAplicacoesSeleccionadas.remove(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = false;
                }
            }});
    }


    private static class AplicacaoViewHolder {

        private CheckBox checkBox;
        private TextView nomeAplicacao;
        private ImageView icon;

        public AplicacaoViewHolder(TextView nomeAplicacao, ImageView icon, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.nomeAplicacao = nomeAplicacao;
            this.icon = icon;
        }

        public CheckBox devolveCheckBox() {
            return checkBox;
        }

        public TextView devolveNomeAplicacao() {
            return nomeAplicacao;
        }

        public ImageView devolveIcon() {
            return icon;
        }
    }

    private class AdaptadorAplicacoes extends ArrayAdapter<ItemLista> {

        public AdaptadorAplicacoes(Context context, List<ItemLista> listaAplicacoes) {
            super(context, R.layout.item_lista_aplicacoes_albuns, listaAplicacoes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemLista aplicacao = this.getItem(position);
            TextView nomeAplicacao;
            CheckBox checkBox;
            ImageView icon;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_lista_aplicacoes_albuns, null);

                nomeAplicacao = (TextView) convertView.findViewById(R.id.nome_aplicacao);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                icon = (ImageView) convertView.findViewById(R.id.icon_aplicacao);

                convertView.setTag(new AplicacaoViewHolder(nomeAplicacao, icon, checkBox));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        ItemLista aplicacao = (ItemLista) checkBox.getTag();
                        aplicacao.colocaSeleccionado(checkBox.isChecked());
                        if (aplicacao.estaSeleccionado())
                            listaAplicacoesSeleccionadas.add(aplicacao.devolveNome() + aplicacao.devolveNomePacote());
                        else {
                            listaAplicacoesSeleccionadas.remove(aplicacao.devolveNome() + aplicacao.devolveNomePacote());
                        }
                    }
                });
            } else {
                AplicacaoViewHolder viewHolder = (AplicacaoViewHolder) convertView.getTag();
                checkBox = viewHolder.devolveCheckBox();
                nomeAplicacao = viewHolder.devolveNomeAplicacao();
                icon = viewHolder.devolveIcon();
            }

            checkBox.setTag(aplicacao);
            checkBox.setChecked(aplicacao.estaSeleccionado());
            nomeAplicacao.setText(aplicacao.devolveNome());
            icon.setImageDrawable(aplicacao.devolveIcon());
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
            if (entry.getValue().equals(GestorAlbunsVideos.class.getName())) {
                editor.remove(entry.getKey());
                editor.commit();
            }
            if (entry.getValue().equals(DesativarAplicacoes.class.getName())) {
                editor.remove(entry.getKey());
                editor.commit();
            }
        }
        SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
        SharedPreferences.Editor editorPosicoes = posicoes.edit();
        for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
            if (entrada.getValue().equals(ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote())) {
                editorPosicoes.remove(entrada.getKey());
                editorPosicoes.commit();
            }
        }
        finish();
    }
}

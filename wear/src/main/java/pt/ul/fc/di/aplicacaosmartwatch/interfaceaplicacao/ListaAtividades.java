package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;
import java.util.Collections;

import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class ListaAtividades extends Activity {

    public static ArrayList<Drawable> listaIcons;
    public static ArrayList<String> listaNomeAtividades;
    private TextView cabecalho;
    private TextView novaAtividade;
    public static AdapterListaAtividades lista;
    public static boolean iniciouAtividade = false;
    public static WearableListView listaAtividades;
    private int posicaoAtual;
    private Animation animacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_atividades);
        iniciouAtividade = true;
        ListaRespostas.iniciouAplicacao = true;
        cabecalho = (TextView) findViewById(R.id.cabecalho);

        novaAtividade = (TextView) findViewById(R.id.novaAtividade);

        listaAtividades = (WearableListView) findViewById(R.id.listaAtividades);
        listaNomeAtividades = new ArrayList<>();
        if(getIntent().getByteArrayExtra("Icon")!=null) {
            Drawable icon = new BitmapDrawable(getApplicationContext().getResources(), BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("Icon"), 0, getIntent().getByteArrayExtra("Icon").length));
            listaIcons.add(0,icon);
        }
        criaLista();
        lista = new AdapterListaAtividades(this, listaNomeAtividades, listaIcons);
        listaAtividades.setAdapter(lista);
        listaAtividades.addOnScrollListener(mOnScrollListener);
    }

    private void criaLista() {
        listaNomeAtividades.clear();
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("ListaAtividades", MODE_PRIVATE);
        for (int i = 0; i < preferencias.getAll().size(); i++) {
            listaNomeAtividades.add(preferencias.getString("atividade" + String.valueOf(i), ""));
        }
        Collections.reverse(listaNomeAtividades);
    }

    private WearableListView.OnScrollListener mOnScrollListener = new WearableListView.OnScrollListener() {
        @Override
        public void onAbsoluteScrollChange(int i) {
            if (i >= 0)
                cabecalho.setY(-i);
        }

        @Override
        public void onScroll(int i) {
            if (animacao != null) {
                if (animacao.isInitialized()) {
                    novaAtividade.clearAnimation();
                }
            }
        }

        @Override
        public void onScrollStateChanged(int i) {
        }

        @Override
        public void onCentralPositionChanged(int i) {
            posicaoAtual = i;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        iniciouAtividade = true;
        criaLista();
        if (posicaoAtual != 0) {
            apresentaNotificacao();
            listaAtividades.scrollToPosition(posicaoAtual + 1);
        }
        lista.notifyDataSetChanged();
        RelativeLayout layoutAtividades = (RelativeLayout) findViewById(R.id.frame_layout_atividades);
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoBT":
                setTheme(R.style.BlueTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#DDFFFF"));
                break;
            case "ComLigacaoWF":
                setTheme(R.style.YellowTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                setTheme(R.style.RedTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
    }

    private void apresentaNotificacao() {
        animacao = new AlphaAnimation(0.0f, 1.0f);
        animacao.setDuration(500);
        animacao.setRepeatMode(Animation.REVERSE);
        animacao.setRepeatCount(Animation.INFINITE);
        novaAtividade.startAnimation(animacao);
        animacao.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                novaAtividade.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                novaAtividade.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getByteArrayExtra("Icon")!=null) {
            Drawable icon = new BitmapDrawable(getApplicationContext().getResources(), BitmapFactory.decodeByteArray(intent.getByteArrayExtra("Icon"), 0, intent.getByteArrayExtra("Icon").length));
            ListaAtividades.listaIcons.add(0,icon);
        }
        iniciouAtividade = true;
        if (intent.getStringExtra("MudarFundo") != null) {
            if (intent.getStringExtra("MudarFundo").equals("MudarFundo")) {
                String estado = DetetorLigacaoSmartphone.estado;
                switch (estado) {
                    case "SemLigacao":
                        setTheme(R.style.RedTheme);
                        break;
                    case "ComLigacaoBT":
                        setTheme(R.style.BlueTheme);
                        break;
                    default:
                        setTheme(R.style.YellowTheme);
                        break;
                }
            }
        }
    }
}
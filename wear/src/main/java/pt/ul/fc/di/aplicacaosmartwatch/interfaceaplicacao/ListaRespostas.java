package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;
import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.RecetorMensagens;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class ListaRespostas extends Activity {

    private String mensagem;
    private static ArrayList<Integer> listaIcons;
    public static TextView cabecalho;
    private AdapterListaRespostas lista;
    public static boolean iniciouAplicacao = false;
    public static ProgressBar iconProgresso;
    public static WearableListView listaRespostas;
    private  RelativeLayout layoutRespostas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_respostas);
        ListaAtividades.listaIcons= new ArrayList<>();
        layoutRespostas = (RelativeLayout) findViewById(R.id.frame_layout_respostas);
        RecetorMensagens.numeroAtividades=0;
        iconProgresso = (ProgressBar) findViewById(R.id.progresso);
        iconProgresso.setVisibility(View.INVISIBLE);
        ListaAtividades.iniciouAtividade = false;
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("ListaAtividades", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.clear();
        editor.commit();
        cabecalho = (TextView) findViewById(R.id.cabecalho);
        criaLista();
        iniciouAplicacao = true;
    }

    private void criaLista() {
        listaRespostas = (WearableListView) findViewById(R.id.listaRespostas);
        listaIcons = new ArrayList<>();
        listaIcons.add(R.drawable.iconbloquearecra);
        listaIcons.add(R.drawable.iconautenticacao);
        listaIcons.add(R.drawable.iconaplicacao);
        listaIcons.add(R.drawable.iconalbumfoto);
        listaIcons.add(R.drawable.iconalbumvideo);
        listaIcons.add(R.drawable.iconalarmesonoro);
        listaIcons.add(R.drawable.iconexibirmensagem);
        listaIcons.add(R.drawable.iconnotificacao);
        listaIcons.add(R.drawable.iconatividades);
        listaIcons.add(R.drawable.icontirarfotografia);
        listaIcons.add(R.drawable.iconregistoatividades);
        lista = new AdapterListaRespostas(this, listaIcons);
        listaRespostas.setAdapter(lista);
        listaRespostas.setClickListener(mClickListener);
        listaRespostas.addOnScrollListener(mOnScrollListener);
    }

    private WearableListView.ClickListener mClickListener = new WearableListView.ClickListener() {
        @Override
        public void onClick(WearableListView.ViewHolder viewHolder) {
            int numeroOpcao = viewHolder.getLayoutPosition();
            mensagem = "";
            switch (numeroOpcao) {
                case 0:
                    mensagem = "Bloquear";
                    break;
                case 1:
                    mensagem = "PedirAutenticacao";
                    break;
                case 2:
                    mensagem = "DesativarAplicacoes";
                    break;
                case 3:
                    mensagem = "GestorAlbunsFotografias";
                    break;
                case 4:
                    mensagem = "GestorAlbunsVideos";
                    break;
                case 5:
                    mensagem = "AlarmeSonoro";
                    break;
                case 6:
                    mensagem = "ExibirMensagemTexto";
                    break;
                case 7:
                    mensagem = "Notificacao";
                    break;
                case 8:
                    mensagem = "RegistoAtividades";
                    break;
                case 9:
                    mensagem = "IniciarFotografarIntruso";
                    break;
                case 10:
                    Intent listaAtividades = new Intent(getApplicationContext(), ListaAtividades.class);
                    listaAtividades.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(listaAtividades);
            }
            if (!mensagem.equals("")) {
                if (!DetetorLigacaoSmartphone.estado.equals("SemLigacao")) {
                    Mensagem mensagemResposta = new Mensagem(mensagem,getApplication());
                    mensagemResposta.enviaMensagem();
                    iconProgresso.setVisibility(View.VISIBLE);
                    cabecalho.setVisibility(View.INVISIBLE);
                    listaRespostas.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Sem Ligação!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onTopEmptyRegionClick() {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        iniciouAplicacao = false;
    }

    private WearableListView.OnScrollListener mOnScrollListener = new WearableListView.OnScrollListener() {
        @Override
        public void onAbsoluteScrollChange(int i) {
            if (i >= 0)
                cabecalho.setY(-i);
        }

        @Override
        public void onScroll(int i) {
        }

        @Override
        public void onScrollStateChanged(int i) {
        }

        @Override
        public void onCentralPositionChanged(int i) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ListaAtividades.iniciouAtividade = false;
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoBT":
                setTheme(R.style.BlueTheme);
                layoutRespostas.setBackgroundColor(Color.parseColor("#DDFFFF"));
                break;
            case "ComLigacaoWF":
                setTheme(R.style.YellowTheme);
                layoutRespostas.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                setTheme(R.style.RedTheme);
                layoutRespostas.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ListaAtividades.iniciouAtividade = false;
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
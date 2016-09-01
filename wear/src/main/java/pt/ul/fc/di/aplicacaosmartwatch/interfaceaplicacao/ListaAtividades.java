package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.RecetorMensagens;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class ListaAtividades extends Fragment {

    public static ArrayList<Drawable> listaIcons;
    public static ArrayList<String> listaNomeAtividades;
    public static ArrayList<String> listaAtividades;
    private TextView cabecalho;
    private TextView novaAtividade;
    public static AdapterListaAtividades lista;
    public static boolean iniciouAtividade;
    private WearableListView vistalistaAtividades;
    private int posicaoAtual;
    public static boolean iniciouAplicacao;
    private Animation animacao;
    private int novasAtividades;
    private ArrayList<String> timeStampAtividade;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_atividades, container, false);

        iniciouAtividade = true;
        iniciouAplicacao = true;
        cabecalho = (TextView) view.findViewById(R.id.cabecalho);
        novaAtividade = (TextView) view.findViewById(R.id.novaAtividade);

        novaAtividade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistalistaAtividades.scrollToPosition(0);
                novaAtividade.clearAnimation();
            }
        });

        vistalistaAtividades = (WearableListView) view.findViewById(R.id.listaAtividades);
        vistalistaAtividades.setGreedyTouchMode(true);

        vistalistaAtividades.scrollToPosition(posicaoAtual + RecetorMensagens.numeroAtividades);

        listaNomeAtividades = new ArrayList<>();
        timeStampAtividade = new ArrayList<>();


        if (listaAtividades == null)
            listaAtividades = new ArrayList<>();

        if (lista != null) {
            if (lista.getItemCount() == 40) {
                listaAtividades.remove(0);
            }
        }

        criaLista();
        lista = new AdapterListaAtividades(getActivity().getApplicationContext(), listaNomeAtividades);
        vistalistaAtividades.setAdapter(lista);
        vistalistaAtividades.addOnScrollListener(mOnScrollListener);
        vistalistaAtividades.setClickListener(mClickListener);
        return view;
    }

    private void criaLista() {
        listaNomeAtividades.clear();
        for (int i = 0; i < listaAtividades.size(); i++) {
            String nomeAtividade = listaAtividades.get(i);
            listaNomeAtividades.add(nomeAtividade.substring(nomeAtividade.indexOf(' ') + 1));
            timeStampAtividade.add(nomeAtividade.substring(0, nomeAtividade.indexOf(' ')));
        }
        Collections.reverse(listaNomeAtividades);
        Collections.reverse(timeStampAtividade);
    }

    private WearableListView.OnScrollListener mOnScrollListener = new WearableListView.OnScrollListener() {
        @Override
        public void onAbsoluteScrollChange(int i) {
            if (i >= 0)
                cabecalho.setY(-i);
            if (animacao != null) {
                if (animacao.hasStarted()) {
                    novasAtividades = 0;
                    novaAtividade.clearAnimation();
                }
            }
        }

        @Override
        public void onScroll(int i) {
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
        if (posicaoAtual != 0 && !DetalheAtividade.iniciouDetalhe) {
            vistalistaAtividades.scrollToPosition(posicaoAtual + 1);
            novasAtividades++;
            apresentaNotificacao();
        }

        RelativeLayout layoutAtividades = (RelativeLayout) view.findViewById(R.id.frame_layout_atividades);
        String estado = DetetorLigacaoSmartphone.estado;
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.BlueTheme);
        switch (estado) {
            case "ComLigacaoBT":
                contextThemeWrapper.setTheme(R.style.BlueTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#DDFFFF"));
                break;
            case "ComLigacaoWF":
                contextThemeWrapper.setTheme(R.style.YellowTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                contextThemeWrapper.setTheme(R.style.RedTheme);
                layoutAtividades.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
        DetalheAtividade.iniciouDetalhe = false;
    }

    private WearableListView.ClickListener mClickListener = new WearableListView.ClickListener() {

        @Override
        public void onClick(WearableListView.ViewHolder viewHolder) {
            Intent detalhaAtividade = new Intent(getActivity().getApplicationContext(), ConstroiFragmentos.class);
            detalhaAtividade.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            detalhaAtividade.putExtra("timestamp", timeStampAtividade.get(viewHolder.getLayoutPosition()));
            detalhaAtividade.putExtra("descricao", listaNomeAtividades.get(viewHolder.getLayoutPosition()));
            Drawable icon = listaIcons.get(viewHolder.getLayoutPosition());
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] bitmapdata = stream.toByteArray();
                detalhaAtividade.putExtra("Icon", bitmapdata);
            }
            startActivity(detalhaAtividade);
        }

        @Override
        public void onTopEmptyRegionClick() {
        }
    };

    private void apresentaNotificacao() {
        animacao = new AlphaAnimation(0.0f, 1.0f);
        animacao.setDuration(500);
        animacao.setRepeatMode(Animation.REVERSE);
        animacao.setRepeatCount(Animation.INFINITE);
        CharSequence textoNovasAtividades = novasAtividades + " new";
        novaAtividade.setText(textoNovasAtividades);
        novaAtividade.startAnimation(animacao);

        animacao.setAnimationListener(new Animation.AnimationListener() {

            private View itemLista;

            @Override
            public void onAnimationStart(Animation arg0) {
                itemLista = vistalistaAtividades.getChildAt(0);
                itemLista.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                novasAtividades = 0;
                itemLista.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        iniciouAplicacao=false;
        iniciouAtividade=false;
    }
}
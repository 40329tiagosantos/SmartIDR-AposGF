package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;
import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.RecetorMensagens;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class DetalheAtividade extends Fragment {

    public static boolean iniciouDetalhe;
    private RelativeLayout layoutDetalhesAtividades;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detalhe_atividade, container, false);
        Mensagem iniciaRegistarAtividades = new Mensagem("IniciaRegistoAtividades", getActivity().getApplication());
        iniciaRegistarAtividades.enviaMensagem();

        ListaAtividades.iniciouAtividade = false;
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);
        TextView descricao = (TextView) view.findViewById(R.id.descricao);
        ImageView iconAtividade = (ImageView) view.findViewById(R.id.iconAtividade);
        byte[] iconAtividadeArray = getActivity().getIntent().getByteArrayExtra("Icon");
        if (iconAtividadeArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(iconAtividadeArray, 0, iconAtividadeArray.length);
            iconAtividade.setImageBitmap(bitmap);
        }
        long agora = System.currentTimeMillis();
        long passados = Long.parseLong(getActivity().getIntent().getStringExtra("timestamp"));
        long segundosPassados = agora - ((agora - passados));
        timestamp.setText(DateUtils.getRelativeDateTimeString(getActivity().getApplicationContext(), segundosPassados, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME));

        descricao.setText(getActivity().getIntent().getStringExtra("descricao"));

        iniciouDetalhe = true;
        String estado = DetetorLigacaoSmartphone.estado;
        layoutDetalhesAtividades = (RelativeLayout) view.findViewById(R.id.frame_layout_atividades);

        switch (estado) {
            case "ComLigacaoBT":
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#DDFFFF"));
                break;
            case "ComLigacaoWF":
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoBT":
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#DDFFFF"));
                break;
            case "ComLigacaoWF":
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                layoutDetalhesAtividades.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecetorMensagens.numeroAtividades = 0;
    }
}

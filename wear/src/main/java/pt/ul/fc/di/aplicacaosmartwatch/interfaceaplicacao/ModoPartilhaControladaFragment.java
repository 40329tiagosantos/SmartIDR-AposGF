package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.ActionPage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class ModoPartilhaControladaFragment extends Fragment {

    public static ActionPage actionPage;
    public static ProgressBar progresso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_resposta, container, false);
        actionPage = (ActionPage) view.findViewById(R.id.action_page);
        SharedPreferences preferencias = getActivity().getSharedPreferences("NomeBotaoPartilhaControlada", Context.MODE_PRIVATE);

        if (preferencias.getAll().isEmpty()) {
            actionPage.setText("Enable sharing mode");
            actionPage.setImageResource(R.drawable.iconpartilha);
        } else {
            String textoResposta = preferencias.getString("NomeBotaoPartilhaControlada", "");
            actionPage.setText(textoResposta);
            if (textoResposta.equals("Enable sharing mode"))
                actionPage.setImageResource(R.drawable.iconpartilha);

            else
                actionPage.setImageResource(R.drawable.icondesativarpartilha);
        }

        actionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetetorLigacaoSmartphone.estado.equals("SemLigacao")) {
                    Toast.makeText(getActivity(), "Sem Ligação", Toast.LENGTH_SHORT).show();
                } else {
                    actionPage.getButton().setVisibility(View.INVISIBLE);
                    progresso = (ProgressBar) view.findViewById(R.id.progresso);
                    progresso.setVisibility(View.VISIBLE);
                    progresso.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    String mensagem = "AtivarModoPartilha";
                    Mensagem mensagemResposta = new Mensagem(mensagem, getActivity().getApplication());
                    mensagemResposta.enviaMensagem();
                }
            }
        });
        actionPage.setColor(Color.parseColor("#FF00FF"));
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoBT":
                view.setBackgroundColor(Color.parseColor("#00CCFF"));
                break;
            case "ComLigacaoWF":
                view.setBackgroundColor(Color.parseColor("#FFEDA8"));
                break;
            default:
                view.setBackgroundColor(Color.parseColor("#FC807C"));
                break;
        }
        return view;
    }
}

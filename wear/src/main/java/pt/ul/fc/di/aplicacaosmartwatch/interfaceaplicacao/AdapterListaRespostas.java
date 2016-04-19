package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;

public class AdapterListaRespostas extends WearableListView.Adapter {

    private ArrayList<Integer> listaRespostas;
    private final LayoutInflater mInflater;

    public AdapterListaRespostas(Context context, ArrayList<Integer> items) {
        mInflater = LayoutInflater.from(context);
        listaRespostas = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.item_lista_respostas, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        CircledImageView circledView = itemViewHolder.iconResposta;
        circledView.setImageResource(listaRespostas.get(position));
        TextView textView = itemViewHolder.nomeResposta;
        String nomeResposta = "";
        switch (position) {
            case 0:
                nomeResposta = "Bloquear";
                break;
            case 1:
                nomeResposta = "Pedir" + "\n" + "Autenticação";
                break;
            case 2:
                nomeResposta = "Desativar" + "\n" + "Aplicações";
                break;
            case 3:
                nomeResposta = "Ocultar" + "\n" + "Álbuns de" + "\n" + "Fotografias";
                break;
            case 4:
                nomeResposta = "Ocultar" + "\n" + "Álbuns de" + "\n" + "Vídeos";
                break;
            case 5:
                nomeResposta = "Emitir" + "\n" + "Alarme" + "\n" + "Sonoro";
                break;
            case 6:
                nomeResposta = "Exibir" + "\n" + "Mensagem de" + "\n" +"Texto";
                break;
            case 7:
                nomeResposta = "Receber" + "\n" + "Notificação";
                break;
            case 8:
                nomeResposta = "Registar" + "\n" + "Atividades";
                break;
            case 9:
                nomeResposta = "Fotografar" + "\n" + "Intruso";
                break;
            case 10:
                nomeResposta = "Exibir" + "\n" + "Atividades";
                break;
        }
        textView.setText(nomeResposta);
    }

    @Override
    public int getItemCount() {
        return listaRespostas.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {

        private CircledImageView iconResposta;
        private TextView nomeResposta;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.iconResposta = (CircledImageView) itemView.findViewById(R.id.iconResposta);
            this.nomeResposta = (TextView) itemView.findViewById(R.id.nomeResposta);
        }
    }
}

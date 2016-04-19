package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;

public class AdapterListaAtividades extends WearableListView.Adapter {

    public static ArrayList<String> listaAtividades;
    public static ArrayList<Drawable> listaIcons;

    private final LayoutInflater mInflater;

    public AdapterListaAtividades(Context context, ArrayList<String> items, ArrayList<Drawable> listaIc) {
        mInflater = LayoutInflater.from(context);
        listaAtividades = items;
        this.listaIcons=listaIc;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.item_lista_atividades, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        CircledImageView circledView = itemViewHolder.iconAtividade;
        circledView.setImageDrawable(listaIcons.get(position));
        TextView textView = itemViewHolder.nomeAtividade;
        textView.setText(listaAtividades.get(position));
    }


    @Override
    public int getItemCount() {
        return listaAtividades.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {

        private TextView nomeAtividade;
        private CircledImageView iconAtividade;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.iconAtividade = (CircledImageView) itemView.findViewById(R.id.iconAtividade);
            this.nomeAtividade = (TextView) itemView.findViewById(R.id.nomeAtividade);
        }
    }
}

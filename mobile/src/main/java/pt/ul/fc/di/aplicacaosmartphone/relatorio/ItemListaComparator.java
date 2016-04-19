package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import java.util.Comparator;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;


public class ItemListaComparator implements Comparator<ItemLista> {


    public ItemListaComparator() {
    }

    public int compare(ItemLista itemLista, ItemLista outroItemLista) {
        return itemLista.devolveNome().compareTo(outroItemLista.devolveNome());
    }
}


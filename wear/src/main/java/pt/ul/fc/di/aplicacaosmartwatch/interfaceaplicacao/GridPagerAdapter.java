

package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private List<Row> mRows= new ArrayList<Row>();

    public GridPagerAdapter(FragmentManager fm, ArrayList<Fragment> listaFragmentos) {
        super(fm);
        mRows.add(new Row(listaFragmentos));
    }

    private class Row {

        List<Fragment> colunas = new ArrayList<Fragment>();

        public Row(ArrayList<Fragment> fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            colunas.add(f);
        }

        Fragment getColumn(int i) {
            return colunas.get(i);
        }

        public int getColumnCount() {
            return colunas.size();
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);

        return adapterRow.getColumn(col);
    }

    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mRows.get(rowNum).getColumnCount();
    }

}

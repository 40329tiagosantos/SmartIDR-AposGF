package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ItemLista extends LinearLayout implements WearableListView.OnCenterProximityListener {

    private static final float NO_ALPHA = 1f, PARTIAL_ALPHA = 0.60f;

    public ItemLista(Context context) {
        this(context, null);
    }

    public ItemLista(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemLista(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void onCenterPosition(boolean animate) {
        if(animate)
            animate().alpha(NO_ALPHA).start();
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        if(animate)
            animate().alpha(PARTIAL_ALPHA).start();
    }
}
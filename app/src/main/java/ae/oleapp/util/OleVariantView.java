package ae.oleapp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleVariantOptionAdapter;
import ae.oleapp.models.OleChoiceOption;

public class OleVariantView extends RelativeLayout {

    private final Context context;
    private TextView tvTitle;
    private RecyclerView recyclerView;
    private OleChoiceOption oleChoiceOption;
    private final List<String> optionList = new ArrayList<>();
    private OleVariantOptionAdapter adapter;
    private VariantViewCallback variantViewCallback;

    public OleVariantView(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public OleVariantView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public OleVariantView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void setVariantViewCallback(VariantViewCallback variantViewCallback) {
        this.variantViewCallback = variantViewCallback;
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.olevariant_view, this);

        tvTitle = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recycler_vu);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OleVariantOptionAdapter(context, optionList);
        adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    OleVariantOptionAdapter.ItemClickListener itemClickListener = new OleVariantOptionAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.setSelectedIndex(pos);
            variantViewCallback.selectItem(OleVariantView.this, oleChoiceOption, optionList.get(pos));
        }
    };

    public void populateData(OleChoiceOption oleChoiceOption) {
        this.oleChoiceOption = oleChoiceOption;
        tvTitle.setText(oleChoiceOption.getTitle());
        optionList.clear();
        optionList.addAll(oleChoiceOption.getValues());
        adapter.notifyDataSetChanged();
    }

    public interface VariantViewCallback {
        void selectItem(OleVariantView view, OleChoiceOption oleChoiceOption, String value);
    }
}

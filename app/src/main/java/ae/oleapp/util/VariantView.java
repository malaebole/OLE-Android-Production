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
import ae.oleapp.adapters.VariantOptionAdapter;
import ae.oleapp.models.ChoiceOption;

public class VariantView extends RelativeLayout {

    private final Context context;
    private TextView tvTitle;
    private RecyclerView recyclerView;
    private ChoiceOption choiceOption;
    private final List<String> optionList = new ArrayList<>();
    private VariantOptionAdapter adapter;
    private VariantViewCallback variantViewCallback;

    public VariantView(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public VariantView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public VariantView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void setVariantViewCallback(VariantViewCallback variantViewCallback) {
        this.variantViewCallback = variantViewCallback;
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.variant_view, this);

        tvTitle = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.recycler_vu);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VariantOptionAdapter(context, optionList);
        adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    VariantOptionAdapter.ItemClickListener itemClickListener = new VariantOptionAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.setSelectedIndex(pos);
            variantViewCallback.selectItem(VariantView.this, choiceOption, optionList.get(pos));
        }
    };

    public void populateData(ChoiceOption choiceOption) {
        this.choiceOption = choiceOption;
        tvTitle.setText(choiceOption.getTitle());
        optionList.clear();
        optionList.addAll(choiceOption.getValues());
        adapter.notifyDataSetChanged();
    }

    public interface VariantViewCallback {
        void selectItem(VariantView view, ChoiceOption choiceOption, String value);
    }
}

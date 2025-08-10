package ae.oleapp.shop;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleOrdersProductReviewAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityOrdersProductReviewBinding;
import ae.oleapp.dialogs.OleOrderReviewDialogFragment;
import ae.oleapp.models.Product;

public class OrdersProductReviewActivity extends BaseActivity {

    private ActivityOrdersProductReviewBinding binding;
    private List<Product> productList = new ArrayList<>();
    OleOrdersProductReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersProductReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.review);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Product>>(){}.getType();
            productList = gson.fromJson(bundle.getString("products", ""), type);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleOrdersProductReviewAdapter(getContext(), productList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    OleOrdersProductReviewAdapter.ItemClickListener itemClickListener = new OleOrdersProductReviewAdapter.ItemClickListener() {
        @Override
        public void reviewClicked(View view, int pos) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("OrderReviewDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleOrderReviewDialogFragment dialogFragment = new OleOrderReviewDialogFragment(productList.get(pos).getId());
            dialogFragment.setDialogCallback(new OleOrderReviewDialogFragment.OrderReviewDialogCallback() {
                @Override
                public void didRate(DialogFragment df, boolean isRate) {
                    df.dismiss();
                    if (isRate) {
                        productList.get(pos).setIsReviewed("1");
                        adapter.notifyItemChanged(pos);
                    }
                }
            });
            dialogFragment.show(fragmentTransaction, "OrderReviewDialogFragment");
        }
    };

}
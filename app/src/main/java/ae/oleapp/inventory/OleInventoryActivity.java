package ae.oleapp.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityInventoryBinding;


public class OleInventoryActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityInventoryBinding binding;
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.inventory);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        binding.bar.backBtn.setOnClickListener(this);
        binding.saleVu.setOnClickListener(this);
        binding.purchaseVu.setOnClickListener(this);
        binding.saleReportVu.setOnClickListener(this);
        binding.purchaseReportVu.setOnClickListener(this);
        binding.stockVu.setOnClickListener(this);
        binding.saleOrderVu.setOnClickListener(this);
        binding.profitReportVu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.saleVu) {
            Intent intent = new Intent(getContext(), OleNewSaleActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.purchaseVu) {
            Intent intent = new Intent(getContext(), OlePurchaseListActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.saleReportVu) {
            Intent intent = new Intent(getContext(), OleSalesReportActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.purchaseReportVu) {
            Intent intent = new Intent(getContext(), OlePurchaseReportActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.stockVu) {
            Intent intent = new Intent(getContext(), OleStockReportActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.saleOrderVu) {
            Intent intent = new Intent(getContext(), OleSalesOrdersActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
        else if (v == binding.profitReportVu) {
            Intent intent = new Intent(getContext(), OleProfitReportActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
    }
}
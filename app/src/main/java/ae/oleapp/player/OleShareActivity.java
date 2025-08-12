package ae.oleapp.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityShareBinding;
import ae.oleapp.owner.OleDiscountCardsActivity;
import ae.oleapp.owner.OlePromoCodeListActivity;

public class OleShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityShareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.share);

        binding.bar.backBtn.setOnClickListener(this);
        binding.relSlots.setOnClickListener(this);
        binding.relFriendly.setOnClickListener(this);
        binding.relMatch.setOnClickListener(this);
        binding.relResult.setOnClickListener(this);
        binding.relLoyalty.setOnClickListener(this);
        binding.relPromo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.relSlots) {
            startActivity(new Intent(getContext(), OleEmptySlotsActivity.class));
        }
        else if (v == binding.relFriendly) {
            Intent intent = new Intent(getContext(), OleMatchShareActivity.class);
            intent.putExtra("is_match", false);
            startActivity(intent);
        }
        else if (v == binding.relMatch) {
            Intent intent = new Intent(getContext(), OleMatchShareActivity.class);
            intent.putExtra("is_match", true);
            startActivity(intent);
        }
        else if (v == binding.relResult) {
            Intent intent = new Intent(getContext(), OleResultListShareActivity.class);
            startActivity(intent);
        }
        else if (v == binding.relLoyalty) {
            Intent intent = new Intent(getContext(), OleDiscountCardsActivity.class);
            intent.putExtra("is_share", true);
            startActivity(intent);
        }
        else if (v == binding.relPromo) {
            Intent intent = new Intent(getContext(), OlePromoCodeListActivity.class);
            intent.putExtra("is_share", true);
            startActivity(intent);
        }
    }
}
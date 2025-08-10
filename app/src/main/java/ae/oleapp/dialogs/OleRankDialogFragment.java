package ae.oleapp.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.databinding.OlefragmentRankDialogBinding;
import ae.oleapp.owner.OleRankActivity;
import ae.oleapp.padel.OlePadelLevelsActivity;

public class OleRankDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentRankDialogBinding binding;
    private Context context;

    public OleRankDialogFragment() {
        // Required empty public constructor
    }

    public OleRankDialogFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentRankDialogBinding.inflate(inflater, container, false);
        View  view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnFootballRank.setOnClickListener(this);
        binding.btnPadelRank.setOnClickListener(this);
        binding.btnGlobalRank.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == binding.btnFootballRank) {
            Intent intent = new Intent(getContext(), OleRankActivity.class);
            intent.putExtra("is_padel", false);
            context.startActivity(intent);
        }
        else if (v == binding.btnPadelRank) {
            Intent intent = new Intent(getContext(), OleRankActivity.class);
            intent.putExtra("is_padel", true);
            context.startActivity(intent);
        }
        else if (v == binding.btnGlobalRank) {
            context.startActivity(new Intent(getContext(), OlePadelLevelsActivity.class));
        }
    }
}
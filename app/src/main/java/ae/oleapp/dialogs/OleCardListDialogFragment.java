package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import ae.oleapp.adapters.OleCardListDialogAdapter;
import ae.oleapp.databinding.OlefragmentCardListDialogBinding;
import ae.oleapp.models.OleUserCard;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleCardListDialogFragment extends DialogFragment {

    private OlefragmentCardListDialogBinding binding;
    private CardListDialogCallback cardListDialogCallback;
    private List<OleUserCard> cardList;

    public OleCardListDialogFragment() {
        // Required empty public constructor
    }

    public OleCardListDialogFragment(List<OleUserCard> oleUserCards) {
        cardList = oleUserCards;
    }

    public void setCardListDialogCallback(CardListDialogCallback cardListDialogCallback) {
        this.cardListDialogCallback = cardListDialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentCardListDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        OleCardListDialogAdapter adapter = new OleCardListDialogAdapter(getContext(), cardList);
        adapter.setItemClickListener(new OleCardListDialogAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int pos) {
                cardListDialogCallback.didSelectCard(cardList.get(pos));
                dismiss();
            }
        });
        binding.recyclerVu.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface CardListDialogCallback {
        void didSelectCard(OleUserCard oleUserCard);
    }
}

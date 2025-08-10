package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import  ae.oleapp.R;
import ae.oleapp.adapters.RatingPagerAdapter;
import ae.oleapp.databinding.FragmentRatingPagerDialogBinding;
import  ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import  ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.util.LinePagerIndicatorDecoration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingPagerDialogFragment extends DialogFragment {

    private FragmentRatingPagerDialogBinding binding;
    private RatingPagerAdapter adapter;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private String gameId = "";

    public RatingPagerDialogFragment() {
        //Required empty public constructor
    }

    public RatingPagerDialogFragment(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRatingPagerDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setDimAmount(0.9f);
            getDialog().setCanceledOnTouchOutside(false);
        }

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerVu);
        binding.recyclerVu.addItemDecoration(new LinePagerIndicatorDecoration());
        adapter = new RatingPagerAdapter(getContext(), playerList);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.progressVu.setVisibility(View.GONE);

        getGamePlayersAPI(true);

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    RatingPagerAdapter.OnItemClickListener clickListener = new RatingPagerAdapter.OnItemClickListener() {
        @Override
        public void OnSubmitClick(View v, int pos, int Speed, int Shooting, int Dribble, int Iq, int Fitness, int Defence, String reachTime, String feedback, String gotCard) {
            ratePlayerAPI(true, playerList.get(pos).getId(), playerList.get(pos).getFriendShipId(), pos,  Speed, Shooting, Dribble, Iq, Fitness, Defence, reachTime, feedback, gotCard);
        }
    };

    private void getGamePlayersAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.friendsInGame(Functions.getAppLang(getContext()),  Functions.getPrefValue(getContext(), Constants.kUserID), gameId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                PlayerInfo info = gson.fromJson(arr.get(i).toString(), PlayerInfo.class);
                                if (!info.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                    playerList.add(info);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void ratePlayerAPI(boolean isLoader, String playerId, String friendShipID,  int pos, int Speed, int Shooting, int Dribble, int Iq, int Fitness, int Defence, String reachTime, String feedback, String gotCard) {
        binding.progressVu.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPlayerRating(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),
                playerId, friendShipID, gameId, reachTime, feedback, Speed, Shooting, Dribble, Iq, Fitness, Defence, Speed,gotCard);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                binding.progressVu.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            int p = pos + 1;
                            if (p < playerList.size()) {
                                binding.recyclerVu.smoothScrollToPosition(p);
                            }
                            else {
                                dismiss();
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.progressVu.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}

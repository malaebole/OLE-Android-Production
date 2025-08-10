package ae.oleapp.fragments;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.ProfileActivity;
import ae.oleapp.adapters.BestPlayerAdapter;
import ae.oleapp.adapters.PlayerListAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.FragmentBestPlayerDialogBinding;
import ae.oleapp.dialogs.GameResultDialogFragment;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BestPlayerDialogFragment extends DialogFragment {

    private FragmentBestPlayerDialogBinding binding;
    private BestPlayerAdapter adapter;
    private ResultDialogCallback dialogCallback;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private String gameId = "";
    private  String playerID = "";
    private int position;
    Boolean Submitted = true;

    public BestPlayerDialogFragment() {
        // Required empty public constructor
    }

    public BestPlayerDialogFragment(String gameId) {
        this.gameId = gameId;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBestPlayerDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new BestPlayerAdapter(getContext(), playerList);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);



        getGamePlayersAPI(true);

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogCallback.didSubmitResult(BestPlayerDialogFragment.this, Submitted);

            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogCallback.didSubmitResult(BestPlayerDialogFragment.this, Submitted);

            }
        });

        binding.spark.setVisibility(View.INVISIBLE);
        binding.crownVu.setVisibility(View.INVISIBLE);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBestPlayer(true, playerID, position);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    BestPlayerAdapter.ItemClickListener clickListener = new BestPlayerAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View v, int pos) {

            Glide.with(getContext()).load(playerList.get(pos).getEmojiUrl()).into(binding.emojiImgVu);
            Glide.with(getContext()).load(playerList.get(pos).getBibUrl()).into(binding.shirtImgVu);
                binding.spark.setVisibility(View.VISIBLE);
                if (playerList.get(pos).getEmojiUrl().equalsIgnoreCase("")){
                    binding.crownVu.setVisibility(View.INVISIBLE);
                }else{
                    binding.crownVu.setVisibility(View.VISIBLE);
                }
                binding.bisht.setVisibility(View.VISIBLE);
                playerID = playerList.get(pos).getId();
                position = pos;

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

    private void addBestPlayer(boolean isLoader, String playerId, int pos) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addBestPlayer(Functions.getAppLang(getContext()),
                Functions.getPrefValue(getContext(), Constants.kUserID),
                 gameId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                dialogCallback.didSubmitResult(BestPlayerDialogFragment.this, Submitted);

                            dismiss();
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
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isSubmitted);
    }

}
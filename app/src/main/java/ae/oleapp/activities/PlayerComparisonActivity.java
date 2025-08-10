package ae.oleapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import ae.oleapp.adapters.PlayerComparisonAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityPlayerComparisonBinding;
import ae.oleapp.models.DragData;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerComparisonActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPlayerComparisonBinding binding;
    private PlayerComparisonAdapter adapter;
    private final List<PlayerInfo> playerList = new ArrayList<>();
    private PlayerInfo playerInfo1, playerInfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerComparisonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new PlayerComparisonAdapter(getContext(), playerList);
        adapter.user(true);
        binding.recyclerVu.setAdapter(adapter);

        binding.p1Vu.setVisibility(View.INVISIBLE);
        binding.p2Vu.setVisibility(View.INVISIBLE);
        binding.p1Card.setOnDragListener(vuDragListener);
        binding.p2Card.setOnDragListener(vuDragListener);

        getPlayers(true);

        binding.btnClose.setOnClickListener(this);
        binding.btnRemoveP1.setOnClickListener(this);
        binding.btnRemoveP2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.btnRemoveP1) {
            if (playerInfo1 != null) {
                binding.p1Vu.setVisibility(View.INVISIBLE);
                binding.p1PlaceVu.setVisibility(View.VISIBLE);
                playerList.add(0, playerInfo1);
                adapter.notifyDataSetChanged();
                playerInfo1 = null;
                binding.tvMatch.setText("- - -");
                binding.tvP1Win.setText("- - -");
                binding.tvP1Lost.setText("- - -");
                binding.tvDraw.setText("- - -");
            }
        }
        else if (view == binding.btnRemoveP2) {
            if (playerInfo2 != null) {
                binding.p2Vu.setVisibility(View.INVISIBLE);
                binding.p2PlaceVu.setVisibility(View.VISIBLE);
                playerList.add(0, playerInfo2);
                adapter.notifyDataSetChanged();
                playerInfo2 = null;
                binding.tvMatch.setText("- - -");
                binding.tvP2Win.setText("- - -");
                binding.tvP2Lost.setText("- - -");
                binding.tvDraw.setText("- - -");
            }
        }
    }

    View.OnDragListener vuDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v == binding.p1Card) {
                        binding.p1Card.setCardBackgroundColor(Color.GREEN);
                    }
                    else if (v == binding.p2Card) {
                        binding.p2Card.setCardBackgroundColor(Color.GREEN);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED: case DragEvent.ACTION_DRAG_ENDED:
                    if (v == binding.p1Card) {
                        binding.p1Card.setCardBackgroundColor(Color.WHITE);
                    }
                    else if (v == binding.p2Card) {
                        binding.p2Card.setCardBackgroundColor(Color.WHITE);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    final DragData state = (DragData) event.getLocalState();
                    if (v == binding.p1Card) {
                        int pos = state.getPos();
                        binding.p1Vu.setVisibility(View.VISIBLE);
                        binding.p1PlaceVu.setVisibility(View.INVISIBLE);
                        if (playerInfo1 != null) {
                            playerList.add(0, playerInfo1);
                            pos += 1;
                        }
                        playerInfo1 = state.getItem();
                        String[] arr = playerInfo1.getNickName().split(" ");
                        if (arr.length > 0) {
                            binding.tvP1Name.setText(arr[0]);
                        }
                        else {
                            binding.tvP1Name.setText(playerInfo1.getNickName());
                        }
                        Glide.with(getContext()).load(playerInfo1.getEmojiUrl()).into(binding.emojiImgVuP1);
                        Glide.with(getContext()).load(playerInfo1.getBibUrl()).into(binding.shirtImgVuP1);
                        playerList.remove(pos);
                        adapter.notifyDataSetChanged();
                        callApi();
                    }
                    else if (v == binding.p2Card) {
                        int pos = state.getPos();
                        binding.p2Vu.setVisibility(View.VISIBLE);
                        binding.p2PlaceVu.setVisibility(View.INVISIBLE);
                        if (playerInfo2 != null) {
                            playerList.add(0, playerInfo2);
                            pos += 1;
                        }
                        playerInfo2 = state.getItem();
                        String[] arr = playerInfo2.getNickName().split(" ");
                        if (arr.length > 0) {
                            binding.tvP2Name.setText(arr[0]);
                        }
                        else {
                            binding.tvP2Name.setText(playerInfo2.getNickName());
                        }
                        Glide.with(getContext()).load(playerInfo2.getEmojiUrl()).into(binding.emojiImgVuP2);
                        Glide.with(getContext()).load(playerInfo2.getBibUrl()).into(binding.shirtImgVuP2);
                        playerList.remove(pos);
                        adapter.notifyDataSetChanged();
                        callApi();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void callApi() {
        if (playerInfo1 != null && playerInfo2 != null) {
            playerComparisonAPI(true);
        }
    }

    private void populateData(PlayerInfo info1, PlayerInfo info2) {
        binding.tvP1Win.setText(info1.getMatchWon());
        binding.tvP1Lost.setText(info1.getMatchLoss());
        binding.tvP2Win.setText(info2.getMatchWon());
        binding.tvP2Lost.setText(info2.getMatchLoss());
    }

    private void getPlayers(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.lineupPlayersList(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray array = object.getJSONArray(Constants.kData);
                            playerList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < array.length(); i++) {
                                playerList.add(gson.fromJson(array.get(i).toString(), PlayerInfo.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void playerComparisonAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.compareTwoPlayers(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerInfo1.getId(), playerInfo2.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            PlayerInfo info1 = gson.fromJson(object.getJSONObject(Constants.kData).getJSONObject("player_one").toString(), PlayerInfo.class);
                            PlayerInfo info2 = gson.fromJson(object.getJSONObject(Constants.kData).getJSONObject("player_two").toString(), PlayerInfo.class);
                            binding.tvMatch.setText(object.getJSONObject(Constants.kData).getString("match_played"));
                            binding.tvDraw.setText(object.getJSONObject(Constants.kData).getString("match_drawn"));
                            populateData(info1, info2);
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
}
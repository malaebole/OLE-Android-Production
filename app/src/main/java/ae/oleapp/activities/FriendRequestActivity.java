package ae.oleapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.adapters.FriendRequestAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityFriendRequestBinding;
import ae.oleapp.models.FriendRequestModel;
import ae.oleapp.socket.SocketManager;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFriendRequestBinding binding;

    private final List<FriendRequestModel> friendRequestModelList = new ArrayList<>();

    private FriendRequestAdapter friendRequestAdapter;
    private SocketManager socketManager;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        socketManager =  SocketManager.getInstance();
        socket = socketManager.getSocket();

        LinearLayoutManager partnerDocumentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.requestRecyclerVu.setLayoutManager(partnerDocumentLayoutManager);
        friendRequestAdapter = new FriendRequestAdapter(getContext(), friendRequestModelList);
        friendRequestAdapter.setItemClickListener(itemClickListener);
        binding.requestRecyclerVu.setAdapter(friendRequestAdapter);

        binding.btnClose.setOnClickListener(this);
        getFriendRequests(true);


    }

    FriendRequestAdapter.ItemClickListener itemClickListener = new FriendRequestAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos, Boolean decision) {
            if (decision){
                acceptDeclineRequest(true,friendRequestModelList.get(pos).getId(), friendRequestModelList.get(pos).getUserId(), "accept");
            }else{
                acceptDeclineRequest(true,friendRequestModelList.get(pos).getId(),friendRequestModelList.get(pos).getUserId(),"decline");
            }
        }
    };

    private void getFriendRequests(Boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getFriendRequests(Functions.getAppLang(getContext()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                friendRequestModelList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    friendRequestModelList.add(gson.fromJson(data.get(i).toString(), FriendRequestModel.class));
                                }
                            }
                            else {
                                friendRequestModelList.clear();
                            }
                            friendRequestAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void acceptDeclineRequest(Boolean isLoader, String requestId, String friendId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptDeclineRequest(Functions.getAppLang(getContext()),requestId,type);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                if (type.equalsIgnoreCase("accept")){
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                    editor.putString(Constants.groupCallStatus, "true");
                                    editor.apply();
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("friend_id", friendId);
                                        socket.emit("player:accept-friend-request", data);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                populateData(requestId);
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void populateData(String id) {
        Iterator<FriendRequestModel> iterator = friendRequestModelList.iterator();
        while (iterator.hasNext()) {
            FriendRequestModel player = iterator.next();
            if (id.contains(player.getId())) {
                iterator.remove();
            }
        }
        friendRequestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            finish();
        }

    }

}
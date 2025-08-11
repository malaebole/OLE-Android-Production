package ae.oleapp.player;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleChatAdapter;
import ae.oleapp.adapters.OleGamePictureAdapter;
import ae.oleapp.adapters.OleOverlapDecoration;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.database.Chat;
import ae.oleapp.database.Message;
import ae.oleapp.databinding.OleactivityChatBinding;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleChatActivity extends BaseActivity {

    private OleactivityChatBinding binding;
    private OleChatAdapter adapter;
    private final List<Chat> chatList = new ArrayList<>();
    private String isChatOn = "1";
    private String bookingId = "";
    private String bookingStatus = "";
    private String bookingType = "";
    private boolean isFromMatchDetail = false;
    private List<OlePlayerInfo> joinedPlayers;
    private UserInfo p1Info;
    private OlePlayerInfo p2Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(getString(R.string.chat));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            if (bundle.containsKey("from_notif")) {
                getBookingDetail(true);
            }
            else {
                bookingStatus = bundle.getString("booking_status", "");
                bookingType = bundle.getString("booking_type", "");
                isFromMatchDetail = bundle.getBoolean("is_match_detail", false);
                Gson gson = new Gson();
                if (bundle.containsKey("join_players")) {
                    joinedPlayers = gson.fromJson(bundle.getString("join_players", ""), new TypeToken<List<OlePlayerInfo>>() {
                    }.getType());
                }
                if (bundle.containsKey("p1_info")) {
                    p1Info = gson.fromJson(bundle.getString("p1_info", ""), UserInfo.class);
                }
                if (bundle.containsKey("p2_info")) {
                    p2Info = gson.fromJson(bundle.getString("p2_info", ""), OlePlayerInfo.class);
                }
            }
        }

        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chatOnOffAPI(true, "1");
                }
                else {
                    chatOnOffAPI(true, "0");
                }
            }
        });

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OleChatAdapter(this, chatList);
        adapter.setItemClickListener(new OleChatAdapter.ItemClickListener() {
            @Override
            public void btnErrorClicked(View view, int section, int pos) {
                Message message = chatList.get(section).getMessages().get(pos);
                if (message != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            message.setMsgStatus(0);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    sendMessageAPI(message.getMessage(), message.getMsgId());
                }
            }
        });
        binding.recyclerVu.setAdapter(adapter);

        binding.titleBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backClicked();
            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendClicked();
            }
        });

        populateData();
    }

    private void populateData() {
        if (isFromMatchDetail) {
            binding.btnSwitch.setVisibility(View.GONE);
        }

        if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
            binding.tvRestrict.setText(getString(R.string.game_creator_can_send));
            binding.picturesVu.setVisibility(View.VISIBLE);
            binding.playersVu.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            layoutManager.setStackFromEnd(true);
            binding.picturesVu.setLayoutManager(layoutManager);
            if (binding.picturesVu.getItemDecorationCount() == 0) {
                binding.picturesVu.addItemDecoration(new OleOverlapDecoration((int) getResources().getDimension(R.dimen._minus10sdp)));
            }
            binding.picturesVu.setHasFixedSize(true);
            OleGamePictureAdapter adapter = new OleGamePictureAdapter(this, joinedPlayers);
            binding.picturesVu.setAdapter(adapter);
        }
        else {
            binding.tvRestrict.setText(getString(R.string.match_creator_can_send));
            binding.picturesVu.setVisibility(View.GONE);
            binding.playersVu.setVisibility(View.VISIBLE);

            populatePlayerData();
        }

        updateChatStatus();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive_new_msg"));
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("receive_new_msg")) {
                Bundle bundle = intent.getExtras();
                String bookId = bundle.getString("booking_id", "");
                String recId = bundle.getString("receiver_id", "");
                if (bookId.equalsIgnoreCase(bookingId) && recId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    String senderId = bundle.getString("sender_id", "");
                    String date = bundle.getString("date", "");
                    String msgId = bundle.getString("message_id", "");
                    String message = bundle.getString("message_text", "");
                    String senderName = bundle.getString("sender_name", "");
                    String senderPhoto = bundle.getString("sender_photo", "");
                    String senderRanking = bundle.getString("sender_ranking", "");
                    String teamName = bundle.getString("team_name", "");
                    saveMessage(msgId, message, date, senderId, senderName, senderPhoto, senderRanking, teamName, true);
                }
            }
        }
    };

    private void loadData() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Chat> chatRealmList = realm.where(Chat.class).equalTo("bookingId", bookingId).findAll();
        chatList.clear();
        chatList.addAll(realm.copyFromRealm(chatRealmList));
        if (chatList.size() > 0) {
            reloadRecyclerVuData(true);
            getMessagesAPI(false);
        }
        else {
            getMessagesAPI(true);
        }
    }

    private void reloadRecyclerVuData(boolean isScrollBtm) {

        adapter.notifyDataSetChanged();

        if (isScrollBtm) {
            if (chatList.size()>0) {
                binding.recyclerVu.scrollToPosition(adapter.getItemCount()-1);
            }
        }
    }

    private void updateChatStatus() {
        if (bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking)) {
            binding.tvRestrict.setVisibility(View.VISIBLE);
            if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                binding.tvRestrict.setText(R.string.game_finished);
            }
            else {
                binding.tvRestrict.setText(R.string.match_finished);
            }
            binding.btnSwitch.setVisibility(View.GONE);
            return;
        }
        if (isFromMatchDetail) {
            if (isChatOn.equalsIgnoreCase("1")) {
                binding.tvRestrict.setVisibility(View.GONE);
            }
            else {
                binding.tvRestrict.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.btnSwitch.setChecked(isChatOn.equalsIgnoreCase("1"));
            binding.tvRestrict.setVisibility(View.GONE);
        }
    }

    private void updateMsgStatus(String localMsgId, String msgId, int status) {
        Realm realm = Realm.getDefaultInstance();
        Message message = realm.where(Message.class).equalTo("msgId", localMsgId).findFirst();
        if (message != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (!msgId.isEmpty()) {
                        message.setMsgId(msgId);
                    }
                    message.setMsgStatus(status);
                    // first message was not updating that's why write this
                    for (Chat chat: chatList) {
                        if (chat.getMessages().size() == 1) {
                            chat.getMessages().get(0).setMsgId(msgId);
                            chat.getMessages().get(0).setMsgStatus(status);
                        }
                    }
                    ////////////////////////
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void populatePlayerData() {
        if (p1Info != null) {
            if (p1Info.getPhotoUrl().equalsIgnoreCase("")) {
                binding.p1ImgVu.setImageResource(R.drawable.player_active);
                binding.p1ImgVu.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else {
                Glide.with(this).load(p1Info.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1ImgVu);
                binding.p1ImgVu.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            if (p1Info.getLevel() != null && !p1Info.getLevel().isEmpty() && !p1Info.getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvP1Rank.setVisibility(View.VISIBLE);
                binding.tvP1Rank.setText(String.format("LV: %s", p1Info.getLevel().getValue()));
            }
            else {
                binding.tvP1Rank.setVisibility(View.INVISIBLE);
            }
            binding.tvP1Name.setText(p1Info.getNickName());
            binding.tvP1Played.setText(getString(R.string.place_times_played, p1Info.getMatchPlayed()));
        }

        if (p2Info != null) {
            if (p2Info.getPhotoUrl().equalsIgnoreCase("")) {
                binding.p2ImgVu.setImageResource(R.drawable.player_active);
                binding.p2ImgVu.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else {
                Glide.with(this).load(p2Info.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p2ImgVu);
                binding.p2ImgVu.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            if (p2Info.getLevel() != null && !p2Info.getLevel().isEmpty() && !p2Info.getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvP2Rank.setVisibility(View.VISIBLE);
                binding.tvP2Rank.setText(String.format("LV: %s", p2Info.getLevel().getValue()));
            }
            else {
                binding.tvP2Rank.setVisibility(View.INVISIBLE);
            }
            binding.tvP2Name.setText(p2Info.getNickName());
            binding.tvP2Played.setText(getString(R.string.place_times_played, p2Info.getMatchPlayed()));
        }
    }

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    private void sendClicked() {
        if (binding.etMsg.getText().toString().trim().equalsIgnoreCase("")) {
            return;
        }
        String dateStr = getDateStr(new Date());
        String localMsgId = String.valueOf(System.currentTimeMillis());
        Realm realm = Realm.getDefaultInstance();
        Chat chat = realm.where(Chat.class).equalTo("bookingId", bookingId).and().equalTo("date", dateStr).findFirst();
        if (chat != null) {
            realm.beginTransaction();
            Message message = realm.createObject(Message.class);
            message.setMsgId(localMsgId);
            message.setMessage(binding.etMsg.getText().toString());
            message.setSenderId(Functions.getPrefValue(getContext(), Constants.kUserID));
            message.setSenderName("");
            message.setSenderImage("");
            message.setGameRanking("");
            message.setTeamName("");
            message.setMsgStatus(0);
            message.setChat(chat);
            chat.getMessages().add(message);
            realm.commitTransaction();
            for (Chat data : chatList) {
                if (data.getDate().equalsIgnoreCase(dateStr)) {
                    data.getMessages().add(message);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        else {
            realm.beginTransaction();
            Chat chatData = realm.createObject(Chat.class);
            chatData.setChatId(String.valueOf(System.currentTimeMillis()));
            chatData.setBookingId(bookingId);
            chatData.setDate(dateStr);
            Message message = realm.createObject(Message.class);
            message.setMsgId(localMsgId);
            message.setMessage(binding.etMsg.getText().toString());
            message.setSenderId(Functions.getPrefValue(getContext(), Constants.kUserID));
            message.setSenderName("");
            message.setSenderImage("");
            message.setGameRanking("");
            message.setTeamName("");
            message.setMsgStatus(0);
            message.setChat(chatData);
            chatData.getMessages().add(message);
            realm.insert(chatData);
            realm.commitTransaction();

            RealmResults<Chat> chatRealmList = realm.where(Chat.class).equalTo("bookingId", bookingId).findAll();
            chatList.clear();
            chatList.addAll(realm.copyFromRealm(chatRealmList));
            reloadRecyclerVuData(true);
        }

        if (chatList.size()>0) {
            adapter.getItemCount();
            binding.recyclerVu.scrollToPosition(adapter.getItemCount()-1);
        }

        sendMessageAPI(binding.etMsg.getText().toString(), localMsgId);

        binding.etMsg.setText("");
    }

    private void saveMessage(String msgId, String text, String date, String senderId, String senderName, String senderPhoto, String senderRank, String teamName, boolean isNewMsg) {
        Realm realm = Realm.getDefaultInstance();
        Chat chat = realm.where(Chat.class).equalTo("bookingId", bookingId).and().equalTo("date", date).findFirst();
        if (chat != null) {
            realm.beginTransaction();
            Message message = realm.createObject(Message.class);
            message.setMsgId(msgId);
            message.setMessage(text);
            message.setSenderId(senderId);
            message.setSenderName(senderName);
            message.setSenderImage(senderPhoto);
            message.setGameRanking(senderRank);
            message.setTeamName(teamName);
            message.setMsgStatus(1);
            message.setChat(chat);
            chat.getMessages().add(message);
            realm.commitTransaction();
            if (isNewMsg) {
                for (Chat data : chatList) {
                    if (data.getDate().equalsIgnoreCase(date)) {
                        data.getMessages().add(message);
                        adapter.notifyDataSetChanged();
                        binding.recyclerVu.scrollToPosition(adapter.getItemCount()-1);
                        break;
                    }
                }
            }
        }
        else {
            realm.beginTransaction();
            Chat chatData = realm.createObject(Chat.class);
            chatData.setChatId(String.valueOf(System.currentTimeMillis()));
            chatData.setBookingId(bookingId);
            chatData.setDate(date);
            Message message = realm.createObject(Message.class);
            message.setMsgId(msgId);
            message.setMessage(text);
            message.setSenderId(senderId);
            message.setSenderName(senderName);
            message.setSenderImage(senderPhoto);
            message.setGameRanking(senderRank);
            message.setTeamName(teamName);
            message.setMsgStatus(1);
            message.setChat(chatData);
            chatData.getMessages().add(message);
            realm.insert(chatData);
            realm.commitTransaction();
            if (isNewMsg) {
                RealmResults<Chat> chatRealmList = realm.where(Chat.class).equalTo("bookingId", bookingId).findAll();
                chatList.clear();
                chatList.addAll(realm.copyFromRealm(chatRealmList));
                reloadRecyclerVuData(true);
            }
        }
    }

    private void backClicked() {
        finish();
    }

    private void getMessagesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getMessages(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            isChatOn = obj.getString("chat_status");
                            updateChatStatus();
                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<Chat> results = realm.where(Chat.class).equalTo("bookingId", bookingId).findAll();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    results.deleteAllFromRealm();
                                }
                            });
                            JSONArray arr = obj.getJSONArray("chat_list");
                            for (int i = 0; i < arr.length(); i++) {
                                String date = arr.getJSONObject(i).getString("date");
                                JSONArray arrM = arr.getJSONObject(i).getJSONArray("chat_data");
                                for (int j = 0; j < arrM.length(); j++) {
                                    JSONObject msg = arrM.getJSONObject(j);
                                    saveMessage(msg.getString("message_id"), msg.getString("message_text"), date, msg.getString("sender_id"), msg.getString("sender_name"),
                                            msg.getString("sender_photo"), msg.getString("sender_ranking"), msg.getString("team_name"), false);
                                }
                            }
                            RealmResults<Chat> chatRealmList = realm.where(Chat.class).equalTo("bookingId", bookingId).findAll();
                            chatList.clear();
                            chatList.addAll(realm.copyFromRealm(chatRealmList));
                            reloadRecyclerVuData(true);
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

    private void sendMessageAPI(String msg, String localMsgId) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendMessage(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, msg);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            String msgId = object.getJSONObject(Constants.kData).getString("id");
                            updateMsgStatus(localMsgId, msgId, 1);
                        }
                        else if (object.getInt(Constants.kStatus) == 204) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            isChatOn = "0";
                            updateChatStatus();
                            updateMsgStatus(localMsgId, "", 2);
                        }
                        else {
                            updateMsgStatus(localMsgId, "", 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateMsgStatus(localMsgId, "", 2);
                    }
                }
                else {
                    updateMsgStatus(localMsgId, "", 2);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateMsgStatus(localMsgId, "", 2);
            }
        });
    }

    private void chatOnOffAPI(boolean isLoader, String flag) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.chatOnOff(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, flag);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            isChatOn = flag;
                            updateChatStatus();
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

    private void getBookingDetail(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerBookingDetail(Functions.getAppLang(getContext()), bookingId, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            OlePlayerBookingList bookingDetail = gson.fromJson(obj.toString(), OlePlayerBookingList.class);
                            isFromMatchDetail = !bookingDetail.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID));
                            bookingStatus = bookingDetail.getBookingStatus();
                            bookingType = bookingDetail.getBookingType();
                            joinedPlayers = bookingDetail.getJoinedPlayers();
                            p1Info = bookingDetail.getCreatedBy();
                            if (bookingDetail.getJoinedPlayers().size() > 0) {
                                p2Info = joinedPlayers.get(0);
                            }
                            populateData();
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

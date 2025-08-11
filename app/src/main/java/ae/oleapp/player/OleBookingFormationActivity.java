package ae.oleapp.player;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleTeamAdapter;
import ae.oleapp.adapters.OleTeamDragItem;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityBookingFormationBinding;
import ae.oleapp.dialogs.OleCreateTeamDialogFragment;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingFormationActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityBookingFormationBinding binding;

    private String bookingId = "";
    private String bookingStatus = "";
    private OleGameTeam oleGameTeam = null;
    private final List<OlePlayerInfo> allPlayerList = new ArrayList<>();
    private final List<OlePlayerInfo> teamAList = new ArrayList<>();
    private final List<OlePlayerInfo> teamBList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityBookingFormationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.formation);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            bookingStatus = bundle.getString("booking_status", "");
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnPreview.setOnClickListener(this);
        binding.btnAddPlayer.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTeamAPI();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnEdit) {
            addEditClicked();
        }
        else if (v == binding.btnAddPlayer) {
            if (oleGameTeam != null) {
                Intent intent = new Intent(getContext(), OleManualPlayersActivity.class);
                intent.putExtra("booking_id", bookingId);
                startActivityForResult(intent, 110);
            }
        }
        else if (v == binding.btnPreview) {
            if (oleGameTeam != null) {
                Intent intent = new Intent(getContext(), OleNormalPreviewFieldActivity.class);
                intent.putExtra("booking_id", bookingId);
                intent.putExtra("team", new Gson().toJson(oleGameTeam));
                startActivity(intent);
            }
        }
    }

    private void addEditClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CreateTeamDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        OleCreateTeamDialogFragment dialogFragment = new OleCreateTeamDialogFragment(oleGameTeam, bookingId);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogCallback((df, team) -> {
            df.dismiss();
            if (oleGameTeam == null) {
                oleGameTeam = team;
            }
            else {
                oleGameTeam.setTeamAName(team.getTeamAName());
                oleGameTeam.setTeamBName(team.getTeamBName());
                oleGameTeam.setTeamAColor(team.getTeamAColor());
                oleGameTeam.setTeamBColor(team.getTeamBColor());
            }
            checkTeamCreatedOrNot();
        });
        dialogFragment.show(fragmentTransaction, "CreateTeamDialogFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 110) {
                boolean result = data.getExtras().getBoolean("add_to_game");
                if (result) {
                    checkTeamAPI();
                }
            }
        }
    }

    private void checkTeamCreatedOrNot() {
        binding.boardVu.setSnapToColumnsWhenScrolling(true);
        binding.boardVu.setSnapToColumnWhenDragging(true);
        binding.boardVu.setSnapDragItemToTouch(true);
        binding.boardVu.setSnapToColumnInLandscape(false);
        binding.boardVu.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        binding.boardVu.setBoardListener(boardListener);
        binding.boardVu.setBoardCallback(boardCallback);

        binding.tvTeamA.setText(oleGameTeam.getTeamAName());
        binding.tvTeamB.setText(oleGameTeam.getTeamBName());
        binding.vuColorA.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamAColor()));
        binding.vuColorB.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamBColor()));
        teamAList.clear();
        teamAList.addAll(oleGameTeam.getTeamAPlayers());
        teamBList.clear();
        teamBList.addAll(oleGameTeam.getTeamBPlayers());
        Iterator<OlePlayerInfo> iterator = allPlayerList.iterator();
        while (iterator.hasNext()) {
            OlePlayerInfo info = iterator.next();
            int index = checkPlayerExistInTeamA(info.getId());
            if (index != -1) {
                iterator.remove();
            }
            index = checkPlayerExistInTeamB(info.getId());
            if (index != -1) {
                iterator.remove();
            }
        }
        showHideTeamPlaceholder();
        setupBoard();
    }

    private void setupBoard() {
        binding.boardVu.clearBoard();
        binding.boardVu.setCustomDragItem(new OleTeamDragItem(getContext() , R.layout.oleteam_player));
        binding.boardVu.setCustomColumnDragItem(null);
        binding.boardVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.boardVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int boardWidth = binding.boardVu.getWidth();
                if (boardWidth<=0) {
                    return;
                }
                int width = boardWidth -(int)getResources().getDimension(R.dimen._10sdp);
                binding.boardVu.setColumnWidth(width/3);
                addTeamAList();
                addAllPlayerList();
                addTeamBList();
            }
        });
    }

    BoardView.BoardListener boardListener = new BoardView.BoardListenerAdapter() {
        @Override
        public void onItemChangedColumn(int oldColumn, int newColumn) {

        }

        @Override
        public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
            if (fromColumn != toColumn) {
                OlePlayerInfo olePlayerInfo = (OlePlayerInfo) binding.boardVu.getAdapter(toColumn).getItemList().get(toRow);
                if (olePlayerInfo != null) {
                    if (toColumn == 0) {
                        // team A
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), oleGameTeam.getTeamAId(), olePlayerInfo.getId(), "add");
                    } else if (toColumn == 2) {
                        // team B
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), oleGameTeam.getTeamBId(), olePlayerInfo.getId(), "add");
                    } else if (toColumn == 1) {
                        // no team
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), "", olePlayerInfo.getId(), "remove");
                    }
                }
                showHideTeamPlaceholder();
            }
        }
    };

    BoardView.BoardCallback boardCallback = new BoardView.BoardCallback() {
        @Override
        public boolean canDragItemAtPosition(int column, int row) {
            return !bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking);
        }

        @Override
        public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
            return !bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking);
        }

        @Override
        public boolean canDragColumnAtPosition(int index) { //any issue check these
            return false;
        }

        @Override
        public boolean canDropColumnAtPosition(int oldIndex, int newIndex) { //any issue check these
            return false;
        }
    };

    private void addAllPlayerList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), allPlayerList, R.layout.oleteam_player, R.id.item_layout, true, 1);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamAList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamAList, R.layout.oleteam_player, R.id.item_layout, true, 0);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamBList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamBList, R.layout.oleteam_player, R.id.item_layout, true, 2);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    OleTeamAdapter.OnItemClickListener teamItemClickListener = new OleTeamAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int pos, int columnIndex) {
            if (bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking)) {
                return;
            }
            if (columnIndex == 1) { // all player
                removeFromGame(pos);
            }
        }
    };

    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < teamAList.size(); i++) {
            if (teamAList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int checkPlayerExistInTeamB(String id) {
        int result = -1;
        for (int i = 0; i < teamBList.size(); i++) {
            if (teamBList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void showHideTeamPlaceholder() {
        if (teamAList.size() > 0) {
            binding.teamAPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamAPlace.setVisibility(View.VISIBLE);
        }
        if (teamBList.size() > 0) {
            binding.teamBPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamBPlace.setVisibility(View.VISIBLE);
        }
    }

    private void removeFromGame(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.remove_from_game))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            removePlayerFromGame(true, allPlayerList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    private void checkTeamAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkTeamExist(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            JSONArray array = obj.getJSONArray("booking_players");
                            allPlayerList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < array.length(); i++) {
                                allPlayerList.add(gson.fromJson(array.get(i).toString(), OlePlayerInfo.class));
                            }
                            oleGameTeam = gson.fromJson(obj.getJSONObject("teams_data").toString(), OleGameTeam.class);
                            checkTeamCreatedOrNot();
                        }
                        else {
                            addEditClicked();
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

    private void removePlayerFromGame(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removePlayerFromBooking(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            allPlayerList.remove(pos);
                            binding.boardVu.getAdapter(1).notifyDataSetChanged();
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

    private void addRemovePlayerToTeam(boolean isLoader, String teamAId, String teamBId, String targetTeamId, String playerId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.friendsToTeams(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), teamAId, teamBId, targetTeamId, playerId, type);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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
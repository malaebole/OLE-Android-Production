package ae.oleapp.activities;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.adapters.ContactsGridAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddFromContactsBinding;
import ae.oleapp.fragments.FriendRequestMessageDialogFragment;
import ae.oleapp.models.ContactFetcher;
import ae.oleapp.models.ContactList;
import ae.oleapp.models.ContactPlayers;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFromContactsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddFromContactsBinding binding;
    private final List<ContactList> contactList = new ArrayList<>();
    private final List<ContactPlayers> contactPlayers = new ArrayList<>();
    private ContactsGridAdapter adapter;
    private String formattedContactList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFromContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        checkPermission();

        phoneBookPlayer(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(gridLayoutManager);
        adapter = new ContactsGridAdapter(getContext(), contactPlayers, false);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.backBtn.setOnClickListener(this);
        binding.searchVu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPlayers(newText);
                return true;
            }
        });

    }

    ContactsGridAdapter.ItemClickListener itemClickListener = new ContactsGridAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            UserInfo info = Functions.getUserinfo(getContext());
            showMessageDialog(contactPlayers.get(pos).getId(),info.getNickName());


        }
    };

    protected void showMessageDialog(String id, String name) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FriendRequestMessageDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FriendRequestMessageDialogFragment dialogFragment = new FriendRequestMessageDialogFragment(name);
        dialogFragment.setDialogCallback((df, message) -> {
            df.dismiss();
            if (!message.isEmpty()){
                linkPlayersAPI(true,id,message);
            }

        });
        dialogFragment.show(fragmentTransaction, "FriendRequestMessageDialogFragment");
    }

    private String fetchContactsFromDevice() {
        return ContactFetcher.fetchContactList(this);
    }

    private void parseContactsAndPopulateList(String formattedContactList) {
        String[] contacts = formattedContactList.split(",");
        for (String contact : contacts) {
            // Assuming ContactList is your custom data model to hold contact information
            ContactList contactItem = new ContactList();
            contactItem.setPhoneNumber(contact);
            contactList.add(contactItem);

        }
    }

    private void checkPermission() {

        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        }else {
            permissions = new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                formattedContactList = fetchContactsFromDevice();
                parseContactsAndPopulateList(formattedContactList);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == binding.backBtn){
            this.finish();
        }

    }

    private void phoneBookPlayer(boolean isLoader) {
        if (formattedContactList.isEmpty()){
            return;
        }
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.phoneBookPlayer(Functions.getAppLang(getContext()),formattedContactList);
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
                            contactPlayers.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                contactPlayers.add(gson.fromJson(arr.get(i).toString(), ContactPlayers.class));
                            }
                        }
                        adapter.notifyDataSetChanged();
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

    private void linkPlayersAPI(boolean isLoader, String ids,String msg) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.linkPlayers(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID),msg, ids);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            setResult(456, intent);
                            //finish();
                            refreshList(ids);
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

    private void refreshList(String ids) {
        //phoneBookPlayer(false);
        Iterator<ContactPlayers> iterator = contactPlayers.iterator();
        while (iterator.hasNext()) {
            ContactPlayers player = iterator.next();
            if (ids.contains(player.getId())) {
                iterator.remove();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterPlayers(String query) {
        List<ContactPlayers> filteredPlayers = new ArrayList<>();
        for (ContactPlayers player : contactPlayers) {
            if (player.getNickName().toLowerCase().contains(query.toLowerCase()) ||
                    player.getPhone().contains(query)) {
                filteredPlayers.add(player);
            }
        }
        adapter.setFilteredList(filteredPlayers,true);
    }

}
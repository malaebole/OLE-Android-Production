package ae.oleapp.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityOleFinanceBinding;
import ae.oleapp.dialogs.SwapPlayerDialogFragment;
import ae.oleapp.fragments.FinanceAddFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.fragments.OleBankHomeFragment;
import ae.oleapp.fragments.OleEmployeeHomeFragment;
import ae.oleapp.fragments.OleFinanceHomeFragment;
import ae.oleapp.fragments.OlePartnerHomeFragment;


public class OleFinanceActivity extends BaseActivity {

    private  ActivityOleFinanceBinding binding;
    private String clubId = "";
    private final List<Fragment> list = new ArrayList<>();

    private final OleFinanceHomeFragment oleFinanceHomeFragment = new OleFinanceHomeFragment();
    private final OleEmployeeHomeFragment oleEmployeeHomeFragment = new OleEmployeeHomeFragment();
    private final OlePartnerHomeFragment olePartnerHomeFragment = new OlePartnerHomeFragment();
    private final OleBankHomeFragment oleBankHomeFragment = new OleBankHomeFragment();


    private int selectedTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOleFinanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            clubId = bundle.getString("club","");
        }

        setupTabLayout();
        binding.contentMainLay.contentMain.tabBar.setSelectTab(0);
        binding.contentMainLay.contentMain.viewPager.setCurrentItem(0);

    }

    public Bundle getClubId() {
        Bundle bundle = new Bundle();
        bundle.putString("club_id",clubId);
        return bundle;
    }



    private void setupTabLayout() {
        binding.contentMainLay.contentMain.tabBar.setTitles(getString(R.string.dashboard), getString(R.string.employee), getString(R.string.partner), getString(R.string.bank))
                .setNormalIcons(R.drawable.finance_dashboard, R.drawable.employee_icon, R.drawable.partner_icon, R.drawable.bank_icon).generate();
        binding.contentMainLay.contentMain.tabBar.setNormalColor(getResources().getColor(R.color.separatorColor));
        binding.contentMainLay.contentMain.tabBar.setSelectedColor(getResources().getColor(R.color.blueColorNew));
        binding.contentMainLay.contentMain.tabBar.setIconSize(17);
        binding.contentMainLay.contentMain.tabBar.setTabTextSize(12);
        list.add(oleFinanceHomeFragment); //Finance Home
        list.add(oleEmployeeHomeFragment); //Employee
        list.add(olePartnerHomeFragment); //Partners
        list.add(oleBankHomeFragment); //Bank
        binding.contentMainLay.contentMain.tabBar.setGradientEnable(false);
        binding.contentMainLay.contentMain.tabBar.setPageAnimateEnable(true);

        binding.contentMainLay.contentMain.viewPager.setAdapter(new OleFinanceActivity.PagerAdapter(getSupportFragmentManager(), list));
        binding.contentMainLay.contentMain.tabBar.setContainer(binding.contentMainLay.contentMain.viewPager);

        binding.contentMainLay.contentMain.tabBar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int index) {
                selectedTabIndex = index;
                //devicesLoginLimit();
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }
        });
        if(binding.contentMainLay.contentMain.tabBar.getMiddleView() != null)
            binding.contentMainLay.contentMain.tabBar.getMiddleView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddtypeDialog();
                }
            });
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> list;

        public PagerAdapter(FragmentManager supportFragmentManager, List<Fragment> list) {
            super(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }
    }

    protected void showAddtypeDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FinanceAddFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FinanceAddFragment dialogFragment = new FinanceAddFragment();

        dialogFragment.setDialogCallback(new FinanceAddFragment.ResultDialogCallback() {

            @Override
            public void addincome(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), AddIncomeActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }

            @Override
            public void addExpense(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), AddExpenseActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }

            @Override
            public void addUpcomingExpense(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), AddUpcomingExpenseActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }

            @Override
            public void paySalary(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), PaySalaryActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }

            @Override
            public void payToPartner(DialogFragment df) {
                df.dismiss();
                Intent intent = new Intent(getContext(), PayToPartnerActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }
        });



        dialogFragment.show(fragmentTransaction, "FinanceAddFragment");
    }


}
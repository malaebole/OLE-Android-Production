package ae.oleapp.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.util.OleCustomTabView;

public class OleMyPagerAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final int[] tabIcons = {R.drawable.home_tab, R.drawable.booking_tab, R.drawable.match_tab, R.drawable.shop_tab, R.drawable.fav_tab};
    private final int[] tabText = {R.string.clubs, R.string.bookings, R.string.matches, R.string.shop, R.string.favorites};

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public OleMyPagerAdapter(Context context, FragmentManager manager) {
        super(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public OleCustomTabView getTabView(int position) {
        OleCustomTabView tabView = new OleCustomTabView(context);
        tabView.tvTitle.setText(tabText[position]);
        tabView.iconVu.setImageResource(tabIcons[position]);
        return tabView;
    }
}

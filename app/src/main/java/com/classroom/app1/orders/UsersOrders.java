package com.classroom.app1.orders;

import android.os.Bundle;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.classroom.app1.Helpers.BaseActivity;
import com.classroom.app1.R;
import com.classroom.app1.UI.Fragements.InProcessOrders;
import com.classroom.app1.UI.Fragements.ShippedOrders;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersOrders extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private androidx.viewpager.widget.ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_orders);

        //showProgressDialog();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Your Orders");
        setSupportActionBar(toolbar);



        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void setSupportActionBar(Toolbar toolbar) {
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.instantiateItem(new InProcessOrders(), "In Process");
        adapter.addFragment(new ShippedOrders(), "Shipped");
        //adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(ShippedOrders fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void instantiateItem(InProcessOrders inProcessOrders, String in_process) {
        }
    }

}

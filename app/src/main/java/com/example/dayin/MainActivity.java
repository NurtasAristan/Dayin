package com.example.dayin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private final String[] tabs_titles = {"Істер", "GPT-жоспар", "Күнтізбе"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setAdapter(new ViewPagerAdapter(this));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabs_titles[position]));
        tabLayoutMediator.attach();
    }

    public void addNewProject(View view) {
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(intent);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new ProjectsFragment();
            } else if (position ==1) {
                return new MethodsFragment();
            } else {
                return new CalendarFragment();
            }

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
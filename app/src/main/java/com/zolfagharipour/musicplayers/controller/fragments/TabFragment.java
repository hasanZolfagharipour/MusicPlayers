package com.zolfagharipour.musicplayers.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zolfagharipour.musicplayers.R;
import com.zolfagharipour.musicplayers.adapter.TabViewPagerAdapter;
import com.zolfagharipour.musicplayers.controller.activity.MusicPlayersActivity;
import com.zolfagharipour.musicplayers.repository.MusicRepository;
import com.zolfagharipour.musicplayers.utils.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


public class TabFragment extends Fragment {

    public static final String TAG = "tag";
    private MusicRepository mRepository;
    private Toolbar mToolbar;
    private ViewPager2 mViewPager;
    private TabViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private OnMusicMenuItemSelectedListener mSelectedListener;

    public static TabFragment newInstance() {

        Bundle args = new Bundle();

        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = MusicRepository.getInstance();
        mRepository.setSongList(MusicManager.getSongList(getActivity().getApplicationContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        findViews(view);
        setToolbar();
        setViewPager();
        setTabLayout();
        setListener();

        return view;
    }


    private void findViews(View view) {
        mToolbar = view.findViewById(R.id.listMenuToolbar);
        mViewPager = view.findViewById(R.id.viewPagerTabs);
        mTabLayout = view.findViewById(R.id.tabLayout);
    }

    private void setToolbar() {
        setHasOptionsMenu(true);
        ((MusicPlayersActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    private void setTabLayout() {

        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    default:
                        tab.setText(getString(R.string.tracks));
                        break;
                    case 1:
                        tab.setText(getString(R.string.albums));
                        break;
                    case 2:
                        tab.setText(getString(R.string.playlist));
                }
            }
        }).attach();
    }

    private void setViewPager() {
        mPagerAdapter = new TabViewPagerAdapter(getActivity());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void setListener(){
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                for (int i = 0; i < getActivity().getSupportFragmentManager().getFragments().size(); i++) {
                    if (getActivity().getSupportFragmentManager().getFragments().get(i) instanceof PlayListTabFragment)
                        ((PlayListTabFragment)getActivity().getSupportFragmentManager().getFragments().get(i)).setAdapter();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.music_tab_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.tabMenuRefresh:
                mRepository.setSongList(MusicManager.getSongList(getActivity().getApplicationContext()));
                Log.d(TAG, "MusicManager.getSongList(getActivity()).size(): " + MusicManager.getSongList(getActivity().getApplicationContext()).size());
                Log.d(TAG, "mRepository.getSongList().size(): " + mRepository.getSongList().size());
                mSelectedListener.onRefreshMusicList();
                return true;
            case R.id.tabMenuExitApp:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public interface OnMusicMenuItemSelectedListener {
        void onRefreshMusicList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMusicMenuItemSelectedListener)
            mSelectedListener = (OnMusicMenuItemSelectedListener) context;

    }
}
package com.parsroyal.solutiontablet.ui.adapter;


import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class CustomerDetailViewPagerAdapter extends FragmentStatePagerAdapter{
  private ArrayList<String> titles = new ArrayList<>();
  private ArrayList<Fragment> fragments = new ArrayList<>();

  public CustomerDetailViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void add(Fragment fragment, String title) {
    titles.add(title);
    fragments.add(fragment);
  }

  @Override public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override public int getCount() {
    return fragments.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return titles.get(position);
  }

  @Override
  public Parcelable saveState() {
    super.saveState();
    return null;
  }
}

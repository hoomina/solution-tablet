package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;

/**
 * Created by Mahyar on 8/4/2015.
 */
public class AboutUsFragment extends BaseFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
//TODO: Old code, need new style
    return inflater.inflate(R.layout.fragment_about_us, null);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ABOUT_US_FRAGMENT_ID;
  }
}

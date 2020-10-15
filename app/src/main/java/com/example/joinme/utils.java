package com.example.joinme;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class utils {
    public static int defaultContainer=R.id.main_fragment_container;
    public static void replaceFragment(FragmentManager fm, Fragment fragment,String Tag){
        Fragment frag = fm.findFragmentByTag(Tag);
        if(frag==null) frag = fragment;
        fm.beginTransaction().replace(defaultContainer,frag,Tag).addToBackStack(null).commit();
    }
}

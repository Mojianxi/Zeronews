package com.fanxi.zeronews.activity.fragment;

import java.util.HashMap;

import android.support.v4.app.Fragment;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.util.UiUtils;
public class FragmentFactory {
    static String title;
    static String[] titleArrays= UiUtils.getResources().getStringArray(R.array.titles);
    static String[] titleArraysId= UiUtils.getResources().getStringArray(R.array.titlesId);
    private static HashMap<Integer, Fragment> hashMap=new HashMap<Integer, Fragment>();
    public static BaseFragment createFragment(int position){
//        NewsFragment newsFragment=null;
//        ReaderFragment readerFragment=null;
    	BaseFragment newsFragment=null;
        if(hashMap.containsKey(position)){
           if(position!=2){
        	   newsFragment=(NewsFragment) hashMap.get(position);
           }else{
        	   newsFragment=(ReaderFragment) hashMap.get(position);
           }
        }else{
               if(position!=2){
            	   newsFragment=new NewsFragment();
                   newsFragment.setTitle(titleArrays[position]);
                   newsFragment.setTitleId(titleArraysId[position]);
                   if(position==0){
                       newsFragment.initData();
                   }
                }else{
                	newsFragment=new ReaderFragment();
                }
                hashMap.put(position, newsFragment);
        }
        return newsFragment;
    }
    public static void clear(){
		hashMap.clear();
	}
}

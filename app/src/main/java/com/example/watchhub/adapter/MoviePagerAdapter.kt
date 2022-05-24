package com.example.watchhub.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.watchhub.fragment.ActionFragment
import com.example.watchhub.fragment.TvShowsFragment

class MoviePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


//    override fun getCount(): Int {
//        return 3
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> ActionFragment()
//            1 -> TvShowsFragment()
//            else -> AnimeFragment()
//        }
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return when(position){
//            0 -> "Action"
//            1 -> "Tv Shows"
//            else -> "Anime"
//        }
//    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ActionFragment()
            else -> TvShowsFragment()
        }
    }

}
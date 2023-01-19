package com.peterstev.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peterstev.fragment.LaterFragment
import com.peterstev.fragment.TodayFragment
import com.peterstev.fragment.TomorrowFragment

class FragmentPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFragment()
            1 -> TomorrowFragment()
            else -> LaterFragment()
        }
    }
}

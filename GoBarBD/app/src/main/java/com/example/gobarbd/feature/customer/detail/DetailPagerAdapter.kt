package com.example.gobarbd.feature.customer.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailPagerAdapter(
    activity: FragmentActivity,
    private val shopId: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailAboutFragment.newInstance(shopId)
            1 -> DetailServiceFragment.newInstance(shopId)
            2 -> DetailScheduleFragment.newInstance(shopId)
            else -> DetailReviewFragment.newInstance(shopId)
        }
    }
}

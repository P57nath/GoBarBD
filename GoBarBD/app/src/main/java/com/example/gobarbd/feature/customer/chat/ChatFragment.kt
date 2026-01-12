package com.example.gobarbd.feature.customer.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gobarbd.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutChat)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerChat)
        viewPager.adapter = ChatPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Active Chat" else "Finished"
        }.attach()

        return view
    }
}

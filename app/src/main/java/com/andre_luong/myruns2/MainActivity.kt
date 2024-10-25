package com.andre_luong.myruns2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAB_NAMES = listOf("Start", "History", "Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for camera permissions
        Utils.checkPermissions(this)

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        val fragments = ArrayList<Fragment>()
        fragments.add(StartFragment())
        fragments.add(HistoryFragment())
        fragments.add(SettingsFragment())

        viewPager2.adapter = FragmentStateAdapter(this, fragments)

        // Set the title of each tab
        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            tab.setText(TAB_NAMES[position])
        }

        // Synchronizes the ViewPager and TabLayout to change the position when a tab gets clicked or swiped
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }
}

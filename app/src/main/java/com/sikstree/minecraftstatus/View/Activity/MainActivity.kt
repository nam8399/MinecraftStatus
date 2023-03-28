package com.sikstree.minecraftstatus.View.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.sikstree.minecraftstatus.databinding.ActivityMainBinding
import com.sikstree.minecraftstatus.R
import com.sikstree.minecraftstatus.View.Fragment.UserFragment
import com.sikstree.minecraftstatus.View.Fragment.HomeFragment
import com.sikstree.minecraftstatus.View.Fragment.ServerListFragment
import com.sikstree.minecraftstatus.viewModel.MainViewModel
import com.sikstree.minecraftstatus.viewModel.MainViewModel.Companion.TAG_ABOUT
import com.sikstree.minecraftstatus.viewModel.MainViewModel.Companion.TAG_HOME
import com.sikstree.minecraftstatus.viewModel.MainViewModel.Companion.TAG_SERVERLIST
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private val fragmentManager : FragmentManager = supportFragmentManager
    var backPressedTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.setFragment(TAG_HOME, HomeFragment(), fragmentManager)

        var requestConfiguration : RequestConfiguration = MobileAds.
        getRequestConfiguration().toBuilder().
            setTagForChildDirectedTreatment(
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE).
                setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G).
                build()

        // 1. 모바일광고 SDK 초기화
        MobileAds.initialize(this) {}

        MobileAds.setRequestConfiguration(requestConfiguration)
        
        // 2. 광고 띄우기\
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        onClick()
    }

    fun onClick() {
        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> viewModel.setFragment(TAG_HOME, HomeFragment(), fragmentManager)
                R.id.serverlistFragment -> viewModel.setFragment(TAG_SERVERLIST, ServerListFragment(), fragmentManager)
                R.id.aboutFragment-> viewModel.setFragment(TAG_ABOUT, UserFragment(), fragmentManager)
            }
            true
        }

    }


    override fun onBackPressed() {
        //2.5초이내에 한 번 더 뒤로가기 클릭 시
        if (System.currentTimeMillis() - backPressedTime < 2500) {
            super.onBackPressed()
            return
        }
        Toast.makeText(this, "한번 더 클릭 시 홈으로 이동됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }


}
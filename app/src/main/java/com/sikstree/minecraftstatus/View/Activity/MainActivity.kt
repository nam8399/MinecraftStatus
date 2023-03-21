package com.sikstree.minecraftstatus.View.Activity

import android.os.Bundle
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


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private val fragmentManager : FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.setFragment(TAG_HOME, HomeFragment(), fragmentManager)

        // 1. 모바일광고 SDK 초기화
        MobileAds.initialize(this) {}

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





}
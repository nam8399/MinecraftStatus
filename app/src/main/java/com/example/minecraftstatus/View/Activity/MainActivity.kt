package com.example.minecraftstatus.View.Activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Fragment.UserFragment
import com.example.minecraftstatus.View.Fragment.HomeFragment
import com.example.minecraftstatus.View.Fragment.ServerListFragment
import com.example.minecraftstatus.databinding.ActivityMainBinding
import com.example.minecraftstatus.viewModel.MainViewModel
import com.example.minecraftstatus.viewModel.MainViewModel.Companion.TAG_ABOUT
import com.example.minecraftstatus.viewModel.MainViewModel.Companion.TAG_HOME
import com.example.minecraftstatus.viewModel.MainViewModel.Companion.TAG_SERVERLIST


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
package com.example.minecraftstatus.View.Fragment

import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.minecraftstatus.Model.EventObserver
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Activity.MainActivity
import com.example.minecraftstatus.viewModel.HomeViewModel
import com.example.minecraftstatus.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val title = "HomeFragment"

class HomeFragment() : Fragment() {
    lateinit var binding : FragmentHomeBinding
    var isSeverAdd : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        isSeverAdd = viewModel.isServerAdd.value.toString().equals("true")

        if (isSeverAdd) {
            binding.serverStatusOff.visibility = View.GONE
            binding.serverStatusOn.visibility = View.VISIBLE
        }

        binding.serverEditionSpinner.adapter = ArrayAdapter.createFromResource(activity as MainActivity, R.array.editionList, android.R.layout.simple_spinner_dropdown_item)

        observerServerStatus()
    }

    fun observerServerStatus() {
        binding.viewModel?.apply {
            event.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    binding.serverStatusOff.visibility = View.GONE
                    binding.serverStatusOn.visibility = View.VISIBLE
                } else {
                    binding.serverStatusOff.visibility = View.VISIBLE
                    binding.serverStatusOn.visibility = View.GONE
                }

            })

        }
    }

}
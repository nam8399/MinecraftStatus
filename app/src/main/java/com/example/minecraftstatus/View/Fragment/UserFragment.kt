package com.example.minecraftstatus.View.Fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minecraftstatus.Model.EventObserver
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Activity.MainActivity
import com.example.minecraftstatus.View.Dialog.CustomLoadingDialog
import com.example.minecraftstatus.databinding.FragmentUserBinding
import com.example.minecraftstatus.viewModel.HomeViewModel
import com.example.minecraftstatus.viewModel.UserViewModel

class UserFragment : Fragment() {
    lateinit var binding : FragmentUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.editId.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSearch.performClick()
                handled = true
            }
            handled
        }

        observerUserStatus()
    }

    fun observerUserStatus() {
        val loadingAnimDialog = CustomLoadingDialog(activity as MainActivity)
        binding.viewModel?.apply {
            event.observe(viewLifecycleOwner, EventObserver {
                Glide.with(activity as MainActivity).load("https://crafatar.com/avatars/" + it).into(binding.userSkin)
                Glide.with(activity as MainActivity).load("https://crafatar.com/renders/body/" + it).into(binding.skinBodyImage)
            })

            showDialog.observe(viewLifecycleOwner, Observer { // showDialog 변수를 observing 하면서 다이얼로그 show 및 dismiss
                if (it) {
                    loadingAnimDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    loadingAnimDialog.show()
                } else {
                    loadingAnimDialog.dismiss()
                }
            })

        }
    }

}
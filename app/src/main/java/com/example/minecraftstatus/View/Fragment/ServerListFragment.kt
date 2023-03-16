package com.example.minecraftstatus.View.Fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minecraftstatus.Data.ServerItem
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Activity.MainActivity
import com.example.minecraftstatus.View.Adapter.BoardAdapter
import com.example.minecraftstatus.View.Dialog.CustomLoadingDialog
import com.example.minecraftstatus.databinding.FragmentServerListBinding
import com.example.minecraftstatus.viewModel.ServerListViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val title = "ServerListFragment"
class ServerListFragment() : Fragment() {
    lateinit var binding : FragmentServerListBinding
    var isSeverAdd : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_server_list, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        observerServerStatus()

    }

    private fun initView() {
        val viewModel = ViewModelProvider(this).get(ServerListViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getMineacraftServer()



        binding.serverEditionSpinner.adapter = ArrayAdapter.createFromResource(
            activity as MainActivity,
            R.array.editionList,
            android.R.layout.simple_spinner_dropdown_item
        )

    }



    fun observerServerStatus() {
        val loadingAnimDialog = CustomLoadingDialog(activity as MainActivity)
        val itemList = ArrayList<ServerItem>()
        binding.viewModel?.apply {
            showDialog.observe(viewLifecycleOwner, Observer { // showDialog 변수를 observing 하면서 다이얼로그 show 및 dismiss
                if (it) {
                    loadingAnimDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    loadingAnimDialog.show()
                } else {
                    loadingAnimDialog.dismiss()
                }
            })
            serverList.observe(viewLifecycleOwner, Observer {
                itemList.add(it)

                val boardAdapter = BoardAdapter(itemList)
                boardAdapter.notifyDataSetChanged()

                binding.rvBoard.adapter = boardAdapter
                binding.rvBoard.layoutManager = LinearLayoutManager(activity as MainActivity, LinearLayoutManager.VERTICAL, false)

            })
        }

    }

}
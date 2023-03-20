package com.example.minecraftstatus.View.Fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minecraftstatus.Data.MyApplication
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
    val itemList = ArrayList<ServerItem>()

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
        if (!"".equals(MyApplication.prefs.getString("slot1", ""))) {
            viewModel.getMinecraftServer("저장슬롯 1",MyApplication.prefs.getString("slot1",""))
        }
        if (!"".equals(MyApplication.prefs.getString("slot2", ""))) {
            viewModel.getMinecraftServer("저장슬롯 2",MyApplication.prefs.getString("slot2", ""))
        }
        if (!"".equals(MyApplication.prefs.getString("slot3", ""))) {
            viewModel.getMinecraftServer("저장슬롯 3", MyApplication.prefs.getString("slot3", ""))
        }
        if (!"".equals(MyApplication.prefs.getString("slot4", ""))) {
            viewModel.getMinecraftServer("저장슬롯 4", MyApplication.prefs.getString("slot4", ""))
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.serverEditionSpinner.adapter = ArrayAdapter.createFromResource(
            activity as MainActivity,
            R.array.editionList,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.serverSlotSpinner.adapter = ArrayAdapter.createFromResource(
            activity as MainActivity,
            R.array.slotList,
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.btnRefresh.setOnClickListener {
            itemList.clear()
            initView()
        }

    }

    fun observerServerStatus() {
        val loadingAnimDialog = CustomLoadingDialog(activity as MainActivity)
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
                val boardAdapter = BoardAdapter(itemList)

                binding.rvBoard.adapter = boardAdapter
                binding.rvBoard.layoutManager = LinearLayoutManager(activity as MainActivity, LinearLayoutManager.VERTICAL, false)

                boardAdapter.setItemClickListener(object : BoardAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int, serverhost : String) {
                        Log.d(title, serverhost)

                        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                        builder.setTitle("서버리스트 삭제")
                        builder.setMessage("해당 리스트를 삭제하시겠습니까?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        // Yes 버튼 및 이벤트 생성

                        // Yes 버튼 및 이벤트 생성
                        builder.setPositiveButton(
                            "삭제",
                            DialogInterface.OnClickListener { dialog, which ->
                                if (MyApplication.prefs.getString("slot1","").equals(serverhost)) {
                                    MyApplication.prefs.setString("slot1","")
                                } else if (MyApplication.prefs.getString("slot2","").equals(serverhost)) {
                                    MyApplication.prefs.setString("slot2","")
                                } else if (MyApplication.prefs.getString("slot3","").equals(serverhost)) {
                                    MyApplication.prefs.setString("slot3","")
                                } else if (MyApplication.prefs.getString("slot4","").equals(serverhost)) {
                                    MyApplication.prefs.setString("slot4","")
                                }
                                itemList.clear()
                                boardAdapter.notifyDataSetChanged()
                                initView()
                                Toast.makeText(activity, "리스트 삭제 완료", Toast.LENGTH_SHORT).show()
                            })
                        //Cancel 버튼 및 이벤트 생성
                        //Cancel 버튼 및 이벤트 생성
                        builder.setNeutralButton(
                            "취소",
                            DialogInterface.OnClickListener { dialog, which ->
                                //Pass
                            })

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                })

                itemList.add(it)

                boardAdapter.notifyDataSetChanged()

            })
        }

    }

}
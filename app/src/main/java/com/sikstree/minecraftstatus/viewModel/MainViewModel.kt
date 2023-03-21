package com.sikstree.minecraftstatus.viewModel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikstree.minecraftstatus.R

class MainViewModel() : ViewModel() {
    private val title = "MainViewModel"

    var serverStatus = MutableLiveData<String>()
    var serverHostTxt = MutableLiveData<String>()

    companion object {
        const val TAG_HOME = "home_fragment"
        const val TAG_SERVERLIST = "serverlist_fragment"
        const val TAG_ABOUT = "about_fragment"
    }

    init {
        serverStatus.value = ""
        serverHostTxt.value = ""

    }



    fun setFragment(tag: String, fragment: Fragment, fragmentManager: FragmentManager) {
        val manager: FragmentManager = fragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(Companion.TAG_HOME)
        val serverlist = manager.findFragmentByTag(Companion.TAG_SERVERLIST)
        val about = manager.findFragmentByTag(Companion.TAG_ABOUT)

        if (home != null){
            fragTransaction.hide(home)
        }

        if (serverlist != null){
            fragTransaction.hide(serverlist)
        }

        if (about != null) {
            fragTransaction.hide(about)
        }

        if (tag == Companion.TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == Companion.TAG_SERVERLIST) {
            if (serverlist != null) {
                fragTransaction.show(serverlist)
            }
        }

        else if (tag == Companion.TAG_ABOUT){
            if (about != null){
                fragTransaction.show(about)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }


}
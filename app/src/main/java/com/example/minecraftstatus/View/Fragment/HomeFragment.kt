package com.example.minecraftstatus.View.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.minecraftstatus.Model.EventObserver
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Activity.MainActivity
import com.example.minecraftstatus.View.Activity.WebviewActivity
import com.example.minecraftstatus.View.Adapter.ViewPager2Adater
import com.example.minecraftstatus.View.Dialog.CustomLoadingDialog
import com.example.minecraftstatus.databinding.FragmentHomeBinding
import com.example.minecraftstatus.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val title = "HomeFragment"

private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

class HomeFragment() : Fragment() {
    lateinit var binding : FragmentHomeBinding
    var isSeverAdd : Boolean = false
    lateinit var job : Job
    var bannerPosition : Int = 0

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

        initView()

        observerServerStatus()

    }

    private fun initView() { // 홈 화면 뷰 초기화
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getMineacraftServer()



        isSeverAdd = viewModel.isServerAdd.value.toString().equals("true")

        if (isSeverAdd) {
            binding.serverStatusOff.visibility = View.GONE
            binding.serverStatusOn.visibility = View.VISIBLE
        }

        binding.serverEditionSpinner.adapter = ArrayAdapter.createFromResource(
            activity as MainActivity,
            R.array.editionList,
            android.R.layout.simple_spinner_dropdown_item
        )

        var list = ArrayList<Int>()

        list.add(Color.parseColor("#ffff00"))
        list.add(Color.parseColor("#bdbdbd"))
        list.add(Color.parseColor("#0f9231"))
        var adapter = ViewPager2Adater(list,activity as MainActivity)

//        binding.viewpager2.offscreenPageLimit=3
        binding.viewpager2.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER
        binding.viewpager2.adapter = adapter

        setupOnBoardingIndicators()
        setCurrentOnboardingIndicator(0)

        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(8))

        transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
            var v = 1-Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })

        binding.viewpager2.setPageTransformer(transform)

        adapter.setItemClickListener(object : ViewPager2Adater.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val intent = Intent(context, WebviewActivity::class.java)
                if (position == 0) {
                    intent.putExtra("url","https://www.youtube.com/watch?v=n1PkmOU7H2w")
                } else if(position == 1) {
                    intent.putExtra("url", "https://www.youtube.com/watch?v=hvydITbP-YE&t=95s")
                } else if(position == 2) {
                    intent.putExtra("url", "https://www.youtube.com/watch?v=n1PkmOU7H2w&t=2s")
                }
                startActivity(intent)
            }
        })

        binding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position : Int){
                super.onPageSelected(position)
                bannerPosition = position

                setCurrentOnboardingIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE ->{
                        if (!job.isActive) scrollJobCreate()
                    }

                    ViewPager2.SCROLL_STATE_DRAGGING -> job.cancel()

                    ViewPager2.SCROLL_STATE_SETTLING -> {}
                }
            }
        })

        binding.imgMinelist.setOnClickListener {
            intentWebView("https://minelist.kr/")
        }
        binding.imgAddon.setOnClickListener {
            intentWebView("https://play.google.com/store/apps/details?id=com.kayenworks.mcpeaddons")
        }
        binding.imgSkin.setOnClickListener {
            intentWebView("https://ko.namemc.com/minecraft-skins")
        }
        binding.imgCafe.setOnClickListener {
            intentWebView("https://cafe.naver.com/minecraftgame")
        }
    }

    fun intentWebView(url : String) {
        val intent = Intent(context, WebviewActivity::class.java)
        intent.putExtra("url",url)

        startActivity(intent)
    }

    private fun setupOnBoardingIndicators(){ // 건축강의 뷰 인디게이터 구성셋팅
        val indicators =
            arrayOfNulls<ImageView>(3)

        var layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(8,0,8,0)

        for( i in indicators.indices){
            indicators[i] = ImageView(activity as MainActivity)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    activity as MainActivity,
                    R.drawable.onboarding_indicator_inactive
                ))

            indicators[i]?.layoutParams = layoutParams

            binding.indicators?.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator( index : Int){ // 건축 강의 인디게이터 뷰 이미지 셋팅
        var childCount = binding.indicators?.childCount
        for(i in  0 until childCount!!){
            var imageView = binding.indicators?.getChildAt(i) as ImageView
            if(i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(activity as MainActivity,
                    R.drawable.onboarding_indicator_active))
            }else{
                imageView.setImageDrawable(ContextCompat.getDrawable(activity as MainActivity,
                    R.drawable.onboarding_indicator_inactive))
            }
        }
    }

    fun scrollJobCreate() { // auto Scroll을 위한 함수
        job = lifecycleScope.launchWhenResumed {
            delay(2000)
            binding.viewpager2.setCurrentItem(++bannerPosition, true)
            if (bannerPosition == 2) {
                bannerPosition = -1
            }
        }
    }

    fun observerServerStatus() {
        val loadingAnimDialog = CustomLoadingDialog(activity as MainActivity)
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

    override fun onResume() {
        super.onResume()
        scrollJobCreate()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

}
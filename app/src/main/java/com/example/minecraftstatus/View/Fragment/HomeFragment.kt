package com.example.minecraftstatus.View.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.minecraftstatus.Model.EventObserver
import com.example.minecraftstatus.R
import com.example.minecraftstatus.View.Activity.MainActivity
import com.example.minecraftstatus.View.Adapter.ViewPager2Adater
import com.example.minecraftstatus.databinding.FragmentHomeBinding
import com.example.minecraftstatus.viewModel.HomeViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val title = "HomeFragment"

private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

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

        initView()

        observerServerStatus()

    }

    private fun initView() {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

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
        var adater = ViewPager2Adater(list,activity as MainActivity)

//        binding.viewpager2.offscreenPageLimit=3
        binding.viewpager2.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER
        binding.viewpager2.adapter = adater

        setupOnBoardingIndicators()
        setCurrentOnboardingIndicator(0)

        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(8))

        transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
            var v = 1-Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })

        binding.viewpager2.setPageTransformer(transform)


        binding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position : Int){
                setCurrentOnboardingIndicator(position)
            }

        })
    }

    private fun setupOnBoardingIndicators(){
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

    private fun setCurrentOnboardingIndicator( index : Int){
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
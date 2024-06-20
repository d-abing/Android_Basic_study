package com.example.fastcampusbasic.Part2.chapter11

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val homeData = context?.readData("home.json", Home::class.java) ?: return
        val menuData = context?.readData("menu.json", Menu::class.java) ?: return

        initAppBar(homeData)
        initRecommendMenuList(homeData, menuData)
        initBanner(homeData)
        initFloatingActionButton()
    }

    private fun initAppBar(homeData: Home) {
        binding.appbarTitleTv.text = getString(R.string.appbar_title_text, homeData.user.nickname)
        binding.starCountTv.text = getString(
            R.string.appbar_star_count_text,
            homeData.user.starCount,
            homeData.user.totalCount
        )

        binding.appbarProgress.progress = homeData.user.starCount
        binding.appbarProgress.max = homeData.user.totalCount

        Glide.with(binding.appbarIv)
            .load(homeData.appbarImage)
            .into(binding.appbarIv)

        ValueAnimator.ofInt(0, homeData.user.starCount).apply {
            duration = 1000
            addUpdateListener {
                binding.appbarProgress.progress = it.animatedValue as Int
            }
            start()
        }
    }

    private fun initFloatingActionButton() {
        binding.nestedSv.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                binding.deliveryFab.extend()
            } else {
                binding.deliveryFab.shrink()
            }
        }
    }

    private fun initRecommendMenuList(
        homeData: Home,
        menuData: Menu
    ) {
        binding.coffeeMenuList.titleTv.text =
            getString(R.string.coffee_menu_title, homeData.user.nickname)
        menuData.coffee.forEach { menuItem ->
            binding.coffeeMenuList.menuListHorizontalLl.addView(
                MenuView(context = requireContext()).apply {
                    setTitle(menuItem.name)
                    setImageUrl(menuItem.image)
                }
            )
        }

        binding.foodMenuList.titleTv.text =
            getString(R.string.food_menu_title)
        menuData.food.forEach { menuItem ->
            binding.foodMenuList.menuListHorizontalLl.addView(
                MenuView(context = requireContext()).apply {
                    setTitle(menuItem.name)
                    setImageUrl(menuItem.image)
                }
            )
        }
    }


    private fun initBanner(homeData: Home) {
        binding.bannerLayout.bannerIv.apply {
            Glide.with(binding.bannerLayout.bannerIv)
                .load(homeData.banner.image)
                .into(binding.bannerLayout.bannerIv)
            contentDescription = homeData.banner.contentDescription
        }
    }
}
package com.casecode.pos.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.casecode.pos.R
import com.casecode.pos.databinding.FragmentProfileBinding
import com.casecode.pos.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupPagerAdapter()

        return binding.root
    }

    private fun setupPagerAdapter() {
        val profilePagerAdapter = ProfilePagerAdapter(this)
        binding.vpProfile.adapter = profilePagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.vpProfile) { tab, position ->
            tab.text =
                when (position) {
                    0 -> getString(R.string.business_info_title)
                    1 -> getString(R.string.branches)
                    else -> getString(R.string.subscription_title)
                }
        }.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.currentUser.observe(viewLifecycleOwner){
            binding.user = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }
}
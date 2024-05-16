package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.R
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment responsible for managing and displaying a list of branches in a stepper activity.
 *
 * This fragment is part of the Business setup process, allowing users to add, update, and view branches.
 * It includes UI components such as a RecyclerView for displaying branches and buttons for navigation.
 *
 * @constructor Creates a new instance of BranchesFragment.
 */
@AndroidEntryPoint
class BranchesFragment : Fragment() {
    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentBranchesBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel associated with the hosting activity
    internal val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBranchesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding ?: return
        init()
    }

    /**
     * Initializes the ViewModel, Adapter, and click events for the fragment.
     */
    private fun init() {
        setupViewModel()
        setupAdapter()
        setupClicks()
        setupWithTwoPane()
        observerUpdateBranchInTablet()
    }

    private fun setupWithTwoPane() {

        if (businessViewModel.isCompact.value == false) {
            binding.fcvAddBranch.visibility = View.VISIBLE
            binding.branches.btnBranchesAdd.visibility = View.GONE

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fcv_add_branch,
                    AddBranchesDialogFragment.newInstance(),
                    AddBranchesDialogFragment.ADD_BRANCH_TAG,
                ).commit()
        } else {
            binding.branches.btnBranchesAdd.visibility = View.VISIBLE
            binding.fcvAddBranch.visibility = View.GONE
        }
    }

    /**
     * Initializes the ViewModel associated with the layout.
     */
    private fun setupViewModel() {
        binding.branches.lifecycleOwner = this.viewLifecycleOwner
        binding.branches.viewModel = businessViewModel
    }

    private fun setupAdapter() {
        // Lazy initialization of BranchesAdapter
        val branchAdapter: BranchesAdapter by lazy {
            BranchesAdapter {
                businessViewModel.setBranchSelected(it)
                if (businessViewModel.isCompact.value == true) {
                    val dialog = AddBranchesDialogFragment.newInstance()
                    dialog.show(parentFragmentManager, AddBranchesDialogFragment.UPDATE_BRANCH_TAG)
                } else {
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fcv_add_branch,
                            AddBranchesDialogFragment.newInstance(),
                            AddBranchesDialogFragment.UPDATE_BRANCH_TAG,
                        ).commit()
                }
            }
        }
        binding.branches.rvBranches.adapter = branchAdapter
    }

    /**
     * Initializes click events for buttons and subscription for network availability.
     */
    private fun setupClicks() {
        initClickSubscription()
        binding.apply {
            btnBranchesInfo.setOnClickListener {
                businessViewModel.moveToPreviousStep()
            }
            branches.btnBranchesAdd.setOnClickListener {
                val dialog = AddBranchesDialogFragment.newInstance()
                dialog.show(parentFragmentManager, AddBranchesDialogFragment.ADD_BRANCH_TAG)
            }
        }
    }

    private fun observerUpdateBranchInTablet() {
        if (businessViewModel.isCompact.value == false) {
            businessViewModel.isUpdateBranch.observe(
                viewLifecycleOwner,
                EventObserver {
                    // Clear previous update branch dialog.
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fcv_add_branch,
                            AddBranchesDialogFragment.newInstance(),
                            AddBranchesDialogFragment.ADD_BRANCH_TAG,
                        ).commit()
                },
            )
        }
    }

    /**
     * Initializes the subscription to check network availability before adding a business.
     */
    private fun initClickSubscription() {
        binding.btnBranchesSubscription.setOnClickListener {
            businessViewModel.setBusiness()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
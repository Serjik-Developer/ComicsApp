package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscriptionMode
import com.example.texnostrelka_2025_otbor.databinding.FragmentSubscribedUsersBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.SubscribeAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemSubscribedClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.userinfo.UserInfoActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.userinfo.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscribedUsersFragment : Fragment(), OnItemSubscribedClickListener {
    companion object {
        private const val ARG_MODE = "MODE"

        fun newInstance(mode: SubscriptionMode): SubscribedUsersFragment {
            val fragment = SubscribedUsersFragment()
            val args = Bundle()
            args.putString(ARG_MODE, mode.name)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var mode: SubscriptionMode
    private var _binding : FragmentSubscribedUsersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserInfoViewModel by activityViewModels()
    private lateinit var adapter: SubscribeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mode = SubscriptionMode.valueOf(arguments?.getString("MODE") ?: SubscriptionMode.SUBSCRIBERS.name)
        _binding = FragmentSubscribedUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SubscribeAdapter(mutableListOf(), this)
        binding.recyclerViewSubscribe.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSubscribe.adapter = adapter
        viewModel.subscribeUsers.observe(viewLifecycleOwner) { users ->
            adapter.updateData(users)
        }
        val userId = (activity as? UserInfoActivity)?.intent?.getStringExtra("USER-ID") ?: return

        when (mode) {
            SubscriptionMode.SUBSCRIBERS -> viewModel.fetchSubscribersUsers(userId)
            SubscriptionMode.FOLLOWING -> viewModel.fetchSubscriptionsUsers(userId)
        }
    }

    override fun onButtonSubscribeClick(userId: String) {
        viewModel.postSubscribe(userId)
    }
}

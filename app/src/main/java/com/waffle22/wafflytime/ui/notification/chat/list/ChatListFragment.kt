package com.waffle22.wafflytime.ui.notification.chat.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentChatboxBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.ui.notification.BaseNotificationViewModel
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatListFragment : Fragment() {
    private val viewModel: ChatListViewModel by sharedViewModel()
    private val baseNotificationViewModel: BaseNotificationViewModel by sharedViewModel()
    private lateinit var binding : FragmentChatboxBinding
    private lateinit var adapter: ChatListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getChatList()

        recyclerView = binding.chatBoxRecyclerView
        adapter = ChatListAdapter{moveToChatRoom(it)}
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int){
                if (positionStart == 0){
                    recyclerView.scrollToPosition(0)
                }
            }
        })

        binding.apply {
            recyclerView.adapter = adapter
            swipeRefreshLayout.setOnRefreshListener { viewModel.getChatList() }
        }

        lifecycleScope.launch {
            viewModel.chatBoxState.collect{
                chatBoxLogic(it)
            }
        }

    }

    private fun chatBoxLogic(state: SlackState<List<ChatSimpleInfo>>) {
        when(state.status) {
            "0" -> null
            else -> {
                when(state.status){
                    "200" -> {
                        adapter.submitList(state.dataHolder)
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetState()
                binding.swipeRefreshLayout.isRefreshing = false
            }

        }

    }

    private fun moveToChatRoom(chatSimpleInfo: ChatSimpleInfo) {
        baseNotificationViewModel.setStateChatRoom(chatSimpleInfo)
    }
}
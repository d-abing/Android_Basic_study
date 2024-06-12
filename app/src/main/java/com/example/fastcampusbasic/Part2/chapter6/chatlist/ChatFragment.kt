package com.example.fastcampusbasic.Part2.chapter6.chatlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusbasic.Part2.chapter6.Key
import com.example.fastcampusbasic.Part2.chapter6.messagelist.MessageActivity
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserFragment
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentChatBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        val chatListAdapter = ChatAdapter { chatItem ->
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra(UserFragment.EXTRA_OTHER_USER_ID, chatItem.otherUserId)
            intent.putExtra(UserFragment.EXTRA_CHAT_ID, chatItem.chatId)
            startActivity(intent)
        }
        binding.chatListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatListAdapter
        }

        val currentUserId = Firebase.auth.currentUser?.uid ?: return
        val chatDB = Firebase.database.reference.child(Key.DB_CHATS).child(currentUserId)

        chatDB.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = snapshot.children.map {
                    it.getValue(ChatItem::class.java)
                }
                Log.e("ChatFragment", "chatList: $chatList")
                chatListAdapter.submitList(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
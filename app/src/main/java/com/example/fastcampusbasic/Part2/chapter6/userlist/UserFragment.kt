package com.example.fastcampusbasic.Part2.chapter6.userlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusbasic.Part2.chapter6.Key.Companion.DB_CHATS
import com.example.fastcampusbasic.Part2.chapter6.Key.Companion.DB_USERS
import com.example.fastcampusbasic.Part2.chapter6.chatlist.ChatItem
import com.example.fastcampusbasic.Part2.chapter6.messagelist.MessageActivity
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.UUID

class UserFragment : Fragment(R.layout.fragment_user) {

    private lateinit var binding: FragmentUserBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)

        val userListAdapter = ChatUserAdapter { otherUser ->

            val myUserId = Firebase.auth.currentUser?.uid ?: ""
            val chatDB = Firebase.database.reference.child(DB_CHATS).child(myUserId).child(otherUser.userId ?: "")

            chatDB.get().addOnSuccessListener {
                val chatId: String

                if (it.value != null) {
                    val chat = it.getValue(ChatItem::class.java)
                    chatId = chat?.chatId ?: ""
                } else {
                    chatId = UUID.randomUUID().toString()
                    val newChat = ChatItem(
                        chatId = chatId,
                        otherUserName = otherUser.username,
                        otherUserId = otherUser.userId,
                    )

                    chatDB.setValue(newChat)
                }

                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra(EXTRA_OTHER_USER_ID, otherUser.userId)
                intent.putExtra(EXTRA_CHAT_ID, chatId)
                startActivity(intent)
            }
        }
        binding.userListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userListAdapter
        }

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""

        Firebase.database.reference.child(DB_USERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   val userList = snapshot.children.map {
                        it.getValue(UserItem::class.java)
                    }.filter {
                        it?.userId != currentUserId
                   }
                    userListAdapter.submitList(userList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    companion object {
        const val EXTRA_CHAT_ID = "CHAT_ID"
        const val EXTRA_OTHER_USER_ID = "OTHER_USER_ID"
    }
}
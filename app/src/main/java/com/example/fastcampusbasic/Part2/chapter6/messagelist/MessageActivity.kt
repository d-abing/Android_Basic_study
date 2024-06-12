package com.example.fastcampusbasic.Part2.chapter6.messagelist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusbasic.Part2.chapter6.Key
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserFragment.Companion.EXTRA_CHAT_ID
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserFragment.Companion.EXTRA_OTHER_USER_ID
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserItem
import com.example.fastcampusbasic.databinding.ActivityMessageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var chatId: String = ""
    private var otherUserId: String = ""
    private var myUserId: String = ""
    private var myUserName: String = ""

    private val messageItemList = mutableListOf<MessageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatId = intent.getStringExtra(EXTRA_CHAT_ID) ?: return
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserId = Firebase.auth.currentUser?.uid ?: ""

        messageAdapter = MessageAdapter()
        linearLayoutManager = LinearLayoutManager(applicationContext)

        Firebase.database.reference.child(Key.DB_USERS).child(myUserId).get()
            .addOnSuccessListener {
                val myUserItem = it.getValue(UserItem::class.java)
                myUserName = myUserItem?.username ?: ""
            }

        Firebase.database.reference.child(Key.DB_USERS).child(otherUserId).get()
            .addOnSuccessListener {
                val otherUserItem = it.getValue(UserItem::class.java)

                messageAdapter.otherUserItem = otherUserItem
            }

        Firebase.database.reference.child(Key.DB_CHATS).child(chatId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageItem = snapshot.getValue(MessageItem::class.java)
                    messageItem ?: return

                    messageItemList.add(messageItem)
                    messageAdapter.submitList(messageItemList.toMutableList())
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })

        binding.messageListRv.apply {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
        }

        messageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                linearLayoutManager.smoothScrollToPosition(
                    binding.messageListRv,
                    null,
                    messageItemList.size
                )
            }
        })

        binding.sendBtn.setOnClickListener {
            val message = binding.messageEt.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "빈 메시지를 전송할 수는 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newMessageItem = MessageItem(
                userId = myUserId,
                message = message
            )

            Firebase.database.reference.child(Key.DB_CHATS).child(chatId).push().apply {
                newMessageItem.messageId = key
                setValue(newMessageItem)
            }

            val updates: MutableMap<String, Any> = hashMapOf(
                "${Key.DB_CHATS}/$myUserId/$otherUserId/lastMessage" to message,
                "${Key.DB_CHATS}/$otherUserId/$myUserId/lastMessage" to message,
                "${Key.DB_CHATS}/$otherUserId/$myUserId/chatId" to chatId,
                "${Key.DB_CHATS}/$otherUserId/$myUserId/otherUserId" to myUserId,
                "${Key.DB_CHATS}/$otherUserId/$myUserId/otherUserName" to myUserName,
            )
            Firebase.database.reference.updateChildren(updates)

            binding.messageEt.setText("")
        }
    }
}
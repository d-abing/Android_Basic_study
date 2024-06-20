package com.example.fastcampusbasic.Part2.chapter10.bookmark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fastcampusbasic.Part2.chapter10.data.ArticleModel
import com.example.fastcampusbasic.Part2.chapter10.ui.house.HouseFragmentDirections
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentBookmarkBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class BookmarkArticleFragment : Fragment(R.layout.fragment_bookmark) {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookmarkAdapter: BookmarkArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)

        binding.toolbar.setupWithNavController(findNavController())

        bookmarkAdapter = BookmarkArticleAdapter {
            findNavController().navigate(
                BookmarkArticleFragmentDirections.actionBookmarkArticleFragmentToArticleFragment(
                    it.articleId.orEmpty()
                )
            )
        }

        binding.bookmarkArticleRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = bookmarkAdapter
        }

        val uid = Firebase.auth.currentUser?.uid.orEmpty()
        Firebase.firestore.collection("bookmark")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val list = it.get("articleIds") as List<*>

                if (list.isNotEmpty()) {
                    Firebase.firestore.collection("articles")
                        .whereIn("articleId", list)
                        .get()
                        .addOnSuccessListener { result ->
                            bookmarkAdapter.submitList(result.map { article -> article.toObject<ArticleModel>() })
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}
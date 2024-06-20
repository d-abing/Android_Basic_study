package com.example.fastcampusbasic.Part2.chapter10.ui.house

import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fastcampusbasic.Part2.chapter10.data.ArticleModel
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentHouseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class HouseFragment : Fragment(R.layout.fragment_house) {
    private lateinit var binding: FragmentHouseBinding
    private lateinit var articleAdapter: HouseArticleAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHouseBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore

        setupWriteButton(view)
        setupBookmarkButton()
        setupRecyclerView()

        fetchFirestoreData()
    }

    private fun setupWriteButton(view: View) {
        binding.writeFab.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                val action = HouseFragmentDirections.actionHouseFragmentToWriteArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBookmarkButton() {
        binding.bookmarkIb.setOnClickListener {
            findNavController().navigate(HouseFragmentDirections.actionHouseFragmentToBookmarkArticleFragment())
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = HouseArticleAdapter (
            onItemClicked = {
                findNavController().navigate(
                    HouseFragmentDirections.actionHouseFragmentToArticleFragment(
                        it.articleId.orEmpty()
                    )
                )
            },
            onBookmarkClicked = { articleId, isBookmarked ->
                val uid = Firebase.auth.currentUser?.uid ?: return@HouseArticleAdapter
                Firebase.firestore.collection("bookmark").document(uid)
                    .update(
                        "articleIds",
                        if (isBookmarked) {
                            FieldValue.arrayUnion(articleId)
                        } else {
                            FieldValue.arrayRemove(articleId)
                        }
                    )
                    .addOnFailureListener {
                        if (it is FirebaseFirestoreException && it.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                            if (isBookmarked) {
                                Firebase.firestore.collection("bookmark").document(uid)
                                    .set(
                                        hashMapOf(
                                            "articleIds" to listOf(articleId)
                                        ))
                            }
                        }
                    }

            }
        )

        binding.houseRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = articleAdapter
        }
    }

    private fun fetchFirestoreData() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("bookmark").document(uid)
            .get()
            .addOnSuccessListener {
                val bookmarkList = it.get("articleIds") as? List<*>

                Firebase.firestore.collection("articles")
                    .get()
                    .addOnSuccessListener { result ->
                        val list = result
                            .map { snapshot -> snapshot.toObject<ArticleModel>() }
                            .map { model ->

                                ArticleItem(
                                    articleId = model.articleId.orEmpty(),
                                    description = model.description.orEmpty(),
                                    imageUrl = model.imageUrl.orEmpty(),
                                    isBookmark = bookmarkList?.contains(model.articleId.orEmpty()) ?: false
                                )
                            }

                        articleAdapter.submitList(list)
                    }
            }
    }
}
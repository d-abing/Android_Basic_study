package com.example.fastcampusbasic.Part2.chapter10.ui.article

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.fastcampusbasic.Part2.chapter10.data.ArticleModel
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentArticleBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        val articleId = args.articleId

        binding.toolbar.setupWithNavController(findNavController())

        Firebase.firestore.collection("articles").document(articleId)
            .get()
            .addOnSuccessListener {
                val model = it.toObject<ArticleModel>()

                Glide.with(binding.thumbnailIv)
                    .load(model?.imageUrl)
                    .into(binding.thumbnailIv)
                binding.descriptionTv.text = model?.description

            }
            .addOnFailureListener {

            }

    }
}
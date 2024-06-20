package com.example.fastcampusbasic.Part2.chapter10.ui.article

import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.fastcampusbasic.Part2.chapter10.data.ArticleModel
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentWriteArticleBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID

class WriteArticleFragment : Fragment(R.layout.fragment_write_article) {
    private lateinit var binding: FragmentWriteArticleBinding
    private lateinit var viewModel: WriteArticleViewModel
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.updateSelectedUri(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWriteArticleBinding.bind(view)

        setupViewModel()

        if (viewModel.selectedUri.value == null) {
            pickPhoto()
        }

        setupPhotoImageView()
        setupClearButton()
        setupSubmitButton(view)
        setupBackButton()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get<WriteArticleViewModel>()

        viewModel.selectedUri.observe(viewLifecycleOwner) { uri ->

            if (uri != null) {
                binding.photoIv.setImageURI(uri)
                binding.addPhotoBtn.isVisible = false
                binding.clearPhotoBtn.isVisible = true
            } else {
                binding.photoIv.setImageURI(null)
                binding.clearPhotoBtn.isVisible = false
                binding.addPhotoBtn.isVisible = true
            }

        }
    }

    private fun setupPhotoImageView() {
        binding.photoIv.setOnClickListener {
            if (viewModel.selectedUri.value == null) {
                pickPhoto()
            }
        }
    }

    private fun setupClearButton() {
        binding.clearPhotoBtn.setOnClickListener {
            viewModel.updateSelectedUri(null)
        }
    }

    private fun setupSubmitButton(view: View) {
        binding.submitBtn.setOnClickListener {
            showProgress()

            if (viewModel.selectedUri.value != null) {
                val photoUri = viewModel.selectedUri.value ?: return@setOnClickListener
                uploadImage(
                    uri = photoUri,
                    successHandler = {
                        uploadArticle(it, binding.descriptionEt.text.toString())
                    },
                    errorHandler = {
                        hideProgress()
                        Snackbar.make(view, "사진 업로드에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                )

            } else {
                hideProgress()
                Snackbar.make(requireView(), "사진이 선택되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBackButton() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }

    private fun pickPhoto() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showProgress() {
        binding.progressBarL.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBarL.isVisible = false
    }

    private fun uploadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("articles/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.storage.reference.child("articles/photo/$fileName")
                        .downloadUrl
                        .addOnSuccessListener {
                            successHandler(it.toString())
                        }
                        .addOnFailureListener {
                            errorHandler(it)
                        }
                } else {
                    errorHandler(task.exception)
                }
            }
    }

    private fun uploadArticle(photoUrl: String, description: String) {
        val articleId = UUID.randomUUID().toString()
        val articleModel = ArticleModel(
            articleId = articleId,
            createdAt = System.currentTimeMillis(),
            description = description,
            imageUrl = photoUrl,
        )

        Firebase.firestore.collection("articles").document(articleId)
            .set(articleModel)
            .addOnSuccessListener {
                hideProgress()
                findNavController().navigate(WriteArticleFragmentDirections.actionWriteArticleFragmentToHouseFragment())
            }
            .addOnFailureListener {
                hideProgress()
                it.printStackTrace()
                view?.let { view ->
                    Snackbar.make(view, "게시글 업로드에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }

        hideProgress()
    }
}
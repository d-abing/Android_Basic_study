package com.example.fastcampusbasic.Part2.chapter10.ui.article

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteArticleViewModel : ViewModel() {

    private val _selectedUri = MutableLiveData<Uri?>()
    var selectedUri: LiveData<Uri?> = _selectedUri

    fun updateSelectedUri(uri: Uri?) {
        _selectedUri.value = uri
    }
}
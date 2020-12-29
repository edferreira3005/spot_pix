package com.example.spotpix.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ShareImageViewModel : ViewModel() {
    val isUndo = MutableLiveData<Boolean>()
    val isClient = MutableLiveData<Boolean>()
    val isCompetitor = MutableLiveData<Boolean>()
    val isEmpty = MutableLiveData<Boolean>()
    val isInvaders = MutableLiveData<Boolean>()
    val isSaveShare = MutableLiveData<Boolean>()
    val isCalculateAll = MutableLiveData<Boolean>()
    val isCalculateSingle =
        MutableLiveData<Boolean>()
    var viewGallery = MutableLiveData<Int>()
    var viewPicture = MutableLiveData<Int>()
    fun showViewGallery() {
        viewGallery.value = View.GONE
        viewPicture.value = View.VISIBLE
    }

    fun hideViewGallery() {
        viewGallery.value = View.GONE
        viewPicture.value = View.VISIBLE
    }

    fun onUndoClicked() {
        isUndo.value = true
    }

    fun onClientClicked() {
        isClient.value = true
    }

    fun onCompetitorClicked() {
        isCompetitor.value = true
    }

    fun onEmptyClicked() {
        isEmpty.value = true
    }

    fun onInvadersClicked() {
        isInvaders.value = true
    }

    fun onSaveShareClicked() {
        isSaveShare.value = true
    }

    fun onCalculateAllClicked() {
        isCalculateAll.value = true
    }

    fun onCalculateSingleClicked() {
        isCalculateSingle.value = true
    }

    init {
        showViewGallery()
    }
}

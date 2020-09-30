package com.userstar.livedemo.ui.main.viewModel

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel : ViewModel() {
    val reviewList = loadReviewList()

    private fun loadReviewList() : LiveData<List<Review>> {
        val liveData = MutableLiveData<List<Review>>()

        val idList = listOf("xxcTXbWFD1k", "zQcCjoSRyyA", "R6GPb2aDpGg", "R-xyfF3Sjw0")

        val list = ArrayList<Review>()
        for (i in 1 .. 4) {
            list.add(Review("精彩回顧 - ${i}", "11:${i}0", idList[i-1], "精彩回顧 - ${i}"))
        }
        liveData.postValue(list)

        return liveData
    }
}

data class Review(
    val title: String,
    val time: String,
    val id: String,
    val thumbnailUri: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(time)
        parcel.writeString(id)
        parcel.writeString(thumbnailUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}
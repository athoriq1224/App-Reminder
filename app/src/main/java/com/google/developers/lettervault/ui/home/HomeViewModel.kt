package com.google.developers.lettervault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.developers.lettervault.data.DataRepository
import com.google.developers.lettervault.data.Letter

/**
 * ViewMode for the HomeActivity only holds recent letter.
 */
class HomeViewModel(dataRepository: DataRepository) : ViewModel() {
    val letter = dataRepository.getRecentLetter()
}

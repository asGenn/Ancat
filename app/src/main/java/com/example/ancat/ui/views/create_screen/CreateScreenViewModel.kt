package com.example.ancat.ui.views.create_screen

import androidx.lifecycle.ViewModel
import com.example.ancat.data.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CreateScreenViewModel @Inject constructor(
    private val repository: SurveyRepository
): ViewModel() {


}
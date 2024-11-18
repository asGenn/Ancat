package com.example.ancat.ui.views.home_screen

import androidx.lifecycle.ViewModel
import com.example.ancat.core.helper.SurveyHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val surveyHelper: SurveyHelper
) : ViewModel()
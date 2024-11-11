package com.example.ancat.ui.views.create_survey_screen

import androidx.lifecycle.ViewModel
import com.example.ancat.data.repository.JsonFilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class DialogType {
    object Type1: DialogType()
    object Type2: DialogType()
}
@HiltViewModel
class CreateSurveyViewModel  @Inject constructor(private val jsonFilesRepository: JsonFilesRepository) : ViewModel() {
    private val _dialogType = MutableStateFlow<DialogType?>(null)
    val dialogType: StateFlow<DialogType?> = _dialogType
    fun showDialog(dialogType: DialogType) {
        _dialogType.value = dialogType
    }
    fun hideDialog() {
        _dialogType.value = null
    }
}
package edu.aibu.ancat.ui.views.create_survey_screen.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import edu.aibu.ancat.ui.views.create_survey_screen.CreateSurveyViewModel
import edu.aibu.ancat.ui.views.create_survey_screen.DialogType

/**
 * Dialog yöneticisi
 * 
 * @param viewModel ViewModel
 */
@Composable
fun DialogHandler(
    viewModel: CreateSurveyViewModel
) {
    val dialogType by viewModel.dialogType.collectAsState()

    dialogType?.let { type ->
        when (type) {
            DialogType.DescriptionType -> {
                SimpleQuestionDialog(
                    onDismissRequest = { viewModel.hideDialog() },
                    viewModel = viewModel
                )
            }

            DialogType.MultipleChoiceType -> {
                MultipleChoiceQuestionDialog(
                    onDismissRequest = { viewModel.hideDialog() },
                    viewModel = viewModel
                )
            }

            DialogType.RatingType -> {
                RatingQuestionDialog(
                    onDismissRequest = { viewModel.hideDialog() },
                    viewModel = viewModel
                )
            }
        }
    }
} 
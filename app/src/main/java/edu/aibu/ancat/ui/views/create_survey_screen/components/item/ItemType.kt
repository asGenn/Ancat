package edu.aibu.ancat.ui.views.create_survey_screen.components.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.ui.views.create_survey_screen.CreateSurveyViewModel
import edu.aibu.ancat.ui.views.create_survey_screen.components.dialog.EditQuestionDialog

@Composable
fun SurveyTitleType(
    item: SurveyItem
) {
    val viewmodel: CreateSurveyViewModel = hiltViewModel()
    var descriptionIndex by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedDescription by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        val title = item.questions[0] as Question.SurveyTitle
        Column {
            Text(
                title.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            HorizontalDivider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            title.description.forEachIndexed { index, desc ->
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "${index + 1}. $desc",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = {
                            selectedDescription = desc
                            descriptionIndex = index
                            showEditDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
    }
    if (showEditDialog && selectedDescription != null) {
        EditQuestionDialog(
            question = selectedDescription!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { newQuestion ->
                // You can implement the onConfirm logic here
                viewmodel.editSurveyDescription(descriptionIndex, newQuestion)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun DescriptionType(
    modifier: Modifier = Modifier,
    range: Int,
    item: SurveyItem
) {
    val viewmodel: CreateSurveyViewModel = hiltViewModel()
    var descriptionIndex by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedDescription by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Açıklama Metinleri",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        HorizontalDivider(
            color = Color.White,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        item.questions.forEachIndexed { index, question ->
            if (question is Question.SurveyDescription) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${(index + 1)}. ${question.description}",
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = {
                            selectedDescription = question.description
                            descriptionIndex = index
                            showEditDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }
            }
        }
        if (showEditDialog && selectedDescription != null) {
            EditQuestionDialog(
                question = selectedDescription!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { newQuestion ->
                    // You can implement the onConfirm logic here
                    viewmodel.editDescriptionsType(range, descriptionIndex, newQuestion)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun RatingType(modifier: Modifier = Modifier, range: Int, item: SurveyItem) {

    val viewmodel: CreateSurveyViewModel = hiltViewModel()
    var questionIndex by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Derecelendirme Soruları",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
        )
        HorizontalDivider(
            color = Color.White,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))

        item.questions.forEachIndexed { index, question ->
            if (question is Question.RatingQuestion) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}. ${question.question}",
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = {
                            selectedQuestion = question.question
                            questionIndex = index
                            showEditDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }

    if (showEditDialog && selectedQuestion != null) {
        EditQuestionDialog(
            question = selectedQuestion!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { newQuestion ->
                // You can implement the onConfirm logic here
                viewmodel.editRatingTypeQuest(range, questionIndex, newQuestion)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun MultipleChoiceType(modifier: Modifier = Modifier, range: Int, item: SurveyItem) {

    val viewmodel: CreateSurveyViewModel = hiltViewModel()
    var questionIndex by remember { mutableIntStateOf(0) }
    var markIndex by remember { mutableIntStateOf(0) }
    var showEditDialogQuest by remember { mutableStateOf(false) }
    var showEditDialogMark by remember { mutableStateOf(false) }
    var selectedQuestOrMark by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Çoktan Seçmeli Sorular",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
        )
        HorizontalDivider(
            color = Color.White,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        item.questions.forEachIndexed { questIndex, question ->
            if (question is Question.MultipleChoiceQuestion) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        question.question,
                        modifier = modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = {
                            selectedQuestOrMark = question.question
                            questionIndex = questIndex
                            showEditDialogQuest = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }
                question.options.forEachIndexed { i, option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${i + 1}. \u25EF $option",
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            modifier = Modifier.size(15.dp),
                            onClick = {
                                selectedQuestOrMark = option
                                questionIndex = questIndex
                                markIndex = i
                                showEditDialogMark = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
    }
    if (showEditDialogQuest && selectedQuestOrMark != null) {
        EditQuestionDialog(
            question = selectedQuestOrMark!!,
            onDismiss = { showEditDialogQuest = false },
            onConfirm = { newQuestion ->
                // You can implement the onConfirm logic here
                viewmodel.editMultiChoiceTypeQuest(range, questionIndex, newQuestion)
                showEditDialogQuest = false
            }
        )
    }
    if (showEditDialogMark && selectedQuestOrMark != null) {
        EditQuestionDialog(
            question = selectedQuestOrMark!!,
            onDismiss = { showEditDialogMark = false },
            onConfirm = { newQuestion ->
                // You can implement the onConfirm logic here
                viewmodel.editMultiChoiceTypeMarks(range, questionIndex, markIndex, newQuestion)
                showEditDialogMark = false
            }
        )
    }
}
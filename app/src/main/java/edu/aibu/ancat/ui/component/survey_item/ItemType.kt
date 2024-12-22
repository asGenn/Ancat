package edu.aibu.ancat.ui.component.survey_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem

@Composable
fun SurveyTitleType(item: SurveyItem) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                item.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 3.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            item.questions.forEachIndexed { _, question ->
                if (question is Question.SurveyTitle) {
                    question.description.forEach {
                        Text(
                            it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(
                                horizontal = 12.dp
                            )
                        )
                    }
                }
            }
            HorizontalDivider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, top = 8.dp, end = 5.dp)
            )
        }

    }
}

@Composable
fun DescriptionType(
    item: SurveyItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
            .fillMaxWidth()
    ) {
        item.questions.forEachIndexed { _, question ->
            if (question is Question.SurveyDescription) {
                question.description.forEachIndexed { index, option ->
                    Text(
                        "${index + 1}. $option",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 8.dp, end = 5.dp)
        )
    }
}

@Composable
fun RatingType(modifier: Modifier = Modifier, item: SurveyItem) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {

        item.questions.forEachIndexed { _, question ->
            if (question is Question.RatingQuestion) {
                // Soru Başlığı
                Text(
                    text = question.question,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Derecelendirme Seçenekleri
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        RadioButton(
                            selected = false,
                            onClick = { /*TODO*/ },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
fun MultipleChoiceType(modifier: Modifier = Modifier, item: SurveyItem) {
    Column(
        modifier = modifier
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
            .fillMaxWidth()
    ) {
        item.questions.forEachIndexed { _, question ->
            if (question is Question.MultipleChoiceQuestion) {
                Text(
                    question.question,
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,

                    )
                question.options.forEachIndexed { index, option ->
                    Text(
                        "${index + 1}. (    ) $option",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
    }
}
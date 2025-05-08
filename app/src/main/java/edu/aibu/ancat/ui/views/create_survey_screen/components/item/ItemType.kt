package edu.aibu.ancat.ui.views.create_survey_screen.components.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
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
                Text(
                    "${index + 1}. $desc",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 12.sp
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }
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
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        var index = 0
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
        item.questions.forEach { question ->
            if (question is Question.SurveyDescription) {
                question.description.forEach { option ->
                    Text(
                        "${(++index)}. $option",
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun RatingType(modifier: Modifier = Modifier, item: SurveyItem) {
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
                Text(
                    text = "${index + 1}. ${question.question}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start),
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
fun MultipleChoiceType(modifier: Modifier = Modifier, item: SurveyItem) {
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
        item.questions.forEach { question ->
            if (question is Question.MultipleChoiceQuestion) {
                Text(
                    question.question,
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start),
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Black,

                    )
                question.options.forEachIndexed { index, option ->
                    Text(
                        "${index + 1}. \u25EF $option",
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
    }
}
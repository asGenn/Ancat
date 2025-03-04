package edu.aibu.ancat.ui.views.create_survey_screen.components.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.aibu.ancat.data.model.SurveyItem

/**
 * Anket öğelerinin listelendiği bileşen
 * 
 * @param surveyItems Anket öğeleri listesi
 * @param modifier Modifier
 */
@Composable
fun SurveyItemsList(
    surveyItems: List<SurveyItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(surveyItems) { _, item ->
            SurveyItemCard(item = item)
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // FAB için boşluk
        }
    }
}

/**
 * Anket öğesi kartı
 * 
 * @param item Anket öğesi
 */
@Composable
fun SurveyItemCard(item: SurveyItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (item.type) {
                "_" -> SurveyTitleType(item = item)
                "0" -> DescriptionType(item = item)
                "1" -> RatingType(item = item)
                "2" -> MultipleChoiceType(item = item)
            }
        }
    }
} 
package edu.aibu.ancat.core.renderer.survey_drawings.other

import android.graphics.Canvas
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.OPTION_SPACING
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.TITLE_PADDING
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TitleAndCommits @Inject constructor(
    private val paintFactory: PaintFactory
) {

    fun surveyTitleCommit(
        canvas: Canvas,
        title: String,
        commits: Question.SurveyTitle
    ): Float {

        var cursorPos = MARGIN * 3

        val titleCenter = (PAGE_WIDTH - paintFactory.title().measureText(title)) / 2
        canvas.drawText(title, titleCenter, cursorPos, paintFactory.title())
        cursorPos += TITLE_PADDING

        commits.description.forEach { commit ->
            canvas.drawText(commit, MARGIN * 3, cursorPos, paintFactory.text())
            cursorPos += OPTION_SPACING
        }
        return cursorPos
    }

    fun questionCommits(
        canvas: Canvas,
        commits: List<Question.SurveyDescription>,
        cursorPosition: Float,
    ): Float {

        var cursorPos = cursorPosition

        commits.forEach { commit ->
            commit.description.forEach { desc ->
                canvas.drawText(desc, MARGIN * 3, cursorPos, paintFactory.text())
                cursorPos += TITLE_PADDING
            }
        }
        return cursorPos + MARGIN
    }
}
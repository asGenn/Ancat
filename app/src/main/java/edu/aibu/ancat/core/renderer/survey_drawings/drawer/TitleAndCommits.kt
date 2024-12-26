package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Canvas
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.TITLE_PADDING
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TitleAndCommits @Inject constructor(
    private val paintFactory: PaintFactory
) {

    fun surveyTitleCommit(
        canvas: Canvas,
        commits: Question.SurveyTitle
    ): Float {

        var cursorPos = START_CURSOR

        val titleCenter = (PAGE_WIDTH - paintFactory.title().measureText(commits.title)) / 2
        canvas.drawText(commits.title, titleCenter, cursorPos, paintFactory.title())
        cursorPos += TITLE_PADDING

        commits.description.forEach { commit ->
            canvas.drawText(commit, MARGIN * 3, cursorPos, paintFactory.text())
            cursorPos += MARGIN * 2
        }
        return cursorPos
    }

    fun questionCommits(
        canvas: Canvas,
        descriptions: Question.SurveyDescription,
        cursorPosition: Float,
    ): Float {

        var cursorPos = cursorPosition
        descriptions.description.forEach {
            canvas.drawText(it, MARGIN * 3, cursorPos, paintFactory.text())
            cursorPos += TITLE_PADDING
        }
        return cursorPos
    }
}
package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Canvas
import android.util.Log
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.TITLE_PADDING
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TitleAndCommits @Inject constructor(
    private val paintFactory: PaintFactory,
    private val textHandler: TextHandler
) {

    fun surveyTitleCommit(
        canvas: Canvas,
        commits: Question.SurveyTitle
    ): Float {

        var cursorPos = START_CURSOR

        val titleCenter = (PAGE_WIDTH - paintFactory.title().measureText(commits.title)) / 2
        canvas.drawText(commits.title, titleCenter, cursorPos, paintFactory.title())

        cursorPos += MARGIN * 3

        commits.description.forEach { commit ->
            val textList: List<String> = textHandler.getWrappedText(
                text = commit,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 3
            )

            textList.forEach {
                cursorPos += TITLE_PADDING
                canvas.drawText(it, MARGIN * 3, cursorPos, paintFactory.text())
            }
            cursorPos += MARGIN
        }
        return cursorPos
    }

    fun questionCommits(
        canvas: Canvas,
        descriptions: Question.SurveyDescription,
        cursorPosition: Float,
    ): Float {

        var cursorPos = cursorPosition

        descriptions.description.forEach { text ->
            val textList: List<String> = textHandler.getWrappedText(
                text = text,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 3
            )

            textList.forEach {
                canvas.drawText(it, MARGIN * 3, cursorPos, paintFactory.text())
                cursorPos += CELL_HEIGHT
            }
        }
        return cursorPos + TITLE_PADDING
    }
}
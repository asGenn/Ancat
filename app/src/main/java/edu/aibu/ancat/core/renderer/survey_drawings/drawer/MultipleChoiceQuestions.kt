package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.content.Context
import android.graphics.Canvas
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultipleChoiceQuestions @Inject constructor(
    private val canvasContentDrawer: CanvasContentDrawer,
    private val jsonHelper: JsonHelper,
    private val textHandler: TextHandler,
    private val paintFactory: PaintFactory
) {

    fun drawMultipleChoiceQuestions(
        context: Context,
        canvas: Canvas,
        data: Question.MultipleChoiceQuestion,
        surveyIndex: Int,
        questionIndex: Int,
        cursorPosition: Float,
        jsonFileName: String,
    ): Float {

        var currentCursor = cursorPosition
        val markPositions = mutableListOf<Float>()

        var tempCursor = currentCursor
        val textList: List<String> = textHandler.getWrappedText(
            text = data.question,
            paint = paintFactory.text(),
            xCursor = MARGIN * 3,
            maxWidth = PAGE_WIDTH - MARGIN * 20
        )

        textList.forEach { text ->
            val textCenter = tempCursor + (CELL_HEIGHT + paintFactory.text().textSize) / 2
            tempCursor = canvasContentDrawer.writeText(
                canvas = canvas,
                text = text,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                yCursor = textCenter
            )
        }

        tempCursor += MARGIN

        currentCursor = drawOptions(
            canvas = canvas,
            options = data.options,
            cursorPosition = currentCursor
        ) { yCursor ->
            markPositions.add(yCursor)
        }

        jsonHelper.addMarksToMultiChoiceQuest(
            context = context,
            data = markPositions,
            surveyIndex = surveyIndex,
            questionIndex = questionIndex,
            fileName = jsonFileName
        )

        if (tempCursor > currentCursor)
            currentCursor = tempCursor

        canvasContentDrawer.drawFrame(
            canvas = canvas,
            xLeft = MARGIN * 2,
            xRight = PAGE_WIDTH - MARGIN * 2,
            yTop = cursorPosition,
            yBottom = currentCursor,
            paint = paintFactory.line()
        )

        return currentCursor
    }

    private fun drawOptions(
        canvas: Canvas,
        options: List<String>,
        cursorPosition: Float,
        callback: (Float) -> Unit
    ): Float {

        var currentCursor = cursorPosition
        options.forEachIndexed { index, option ->

            val textList: List<String> = textHandler.getWrappedText(
                text = "${index + 1}. \u25EF $option",
                paint = paintFactory.text(),
                xCursor = PAGE_WIDTH - MARGIN * 17,
                maxWidth = PAGE_WIDTH - MARGIN * 3
            )

            textList.forEach { text ->
                val textCenter = currentCursor + (CELL_HEIGHT + paintFactory.text().textSize) / 2
                currentCursor = canvasContentDrawer.writeText(
                    canvas = canvas,
                    text = text,
                    paint = paintFactory.text(),
                    xCursor = PAGE_WIDTH - MARGIN * 17,
                    yCursor = textCenter
                )
                callback(textCenter - 10f)
            }
        }
        return currentCursor + MARGIN
    }

}

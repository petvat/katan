package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table


class DiceAnimationWidget(
    private val animationSequence: Array<Image>,
    skin: Skin
) : Table(skin) {

    init {
        isVisible = false
    }

    fun playRoll(finalRoll1: Int, finalRoll2: Int, onComplete: () -> Unit) {
        isVisible = true

        val flickerCount = 8
        val flickerInterval = 0.07f

        val flickerSteps = Array(flickerCount) {
            Actions.sequence(
                Actions.run {
                    //
                },
                Actions.delay(flickerInterval)
            )
        }

        addAction(
            Actions.sequence(
                Actions.sequence(*flickerSteps),
                Actions.run {
                    // Final roll
                },
                Actions.delay(0.5f),
                Actions.run {
                    isVisible = false
                    onComplete()
                }
            )
        )
    }
}

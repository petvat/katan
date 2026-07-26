package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * Minimal placeholder dice animation: flickers random pip values for a
 * short duration, then settles on the real roll. Swap the visuals inside
 * here later for real dice sprites -- the VM/View contract (start, then
 * call back when done) doesn't need to change.
 */
/**
 * Placeholder dice-roll animation using real die-face images. Flickers
 * through random faces, settles on the real roll, briefly holds, then
 * hides and reports completion.
 */
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

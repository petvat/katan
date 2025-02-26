package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.ResourceMap
import ktx.actors.onChangeEvent
import ktx.scene2d.*

/**
 *
 */
@Scene2dDsl
class BuildItemWidget(
    title: String,
    cost: ResourceMap,
    skin: Skin
) : Table(skin), KTable {


    init {
        // skin.getDrawable(selectedBgd)

        label(title, defaultStyle, skin) {
            setEllipsis(true) // it? But not available.
            setEllipsis("...")
            it.top().left().growX().padLeft(2f).padTop(2f)
        }
        // TODO: Image here!
        row()
        // TODO: Replace with image!
        label(
            "${
                cost.get().forEach { (resource, amount) ->
                    if (amount > 0) {
                        "${resource.name} : $amount"
                    }
                }
            }"
        ) {
            onChangeEvent { }
        }
    }

}


@Scene2dDsl
fun <S> KWidget<S>.buildItem(
    title: String,
    cost: ResourceMap,
    skin: Skin,
    init: (@Scene2dDsl BuildItemWidget).(S) -> Unit = {},
): BuildItemWidget = actor(BuildItemWidget(title, cost, skin), init)

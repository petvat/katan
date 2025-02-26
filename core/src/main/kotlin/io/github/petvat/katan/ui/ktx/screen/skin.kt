package io.github.petvat.core.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import ktx.scene2d.Scene2DSkin
import ktx.style.*

enum class Image(
    val atlasKey: String
) {
    BTN_UP("btn"),
    BTN_DOWN("btn"),
    TXT_FLD("text-field"),
    WIN("area")
}

enum class Font(
    val atlasKey: String,
    val scaling: Float
) {
    HEADER("alagard", 0.5f);

    val fontPath = "assets/${atlasKey}.fnt"
}

operator fun Skin.get(img: Image): Drawable = this.getDrawable(img.atlasKey)
operator fun Skin.get(font: Font): BitmapFont = this.getFont(font.atlasKey)


fun loadJsonSkin() {
    Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("assets/first-itr-ui.json"))
}

fun loadVisUISkin() {
    VisUI.load(VisUI.SkinScale.X2)
    Scene2DSkin.defaultSkin = VisUI.getSkin()
}

/**
 * Loads the UI skin.
 */
fun loadSkin() {
    Scene2DSkin.defaultSkin = skin(TextureAtlas("assets/katan-ui.atlas")) { skin ->
        Font.entries.forEach { font ->
            skin[font.atlasKey] = BitmapFont(Gdx.files.internal(font.fontPath), skin.getRegion(font.atlasKey)).apply {
                data.setScale(font.scaling)
                // setColor(136f, 122f, 81f, 1f)
            }
        }

        label {
            font = BitmapFont(Gdx.files.internal("assets/alagard.fnt"), skin.getRegion("alagard"))
        }

        button {
            up = skin[Image.BTN_UP]
            down = skin[Image.BTN_DOWN]
        }

        textButton {
            up = skin[Image.BTN_UP]
            down = skin[Image.BTN_DOWN]
            font = BitmapFont(Gdx.files.internal(Font.HEADER.fontPath), skin.getRegion(Font.HEADER.atlasKey))
            pressedOffsetY = -2f
        }

        textField {

        }


    }
}

fun disposeSkin() {
    Scene2DSkin.defaultSkin.dispose()
}

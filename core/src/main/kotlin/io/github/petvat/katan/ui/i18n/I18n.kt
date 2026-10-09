package io.github.petvat.katan.ui.i18n

import com.badlogic.gdx.utils.I18NBundle

object I18n {
    lateinit var bundle: I18NBundle

    operator fun get(key: String): String = bundle[key]
    fun format(key: String, vararg args: Any?): String = bundle.format(key, *args)

    val buildSettlement get() = this["build.settlement"]
    val buildCity get() = this["build.city"]
}

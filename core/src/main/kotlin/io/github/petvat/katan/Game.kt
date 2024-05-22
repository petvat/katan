package io.github.petvat.katan

import io.github.petvat.katan.board.BoardManager
import io.github.petvat.katan.board.Player

object Game{

    val boardManager = BoardManager
    val players: MutableList<Player> = mutableListOf()


    fun getPlayerByID(ID: Int): Player {
        TODO("FIX")
    }

}

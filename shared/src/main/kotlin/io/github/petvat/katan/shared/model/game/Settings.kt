package io.github.petvat.katan.shared.model.game

import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.MessageFactory
import io.github.petvat.katan.shared.protocol.MessageType
import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.shared.protocol.json.KatanJson
import kotlinx.serialization.Serializable

enum class GameMode {
    STANDARD
}

@Serializable
data class Settings(
    val gameMode: GameMode = GameMode.STANDARD,
    val boardSize: Int = DEFAULT_BOARD_SIZE,
    val terrainNumbers: Array<Int> = TERRAIN_NUMBERS,
    val numRobber: Int = NUM_ROBBER,
    val minPlayers: Int = MIN_PLAYERS,
    val maxPlayers: Int = MAX_PLAYERS,
    val settlementCost: ResourceMap = SETTLEMENT_COST,
    val cityCost: ResourceMap = CITY_COST,
    val roadCost: ResourceMap = ROAD_COST,
    val knightCost: ResourceMap = KNIGHT_COST,
    val maxSettlements: Int = MAX_SETTLEMENTS,
    val maxCities: Int = MAX_CITIES,
    val maxRoads: Int = MAX_ROADS,
    val cardLimit: Int = CARD_LIMIT,
    val initRobberLocation: HexCoordinates = HexCoordinates(0, 0) // Assuming a default position
) {
    companion object {
        const val DEFAULT_BOARD_SIZE = 2

        const val NUM_ROBBER = 7
        const val CARD_LIMIT = 7

        // Num players
        const val MIN_PLAYERS = 3
        const val MAX_PLAYERS = 4

        // COSTS
        val SETTLEMENT_COST = ResourceMap(1, 0, 1, 1, 1)
        val CITY_COST = ResourceMap(0, 3, 2, 0, 0)
        val ROAD_COST = ResourceMap(1, 0, 0, 0, 1)
        val KNIGHT_COST = ResourceMap(0, 1, 1, 1, 0)

        // COUNT
        const val MAX_SETTLEMENTS = 5
        const val MAX_CITIES = 4
        const val MAX_ROADS = 15

        // TERRAINS
        const val NUM_FOREST = 4
        const val NUM_FIELDS = 4
        const val NUM_PASTURE = 4
        const val NUM_MOUNTAINS = 3
        const val NUM_HILLS = 3
        const val NUM_DESERT = 1

        val TERRAIN_NUMBERS = arrayOf(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12)

        // Bank consts
        const val NUM_WOOL = 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Settings

        if (boardSize != other.boardSize) return false
        if (!terrainNumbers.contentEquals(other.terrainNumbers)) return false
        if (numRobber != other.numRobber) return false
        if (minPlayers != other.minPlayers) return false
        if (maxPlayers != other.maxPlayers) return false
        if (settlementCost != other.settlementCost) return false
        if (cityCost != other.cityCost) return false
        if (roadCost != other.roadCost) return false
        if (knightCost != other.knightCost) return false
        if (maxSettlements != other.maxSettlements) return false
        if (maxCities != other.maxCities) return false
        if (maxRoads != other.maxRoads) return false
        if (cardLimit != other.cardLimit) return false
        if (initRobberLocation != other.initRobberLocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = boardSize
        result = 31 * result + terrainNumbers.contentHashCode()
        result = 31 * result + numRobber
        result = 31 * result + minPlayers
        result = 31 * result + maxPlayers
        result = 31 * result + settlementCost.hashCode()
        result = 31 * result + cityCost.hashCode()
        result = 31 * result + roadCost.hashCode()
        result = 31 * result + knightCost.hashCode()
        result = 31 * result + maxSettlements
        result = 31 * result + maxCities
        result = 31 * result + maxRoads
        result = 31 * result + cardLimit
        result = 31 * result + initRobberLocation.hashCode()
        return result
    }
}


fun main() {
    println(Settings())
    val json = KatanJson.messageToJson(
        MessageFactory.create(
            messageType = MessageType.CREATE,
            data = Request.Create(Settings(), true)
        )
    )
    println(json)

}

//
//class BaseSettings(
//    override val boardSize: Int = 2,
//    override val terrainNumbers: Array<Int> = Settings.TERRAIN_NUMBERS,
//    override val numRobber: Int = Settings.NUM_ROBBER,
//    override val minPlayers: Int = Settings.MIN_PLAYERS,
//    override val maxPlayers: Int = Settings.MAX_PLAYERS,
//    override val settlementCost: ResourceMap = Settings.SETTLEMENT_COST,
//    override val cityCost: ResourceMap = Settings.CITY_COST,
//    override val roadCost: ResourceMap = Settings.ROAD_COST,
//    override val knightCost: ResourceMap = Settings.KNIGHT_COST,
//    override val maxSettlements: Int = Settings.MAX_SETTLEMENTS,
//    override val maxCities: Int = Settings.MAX_CITIES,
//    override val maxRoads: Int = Settings.MAX_ROADS,
//    override val cardLimit: Int = Settings.CARD_LIMIT,
//    override val initRobberLocation: HexCoordinate = HexCoordinate(0, 0)
//) : Settings

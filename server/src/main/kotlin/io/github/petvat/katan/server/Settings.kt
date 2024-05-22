package io.github.petvat.katan.server

import io.github.petvat.katan.server.board.ResourceMap


class Settings {

    companion object {
        // COSTS
        val SETTLEMENT_COST = ResourceMap(1, 0, 1, 1, 1)
        val CITY_COST = ResourceMap(0, 3, 2, 0, 0)
        val ROAD_COST = ResourceMap(1, 0, 0, 0, 1)
        val KNIGHT_COST = ResourceMap(0, 1, 1, 1, 0)

        // COUNT
        val MAX_SETTLEMENTS = 5
        val MAX_CITIES = 4
        val MAX_ROADS = 15
        // val MAX_KNIGHT = 0

        // TERRAINS
        val NUM_TERRAINS = 19

        val NUM_FOREST = 4
        val NUM_FIELDS = 4
        val NUM_PASTURE = 4
        val NUM_MOUNTAINS = 3
        val NUM_HILLS = 3
        val NUM_DESERT = 1

        val TERRAIN_NUMBERS = arrayOf(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12)
    }


}

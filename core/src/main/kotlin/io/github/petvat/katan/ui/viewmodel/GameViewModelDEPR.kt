package io.github.petvat.katan.ui.viewmodel


/**
 *
 * Implementers: BoardRenderer, GameView
 *
 * Interface between Model and view. Holds data that is updated on events.
 */
//class GameViewModel(
//    private val gameService: GameActions, // TODO: Use gameState instead! Model does the logic, viewmodel simply makes the data manageable by a view.
//    private val chatService: ChatActions,
//    group: GroupState,
//    val game: GameState,
//) : ViewModel() {
//
//    companion object {
//        data class OtherPlayerViewModel(
//            val playerNumber: Int,
//            val name: String,
//            val color: PlayerColor,
//            val victoryPoints: Int,
//            val cardCount: Int
//        )
//
//        data class ThisPlayerViewModel(
//            val inventory: ResourceMapData,
//            val victoryPoints: Int,
//        )
//    }
//
//    private val logger = KotlinLogging.logger { }
//
//
//    /**
//     * Transform tiles to doubled. We need to do this to work with edges/intersections.
//     */
//    var tilesDoubled = game.board.tiles
//        .map {
//            Tile(
//                HexUtils.transformToDoubled(it.hexCoordinate),
//                it.resource,
//                it.rollListenValue
//            )
//        }.toList()
//
//    var roadFrontier: Set<EdgeCoordinates> = emptySet()
//    var settlementFrontier: Set<ICoordinates> = emptySet()
//    var cityFrontier: Set<ICoordinates> = emptySet()
//    var setUpFrontier: Set<ICoordinates> = emptySet()
//
//
//    // PRESENTATION STATE
//
//    var currentTurnPlayer by propertyNotify(game.turnPlayer)
//    var diceRoll by propertyNotify(Pair(-1, -1))
//    var thisPlayerVM: ThisPlayerViewModel by propertyNotify(
//        ThisPlayerViewModel(
//            game.resources, game.victoryPoints[game.player]!!
//        )
//    )
//    var otherPlayersVM: List<OtherPlayerViewModel> by propertyNotify(
//        game.otherPlayers.map {
//            OtherPlayerViewModel(
//                playerNumber = it,
//                name = "TODO: Name, PlayerDTO needs ID.",
//                color = it.color, // TODO
//                cardCount = it.cityCount, // TODO
//                victoryPoints = game.victoryPoints[it]!!
//            )
//        }
//    )
//
//    // TODO: Chat log view gameState. REMOVE!
//    var chatLog by propertyNotify(group.chatLog)
//    var lastGroupMessage by propertyNotify("" to "") // Nice?
//    var rollDiceMode by propertyNotify(false)
//    var buildMode by propertyNotify(false)
//
//    // UI MODES
//
//    // For card animation
//    var playerResourceDiff: ResourceMap = ResourceMap(0, 0, 0, 0, 0)
//
//    var roadPlacingMode by propertyNotify(false)
//    var settlementPlacingMode by propertyNotify(false)
//    var cityPlacingMode by propertyNotify(false)
//
//
//    private val setupTurnOrder: MutableList<Int> // TODO: MOVE TO STATE MODEL
//
//
//    init {
//        val reversed = game.turnOrder.reversed()
//        setupTurnOrder = game.turnOrder.toMutableList()
//        setupTurnOrder.addAll(reversed)
//
//    }
//
//    override fun onEvent(event: Event) {
//        when (event) {
//            is ChatEvent -> {
//                lastGroupMessage = event.from to event.message
//            }
//
//            is NextTurnEvent -> {
//                currentTurnPlayer = event.playerNumber
//                logger.debug { "Turn event, next player is $currentTurnPlayer." }
//                if (!setupPhase) {
//                    rollDiceMode = true
//                } else {
//                    settlementPlacingMode = true
//                }
//            }
//
//            is PlaceInitialSettlementEvent -> {
//                settlementFrontier = RuleEngine.updateSetupFrontier(game.player, game.board)
//            }
//
//            is RolledDiceEvent -> {
//                diceRoll = event.roll1 to event.roll2
//
//                // Update the resources of this player
//                thisPlayerVM = thisPlayerVM.copy(
//                    inventory = event.playerResources,
//                )
//
//                // Update the resources of other players
//                otherPlayersVM.map {
//                    it.copy(
//                        cardCount = event.otherPlayersCardCounts[it.playerNumber]!!
//                    )
//                }
//
//                if (game.turnPlayer == game.player) {
//                    buildMode = true
//                }
//            }
//
//            is BuildEvent -> {
//                when (event.buildKind) {
//                    is BuildKind.Road -> {
//                        roadFrontier = RuleEngine.updateRoadFrontier(game.player, game.board)
//                    }
//
//                    is BuildKind.Village -> {
//                        if (event.buildKind.kind == VillageKind.SETTLEMENT) {
//                            cityFrontier = updateCityFrontier(game.player, game.board)
//                        }
//                        if (event.buildKind.kind == VillageKind.CITY) {
//                            settlementFrontier = updateSettlementFrontier(game.player, game.board)
//                        }
//                        // Updates the victory points
//                        if (event.playerNumber == game.player) {
//                            thisPlayerVM =
//                                thisPlayerVM.copy(victoryPoints = thisPlayerVM.victoryPoints + 1)
//
////                            _thisPlayerModel.value =
////                                _thisPlayerModel.value.copy(victoryPoints = _thisPlayerModel.value.victoryPoints + 1)
//                        } else {
////                            _otherPlayerViewModels.forEach {
////                                if (it.value.playerNumber == event.playerNumber) {
////                                    it.value = it.value.copy(
////                                        victoryPoints = it.value.victoryPoints + 1,
////                                    )
////                                }
////                            }
//                            otherPlayersVM.map {
//                                if (it.playerNumber == event.playerNumber) {
//                                    it.copy(victoryPoints = it.victoryPoints + 1)
//                                } else {
//                                    it
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            is PlaceBuildingCommand<*> -> {
//                // This will tell the renderer to include the highlights when rendering.
//                // the renderer is responsible for these fields.
//                when (event.buildKind) {
//                    is BuildKind.Village -> {
//                        if (event.buildKind.kind == VillageKind.SETTLEMENT) {
//                            settlementPlacingMode = true
//                        } else if (event.buildKind.kind == VillageKind.CITY) {
//                            cityPlacingMode = true
//                        }
//                    }
//
//                    is BuildKind.Road -> {
//                        roadPlacingMode = true
//                        // TODO: Toggle Highlight,
//                    }
//                }
//            }
//
//            else -> Unit
//        }
//    }
//
//    fun handleChat(message: String) = chatService.sendMessage(message) // TODO: EH
//
//    fun handleRollDice() = gameService.rollDice()
//
//    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) = gameService.handleBuild(buildKind, coordinates)
//
//}
//
//

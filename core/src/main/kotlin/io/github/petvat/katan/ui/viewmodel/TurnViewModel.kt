package io.github.petvat.katan.ui.viewmodel


//class TurnViewModel(
//    private val session: GameSession,
//    private val gameActions: IGameActions
//) : ViewModel() {
//
//    var rollDiceMode by propertyNotify(false)
//        private set
//    var buildMode by propertyNotify(false)
//        private set
//
//
//    init {
//        session.onPropertyChange(session::state) { newState ->
//            rollDiceMode = isRollDiceMode(newState)
//        }
//    }
//
//    fun handleRollDice() = gameActions.rollDice()
//
//    override fun onEvent(event: Event) {
//        if (event is RolledDiceEvent) {
//            onDiceRolled?.invoke(event.roll1, event.roll2)
//            pendingReveal = { buildMode = isBuildMode(session.state) }
//        }
//    }
//
//    fun onDiceAnimationComplete() {
//        pendingReveal?.invoke()
//        pendingReveal = null
//    }
//
//    private fun isRollDiceMode(s: GameState) = s.turnPlayer == s.player && s.phase == Phase.ROLL_DICE
//    private fun isBuildMode(s: GameState) = s.turnPlayer == s.player && s.phase != Phase.ROLL_DICE
//}

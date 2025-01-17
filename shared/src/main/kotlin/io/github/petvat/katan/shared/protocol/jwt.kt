package io.github.petvat.katan.shared.protocol
//
//import com.auth0.jwt.JWT
//import com.auth0.jwt.algorithms.Algorithm
//import com.auth0.jwt.exceptions.JWTVerificationException
//import mu.KotlinLogging
//
//
//data class StateToken(
//    val userId: Int,
//    val groupId: String?,
//    val stateVersion: Int,
//    val permit: Permits
//)
//
//object TokenManager {
//
//    const val SECRET_KEY = "very_secret"
//    private val logger = KotlinLogging.logger {}
//    private val stateMaps = mutableMapOf<Int, Int>()
//
//    fun updateStates(users: List<CID>, group: String, permit: Permits): List<String> {
//        return users.map {
//            createToken(
//                it,
//                group,
//                -1, // State version needed? No?
//                permit
//            )
//        }
//    }
//
//    fun createToken(cid: CID, groupId: String?, stateVersion: Int, permit: Permits): String {
//        return JWT.create()
//            .withIssuer("katan")
//            .withClaim("userId", cid.id)
//            .withClaim("stateVersion", stateVersion)
//            .withClaim("groupId", groupId ?: "")
//            .withClaim("permit", permit.name)
//            .sign(Algorithm.none())
//    }
//
//    suspend fun verifyToken(token: String): StateToken? {
//        try {
//            val verifier = JWT.require(Algorithm.none())
//                .withIssuer("katan")
//                .build()
//            val jwt = verifier.verify(token)
//            val playerId = jwt.getClaim("userId").asInt()
//            val groupId = jwt.getClaim("groupId").asString().takeIf { id -> id != "" }
//            val stateVersion = jwt.getClaim("stateVersion").asInt()
//            val permit = Permits.valueOf(jwt.getClaim("permit").asString())
//
//            if (stateVersion != stateMaps[playerId]) {
//                return null
//            }
//            return StateToken(
//                playerId,
//                groupId,
//                stateVersion,
//                permit
//            )
//        } catch (exception: JWTVerificationException) {
//            logger.error { "Invalid token: ${exception.message}" }
//        }
//        return null
//    }
//
//}

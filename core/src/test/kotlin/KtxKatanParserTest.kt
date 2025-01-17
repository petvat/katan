//import io.github.petvat.katan.KatanParser
//import io.github.petvat.katan.gameState.ResourceMap
//import io.github.petvat.katan.model.action.ActionCode
//import io.github.petvat.katan.dto.*
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//
//class KtxKatanParserTest {
//
//
//    /**
//     * Test that we can serialize to response to json, then back to response so that they are equal.
//     */
//    @Test
//    fun testSerializeThenDeserializeReturnsSame() {
//        val response = ActionResponse(
//            -1, "sessId", io.github.petvat.katan.model.action.ActionCode.ROLL_DICE, true, "roll dice",
//            RollDiceDTO(1, 2, ResourceMap(0, 0, 0, 0, 0), emptyMap(), true)
//        )
//        val json = KatanParser.toJson(response)
//        println(json)
//        val deserializedResponse = KatanParser.toResponse(json)
//        assertEquals(response, deserializedResponse)
//
//        val failedResponse = JoinSessionResponse(
//            -1, false, "Failed", null
//        )
//        val jsonFailed = KatanParser.toJson(failedResponse)
//        val failedDeserializedResponse = KatanParser.toResponse(jsonFailed)
//        assertEquals(failedResponse, failedDeserializedResponse)
//
//
////        val loginResp = Logi(1, "test")
////        val jsonLogin = KatanParser.toJson(loginRequest)
////        val deserializedResp = KatanParser.toResponse(jsonLogin)
////        assertEquals(loginRequest, deserializedResp)
//    }
//
//    @org.junit.jupiter.api.Test
//    fun toCreateRequest() {
//
//    }
//
//    @org.junit.jupiter.api.Test
//    fun toSessionRequest() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun toRequest() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun toJson() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun testToJson() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun toResponse() {
//    }
//}

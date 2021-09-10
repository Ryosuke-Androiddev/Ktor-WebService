import com.example.DrawingServer
import com.example.utility.Constants.TYPE_ANNOUNCEMENT
import com.example.utility.Constants.TYPE_CHAT_MESSAGE
import com.example.utility.Constants.TYPE_DRAW_DATA
import com.example.utility.Constants.TYPE_JOIN_ROOM_HANDSHAKE
import com.example.data.Player
import com.example.data.Room
import com.example.data.models.*
import com.example.session.DrawingSession
import com.example.utility.Constants.TYPE_CHOSEN_WORD
import com.example.utility.Constants.TYPE_GAME_STATE
import com.example.utility.Constants.TYPE_PHASE_CHANGE
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.Exception

val gson = Gson()
val server = DrawingServer()

fun Route.gameWebSocketRoute() {
    route("/ws/draw") {
        standardWebSocket { socket, clientId, message, payload ->
            when(payload) {
                is JoinedRoomHandshake -> {
                    val room = server.rooms[payload.roomName]
                    if (room == null){
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }
                    val player = Player(
                        payload.username,
                        socket,
                        payload.clientId
                    )
                    server.playerJoined(player)
                    if(!room.containsPlayer(player.userName)) {
                        room.addPlayer(player.clientId, player.userName, socket)
                    }
                }
                is DrawData -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if(room.phase == Room.Phase.GAME_RUNNING) {
                        room.broadcastToAllExcept(message, clientId)
                    }
                }
                is ChosenWord -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    room.setWordAndSwitchToGameRunning(payload.chosenWord)
                }
                is ChatMessage -> {

                }
            }
        }
    }
}


fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
        message: String,
        payload: BaseModel
    ) -> Unit
){
    webSocket {
        val session = call.sessions.get<DrawingSession>()
        if (session == null){
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No Session..."))
            return@webSocket
        }
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text){
                    val message = frame.readText()

                    // you can handle this as Json Object
                    val jsonObject = JsonParser.parseString(message).asJsonObject
                    val type = when(jsonObject.get("type").asString){
                        TYPE_CHAT_MESSAGE -> ChatMessage::class.java
                        TYPE_DRAW_DATA -> DrawData::class.java
                        TYPE_ANNOUNCEMENT -> Announcement::class.java
                        TYPE_JOIN_ROOM_HANDSHAKE -> JoinedRoomHandshake::class.java
                        TYPE_PHASE_CHANGE -> PhaseChange::class.java
                        TYPE_CHOSEN_WORD -> ChosenWord::class.java
                        TYPE_GAME_STATE -> GameState::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message,type)
                    handleFrame(this,session.clientId,message,payload)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            // TODO Later handle disconnections
        }
    }
}
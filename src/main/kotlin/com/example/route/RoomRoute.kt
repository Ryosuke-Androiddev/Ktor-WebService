import com.example.DrawingServer
import com.example.Utility.Constants
import com.example.data.Room
import com.example.data.models.BasicApiResponse
import com.example.data.models.CreateRoomRequest
import com.example.data.models.RoomResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import javax.xml.transform.OutputKeys

fun Route.createRoomRoute(){

    val server = DrawingServer() // I'm not sure this will work expects.

    route("/api/createRoom") {
        post {

            val roomRequest = call.receiveOrNull<CreateRoomRequest>()
            if(roomRequest == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if(server.rooms[roomRequest.name] != null) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "Room already exists.")
                )
                return@post
            }
            if(roomRequest.maxPlayers < 2) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "The minimum room size is 2.")
                )
                return@post
            }
            if(roomRequest.maxPlayers > Constants.MAX_ROOM_SIZE) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "The maximum room size is ${Constants.MAX_ROOM_SIZE}")
                )
                return@post
            }
            val room = Room(
                roomRequest.name,
                roomRequest.maxPlayers
            )
            server.rooms[roomRequest.name] = room
            println("Room created: ${roomRequest.name}")

            call.respond(HttpStatusCode.OK, BasicApiResponse(true))
        }
    }
}

fun Route.getRoomsRoute(){

    val server = DrawingServer() // I'm not sure this will work expects.

    route("/api/getRooms"){
        get {
            val searchQuery = call.parameters["searchQuery"]
            if (searchQuery == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val roomsResult = server.rooms.filterKeys {
                it.contains(searchQuery, ignoreCase = true) // you don't have to consider upper case and lower case using ignoreCase = true
            }
            val roomResponses = roomsResult.values.map {
                RoomResponse(it.name,it.maxPlayer,it.players.size)
            }.sortedBy { it.name }

            call.respond(HttpStatusCode.OK, roomResponses)
        }
    }
}

fun Route.joinRoomRoute(){

    val server = DrawingServer() // I'm not sure this will work expects.

    route("/api/joinRoom"){
        get {
            val username = call.parameters["username"]
            val roomName = call.parameters["roomName"]
            if (username == null || roomName == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val room = server.rooms[roomName]
            when {
                room == null -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "Room not found")
                    )
                }
                room.containsPlayer(username) -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "A player with this username already joined.")
                    )
                }
                room.players.size >= room.maxPlayer -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "This room is already full.")
                    )
                }
                else -> {
                    call.respond(HttpStatusCode.OK, BasicApiResponse(true))
                }
            }
        }
    }
}
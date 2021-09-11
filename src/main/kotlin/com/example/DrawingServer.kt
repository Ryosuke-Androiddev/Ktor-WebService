package com.example

import com.example.data.Player
import com.example.data.Room
import java.util.concurrent.ConcurrentHashMap

class DrawingServer {

    val rooms = ConcurrentHashMap<String,Room>()
    val players = ConcurrentHashMap<String,Player>()

    // you can identify the player with clientId
    fun playerJoined(player: Player){
        players[player.clientId] = player
        player.startPinging()
    }

    fun playerLeft(clientId: String, immediatelyDisconnect: Boolean = false) {
        val playersRoom = getRoomWithClientId(clientId)
        if(immediatelyDisconnect || players[clientId]?.isOnline == false) {
            println("Closing connection to ${players[clientId]?.userName}")
            playersRoom?.removePlayer(clientId)
            players[clientId]?.disconnect()
            players.remove(clientId)
        }
    }

    fun getRoomWithClientId(clientId: String): Room? {
        val filteredRooms = rooms.filterValues { room ->
            room.players.find { player ->
                player.clientId == clientId
            } != null
        }
        return if (filteredRooms.values.isEmpty()){
            null
        } else {
            filteredRooms.values.toList()[0] // I can't understand well about this.
        }
    }
}
package com.example.data

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val name: String,
    val maxPlayer: Int,
    var players: List<Player> = listOf()
) {

    // When game start
    suspend fun broadcast(message: String){
        players.forEach{ player ->
            if (player.socket.isActive){
                player.socket.send(Frame.Text(message))
            }
        }
    }

    suspend fun broadcastToAllExcept(message: String, clientId: String){
        players.forEach{ player ->
            if (player.clientId != clientId && player.socket.isActive){
                player.socket.send(Frame.Text(message))
            }
        }
    }

    fun containsPlayer(username: String): Boolean{
        return players.find { it.userName == username } != null
    }
}
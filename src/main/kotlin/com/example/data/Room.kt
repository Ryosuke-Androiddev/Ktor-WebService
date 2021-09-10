package com.example.data

import com.example.data.models.Announcement
import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val name: String,
    val maxPlayer: Int,
    var players: List<Player> = listOf()
) {

    val gson = Gson()

    private var phaseChangedListener: ((Phase) -> Unit)? = null
    var phase = Phase.WAITING_FOR_PLAYERS
    set(value) {
        //you synchronized state here
        synchronized(field){
            field = value
            phaseChangedListener?.let { change ->
                change(value) // this means ((Phase) -> Unit) ??
            }
        }
    }

    private fun setPhaseChangedListener(listener: (Phase) -> Unit) {
        phaseChangedListener = listener
    }

    init {
        setPhaseChangedListener { newPhase ->
            when(newPhase) {
                Phase.WAITING_FOR_PLAYERS -> waitingForPlayers()
                Phase.WAITING_FOR_START -> waitingForStart()
                Phase.NEW_ROUND -> newRound()
                Phase.GAME_RUNNING -> gameRunning()
                Phase.SHOW_WORD -> showWord()
            }
        }
    }

    suspend fun addPlayer(clientId: String, username: String, socket: WebSocketSession): Player{
        val player = Player(username, socket, clientId)
        players = players + player // you can add player after do this

        if(players.size == 1) {
            phase = Phase.WAITING_FOR_PLAYERS
        } else if(players.size == 2 && phase == Phase.WAITING_FOR_PLAYERS) {
            phase = Phase.WAITING_FOR_START
            players = players.shuffled()
        } else if(phase == Phase.WAITING_FOR_START && players.size == maxPlayer) {
            phase = Phase.NEW_ROUND
            players = players.shuffled()
        }

        val announcement = Announcement(
            "$username joined the room!",
            System.currentTimeMillis(),
            Announcement.TYPE_PLAYER_JOINED
        )

        broadcast(gson.toJson(announcement))

        return player
    }


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

    private fun waitingForPlayers() {

    }

    private fun waitingForStart() {

    }

    private fun newRound() {

    }

    private fun gameRunning() {

    }

    private fun showWord() {

    }

    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }
}
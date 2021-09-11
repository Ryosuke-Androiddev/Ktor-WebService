package com.example.data.models

import com.example.utility.Constants.TYPE_JOIN_ROOM_HANDSHAKE

data class JoinedRoomHandshake(
    val username: String,
    val roomName: String,
    val clientId: String
): BaseModel(TYPE_JOIN_ROOM_HANDSHAKE)
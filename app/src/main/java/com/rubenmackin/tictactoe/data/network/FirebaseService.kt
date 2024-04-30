package com.rubenmackin.tictactoe.data.network


import android.util.Log
import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.snapshots
import com.google.firebase.database.snapshots
import com.rubenmackin.tictactoe.data.network.model.GameData
import com.rubenmackin.tictactoe.ui.model.GameModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseService @Inject constructor(private val reference: DatabaseReference) {

    companion object {
        private const val PATH = "games"
    }

    fun createGame(gameData: GameData): String {
        val gameReference = reference.child(PATH).push()
        val key = gameReference.key
        val newGame = gameData.copy(gameId = key)
        gameReference.setValue(newGame)
        return newGame.gameId.orEmpty()
    }

    fun joinToGame(gameId: String): Flow<GameModel?> {
        return reference.database.reference.child("$PATH/$gameId").snapshots.map { dataSnapshot ->
            dataSnapshot.getValue(GameData::class.java)?.toModel()
        }
    }

    fun updateGame(gameData: GameData) {
        if (gameData.gameId != null) {
            reference.child(PATH).child(gameData.gameId).setValue(gameData)
        }
    }
}
package com.rubenmackin.tictactoe.ui.game

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rubenmackin.tictactoe.ui.game.GameViewModel
import com.rubenmackin.tictactoe.ui.model.GameModel
import com.rubenmackin.tictactoe.ui.model.PlayerType
import com.rubenmackin.tictactoe.ui.theme.Accent
import com.rubenmackin.tictactoe.ui.theme.Background
import com.rubenmackin.tictactoe.ui.theme.BlueLink
import com.rubenmackin.tictactoe.ui.theme.Orange1
import com.rubenmackin.tictactoe.ui.theme.Orange2

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = hiltViewModel(),
    gameId: String,
    userId: String,
    owner: Boolean,
    navigateToHome: () -> Unit
) {
    LaunchedEffect(true) {
        gameViewModel.joinToGame(gameId, userId, owner)
    }

    val game: GameModel? by gameViewModel.game.collectAsState()
    val winner: PlayerType? by gameViewModel.winner.collectAsState()

    if (winner != null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Background), contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp),
                backgroundColor = Background,
                border = BorderStroke(2.dp, Orange1),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "¡FELICIDADES!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val currentWinner = if (winner == PlayerType.FirstPlayer) {
                        "Player 1"
                    } else {
                        "Player 2"
                    }

                    Text(text = "Ha ganado el jugador:", fontSize = 22.sp, color = Accent)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentWinner,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange2
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { navigateToHome() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Orange1)
                    ) {
                        Text(text = "Volver al inicio", color = Accent)
                    }

                }
            }
        }
    } else {
        Board(game, onItemSelected = { position -> gameViewModel.onItemSelected(position) })
    }
}

@Composable
fun Board(game: GameModel?, onItemSelected: (Int) -> Unit) {
    if (game == null) return

    val clipboard: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Background), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = game.gameId, color = BlueLink, modifier = Modifier
            .padding(24.dp)
            .clickable {
                clipboard.setText(AnnotatedString(game.gameId))
                Toast
                    .makeText(context, "Copiado!", Toast.LENGTH_SHORT)
                    .show()
            })

        val status = if (game.isGameReady) {
            if (game.isMyTurn) {
                "Tu turno"
            } else {
                "Turno rival"
            }
        } else {
            "Esperando por el jugador 2"
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = status, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Accent)
            Spacer(Modifier.width(6.dp))
            if (!game.isMyTurn || !game.isGameReady) {
                CircularProgressIndicator(
                    Modifier.size(18.dp),
                    color = Orange1,
                    backgroundColor = Orange2
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            GameItem(game.board[0]) { onItemSelected(0) }
            GameItem(game.board[1]) { onItemSelected(1) }
            GameItem(game.board[2]) { onItemSelected(2) }
        }
        Row {
            GameItem(game.board[3]) { onItemSelected(3) }
            GameItem(game.board[4]) { onItemSelected(4) }
            GameItem(game.board[5]) { onItemSelected(5) }
        }
        Row {
            GameItem(game.board[6]) { onItemSelected(6) }
            GameItem(game.board[7]) { onItemSelected(7) }
            GameItem(game.board[8]) { onItemSelected(8) }
        }
    }
}

@Composable
fun GameItem(playerType: PlayerType, onItemSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .size(64.dp)
            .border(BorderStroke(2.dp, Accent))
            .clickable { onItemSelected() },
        contentAlignment = Alignment.Center
    ) {

        AnimatedContent(targetState = playerType.symbol, label = "") {
            Text(
                text = it,
                color = if (playerType is PlayerType.FirstPlayer) Orange1 else Orange2,
                fontSize = 22.sp
            )
        }

    }
}
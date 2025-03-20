package com.example.hangman

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hangman.game.Game.GameStatus
import com.example.hangman.game.GameLayout
import com.example.hangman.game.LetterInputField
import com.example.hangman.ui.theme.HangmanTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun GameStartButton(text: String, enabled: Boolean, textColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(
            text = text,
            textAlign = TextAlign.Center, // Center the text
            // Change the text color when disabled. Note that if the text color wasn't changeable,
            // the text would be greyed out when disabled by default.
            color = if (enabled) textColor else textColor.copy(alpha = 0.5f),
            fontFamily = FontFamily.Cursive, // Add some style to the text
            fontSize = 36.sp,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

/**
 * The MainScreen composable is the entry point of the app.
 * It displays the game screen and related UI elements.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val wordsArray = stringArrayResource(id = R.array.words)
    // The game state is a mutable state that can be changed. The remember function
    // ensures that the value is retained across recompositions.
    var gameStatus: GameStatus by remember { mutableStateOf(GameStatus.NOT_STARTED) }

    // The mysteryWord holds the word that the player needs to guess. It is exposed to
    // the whole screen so that the word can be displayed in the snackbar when the game ends.
    var mysteryWord = ""

    var isTwoPlayerMode = ""


    // The Scaffold is a "canvas" for the screen. It provides a surface for the content.
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // The Column is a composable that places its children in a vertical sequence.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Center the children horizontally
            verticalArrangement = Arrangement.Center, // Center the children vertically
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize() // Fill the whole screen
        ) {
            Log.d("MainScreen", "MainScreenComposable (${gameStatus}): $mysteryWord")
            GameStartButton(
                text = stringResource(R.string.start_text), // Get the text from the resources
                enabled = gameStatus == GameStatus.NOT_STARTED,
                textColor = colorScheme.primary,
            ) {
                // Change the game state to STARTED if it is NOT_STARTED.
                if (gameStatus == GameStatus.NOT_STARTED) {
                    mysteryWord = getMysteryWord(wordsArray)
                    // gameState is a mutable state so it will trigger
                    // a recomposition of the MainScreen composable.
                    gameStatus = GameStatus.STARTED

                }
            }
            if (gameStatus != GameStatus.NOT_STARTED) {
                // Display other elements of the  MainScreen when
                // the gameState changes from NOT_STARTED.
                when (gameStatus) {
                    GameStatus.STARTED -> {
                        //mysteryWord = getMysteryWord(wordsArray)
                        GameLayout(
                            mysteryWord = mysteryWord,
                            onEnd = { gameStatus = it }
                        )
                    }

                    GameStatus.FINISHED_WON -> {
                        Text(
                            text = stringResource(R.string.won_text, mysteryWord),
                            textAlign = TextAlign.Center,
                            color = colorScheme.primary,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                        LaunchedEffect(Unit) {
                            delay(2000) // 2 s delay
                            gameStatus = GameStatus.NOT_STARTED
                        }
                    }
                    GameStatus.FINISHED_LOST -> {
                        Text(
                            text = stringResource(R.string.lost_text, mysteryWord),
                            textAlign = TextAlign.Center,
                            color = colorScheme.primary,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                        LaunchedEffect(Unit) {
                            delay(2000) // 2 s delay
                            gameStatus = GameStatus.NOT_STARTED
                        }

                    }

                    else -> {
                        // The when block must be exhaustive, so we need to add a branch
                        // to handle the other cases. We do nothing here
                    }
                }
            }
        }
    }
}

private fun getMysteryWord(words: Array<String>): String { // Accepts words array
    return words.random().uppercase()
}
package com.example.hangman.game

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hangman.R
import com.example.hangman.game.Game.GameStatus
import java.util.Locale

/**
 * A composable function that displays the gallows image.
 * @param resId The resource ID of the gallows image.
 * @param modifier The modifier to apply to the Image composable.
 * @param tint The color to apply to the gallows image (black is default).
 */
@Composable
private fun GallowsImage(resId: Int, modifier: Modifier = Modifier, tint: Color = Color.Black) {
    Image(
        // The image is drawn by a Painter which is created with the painterResource function which
        // takes a resource ID as a parameter.
        painter = painterResource(resId),
        contentDescription = null, // contentDescription is required for accessibility
        colorFilter = ColorFilter.tint(tint), // The tint is applied via a ColorFilter
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Composable function that displays the current state of the guessed word.
 */
@Composable
private fun GuessWordText(word: String, modifier: Modifier = Modifier) {
    Text(
        text = word.uppercase(Locale.getDefault()),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold, // The font weight of the text.
        letterSpacing = 8.sp, // The spacing between the letters.
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Composable function that displays the used letters string with a label.
 */
@Composable
private fun UsedLettersText(usedLetters: String, modifier: Modifier) {
    Column {
        Text(
            text = stringResource(R.string.used_letters),
            textAlign = TextAlign.Left, // make the text left-aligned
            fontWeight = FontWeight.Light,
            color = colorScheme.primary, // set the color of the text to the primary color defined in Theme.kt
            modifier = modifier.fillMaxWidth()
        )
        Text(
            text = usedLetters.uppercase(Locale.getDefault()),
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Light,
            color = colorScheme.error, // set the color of the text to the error color. By default it is not specified in the Theme.kt
            modifier = modifier.fillMaxWidth()
        )
    }
}

/**
 * A composable function that displays a TextField with a Button. Used to input letters.
 * @param textFieldValue The value to e displayed in the TextField.
 * @param buttonText The text to be displayed on the Button.
 * @param buttonEnabled Whether the Button is enabled or not.
 * @param isError Whether the TextField is in an error state or not.
 * @param onButtonClick The callback to be called when the Button is clicked.
 * @param onValueChange The callback to be called when the TextField value changes.
 * @param modifier The modifier to apply to the Row composable.
 */
@Composable
fun LetterInputField(
    textFieldValue: String,
    buttonText: String,
    buttonEnabled: Boolean,
    isError: Boolean,
    onButtonClick: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The LetterInputField composable is a Row that contains a TextField and a Button.
    // The Row is a container that lays out its children in a horizontal line.
    Row(
        modifier = modifier.fillMaxWidth(), // The Row fills the available width
        horizontalArrangement = Arrangement.Center, // The children are centered horizontally
    ) {
        TextField(
            // The value of the TextField. The label is displayed when the value
            // is empty and not in focus.
            value = textFieldValue,
            // The callback that is called when the value changes.
            onValueChange = onValueChange,
            singleLine = true,
            // The shape can be modified.
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            // hint for the user what should be input in the TextField.
            label = { Text(text = stringResource(R.string.enter_letter)) },
            isError = isError,
            supportingText = {
                // The supportingText is displayed only when the input is not valid.
                // Similar to the trailingIcon, the supportingText is a composable function.
                if (isError) {
                    // display the error message and other information.
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween /* The children are spaced evenly*/
                    ) {
                        Text("Invalid input")
                        Text(
                            "Char limit is 1 (${textFieldValue.length})"
                        )
                    }
                }
            }

        )
        // The Button is displayed to the right of the TextField.
        Button(
            onClick = onButtonClick, // The callback that is called when the Button is clicked.
            enabled = buttonEnabled, // The Button can be enabled/disabled.
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 8.dp,
                bottomStart = 0.dp,
                bottomEnd = 8.dp
            ), // we can modify the shape of the Button.
            modifier = Modifier
                .defaultMinSize(minHeight = TextFieldDefaults.MinHeight) // Match TextField height
        ) {
            Text(text = buttonText)
        }
    }
}

/**
 * Composable function that displays the game layout.
 * Combines all of the game related composable functions.
 * @param mysteryWord The word to be guessed.
 * It is used to notify the parent composable that the game has ended.
 * @param modifier The modifier to apply to the composable.
 */
@Composable
fun GameLayout(
    mysteryWord: String,
    modifier: Modifier = Modifier,
    onEnd: (GameStatus) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val game = remember { Game(mysteryWord) }
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        GallowsImage(resId = game.getGallowsDrawableId(), tint = colorScheme.primary)
        GuessWordText(
            word = game.getGuessWord(),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        LetterInputField(
            textFieldValue = input,
            buttonText = stringResource(R.string.check),
            onButtonClick = {
                if (input.length == 1){
                    game.checkLetter(input)
                }
                else {
                    game.checkWord(input)
                }
                if (game.isGameFinished()) {
                    onEnd(game.getGameStatus())
                }
                input = ""
            },
            onValueChange = {
                // When the user enters the value it's converted to its uppercase version.
                input = it.uppercase()
            },
            // The isError is set to true when the input is not valid
            // i.e. the character in the TextField is provided but it's longer than 1 and
            // is not a letter.
            isError = input.isNotEmpty() && !game.validateInput(input),
            // The buttonEnabled is set to true when the input is not empty and the input is valid.
            buttonEnabled = input.isNotEmpty() && game.validateInput(input),
        )
        UsedLettersText(
            usedLetters = game.getUsedLetters(),
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, device = "id:pixel_8"
)
@Composable
fun GameLayoutPreview() {

}
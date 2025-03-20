package com.example.hangman.game

import com.example.hangman.R

class Game(private val mysteryWord: String) {
    /** The current state of the gallows image. It is used to determine which image to display. */
    private  var currentGallowsState = 0
    /** The current drawable id of the gallows image. It is used to display the gallows image.*/
    private var currentGallowsDrawableId = R.drawable.hangman0
    /** The current guess word. It is used to display the current state of the word.
    The word is displayed with underscores for the letters that have not been guessed yet.
    It is initialized with underscores for each letter in the mystery word using
    a regular expression (Regex("[A-Z]") which looks for all the capital letters. */
    private var guessWord = mysteryWord.replace(Regex("[A-Z]"), "_")
    /** A string containing all the letters that have been used by the player.*/
    private var usedLetters = ""

    /**
     * Returns the drawable id of the gallows image based on the current gallows state.
     * @return The drawable id of the gallows image or -1 in case of a wrong state.
     */
    private fun getGallowsStateDrawable(): Int {
        return when (currentGallowsState) {
            0 -> R.drawable.hangman0
            1 -> R.drawable.hangman1
            2 -> R.drawable.hangman2
            3 -> R.drawable.hangman3
            4 -> R.drawable.hangman4
            5 -> R.drawable.hangman5
            6 -> R.drawable.hangman6
            7 -> R.drawable.hangman7
            8 -> R.drawable.hangman8
            9 -> R.drawable.hangman9
            else -> -1
        }
    }

    fun checkInput(input: String){
        if (input.length == 1){
            checkLetter(input)
        }
        else {
            checkWord(input)
        }
    }

    /**
     * Checks if the input letter is in the mystery word and updates the gallows, [usedLetters],
     * and [guessWord] states accordingly.
     * @param inputLetter The letter to be check.
     */
    fun checkLetter(inputLetter: String) {
        // Add the input letter to the used letters string.
        usedLetters += "$inputLetter, "
        // Check if the mystery word contains the input letter.
        if (mysteryWord.contains(inputLetter)) {
            // Update the guess word with the input letter.
            // buildString function is used to create a new string with the updated guess word.
            guessWord = buildString {
                // we iterate over the indices of the mystery word and append the input letter
                // if the letter in mystery word at the current index is equal to the input letter,
                // otherwise we append the letter in the guess word at the current index
                // (this will be either the previously guessed letter or an underscore).
                for (i in mysteryWord.indices) {
                    append(if (mysteryWord[i].toString() == inputLetter) inputLetter else guessWord[i])
                }
            }
        } else {
            // If the mystery word does not contain the input letter, we update the gallows state.
            currentGallowsState++
            currentGallowsDrawableId = getGallowsStateDrawable()
        }
    }

    fun checkWord(inputWord: String){
        if (inputWord.equals(mysteryWord, ignoreCase = true)){
            guessWord = mysteryWord
        }
        else {
            currentGallowsState += 2
            currentGallowsDrawableId = getGallowsStateDrawable()
        }
    }

    /**
     * Get the drawable id of the gallows image.
     * The [currentGallowsDrawableId] is private hence the need for the getter
     */
    fun getGallowsDrawableId() = currentGallowsDrawableId // this is a compact way of writing a function
    /**
     * Get the current [guessWord].
     */
    fun getGuessWord() = guessWord
    /**
     * Get the [usedLetters] string.
     */
    fun getUsedLetters(): String {
        return usedLetters
    } // this is a full way of writing a function

    /**
     * Enum class representing the state of the game.
     */
    enum class GameStatus {
        NOT_STARTED, STARTED, FINISHED_WON, FINISHED_LOST
    }

    /**
     * Validates the input letter. The input letter must be a single letter
     * (not any other character eg. number) that has not been used before.
     */
    fun validateInput(input: String): Boolean {
        // Sprawdzenie czy input nie jest pusty i zawiera tylko litery
        if (input.isEmpty() || input.any { !it.isLetter() }) return false

        // Jeśli to pojedyncza litera, sprawdzamy czy nie była użyta
        if (input.length == 1) {
            return !usedLetters.contains(input[0])
        }

        // Jeśli to całe słowo, nie ma potrzeby sprawdzania użytych liter, bo nie przechowujemy całych słów w `usedLetters`
        return true
    }

    /**
     * Checks if the game has been won.
     */
    private fun isGameWon(): Boolean {
        // The game is won when the guess word is equal to the mystery word.
        return guessWord == mysteryWord
    }
    /**
     * Checks if the game has been lost.
     */
    private fun isGameLost(): Boolean {
        // The game is lost when the gallows state is equal to the last state.
        return currentGallowsDrawableId == R.drawable.hangman9
    }
    /**
     * Checks if the game has finished (won or lost).
     */
    fun isGameFinished(): Boolean {
        return isGameWon() || isGameLost()
    }
    /**
     * Get the current state of the game.
     * @return The current state of the game (only STARTED, FINISHED_WON and FINISHED_LOST are valid).
     */
    fun getGameStatus(): GameStatus {
        return when {
            isGameWon() -> GameStatus.FINISHED_WON
            isGameLost() -> GameStatus.FINISHED_LOST
            else -> GameStatus.STARTED
        }
    }
}




package org.wolkern.cgg

import kotlinx.serialization.Serializable
import org.wolkern.cgg.domain.ChaosCharacter

@Serializable
data class SaveState(
    val characterList: List<ChaosCharacter>,
    val currentScreen: String,
    val currentTurn: Int,
    val selectedCharIndex: Int,
    val currentTurnRolls: Map<Int, Int>,
    val characterListAtStartOfTurn: List<ChaosCharacter>,
    val deadCharactersIndices: Set<Int>,
    val isGameActive: Boolean
)

const val SAVE_KEY = "chaos_warband_save"
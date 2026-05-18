package org.wolkern.cgg

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.json.Json
import org.wolkern.cgg.logic.EyeOfTheGodsRules
import org.wolkern.cgg.ui.screens.CharacterSetupScreen
import org.wolkern.cgg.ui.theme.*

@Composable
fun App(storage: KeyValueStorage) {
    var savedStateLoaded by remember { mutableStateOf<SaveState?>(null) }
    var isLoadChecked by remember { mutableStateOf(false) }

    // Un seul chargement au démarrage de l'application
    LaunchedEffect(Unit) {
        try {
            val jsonString = storage.getString(SAVE_KEY)
            if (!jsonString.isNullOrEmpty()) {
                savedStateLoaded = Json.decodeFromString<SaveState>(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadChecked = true
        }
    }

    // Écran de chargement pendant la lecture du stockage public
    if (!isLoadChecked) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = DarkBg) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TzeentchGold)
                }
            }
        }
        return
    }

    // Initialisation des états avec les données de sauvegarde (ou valeurs par défaut)
    var characterList by remember { mutableStateOf(savedStateLoaded?.characterList ?: listOf()) }
    var currentScreen by remember { mutableStateOf(savedStateLoaded?.currentScreen ?: "SETUP") }
    var currentTurn by remember { mutableStateOf(savedStateLoaded?.currentTurn ?: 1) }
    var selectedCharIndex by remember { mutableStateOf(savedStateLoaded?.selectedCharIndex ?: -1) }
    var currentTurnRolls by remember { mutableStateOf(savedStateLoaded?.currentTurnRolls ?: mapOf()) }
    var characterListAtStartOfTurn by remember { mutableStateOf(savedStateLoaded?.characterListAtStartOfTurn ?: listOf()) }
    var deadCharactersIndices by remember { mutableStateOf(savedStateLoaded?.deadCharactersIndices ?: setOf()) }
    var isGameActive by remember { mutableStateOf(savedStateLoaded?.isGameActive ?: false) }

    // Fonction centrale de sauvegarde automatique
    val triggerSave: (String) -> Unit = { screen ->
        try {
            val jsonString = Json.encodeToString(
                SaveState(
                    characterList, screen, currentTurn, selectedCharIndex,
                    currentTurnRolls, characterListAtStartOfTurn, deadCharactersIndices, isGameActive
                )
            )
            storage.saveString(SAVE_KEY, jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = DarkBg) {
            when (currentScreen) {
                "SETUP" -> {
                    CharacterSetupScreen(
                        characters = characterList, isGameActive = isGameActive,
                        onResumeGame = { currentScreen = "MONITORING"; triggerSave("MONITORING") },
                        onCharacterCreated = { newChar -> characterList = characterList + newChar; triggerSave("SETUP") },
                        onCharacterUpdated = { index, updatedChar ->
                            val newList = characterList.toMutableList()
                            newList[index] = updatedChar
                            characterList = newList
                            triggerSave("SETUP")
                        },
                        onCharacterDeleted = { index ->
                            val newList = characterList.toMutableList()
                            newList.removeAt(index)
                            characterList = newList
                            triggerSave("SETUP")
                        },
                        onStartGame = {
                            if (characterList.isNotEmpty()) {
                                val resetList = characterList.map { char ->
                                    char.copy().apply {
                                        permCC = 0; permF = 0; permE = 0; permI = 0; permA = 0; permCd = 0
                                        tempE = 0; tempI = 0; isStupid = false
                                    }
                                }
                                characterList = resetList
                                characterListAtStartOfTurn = resetList.map { it.copy() }
                                currentTurn = 1; currentTurnRolls = emptyMap(); deadCharactersIndices = emptySet()
                                isGameActive = true; currentScreen = "MONITORING"; triggerSave("MONITORING")
                            }
                        }
                    )
                }
                "MONITORING" -> {
                    val livingIndices = characterList.indices.filter { it !in deadCharactersIndices }
                    val allLivingHaveRolled = livingIndices.isNotEmpty() && livingIndices.all { currentTurnRolls.containsKey(it) }

                    GameMonitoringScreen(
                        characters = characterList, currentTurn = currentTurn, deadIndices = deadCharactersIndices, isNextTurnEnabled = allLivingHaveRolled,
                        onSelectCharacter = { index -> selectedCharIndex = index; currentScreen = "DASHBOARD"; triggerSave("DASHBOARD") },
                        onNextTurnManual = {
                            val cleanedList = characterList.map { char -> char.copy().apply { tempE = 0; tempI = 0 } }
                            characterList = cleanedList
                            characterListAtStartOfTurn = cleanedList.map { it.copy() }
                            currentTurnRolls = emptyMap(); currentTurn += 1; triggerSave("MONITORING")
                        },
                        onQuitGame = { currentScreen = "SETUP"; triggerSave("SETUP") }
                    )
                }
                "DASHBOARD" -> {
                    DashboardScreen(
                        character = characterList[selectedCharIndex], currentTurn = currentTurn, selectedRoll = currentTurnRolls[selectedCharIndex], isDead = deadCharactersIndices.contains(selectedCharIndex),
                        onRollSelected = { roll ->
                            val baseCharacterClean = characterListAtStartOfTurn[selectedCharIndex].copy()
                            val message = EyeOfTheGodsRules.applyRoll(baseCharacterClean, roll)
                            currentTurnRolls = currentTurnRolls + (selectedCharIndex to roll)
                            val newList = characterList.toMutableList()
                            newList[selectedCharIndex] = baseCharacterClean
                            characterList = newList
                            triggerSave("DASHBOARD")
                            message
                        },
                        onResetCharacter = {
                            currentTurnRolls = currentTurnRolls - selectedCharIndex
                            val newList = characterList.toMutableList()
                            newList[selectedCharIndex] = characterListAtStartOfTurn[selectedCharIndex].copy()
                            characterList = newList
                            triggerSave("DASHBOARD")
                        },
                        onToggleDeath = {
                            if (deadCharactersIndices.contains(selectedCharIndex)) {
                                deadCharactersIndices = deadCharactersIndices - selectedCharIndex
                            } else {
                                deadCharactersIndices = deadCharactersIndices + selectedCharIndex
                                currentTurnRolls = currentTurnRolls - selectedCharIndex
                            }
                            triggerSave("DASHBOARD")
                        },
                        onBackToMonitoring = { currentScreen = "MONITORING"; selectedCharIndex = -1; triggerSave("MONITORING") }
                    )
                }
            }
        }
    }
}
package org.wolkern.cgg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wolkern.cgg.ui.components.SettingsButton
import org.wolkern.cgg.domain.ChaosCharacter
import org.wolkern.cgg.ui.theme.*

@Composable
fun CharacterSetupScreen(
    characters: List<ChaosCharacter>,
    isGameActive: Boolean,
    onOpenSettings: () -> Unit,
    onResumeGame: () -> Unit,
    onCharacterCreated: (ChaosCharacter) -> Unit,
    onCharacterUpdated: (Int, ChaosCharacter) -> Unit,
    onCharacterDeleted: (Int) -> Unit,
    onStartGame: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cc by remember { mutableStateOf("6") }
    var f by remember { mutableStateOf("4") }
    var e by remember { mutableStateOf("4") }
    var i by remember { mutableStateOf("5") }
    var a by remember { mutableStateOf("3") }
    var cd by remember { mutableStateOf("8") }

    var editingIndex by remember { mutableStateOf(-1) }
    var showResetDialog by remember { mutableStateOf(false) }

    val darkFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextLight,
        unfocusedTextColor = TextLight,
        focusedBorderColor = TzeentchGold,
        unfocusedBorderColor = BorderColor,
        focusedLabelColor = TzeentchGold,
        unfocusedLabelColor = TextDark
    )

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = CardBg,
            titleContentColor = TzeentchGold,
            textContentColor = TextLight,
            title = { Text(text = "RÉINITIALISER LA PARTIE ?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text(text = "Attention, guerrier. Cela va effacer la progression actuelle, réinitialiser tous les attributs bonus et ressusciter les morts. Continuer ?") },
            confirmButton = {
                Button(
                    onClick = { showResetDialog = false; onStartGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = KhorneRed),
                    shape = RoundedCornerShape(4.dp)
                ) { Text("OUI, RECOMMENCER", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Button(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MutedSurface),
                    shape = RoundedCornerShape(4.dp)
                ) { Text("ANNULER", color = TextLight) }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.border(1.dp, BorderColor, shape = RoundedCornerShape(8.dp))
        )
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars).padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Création de la Bande".uppercase(), color = TzeentchGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            SettingsButton(onClick = onOpenSettings)
        }

        Box(modifier = Modifier.fillMaxWidth().background(CardBg, shape = RoundedCornerShape(8.dp)).border(1.dp, BorderColor, shape = RoundedCornerShape(8.dp)).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = if (editingIndex == -1) "Invoquer un Champion" else "Modifier ${characters[editingIndex].name}", color = TextLight, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom du Champion") }, colors = darkFieldColors, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val modifier = Modifier.weight(1f)
                    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    OutlinedTextField(value = cc, onValueChange = { cc = it }, label = { Text("CC") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                    OutlinedTextField(value = f, onValueChange = { f = it }, label = { Text("F") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                    OutlinedTextField(value = e, onValueChange = { e = it }, label = { Text("E") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                    OutlinedTextField(value = i, onValueChange = { i = it }, label = { Text("I") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                    OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("A") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                    OutlinedTextField(value = cd, onValueChange = { cd = it }, label = { Text("Cd") }, keyboardOptions = keyboardOptions, colors = darkFieldColors, modifier = modifier)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (editingIndex != -1) {
                        Button(
                            onClick = { editingIndex = -1; name = ""; cc = "6"; f = "4"; e = "4"; i = "5"; a = "3"; cd = "8" },
                            colors = ButtonDefaults.buttonColors(containerColor = MutedSurface),
                            modifier = Modifier.weight(1f)
                        ) { Text("Annuler", color = TextLight) }
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val char = ChaosCharacter(
                                    name = name,
                                    baseCC = cc.toIntOrNull() ?: 4, baseF = f.toIntOrNull() ?: 4, baseE = e.toIntOrNull() ?: 4,
                                    baseI = i.toIntOrNull() ?: 5, baseA = a.toIntOrNull() ?: 3, baseCd = cd.toIntOrNull() ?: 8
                                )
                                if (editingIndex == -1) onCharacterCreated(char) else onCharacterUpdated(editingIndex, char)
                                name = ""; cc = "6"; f = "4"; e = "4"; i = "5"; a = "3"; cd = "8"; editingIndex = -1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TzeentchGold),
                        modifier = Modifier.weight(2f)
                    ) { Text(if (editingIndex == -1) "Ajouter" else "Enregistrer", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Guerriers prêts :", color = TextDark, fontSize = 14.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(characters.size) { index ->
                val char = characters[index]
                Row(
                    modifier = Modifier.fillMaxWidth().background(CardBg, shape = RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(char.name.uppercase(), color = TextLight, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Row {
                        IconButton(onClick = {
                            editingIndex = index; name = char.name; cc = char.baseCC.toString(); f = char.baseF.toString()
                            e = char.baseE.toString(); i = char.baseI.toString(); a = char.baseA.toString(); cd = char.baseCd.toString()
                        }) { Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = TzeentchGold) }
                        IconButton(onClick = { if (editingIndex == index) { editingIndex = -1; name = "" }; onCharacterDeleted(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = KhorneRed)
                        }
                    }
                }
            }
        }

        if (characters.isNotEmpty() && editingIndex == -1) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).height(54.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isGameActive) {
                    Button(onClick = onResumeGame, colors = ButtonDefaults.buttonColors(containerColor = BonusGreen), modifier = Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(8.dp)) {
                        Text(text = "REPRENDRE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { if (isGameActive) showResetDialog = true else onStartGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = KhorneRed),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = if (isGameActive) "RECOMMENCER" else "COMMENCER LA PARTIE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
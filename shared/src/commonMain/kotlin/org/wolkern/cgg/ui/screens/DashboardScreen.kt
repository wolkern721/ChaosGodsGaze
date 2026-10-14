package org.wolkern.cgg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wolkern.cgg.ui.components.StatCell
import org.wolkern.cgg.ui.components.SettingsButton
import org.wolkern.cgg.domain.ChaosCharacter
import org.wolkern.cgg.ui.theme.*

@Composable
fun DashboardScreen(
    character: ChaosCharacter,
    currentTurn: Int,
    selectedRoll: Int?,
    isDead: Boolean,
    onRollSelected: (Int) -> String,
    onResetCharacter: () -> Unit,
    onToggleDeath: () -> Unit,
    onOpenSettings: () -> Unit,
    onBackToMonitoring: () -> Unit
) {
    var gameLog by remember {
        mutableStateOf(
            if (isDead) "Ce personnage est mort. Ses statistiques ne sont plus prises en compte."
            else if (selectedRoll != null) "Ce héros applique le résultat $selectedRoll pour ce tour."
            else "Aucun dé appliqué pour ce tour sur ${character.name}."
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "‹ Retour", color = TzeentchGold, modifier = Modifier.clickable { onBackToMonitoring() }, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "TOUR $currentTurn", color = TextDark, fontSize = 16.sp)
            SettingsButton(onClick = onOpenSettings)
        }

        Text(text = character.name.uppercase(), color = if (isDead) TextDark else TextLight, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        if (isDead) {
            Box(modifier = Modifier.background(KhorneRed, shape = RoundedCornerShape(4.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text("CADAVRE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        } else if (character.isStupid) {
            Box(modifier = Modifier.background(Color(0xFF8B4513), shape = RoundedCornerShape(4.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text("STUPIDITÉ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        } else {
            Text("État : Actif", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCell("CC", character.baseCC + character.permCC, character.baseCC, isDead)
            StatCell("F", character.baseF + character.permF, character.baseF, isDead)
            StatCell("E", character.baseE + character.tempE, character.baseE, isDead)
            StatCell("I", character.baseI + character.tempI, character.baseI, isDead)
            StatCell("A", character.baseA + character.permA, character.baseA, isDead)
            StatCell("Cd", character.baseCd + character.permCd, character.baseCd, isDead)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Box(modifier = Modifier.fillMaxWidth().background(CardBg, shape = RoundedCornerShape(8.dp)).border(1.dp, BorderColor, shape = RoundedCornerShape(8.dp)).padding(16.dp)) {
            Text(text = gameLog, color = TextLight, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (!isDead) {
                Text(text = "Sélectionnez l'effet du tour", color = TextDark, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (roll in 1..6) {
                        val isCurrentButtonSelected = (selectedRoll == roll)
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f).background(if (isCurrentButtonSelected) TzeentchGold else CardBg, shape = RoundedCornerShape(8.dp)).border(2.dp, TzeentchGold, shape = RoundedCornerShape(8.dp))
                                .clickable { if (!isCurrentButtonSelected) { val logMessage = onRollSelected(roll); gameLog = "Jet modifié pour le dé $roll : $logMessage" } },
                            contentAlignment = Alignment.Center
                        ) { Text(text = roll.toString(), color = if (isCurrentButtonSelected) Color.Black else TzeentchGold, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onToggleDeath(); gameLog = if (!isDead) "Le champion a succombé." else "Le champion est ressuscité." },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDead) BonusGreen else KhorneRed),
                    modifier = Modifier.weight(1.2f).height(50.dp), shape = RoundedCornerShape(8.dp)
                ) { Text(if (isDead) "RESSUSCITER" else "TUER LE HÉROS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                if (!isDead) {
                    Button(onClick = { onResetCharacter(); gameLog = "Jet annulé pour ce tour." }, colors = ButtonDefaults.buttonColors(containerColor = MutedSurface), modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(8.dp)) {
                        Text("ANNULER", color = TextLight, fontSize = 11.sp)
                    }
                }
                Button(onClick = onBackToMonitoring, colors = ButtonDefaults.buttonColors(containerColor = if (isDead) MutedSurface else KhorneRed), modifier = Modifier.weight(1.2f).height(50.dp), shape = RoundedCornerShape(8.dp)) {
                    Text("VALIDER", color = if (isDead) TextLight else Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
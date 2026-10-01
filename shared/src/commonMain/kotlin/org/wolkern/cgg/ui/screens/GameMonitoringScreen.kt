package org.wolkern.cgg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wolkern.cgg.ui.components.MonitoringStatLabel
import org.wolkern.cgg.domain.ChaosCharacter
import org.wolkern.cgg.ui.theme.*

@Composable
fun GameMonitoringScreen(
    characters: List<ChaosCharacter>,
    currentTurn: Int,
    deadIndices: Set<Int>,
    onSelectCharacter: (Int) -> Unit,
    onNextTurnManual: () -> Unit,
    onQuitGame: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "MONITORING DE LA PARTIE", color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "TOUR EN COURS : $currentTurn", color = TzeentchGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "Quitter", color = KhorneRed, modifier = Modifier.clickable { onQuitGame() }, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Text(text = "Statut des combattants :", color = TextLight, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(characters.size) { index ->
                val char = characters[index]
                val isDead = deadIndices.contains(index)

                val ccTot = char.baseCC + char.permCC
                val fTot = char.baseF + char.permF
                val eTot = char.baseE + char.tempE
                val iTot = char.baseI + char.tempI
                val aTot = char.baseA + char.permA
                val cdTot = char.baseCd + char.permCd

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDead) Color(0xFF1A1111) else CardBg, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, if (isDead) KhorneRed.copy(alpha = 0.5f) else if (char.isStupid) Color(0xFFD32F2F) else Color(0xFF2C2C35), shape = RoundedCornerShape(8.dp))
                        .clickable { onSelectCharacter(index) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(char.name.uppercase(), color = if (isDead) TextDark else TextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if (isDead) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DÉCÉDÉ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(KhorneRed).padding(horizontal = 4.dp, vertical = 2.dp))
                            } else if (char.isStupid) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("STUPIDE", color = Color(0xFFD32F2F), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color(0xFF2A0808)).padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (isDead) {
                                Text("Mort au combat — Aucun attribut disponible.", color = TextDark, fontSize = 13.sp)
                            } else {
                                MonitoringStatLabel("CC", ccTot, char.baseCC)
                                MonitoringStatLabel("F", fTot, char.baseF)
                                MonitoringStatLabel("E", eTot, char.baseE)
                                MonitoringStatLabel("I", iTot, char.baseI)
                                MonitoringStatLabel("A", aTot, char.baseA)
                                MonitoringStatLabel("Cd", cdTot, char.baseCd)
                            }
                        }
                    }
                    Text(text = if (isDead) "Gérer ›" else "Lancer Dés ›", color = if (isDead) TextDark else TzeentchGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNextTurnManual,
            colors = ButtonDefaults.buttonColors(containerColor = TzeentchGold),
            modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).height(55.dp), shape = RoundedCornerShape(8.dp)
        ) { Text(text = "PASSER AU TOUR SUIVANT ›", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}
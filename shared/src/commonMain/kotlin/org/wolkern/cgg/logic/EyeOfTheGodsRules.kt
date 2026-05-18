package org.wolkern.cgg.logic

import org.wolkern.cgg.domain.ChaosCharacter

object EyeOfTheGodsRules {
    fun applyRoll(char: ChaosCharacter, roll: Int): String {
        return when (roll) {
            1 -> {
                if (!char.isStupid) {
                    char.isStupid = true
                    "Stupide ! Le personnage devient Stupide."
                } else {
                    char.permCd -= 1
                    "Déjà stupide ! Malus cumulatif de -1 en Commandement."
                }
            }
            2 -> {
                char.tempI += 1
                "+1 en Initiative (Temporaire)"
            }
            3 -> {
                char.tempE += 1
                "+1 en Endurance (Temporaire)"
            }
            4 -> {
                char.permCC += 1
                "+1 en Capacité de Combat (Permanent)"
            }
            5 -> {
                char.permA += 1
                "+1 Attaque (Permanent)"
            }
            6 -> {
                char.permF += 1
                char.permCd += 1
                "+1 en Force et +1 en Commandement (Permanent)"
            }
            else -> "Jet invalide"
        }
    }

    fun cancelRoll(character: ChaosCharacter, roll: Int) {
        when (roll) {
            1 -> character.isStupid = false // Annule la stupidité
            2 -> character.permCC = (character.permCC - 1).coerceAtLeast(0) // Retire le +1 CC
            3 -> character.permF = (character.permF - 1).coerceAtLeast(0) // Retire le +1 F
            4 -> character.tempE = (character.tempE - 1).coerceAtLeast(0) // Retire le +1 E
            5 -> character.tempI = (character.tempI - 1).coerceAtLeast(0) // Retire le +1 I
            6 -> {
                character.permA = (character.permA - 1).coerceAtLeast(0) // Retire le +1 A
                character.permCd -= 1 // Exemple de gestion Cd
            }
        }
    }
}
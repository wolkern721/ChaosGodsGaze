package org.wolkern.cgg.domain

import kotlinx.serialization.Serializable

@Serializable
data class ChaosCharacter(
    val name: String,

    // Statistiques de base du profil Warhammer
    val baseCC: Int,
    val baseF: Int,
    val baseE: Int,
    val baseI: Int,
    val baseA: Int,
    val baseCd: Int,

    // États spéciaux de la règle
    var isStupid: Boolean = false,

    // Bonus Permanents (Cumulables, durent toute la partie)
    var permCC: Int = 0,
    var permF: Int = 0,
    var permA: Int = 0,
    var permE: Int = 0,
    var permI: Int = 0,
    var permCd: Int = 0,

    // Bonus Temporaires (Durent jusqu'au prochain tour)
    var tempI: Int = 0,
    var tempE: Int = 0
)
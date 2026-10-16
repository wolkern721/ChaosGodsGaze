export interface ChaosCharacter {
  name: string
  // Statistiques de base du profil Warhammer
  baseCC: number
  baseF: number
  baseE: number
  baseI: number
  baseA: number
  baseCd: number

  // États spéciaux de la règle
  isStupid: boolean

  // Bonus Permanents (Cumulables, durent toute la partie)
  permCC: number
  permF: number
  permA: number
  permE: number
  permI: number
  permCd: number

  // Bonus Temporaires (Durent jusqu'au prochain tour)
  tempI: number
  tempE: number
}

export type ScreenType = 'SETUP' | 'MONITORING' | 'DASHBOARD'

export interface SaveState {
  characterList: ChaosCharacter[]
  currentScreen: ScreenType
  currentTurn: number
  selectedCharIndex: number
  currentTurnRolls: Record<number, number>
  characterListAtStartOfTurn: ChaosCharacter[]
  deadCharactersIndices: number[]
  isGameActive: boolean
}

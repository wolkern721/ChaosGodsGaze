export interface ChaosCharacter {
  id?: string
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

export interface Warband {
  id: string
  name: string
  createdAt: number
  characters: ChaosCharacter[]
}

export interface GameSession {
  id: string
  name: string
  warbandId: string
  warbandName: string
  createdAt: number
  updatedAt: number
  currentTurn: number
  currentTurnRolls: Record<number, number>
  characterList: ChaosCharacter[]
  characterListAtStartOfTurn: ChaosCharacter[]
  deadCharactersIndices: number[]
  selectedCharIndex: number
}

export type ScreenType = 'WARBANDS' | 'SETUP' | 'SESSIONS' | 'MONITORING' | 'DASHBOARD'

export interface SaveState {
  version?: number
  warbands?: Warband[]
  sessions?: GameSession[]
  selectedWarbandId?: string | null
  activeSessionId?: string | null
  currentScreen?: ScreenType

  // Legacy fields for backward compatibility
  characterList?: ChaosCharacter[]
  currentTurn?: number
  selectedCharIndex?: number
  currentTurnRolls?: Record<number, number>
  characterListAtStartOfTurn?: ChaosCharacter[]
  deadCharactersIndices?: number[]
  isGameActive?: boolean
}

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ChaosCharacter, ScreenType, SaveState } from '../types/character'
import { EyeOfTheGodsRules } from '../logic/eyeOfTheGodsRules'

const SAVE_KEY = 'chaos_warband_state'
const THEME_KEY = 'chaos_warband_theme'
const WAKE_LOCK_KEY = 'chaos_warband_wakelock'

function deepClone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj))
}

export const useGameStore = defineStore('game', () => {
  // Theme state : par défaut en mode jour (isDarkTheme = false) au premier démarrage
  const isDarkTheme = ref<boolean>(false)
  const showSettings = ref<boolean>(false)

  // Keep screen awake (Wake Lock)
  const keepScreenAwake = ref<boolean>(true)
  let wakeLockSentinel: WakeLockSentinel | null = null

  // Game state
  const characterList = ref<ChaosCharacter[]>([])
  const currentScreen = ref<ScreenType>('SETUP')
  const currentTurn = ref<number>(1)
  const selectedCharIndex = ref<number>(-1)
  const currentTurnRolls = ref<Record<number, number>>({})
  const characterListAtStartOfTurn = ref<ChaosCharacter[]>([])
  const deadCharactersIndices = ref<number[]>([])
  const isGameActive = ref<boolean>(false)

  // Current selected character
  const selectedCharacter = computed<ChaosCharacter | null>(() => {
    if (selectedCharIndex.value >= 0 && selectedCharIndex.value < characterList.value.length) {
      return characterList.value[selectedCharIndex.value]
    }
    return null
  })

  // Selected roll for current character
  const currentSelectedRoll = computed<number | undefined>(() => {
    return currentTurnRolls.value[selectedCharIndex.value]
  })

  const isSelectedCharacterDead = computed<boolean>(() => {
    return deadCharactersIndices.value.includes(selectedCharIndex.value)
  })

  async function requestWakeLock(): Promise<void> {
    if (!keepScreenAwake.value) return
    if (typeof navigator !== 'undefined' && 'wakeLock' in navigator) {
      try {
        wakeLockSentinel = await navigator.wakeLock.request('screen')
        wakeLockSentinel.addEventListener('release', () => {
          wakeLockSentinel = null
        })
      } catch (err) {
        console.warn('Wake Lock could not be acquired:', err)
      }
    }
  }

  async function releaseWakeLock(): Promise<void> {
    if (wakeLockSentinel !== null) {
      try {
        await wakeLockSentinel.release()
      } catch (err) {
        console.warn('Error releasing Wake Lock:', err)
      } finally {
        wakeLockSentinel = null
      }
    }
  }

  async function toggleKeepScreenAwake(enabled: boolean): Promise<void> {
    keepScreenAwake.value = enabled
    try {
      localStorage.setItem(WAKE_LOCK_KEY, enabled ? 'true' : 'false')
    } catch (e) {
      console.error(e)
    }

    if (enabled) {
      await requestWakeLock()
    } else {
      await releaseWakeLock()
    }
  }

  // Load state from localStorage
  async function loadState(): Promise<void> {
    try {
      const savedTheme = localStorage.getItem(THEME_KEY)
      if (savedTheme !== null) {
        isDarkTheme.value = savedTheme === 'dark'
      }

      const savedWakeLock = localStorage.getItem(WAKE_LOCK_KEY)
      if (savedWakeLock !== null) {
        keepScreenAwake.value = savedWakeLock === 'true'
      }

      const savedJson = localStorage.getItem(SAVE_KEY)
      if (savedJson) {
        const state: SaveState = JSON.parse(savedJson)
        characterList.value = state.characterList || []
        currentScreen.value = state.currentScreen || 'SETUP'
        currentTurn.value = state.currentTurn || 1
        selectedCharIndex.value = state.selectedCharIndex ?? -1
        currentTurnRolls.value = state.currentTurnRolls || {}
        characterListAtStartOfTurn.value = state.characterListAtStartOfTurn || []
        deadCharactersIndices.value = state.deadCharactersIndices || []
        isGameActive.value = state.isGameActive || false
      }
    } catch (e) {
      console.error('Error loading state from localStorage:', e)
    }

    if (keepScreenAwake.value) {
      await requestWakeLock()
    }
  }

  // Save state to localStorage
  function saveState(screen?: ScreenType) {
    if (screen) {
      currentScreen.value = screen
    }
    try {
      const state: SaveState = {
        characterList: characterList.value,
        currentScreen: currentScreen.value,
        currentTurn: currentTurn.value,
        selectedCharIndex: selectedCharIndex.value,
        currentTurnRolls: currentTurnRolls.value,
        characterListAtStartOfTurn: characterListAtStartOfTurn.value,
        deadCharactersIndices: deadCharactersIndices.value,
        isGameActive: isGameActive.value
      }
      localStorage.setItem(SAVE_KEY, JSON.stringify(state))
    } catch (e) {
      console.error('Error saving state to localStorage:', e)
    }
  }

  function toggleTheme(dark: boolean) {
    isDarkTheme.value = dark
    try {
      localStorage.setItem(THEME_KEY, dark ? 'dark' : 'light')
      document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
    } catch (e) {
      console.error(e)
    }
  }

  // Setup Actions
  function addCharacter(character: ChaosCharacter) {
    characterList.value = [...characterList.value, character]
    saveState('SETUP')
  }

  function updateCharacter(index: number, character: ChaosCharacter) {
    const list = [...characterList.value]
    list[index] = character
    characterList.value = list
    saveState('SETUP')
  }

  function deleteCharacter(index: number) {
    const list = [...characterList.value]
    list.splice(index, 1)
    characterList.value = list
    saveState('SETUP')
  }

  function startGame() {
    if (characterList.value.length === 0) return
    const resetList = characterList.value.map((char) => ({
      ...char,
      permCC: 0,
      permF: 0,
      permE: 0,
      permI: 0,
      permA: 0,
      permCd: 0,
      tempE: 0,
      tempI: 0,
      isStupid: false
    }))
    characterList.value = resetList
    characterListAtStartOfTurn.value = deepClone(resetList)
    currentTurn.value = 1
    currentTurnRolls.value = {}
    deadCharactersIndices.value = []
    isGameActive.value = true
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  function resumeGame() {
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  // Monitoring Actions
  function selectCharacter(index: number) {
    selectedCharIndex.value = index
    currentScreen.value = 'DASHBOARD'
    saveState('DASHBOARD')
  }

  function nextTurn() {
    const cleanedList = characterList.value.map((char) => ({
      ...char,
      tempE: 0,
      tempI: 0
    }))
    characterList.value = cleanedList
    characterListAtStartOfTurn.value = deepClone(cleanedList)
    currentTurnRolls.value = {}
    currentTurn.value += 1
    saveState('MONITORING')
  }

  function quitGame() {
    currentScreen.value = 'SETUP'
    saveState('SETUP')
  }

  // Dashboard Actions
  function applyRoll(roll: number): string {
    const idx = selectedCharIndex.value
    if (idx < 0 || idx >= characterList.value.length) return ''
    const baseCharacter = deepClone(characterListAtStartOfTurn.value[idx])
    const message = EyeOfTheGodsRules.applyRoll(baseCharacter, roll)

    currentTurnRolls.value = {
      ...currentTurnRolls.value,
      [idx]: roll
    }

    const list = [...characterList.value]
    list[idx] = baseCharacter
    characterList.value = list

    saveState('DASHBOARD')
    return message
  }

  function resetCharacterRoll() {
    const idx = selectedCharIndex.value
    if (idx < 0) return

    const newRolls = { ...currentTurnRolls.value }
    delete newRolls[idx]
    currentTurnRolls.value = newRolls

    const list = [...characterList.value]
    list[idx] = deepClone(characterListAtStartOfTurn.value[idx])
    characterList.value = list

    saveState('DASHBOARD')
  }

  function toggleDeath() {
    const idx = selectedCharIndex.value
    if (idx < 0) return

    if (deadCharactersIndices.value.includes(idx)) {
      deadCharactersIndices.value = deadCharactersIndices.value.filter((i) => i !== idx)
    } else {
      deadCharactersIndices.value = [...deadCharactersIndices.value, idx]
      const newRolls = { ...currentTurnRolls.value }
      delete newRolls[idx]
      currentTurnRolls.value = newRolls
    }

    saveState('DASHBOARD')
  }

  function backToMonitoring() {
    currentScreen.value = 'MONITORING'
    selectedCharIndex.value = -1
    saveState('MONITORING')
  }

  return {
    isDarkTheme,
    showSettings,
    keepScreenAwake,
    characterList,
    currentScreen,
    currentTurn,
    selectedCharIndex,
    currentTurnRolls,
    characterListAtStartOfTurn,
    deadCharactersIndices,
    isGameActive,
    selectedCharacter,
    currentSelectedRoll,
    isSelectedCharacterDead,
    loadState,
    saveState,
    toggleTheme,
    toggleKeepScreenAwake,
    requestWakeLock,
    releaseWakeLock,
    addCharacter,
    updateCharacter,
    deleteCharacter,
    startGame,
    resumeGame,
    selectCharacter,
    nextTurn,
    quitGame,
    applyRoll,
    resetCharacterRoll,
    toggleDeath,
    backToMonitoring
  }
})

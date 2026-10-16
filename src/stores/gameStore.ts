import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ChaosCharacter, Warband, GameSession, ScreenType, SaveState } from '../types/character'
import { EyeOfTheGodsRules } from '../logic/eyeOfTheGodsRules'

const SAVE_KEY = 'chaos_warband_state'
const THEME_KEY = 'chaos_warband_theme'
const WAKE_LOCK_KEY = 'chaos_warband_wakelock'

function deepClone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj))
}

function generateId(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID()
  }
  return 'id_' + Date.now() + '_' + Math.random().toString(36).substring(2, 9)
}

function createDefaultWarband(): Warband {
  return {
    id: generateId(),
    name: 'Horde du Chaos',
    createdAt: Date.now(),
    characters: [
      {
        id: generateId(),
        name: 'Seigneur du Chaos',
        baseCC: 6,
        baseF: 5,
        baseE: 4,
        baseI: 5,
        baseA: 4,
        baseCd: 9,
        isStupid: false,
        permCC: 0,
        permF: 0,
        permA: 0,
        permE: 0,
        permI: 0,
        permCd: 0,
        tempI: 0,
        tempE: 0
      },
      {
        id: generateId(),
        name: 'Héros Exalté',
        baseCC: 5,
        baseF: 4,
        baseE: 4,
        baseI: 4,
        baseA: 3,
        baseCd: 8,
        isStupid: false,
        permCC: 0,
        permF: 0,
        permA: 0,
        permE: 0,
        permI: 0,
        permCd: 0,
        tempI: 0,
        tempE: 0
      }
    ]
  }
}

export const useGameStore = defineStore('game', () => {
  // Theme state : par défaut en mode jour (isDarkTheme = false) au premier démarrage
  const isDarkTheme = ref<boolean>(false)
  const showSettings = ref<boolean>(false)

  // Keep screen awake (Wake Lock)
  const keepScreenAwake = ref<boolean>(true)
  let wakeLockSentinel: WakeLockSentinel | null = null

  // Navigation & State
  const currentScreen = ref<ScreenType>('WARBANDS')

  // Multi-Warband (Rosters)
  const warbands = ref<Warband[]>([])
  const selectedWarbandId = ref<string | null>(null)

  // Multi-Session (Games)
  const sessions = ref<GameSession[]>([])
  const activeSessionId = ref<string | null>(null)

  // Computed: currently edited warband
  const currentWarband = computed<Warband | null>(() => {
    if (!selectedWarbandId.value) return warbands.value[0] || null
    return warbands.value.find((w) => w.id === selectedWarbandId.value) || warbands.value[0] || null
  })

  // Computed: currently active session
  const activeSession = computed<GameSession | null>(() => {
    if (!activeSessionId.value) return null
    return sessions.value.find((s) => s.id === activeSessionId.value) || null
  })

  // Computed helpers for in-game views (Monitoring & Dashboard)
  const currentTurn = computed<number>(() => {
    return activeSession.value ? activeSession.value.currentTurn : 1
  })

  const characterList = computed<ChaosCharacter[]>(() => {
    if (activeSession.value) {
      return activeSession.value.characterList
    }
    if (currentWarband.value) {
      return currentWarband.value.characters
    }
    return []
  })

  const deadCharactersIndices = computed<number[]>(() => {
    return activeSession.value ? activeSession.value.deadCharactersIndices : []
  })

  const selectedCharIndex = computed<number>(() => {
    return activeSession.value ? activeSession.value.selectedCharIndex : -1
  })

  const selectedCharacter = computed<ChaosCharacter | null>(() => {
    if (!activeSession.value) return null
    const idx = activeSession.value.selectedCharIndex
    if (idx >= 0 && idx < activeSession.value.characterList.length) {
      return activeSession.value.characterList[idx]
    }
    return null
  })

  const currentSelectedRoll = computed<number | undefined>(() => {
    if (!activeSession.value) return undefined
    return activeSession.value.currentTurnRolls[activeSession.value.selectedCharIndex]
  })

  const isSelectedCharacterDead = computed<boolean>(() => {
    if (!activeSession.value) return false
    return activeSession.value.deadCharactersIndices.includes(activeSession.value.selectedCharIndex)
  })

  // Wake Lock functions
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

  // Theme function
  function toggleTheme(dark: boolean) {
    isDarkTheme.value = dark
    try {
      localStorage.setItem(THEME_KEY, dark ? 'dark' : 'light')
      document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
    } catch (e) {
      console.error(e)
    }
  }

  // Save state
  function saveState(screen?: ScreenType) {
    if (screen) {
      currentScreen.value = screen
    }
    try {
      const state: SaveState = {
        version: 2,
        warbands: warbands.value,
        sessions: sessions.value,
        selectedWarbandId: selectedWarbandId.value,
        activeSessionId: activeSessionId.value,
        currentScreen: currentScreen.value
      }
      localStorage.setItem(SAVE_KEY, JSON.stringify(state))
    } catch (e) {
      console.error('Error saving state to localStorage:', e)
    }
  }

  // Load state with migration support
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

        if (state.version === 2 && Array.isArray(state.warbands)) {
          warbands.value = state.warbands
          sessions.value = state.sessions || []
          selectedWarbandId.value = state.selectedWarbandId || null
          activeSessionId.value = state.activeSessionId || null
          currentScreen.value = state.currentScreen || 'WARBANDS'
        } else {
          // Backward Compatibility Migration from V1
          const legacyChars = state.characterList || []
          const migratedWarband: Warband = {
            id: generateId(),
            name: 'Ma Bande du Chaos',
            createdAt: Date.now(),
            characters: legacyChars.map((c) => ({
              ...c,
              id: generateId(),
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
          }
          warbands.value = legacyChars.length > 0 ? [migratedWarband] : [createDefaultWarband()]
          selectedWarbandId.value = warbands.value[0].id

          if (state.isGameActive && legacyChars.length > 0) {
            const migratedSession: GameSession = {
              id: generateId(),
              name: 'Partie Migrée',
              warbandId: migratedWarband.id,
              warbandName: migratedWarband.name,
              createdAt: Date.now(),
              updatedAt: Date.now(),
              currentTurn: state.currentTurn || 1,
              currentTurnRolls: state.currentTurnRolls || {},
              characterList: legacyChars,
              characterListAtStartOfTurn: state.characterListAtStartOfTurn || deepClone(legacyChars),
              deadCharactersIndices: state.deadCharactersIndices || [],
              selectedCharIndex: state.selectedCharIndex ?? -1
            }
            sessions.value = [migratedSession]
            activeSessionId.value = migratedSession.id
            currentScreen.value = state.currentScreen || 'MONITORING'
          } else {
            currentScreen.value = 'WARBANDS'
          }
          saveState()
        }
      } else {
        // Initial clean state with default warband
        warbands.value = [createDefaultWarband()]
        selectedWarbandId.value = warbands.value[0].id
        currentScreen.value = 'WARBANDS'
        saveState()
      }
    } catch (e) {
      console.error('Error loading state from localStorage:', e)
      if (warbands.value.length === 0) {
        warbands.value = [createDefaultWarband()]
        selectedWarbandId.value = warbands.value[0].id
      }
    }

    if (keepScreenAwake.value) {
      await requestWakeLock()
    }
  }

  // --- Warband Management ---

  function navigateTo(screen: ScreenType) {
    currentScreen.value = screen
    saveState(screen)
  }

  function createWarband(name: string): string {
    const trimmed = name.trim() || 'Nouvelle Bande'
    const newWarband: Warband = {
      id: generateId(),
      name: trimmed,
      createdAt: Date.now(),
      characters: []
    }
    warbands.value = [newWarband, ...warbands.value]
    selectedWarbandId.value = newWarband.id
    currentScreen.value = 'SETUP'
    saveState('SETUP')
    return newWarband.id
  }

  function duplicateWarband(warbandId: string): string {
    const source = warbands.value.find((w) => w.id === warbandId)
    if (!source) return ''

    const clonedCharacters = deepClone(source.characters).map((c) => ({
      ...c,
      id: generateId(),
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

    const newWarband: Warband = {
      id: generateId(),
      name: `${source.name} (Copie)`,
      createdAt: Date.now(),
      characters: clonedCharacters
    }

    const sourceIndex = warbands.value.findIndex((w) => w.id === warbandId)
    const updated = [...warbands.value]
    updated.splice(sourceIndex + 1, 0, newWarband)
    warbands.value = updated
    saveState()
    return newWarband.id
  }

  function deleteWarband(warbandId: string) {
    warbands.value = warbands.value.filter((w) => w.id !== warbandId)
    if (selectedWarbandId.value === warbandId) {
      selectedWarbandId.value = warbands.value[0]?.id || null
    }
    saveState()
  }

  function updateWarbandName(warbandId: string, newName: string) {
    const w = warbands.value.find((item) => item.id === warbandId)
    if (w) {
      w.name = newName.trim() || 'Bande sans nom'
      saveState()
    }
  }

  function openWarbandSetup(warbandId: string) {
    selectedWarbandId.value = warbandId
    currentScreen.value = 'SETUP'
    saveState('SETUP')
  }

  // --- Character Edition inside current Warband ---

  function addCharacter(character: ChaosCharacter) {
    const warband = currentWarband.value
    if (!warband) return
    const newChar: ChaosCharacter = {
      ...character,
      id: generateId(),
      permCC: 0,
      permF: 0,
      permE: 0,
      permI: 0,
      permA: 0,
      permCd: 0,
      tempE: 0,
      tempI: 0,
      isStupid: false
    }
    warband.characters.push(newChar)
    saveState('SETUP')
  }

  function updateCharacter(index: number, character: ChaosCharacter) {
    const warband = currentWarband.value
    if (!warband || index < 0 || index >= warband.characters.length) return
    warband.characters[index] = {
      ...character,
      id: warband.characters[index].id || generateId()
    }
    saveState('SETUP')
  }

  function deleteCharacter(index: number) {
    const warband = currentWarband.value
    if (!warband || index < 0 || index >= warband.characters.length) return
    warband.characters.splice(index, 1)
    saveState('SETUP')
  }

  // --- Game Session Management ---

  function startNewGameFromWarband(warbandId: string, sessionName?: string) {
    const warband = warbands.value.find((w) => w.id === warbandId)
    if (!warband || warband.characters.length === 0) return

    const initialChars: ChaosCharacter[] = deepClone(warband.characters).map((c) => ({
      ...c,
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

    const defaultName = `${warband.name} - Bataille ${sessions.value.length + 1}`
    const newSession: GameSession = {
      id: generateId(),
      name: sessionName?.trim() || defaultName,
      warbandId: warband.id,
      warbandName: warband.name,
      createdAt: Date.now(),
      updatedAt: Date.now(),
      currentTurn: 1,
      currentTurnRolls: {},
      characterList: initialChars,
      characterListAtStartOfTurn: deepClone(initialChars),
      deadCharactersIndices: [],
      selectedCharIndex: -1
    }

    sessions.value = [newSession, ...sessions.value]
    activeSessionId.value = newSession.id
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  function resumeSession(sessionId: string) {
    const s = sessions.value.find((item) => item.id === sessionId)
    if (!s) return
    activeSessionId.value = s.id
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  function restartSession(sessionId: string) {
    const s = sessions.value.find((item) => item.id === sessionId)
    if (!s) return
    const warband = warbands.value.find((w) => w.id === s.warbandId)
    const baseSource = warband ? warband.characters : s.characterList

    const resetChars: ChaosCharacter[] = deepClone(baseSource).map((c) => ({
      ...c,
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

    s.currentTurn = 1
    s.currentTurnRolls = {}
    s.characterList = resetChars
    s.characterListAtStartOfTurn = deepClone(resetChars)
    s.deadCharactersIndices = []
    s.selectedCharIndex = -1
    s.updatedAt = Date.now()

    activeSessionId.value = s.id
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  function deleteSession(sessionId: string) {
    sessions.value = sessions.value.filter((s) => s.id !== sessionId)
    if (activeSessionId.value === sessionId) {
      activeSessionId.value = null
    }
    saveState()
  }

  function quitSessionToSessionsList() {
    if (activeSession.value) {
      activeSession.value.selectedCharIndex = -1
    }
    currentScreen.value = 'SESSIONS'
    saveState('SESSIONS')
  }

  // --- Monitoring & Dashboard Actions (Inside active session) ---

  function selectCharacter(index: number) {
    if (!activeSession.value) return
    activeSession.value.selectedCharIndex = index
    currentScreen.value = 'DASHBOARD'
    saveState('DASHBOARD')
  }

  function nextTurn() {
    if (!activeSession.value) return
    const cleanedList = activeSession.value.characterList.map((char) => ({
      ...char,
      tempE: 0,
      tempI: 0
    }))
    activeSession.value.characterList = cleanedList
    activeSession.value.characterListAtStartOfTurn = deepClone(cleanedList)
    activeSession.value.currentTurnRolls = {}
    activeSession.value.currentTurn += 1
    activeSession.value.updatedAt = Date.now()
    saveState('MONITORING')
  }

  function applyRoll(roll: number): string {
    if (!activeSession.value) return ''
    const s = activeSession.value
    const idx = s.selectedCharIndex
    if (idx < 0 || idx >= s.characterList.length) return ''

    const baseCharacter = deepClone(s.characterListAtStartOfTurn[idx])
    const message = EyeOfTheGodsRules.applyRoll(baseCharacter, roll)

    s.currentTurnRolls = {
      ...s.currentTurnRolls,
      [idx]: roll
    }

    const list = [...s.characterList]
    list[idx] = baseCharacter
    s.characterList = list
    s.updatedAt = Date.now()

    saveState('DASHBOARD')
    return message
  }

  function resetCharacterRoll() {
    if (!activeSession.value) return
    const s = activeSession.value
    const idx = s.selectedCharIndex
    if (idx < 0 || idx >= s.characterList.length) return

    const newRolls = { ...s.currentTurnRolls }
    delete newRolls[idx]
    s.currentTurnRolls = newRolls

    const list = [...s.characterList]
    list[idx] = deepClone(s.characterListAtStartOfTurn[idx])
    s.characterList = list
    s.updatedAt = Date.now()

    saveState('DASHBOARD')
  }

  function toggleDeath() {
    if (!activeSession.value) return
    const s = activeSession.value
    const idx = s.selectedCharIndex
    if (idx < 0 || idx >= s.characterList.length) return

    if (s.deadCharactersIndices.includes(idx)) {
      s.deadCharactersIndices = s.deadCharactersIndices.filter((i) => i !== idx)
    } else {
      s.deadCharactersIndices = [...s.deadCharactersIndices, idx]
      const newRolls = { ...s.currentTurnRolls }
      delete newRolls[idx]
      s.currentTurnRolls = newRolls
    }
    s.updatedAt = Date.now()

    saveState('DASHBOARD')
  }

  function backToMonitoring() {
    if (activeSession.value) {
      activeSession.value.selectedCharIndex = -1
    }
    currentScreen.value = 'MONITORING'
    saveState('MONITORING')
  }

  return {
    // Theme & Wakelock
    isDarkTheme,
    showSettings,
    keepScreenAwake,
    toggleTheme,
    toggleKeepScreenAwake,
    requestWakeLock,
    releaseWakeLock,

    // Navigation & Store state
    currentScreen,
    navigateTo,
    warbands,
    selectedWarbandId,
    currentWarband,
    sessions,
    activeSessionId,
    activeSession,

    // In-game computed
    currentTurn,
    characterList,
    selectedCharIndex,
    selectedCharacter,
    currentSelectedRoll,
    isSelectedCharacterDead,
    deadCharactersIndices,

    // Persistence
    loadState,
    saveState,

    // Warband methods
    createWarband,
    duplicateWarband,
    deleteWarband,
    updateWarbandName,
    openWarbandSetup,
    addCharacter,
    updateCharacter,
    deleteCharacter,

    // Session methods
    startNewGameFromWarband,
    resumeSession,
    restartSession,
    deleteSession,
    quitSessionToSessionsList,

    // In-game methods
    selectCharacter,
    nextTurn,
    applyRoll,
    resetCharacterRoll,
    toggleDeath,
    backToMonitoring
  }
})

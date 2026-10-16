<template>
  <div v-if="character" class="screen dashboard-screen">
    <!-- Top Bar -->
    <header class="header">
      <button class="back-btn" @click="store.backToMonitoring">
        ‹ Retour
      </button>
      <div class="turn-indicator">
        TOUR {{ store.currentTurn }}
      </div>
      <SettingsButton @click="store.showSettings = true" />
    </header>

    <!-- Character Name & Status Badge -->
    <div class="character-header">
      <h1 class="char-name" :class="{ 'dead-text': store.isSelectedCharacterDead }">
        {{ character.name.toUpperCase() }}
      </h1>

      <div v-if="store.isSelectedCharacterDead" class="status-badge badge-cadavre">
        CADAVRE
      </div>
      <div v-else-if="character.isStupid" class="status-badge badge-stupidite">
        STUPIDITÉ
      </div>
      <div v-else class="status-active">
        ● État : Actif
      </div>
    </div>

    <!-- Stats Grid (Row of 6 cells) -->
    <div class="stats-row">
      <StatCell label="CC" :current-value="character.baseCC + character.permCC" :base-value="character.baseCC" :is-dead="store.isSelectedCharacterDead" />
      <StatCell label="F" :current-value="character.baseF + character.permF" :base-value="character.baseF" :is-dead="store.isSelectedCharacterDead" />
      <StatCell label="E" :current-value="character.baseE + character.tempE" :base-value="character.baseE" :is-dead="store.isSelectedCharacterDead" />
      <StatCell label="I" :current-value="character.baseI + character.tempI" :base-value="character.baseI" :is-dead="store.isSelectedCharacterDead" />
      <StatCell label="A" :current-value="character.baseA + character.permA" :base-value="character.baseA" :is-dead="store.isSelectedCharacterDead" />
      <StatCell label="Cd" :current-value="character.baseCd + character.permCd" :base-value="character.baseCd" :is-dead="store.isSelectedCharacterDead" />
    </div>

    <!-- Game Log Banner -->
    <div class="log-card">
      <p class="log-text">{{ gameLog }}</p>
    </div>

    <!-- Bottom Actions & Dice Selector -->
    <footer class="footer-section">
      <!-- Dice buttons & Roller (if not dead) -->
      <div v-if="!store.isSelectedCharacterDead" class="dice-selection-container">
        <!-- Roll dice interactive button -->
        <button
          type="button"
          class="btn-roll-dice"
          :class="{ rolling: isRolling }"
          :disabled="isRolling"
          @click="rollRandomDice"
        >
          <span class="dice-icon" :class="{ animated: isRolling }">🎲</span>
          <span class="roll-text">{{ isRolling ? 'LES DIEUX DÉCIDENT...' : 'LANCER LE DÉ DU CHAOS' }}</span>
        </button>

        <!-- Divider with label -->
        <div class="dice-divider">
          <span>OU SÉLECTION MANUELLE DU D6</span>
        </div>

        <div class="dice-row">
          <button
            v-for="roll in 6"
            :key="roll"
            class="die-btn"
            :class="{ active: store.currentSelectedRoll === roll, 'dice-flash': flashRoll === roll }"
            @click="handleRollClick(roll)"
          >
            {{ roll }}
          </button>
        </div>
      </div>

      <!-- Action Buttons Row -->
      <div class="actions-row">
        <button
          class="btn btn-death"
          :class="store.isSelectedCharacterDead ? 'btn-resurrect' : 'btn-kill'"
          @click="handleDeathToggle"
        >
          {{ store.isSelectedCharacterDead ? 'RESSUSCITER' : 'TUER LE HÉROS' }}
        </button>

        <button
          v-if="!store.isSelectedCharacterDead"
          class="btn btn-muted"
          @click="handleReset"
        >
          ANNULER
        </button>

        <button
          class="btn btn-validate"
          :class="store.isSelectedCharacterDead ? 'btn-muted' : 'btn-primary'"
          @click="store.backToMonitoring"
        >
          VALIDER
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'
import StatCell from '../components/StatCell.vue'

const store = useGameStore()
const character = computed(() => store.selectedCharacter)

const gameLog = ref('')
const isRolling = ref(false)
const flashRoll = ref<number | null>(null)

let rollTimer: number | null = null
let cleanupTimer: number | null = null

function updateLogInitial() {
  if (store.isSelectedCharacterDead) {
    gameLog.value = 'Ce personnage est mort. Ses statistiques ne sont plus prises en compte.'
  } else if (store.currentSelectedRoll !== undefined) {
    gameLog.value = `Ce héros applique le résultat ${store.currentSelectedRoll} pour ce tour.`
  } else {
    gameLog.value = `Aucun dé appliqué pour ce tour sur ${character.value?.name || ''}.`
  }
}

watch(
  () => store.selectedCharIndex,
  () => updateLogInitial(),
  { immediate: true }
)

// Générateur cryptographique garanti sans biais pour un D6 (1 à 6)
function secureD6(): number {
  if (typeof window !== 'undefined' && window.crypto && window.crypto.getRandomValues) {
    const array = new Uint32Array(1)
    // Rejet des valeurs pour éviter le biais modulo (2^32 % 6 != 0)
    const maxUsable = 0xFFFFFFFF - (0xFFFFFFFF % 6)
    do {
      window.crypto.getRandomValues(array)
    } while (array[0] >= maxUsable)
    return (array[0] % 6) + 1
  }
  // Fallback au cas exceptionnel où crypto ne serait pas disponible
  return Math.floor(Math.random() * 6) + 1
}

function handleRollClick(roll: number) {
  if (isRolling.value) return
  if (store.currentSelectedRoll !== roll) {
    const logMessage = store.applyRoll(roll)
    gameLog.value = `Jet modifié pour le dé ${roll} : ${logMessage}`
  }
}

function rollRandomDice() {
  if (isRolling.value) return
  isRolling.value = true
  gameLog.value = 'Les Dieux Sombres observent votre champion...'

  let counter = 0
  const totalTicks = 12

  // Nettoyage de tout timer précédent
  if (rollTimer !== null) {
    clearInterval(rollTimer)
    rollTimer = null
  }
  if (cleanupTimer !== null) {
    clearTimeout(cleanupTimer)
    cleanupTimer = null
  }

  rollTimer = window.setInterval(() => {
    flashRoll.value = secureD6()
    counter++
    if (counter >= totalTicks) {
      if (rollTimer !== null) {
        clearInterval(rollTimer)
        rollTimer = null
      }

      const finalRoll = secureD6()
      flashRoll.value = finalRoll
      const logMessage = store.applyRoll(finalRoll)
      gameLog.value = `🎲 Résultat du Dé : ${finalRoll} ! ${logMessage}`
      isRolling.value = false

      cleanupTimer = window.setTimeout(() => {
        flashRoll.value = null
        cleanupTimer = null
      }, 400)
    }
  }, 60)
}

onUnmounted(() => {
  if (rollTimer !== null) {
    clearInterval(rollTimer)
    rollTimer = null
  }
  if (cleanupTimer !== null) {
    clearTimeout(cleanupTimer)
    cleanupTimer = null
  }
})

function handleDeathToggle() {
  store.toggleDeath()
  gameLog.value = store.isSelectedCharacterDead
    ? 'Le champion a succombé.'
    : 'Le champion est ressuscité.'
}

function handleReset() {
  store.resetCharacterRoll()
  gameLog.value = 'Jet annulé pour ce tour.'
}
</script>

<style scoped>
.screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  padding-top: max(12px, env(safe-area-inset-top));
  padding-bottom: max(16px, env(safe-area-inset-bottom));
  overflow-y: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
}

.back-btn {
  background: transparent;
  color: var(--tzeentch-gold);
  font-size: 16px;
  font-weight: 700;
  padding: 6px 0;
  transition: opacity 0.15s;
}

.back-btn:hover {
  opacity: 0.8;
}

.turn-indicator {
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1px;
}

.character-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.char-name {
  color: var(--text-primary);
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  text-align: center;
}

.dead-text {
  color: var(--text-secondary);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.5px;
  color: #fff;
}

.badge-cadavre {
  background-color: var(--khorne-red);
  box-shadow: var(--shadow-red);
}

.badge-stupidite {
  background-color: #8B4513;
  border: 1px solid #D32F2F;
}

.status-active {
  color: var(--bonus-green);
  font-size: 13px;
  font-weight: 700;
}

.stats-row {
  display: flex;
  gap: 8px;
  width: 100%;
  margin-bottom: 24px;
}

.log-card {
  width: 100%;
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
  border-radius: 10px;
  padding: 16px;
  text-align: center;
  margin-bottom: auto;
}

.log-text {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
}

.footer-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin-top: 24px;
}

.dice-selection-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.btn-roll-dice {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.15), rgba(139, 0, 0, 0.2));
  border: 1.5px solid var(--tzeentch-gold);
  border-radius: 10px;
  color: var(--tzeentch-gold);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: var(--shadow-gold);
  transition: all 0.2s ease;
  touch-action: manipulation;
}

.btn-roll-dice:hover:not(:disabled) {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.3), rgba(139, 0, 0, 0.35));
  transform: translateY(-1px);
}

.btn-roll-dice:active:not(:disabled) {
  transform: scale(0.98);
}

.btn-roll-dice.rolling {
  border-color: var(--khorne-red);
  color: var(--text-primary);
  opacity: 0.9;
}

.dice-icon {
  font-size: 20px;
  display: inline-block;
}

.dice-icon.animated {
  animation: dice-spin 0.3s linear infinite;
}

@keyframes dice-spin {
  0% { transform: rotate(0deg) scale(1); }
  50% { transform: rotate(180deg) scale(1.2); }
  100% { transform: rotate(360deg) scale(1); }
}

.roll-text {
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.8px;
}

.dice-divider {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 12px 0 10px;
}

.dice-divider::before,
.dice-divider::after {
  content: "";
  flex: 1;
  border-bottom: 1px solid var(--border-color);
}

.dice-divider span {
  padding: 0 10px;
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.dice-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.die-btn {
  flex: 1;
  aspect-ratio: 1;
  background-color: var(--bg-surface-elevated);
  border: 2px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 24px;
  font-weight: 800;
  box-shadow: var(--shadow-card);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  touch-action: manipulation;
}

.die-btn:hover {
  border-color: var(--tzeentch-gold);
  transform: translateY(-2px);
}

.die-btn.active {
  background-color: var(--tzeentch-gold);
  border-color: var(--tzeentch-gold);
  color: #0F0F12;
  box-shadow: var(--shadow-gold);
  transform: scale(1.05);
}

.die-btn.dice-flash {
  border-color: var(--khorne-red);
  background-color: rgba(212, 175, 55, 0.35);
  transform: scale(1.08);
}

.actions-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.btn {
  height: 52px;
  border-radius: 10px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.btn-death {
  flex: 1.2;
  font-size: 12px;
  color: #fff;
}

.btn-kill {
  background-color: var(--khorne-red);
  box-shadow: var(--shadow-red);
}

.btn-kill:hover {
  background-color: var(--khorne-red-hover);
}

.btn-resurrect {
  background-color: var(--bonus-green);
  box-shadow: 0 4px 14px rgba(76, 175, 80, 0.3);
}

.btn-resurrect:hover {
  background-color: var(--bonus-green-hover);
}

.btn-muted {
  flex: 1;
  background-color: var(--bg-muted);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  font-size: 12px;
}

.btn-muted:hover {
  border-color: var(--border-hover);
}

.btn-validate {
  flex: 1.2;
  font-size: 14px;
}

.btn-primary {
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  box-shadow: var(--shadow-gold);
}

.btn-primary:hover {
  background-color: var(--tzeentch-gold-hover);
}
</style>

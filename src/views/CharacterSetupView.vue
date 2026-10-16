<template>
  <div class="screen setup-screen">
    <!-- Header -->
    <header class="header">
      <h1 class="title">CRÉATION DE LA BANDE</h1>
      <SettingsButton @click="store.showSettings = true" />
    </header>

    <!-- Create / Edit Form Card -->
    <div class="card form-card">
      <h2 class="form-title">
        {{ editingIndex === -1 ? 'Invoquer un Champion' : `Modifier ${store.characterList[editingIndex]?.name}` }}
      </h2>

      <div class="field-group">
        <label class="field-label">Nom du Champion</label>
        <input
          v-model="name"
          type="text"
          placeholder="Ex: Seigneur du Chaos"
          class="text-input"
        />
      </div>

      <div class="stats-grid">
        <StatStepper label="CC" v-model="cc" />
        <StatStepper label="F" v-model="f" />
        <StatStepper label="E" v-model="e" />
        <StatStepper label="I" v-model="i" />
        <StatStepper label="A" v-model="a" />
        <StatStepper label="Cd" v-model="cd" />
      </div>

      <div class="form-actions">
        <button
          v-if="editingIndex !== -1"
          class="btn btn-muted"
          @click="resetForm"
        >
          Annuler
        </button>
        <button class="btn btn-primary" @click="saveCharacter">
          {{ editingIndex === -1 ? 'Ajouter le Champion' : 'Enregistrer les Modifications' }}
        </button>
      </div>
    </div>

    <!-- Character List Header -->
    <div class="list-header">
      <span>Guerriers prêts :</span>
    </div>

    <!-- Characters List -->
    <div class="character-list">
      <div
        v-for="(char, index) in store.characterList"
        :key="index"
        class="character-card"
      >
        <div class="char-details">
          <span class="char-name">{{ char.name.toUpperCase() }}</span>
          <div class="char-stats-summary">
            <span>CC:{{ char.baseCC }}</span>
            <span>F:{{ char.baseF }}</span>
            <span>E:{{ char.baseE }}</span>
            <span>I:{{ char.baseI }}</span>
            <span>A:{{ char.baseA }}</span>
            <span>Cd:{{ char.baseCd }}</span>
          </div>
        </div>
        <div class="char-actions">
          <button class="icon-btn edit-btn" @click="startEdit(index)" aria-label="Modifier">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
            </svg>
          </button>
          <button class="icon-btn delete-btn" @click="deleteChar(index)" aria-label="Supprimer">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Footer Action Buttons -->
    <footer v-if="store.characterList.length > 0 && editingIndex === -1" class="footer-actions">
      <button
        v-if="store.isGameActive"
        class="btn btn-green"
        @click="store.resumeGame"
      >
        REPRENDRE LA PARTIE
      </button>
      <button
        class="btn btn-khorne"
        @click="handleStartGameClick"
      >
        {{ store.isGameActive ? 'RECOMMENCER' : 'COMMENCER LA PARTIE' }}
      </button>
    </footer>

    <!-- Confirm Reset Dialog -->
    <div v-if="showResetDialog" class="modal-overlay">
      <div class="modal-card">
        <h3 class="modal-title">RÉINITIALISER LA PARTIE ?</h3>
        <p class="modal-text">
          Attention, guerrier. Cela va effacer la progression actuelle, réinitialiser tous les attributs bonus et ressusciter les morts. Continuer ?
        </p>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showResetDialog = false">ANNULER</button>
          <button class="btn btn-khorne" @click="confirmRestart">OUI, RECOMMENCER</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'
import StatStepper from '../components/StatStepper.vue'

const store = useGameStore()

const name = ref('')
const cc = ref<number>(6)
const f = ref<number>(4)
const e = ref<number>(4)
const i = ref<number>(5)
const a = ref<number>(3)
const cd = ref<number>(8)

const editingIndex = ref(-1)
const showResetDialog = ref(false)

function resetForm() {
  editingIndex.value = -1
  name.value = ''
  cc.value = 6
  f.value = 4
  e.value = 4
  i.value = 5
  a.value = 3
  cd.value = 8
}

function startEdit(index: number) {
  editingIndex.value = index
  const char = store.characterList[index]
  name.value = char.name
  cc.value = char.baseCC
  f.value = char.baseF
  e.value = char.baseE
  i.value = char.baseI
  a.value = char.baseA
  cd.value = char.baseCd
}

function deleteChar(index: number) {
  if (editingIndex.value === index) {
    resetForm()
  }
  store.deleteCharacter(index)
}

function saveCharacter() {
  if (!name.value.trim()) return

  const character = {
    name: name.value.trim(),
    baseCC: Number(cc.value) || 4,
    baseF: Number(f.value) || 4,
    baseE: Number(e.value) || 4,
    baseI: Number(i.value) || 5,
    baseA: Number(a.value) || 3,
    baseCd: Number(cd.value) || 8,
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

  if (editingIndex.value === -1) {
    store.addCharacter(character)
  } else {
    store.updateCharacter(editingIndex.value, {
      ...store.characterList[editingIndex.value],
      ...character
    })
  }

  resetForm()
}

function handleStartGameClick() {
  if (store.isGameActive) {
    showResetDialog.value = true
  } else {
    store.startGame()
  }
}

function confirmRestart() {
  showResetDialog.value = false
  store.startGame()
}
</script>

<style scoped>
.screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  padding-top: max(16px, env(safe-area-inset-top));
  padding-bottom: max(16px, env(safe-area-inset-bottom));
  overflow-y: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title {
  color: var(--tzeentch-gold);
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.card {
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.form-title {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 14px;
}

.field-group {
  margin-bottom: 14px;
}

.field-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.text-input {
  width: 100%;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  padding: 12px 14px;
  font-size: 15px;
  font-weight: 500;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.text-input:focus {
  border-color: var(--tzeentch-gold);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.25);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
  margin-bottom: 18px;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.3px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.btn-primary {
  flex: 2;
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  box-shadow: var(--shadow-gold);
}

.btn-primary:hover {
  background-color: var(--tzeentch-gold-hover);
}

.btn-muted {
  flex: 1;
  background-color: var(--bg-muted);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-muted:hover {
  border-color: var(--border-hover);
}

.btn-khorne {
  flex: 1;
  background-color: var(--khorne-red);
  color: #fff;
  box-shadow: var(--shadow-red);
}

.btn-khorne:hover {
  background-color: var(--khorne-red-hover);
}

.btn-green {
  flex: 1;
  background-color: var(--bonus-green);
  color: #fff;
  box-shadow: 0 4px 14px rgba(76, 175, 80, 0.3);
}

.btn-green:hover {
  background-color: var(--bonus-green-hover);
}

.list-header {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}

.character-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.character-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 12px 16px;
  transition: border-color 0.15s;
}

.character-card:hover {
  border-color: var(--border-hover);
}

.char-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.char-name {
  color: var(--text-primary);
  font-weight: 700;
  font-size: 15px;
}

.char-stats-summary {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 600;
}

.char-actions {
  display: flex;
  gap: 6px;
}

.icon-btn {
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  padding: 7px;
  border-radius: 6px;
  transition: all 0.15s;
}

.icon-btn:hover {
  border-color: var(--border-hover);
  transform: scale(1.05);
}

.edit-btn {
  color: var(--tzeentch-gold);
}

.delete-btn {
  color: var(--khorne-red);
}

.footer-actions {
  display: flex;
  gap: 10px;
  height: 52px;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: var(--dialog-overlay);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 100;
}

.modal-card {
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.6);
  border-radius: 12px;
  padding: 22px;
  max-width: 400px;
  width: 100%;
}

.modal-title {
  color: var(--tzeentch-gold);
  font-size: 17px;
  font-weight: 800;
  margin-bottom: 12px;
}

.modal-text {
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 22px;
}

.modal-actions {
  display: flex;
  gap: 10px;
}
</style>

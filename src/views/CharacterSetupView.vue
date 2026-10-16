<template>
  <div v-if="warband" class="screen setup-screen">
    <!-- Header with Back button -->
    <header class="header">
      <button class="back-btn" @click="store.navigateTo('WARBANDS')">
        ‹ Mes Bandes
      </button>
      <div class="header-titles">
        <span class="sub-title">CONFIGURATION DU ROSTER</span>
        <h1 class="warband-editable-name" @click="openRenameModal">
          {{ warband.name }}
          <span class="edit-icon" title="Renommer la bande">✏️</span>
        </h1>
      </div>
      <SettingsButton @click="store.showSettings = true" />
    </header>

    <!-- Create / Edit Form Card -->
    <div class="card form-card">
      <h2 class="form-title">
        {{ editingIndex === -1 ? 'Invoquer un Champion' : `Modifier ${warband.characters[editingIndex]?.name}` }}
      </h2>

      <div class="field-group">
        <label class="field-label">Nom du Champion</label>
        <input
          v-model="name"
          type="text"
          placeholder="Ex: Seigneur du Chaos"
          class="text-input"
          @keyup.enter="saveCharacter"
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
        <button class="btn btn-primary" :disabled="!name.trim()" @click="saveCharacter">
          {{ editingIndex === -1 ? 'Ajouter le Champion' : 'Enregistrer' }}
        </button>
      </div>
    </div>

    <!-- Character List Header -->
    <div class="list-header">
      <span>Guerriers dans la bande ({{ warband.characters.length }}) :</span>
    </div>

    <!-- Characters List -->
    <div v-if="warband.characters.length > 0" class="character-list">
      <div
        v-for="(char, index) in warband.characters"
        :key="char.id || index"
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
    <div v-else class="empty-list-notice">
      Aucun guerrier n'a encore été invoqué dans cette bande.
    </div>

    <!-- Footer Action Buttons -->
    <footer v-if="warband.characters.length > 0 && editingIndex === -1" class="footer-actions">
      <button
        class="btn btn-duplicate"
        @click="duplicateCurrentWarband"
      >
        📋 Dupliquer
      </button>
      <button
        class="btn btn-primary btn-start-game"
        @click="openStartBattleModal"
      >
        ⚔️ LANCER UNE PARTIE
      </button>
    </footer>

    <!-- Rename Warband Modal -->
    <div v-if="showRenameModal" class="modal-overlay" @click.self="showRenameModal = false">
      <div class="modal-card">
        <h3 class="modal-title">RENOMMER LA BANDE</h3>
        <div class="field-group">
          <label class="field-label">Nom de la bande</label>
          <input
            v-model="renamedTitle"
            type="text"
            class="text-input"
            autofocus
            @keyup.enter="saveRename"
          />
        </div>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showRenameModal = false">Annuler</button>
          <button class="btn btn-primary" :disabled="!renamedTitle.trim()" @click="saveRename">
            Enregistrer
          </button>
        </div>
      </div>
    </div>

    <!-- Start Battle Modal -->
    <div v-if="showBattleModal" class="modal-overlay" @click.self="showBattleModal = false">
      <div class="modal-card">
        <h3 class="modal-title">LANCER UNE BATAILLE</h3>
        <p class="modal-text">
          Une session de jeu indépendante sera créée avec ces {{ warband.characters.length }} guerriers.
        </p>
        <div class="field-group">
          <label class="field-label">Nom de la partie / adversaire</label>
          <input
            v-model="battleSessionName"
            type="text"
            class="text-input"
            @keyup.enter="confirmStartBattle"
          />
        </div>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showBattleModal = false">Annuler</button>
          <button class="btn btn-primary" @click="confirmStartBattle">
            ⚔️ Commencer
          </button>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="screen">
    <p>Aucune bande sélectionnée.</p>
    <button class="btn btn-primary" @click="store.navigateTo('WARBANDS')">Retour aux bandes</button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'
import StatStepper from '../components/StatStepper.vue'

const store = useGameStore()
const warband = computed(() => store.currentWarband)

const name = ref('')
const cc = ref<number>(6)
const f = ref<number>(4)
const e = ref<number>(4)
const i = ref<number>(5)
const a = ref<number>(3)
const cd = ref<number>(8)

const editingIndex = ref(-1)

// Rename Warband Modal
const showRenameModal = ref(false)
const renamedTitle = ref('')

function openRenameModal() {
  if (warband.value) {
    renamedTitle.value = warband.value.name
    showRenameModal.value = true
  }
}

function saveRename() {
  if (warband.value && renamedTitle.value.trim()) {
    store.updateWarbandName(warband.value.id, renamedTitle.value.trim())
    showRenameModal.value = false
  }
}

// Start Battle Modal
const showBattleModal = ref(false)
const battleSessionName = ref('')

function openStartBattleModal() {
  if (!warband.value) return
  battleSessionName.value = `${warband.value.name} - Bataille ${store.sessions.length + 1}`
  showBattleModal.value = true
}

function confirmStartBattle() {
  if (!warband.value) return
  store.startNewGameFromWarband(warband.value.id, battleSessionName.value)
  showBattleModal.value = false
}

function duplicateCurrentWarband() {
  if (!warband.value) return
  const newId = store.duplicateWarband(warband.value.id)
  if (newId) {
    store.openWarbandSetup(newId)
  }
}

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
  if (!warband.value) return
  editingIndex.value = index
  const char = warband.value.characters[index]
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
  } else if (warband.value) {
    store.updateCharacter(editingIndex.value, {
      ...warband.value.characters[editingIndex.value],
      ...character
    })
  }

  resetForm()
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
  gap: 12px;
}

.back-btn {
  background: transparent;
  color: var(--tzeentch-gold);
  font-size: 15px;
  font-weight: 700;
  padding: 4px 0;
  white-space: nowrap;
}

.header-titles {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex: 1;
}

.sub-title {
  color: var(--text-secondary);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.8px;
}

.warband-editable-name {
  color: var(--tzeentch-gold);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.edit-icon {
  font-size: 12px;
  opacity: 0.7;
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

.btn-primary:hover:not(:disabled) {
  background-color: var(--tzeentch-gold-hover);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.btn-duplicate {
  flex: 1;
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.btn-duplicate:hover {
  border-color: var(--tzeentch-gold);
}

.btn-start-game {
  flex: 2;
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

.empty-list-notice {
  padding: 24px 16px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  font-style: italic;
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
  margin-bottom: 16px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

<template>
  <div class="screen sessions-screen">
    <!-- Header -->
    <header class="header">
      <div class="header-titles">
        <span class="sub-title">WARHAMMER : THE OLD WORLD</span>
        <h1 class="main-title">MES PARTIES EN COURS</h1>
      </div>
      <SettingsButton @click="store.showSettings = true" />
    </header>

    <!-- Top Action: New Game Button -->
    <div class="top-action-bar">
      <button class="btn btn-primary btn-add" @click="openNewSessionModal">
        <span class="icon">+</span>
        <span>NOUVELLE PARTIE</span>
      </button>
    </div>

    <!-- Sessions List -->
    <div v-if="store.sessions.length > 0" class="sessions-list">
      <div
        v-for="session in store.sessions"
        :key="session.id"
        class="session-card"
        :class="{ 'session-active': store.activeSessionId === session.id }"
      >
        <!-- Session Card Header -->
        <div class="session-card-header">
          <div class="session-title-row">
            <h2 class="session-name">{{ session.name }}</h2>
            <span class="turn-badge">TOUR {{ session.currentTurn }}</span>
          </div>
          <div class="session-subtitle-row">
            <span class="warband-tag">🛡️ {{ session.warbandName }}</span>
            <span class="date-tag">{{ formatDate(session.updatedAt) }}</span>
          </div>
        </div>

        <!-- Session Status summary -->
        <div class="session-status-row">
          <div class="status-indicator">
            <span class="dot alive"></span>
            <span class="status-label">
              {{ session.characterList.length - session.deadCharactersIndices.length }} actifs
            </span>
          </div>
          <div v-if="session.deadCharactersIndices.length > 0" class="status-indicator">
            <span class="dot dead"></span>
            <span class="status-label">
              {{ session.deadCharactersIndices.length }} {{ session.deadCharactersIndices.length > 1 ? 'morts' : 'mort' }}
            </span>
          </div>
          <div class="status-indicator rolls-indicator">
            <span class="status-label">
              {{ Object.keys(session.currentTurnRolls).length }}/{{ session.characterList.length - session.deadCharactersIndices.length }} dés appliqués
            </span>
          </div>
        </div>

        <!-- Session Card Actions -->
        <div class="session-actions">
          <button class="btn btn-resume" @click="store.resumeSession(session.id)">
            <span>⚔️ REPRENDRE LA PARTIE</span>
          </button>

          <div class="secondary-actions">
            <button
              class="btn btn-secondary btn-restart"
              title="Recommencer cette partie au tour 1"
              @click="confirmRestartSession(session.id, session.name)"
            >
              <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
                <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
              </svg>
              <span>Recommencer</span>
            </button>

            <button
              class="btn btn-secondary btn-delete"
              title="Supprimer cette partie"
              @click="confirmDeleteSession(session.id, session.name)"
            >
              <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
              </svg>
              <span>Supprimer</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <div class="empty-icon">⚔️</div>
      <h3 class="empty-title">Aucune partie en cours</h3>
      <p class="empty-text">Lancez une nouvelle bataille avec l'une de vos bandes de guerre pour commencer le suivi du Regard des Dieux.</p>
      <button class="btn btn-primary" @click="openNewSessionModal">
        Lancer une Partie
      </button>
    </div>

    <!-- Modal: Nouvelle Partie -->
    <div v-if="showNewSessionModal" class="modal-overlay" @click.self="showNewSessionModal = false">
      <div class="modal-card">
        <h3 class="modal-title">NOUVELLE BATAILLE</h3>

        <div v-if="store.warbands.length > 0" class="modal-body">
          <div class="field-group">
            <label class="field-label">Choisir la bande de guerriers</label>
            <select v-model="selectedWarbandId" class="select-input">
              <option
                v-for="wb in store.warbands"
                :key="wb.id"
                :value="wb.id"
                :disabled="wb.characters.length === 0"
              >
                {{ wb.name }} ({{ wb.characters.length }} guerriers{{ wb.characters.length === 0 ? ' - vide' : '' }})
              </option>
            </select>
          </div>

          <div class="field-group">
            <label class="field-label">Nom de la partie / adversaire</label>
            <input
              v-model="sessionName"
              type="text"
              placeholder="Ex: Partie vs Nains 1500pts"
              class="text-input"
              @keyup.enter="handleCreateSession"
            />
          </div>

          <div class="modal-actions">
            <button class="btn btn-muted" @click="showNewSessionModal = false">Annuler</button>
            <button
              class="btn btn-primary"
              :disabled="!selectedWarbandId || !canSelectedWarbandFight"
              @click="handleCreateSession"
            >
              ⚔️ Commencer
            </button>
          </div>
        </div>

        <div v-else class="no-warbands-alert">
          <p>Vous devez d'abord créer au moins une bande avec des champions avant de lancer une partie.</p>
          <div class="modal-actions">
            <button class="btn btn-muted" @click="showNewSessionModal = false">Fermer</button>
            <button class="btn btn-primary" @click="goToCreateWarband">Créer une bande</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Confirmation Recommencer -->
    <div v-if="showRestartDialog" class="modal-overlay" @click.self="showRestartDialog = false">
      <div class="modal-card">
        <h3 class="modal-title">RECOMMENCER CETTE PARTIE ?</h3>
        <p class="modal-desc">
          Attention : cela réinitialisera « {{ sessionToRestartName }} » au tour 1, effacera les bonus de dé accumulés et ressuscitera les morts.
        </p>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showRestartDialog = false">Annuler</button>
          <button class="btn btn-khorne" @click="executeRestart">Oui, Recommencer</button>
        </div>
      </div>
    </div>

    <!-- Modal: Confirmation Supprimer -->
    <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
      <div class="modal-card">
        <h3 class="modal-title">SUPPRIMER LA PARTIE ?</h3>
        <p class="modal-desc">
          Êtes-vous sûr de vouloir supprimer définitivement « {{ sessionToDeleteName }} » ? Votre bande d'origine sera conservée.
        </p>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showDeleteDialog = false">Annuler</button>
          <button class="btn btn-khorne" @click="executeDelete">Supprimer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'

const store = useGameStore()

const showNewSessionModal = ref(false)
const selectedWarbandId = ref('')
const sessionName = ref('')

const showRestartDialog = ref(false)
const sessionToRestartId = ref('')
const sessionToRestartName = ref('')

const showDeleteDialog = ref(false)
const sessionToDeleteId = ref('')
const sessionToDeleteName = ref('')

const canSelectedWarbandFight = computed(() => {
  const wb = store.warbands.find((w) => w.id === selectedWarbandId.value)
  return wb ? wb.characters.length > 0 : false
})

function formatDate(timestamp: number): string {
  if (!timestamp) return ''
  const d = new Date(timestamp)
  const day = String(d.getDate()).padStart(2, '0')
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const mins = String(d.getMinutes()).padStart(2, '0')
  return `${day}/${month} à ${hours}h${mins}`
}

function openNewSessionModal() {
  const available = store.warbands.find((w) => w.characters.length > 0) || store.warbands[0]
  selectedWarbandId.value = available ? available.id : ''
  const nameBase = available ? available.name : 'Chaos'
  sessionName.value = `${nameBase} - Bataille ${store.sessions.length + 1}`
  showNewSessionModal.value = true
}

function handleCreateSession() {
  if (!selectedWarbandId.value) return
  store.startNewGameFromWarband(selectedWarbandId.value, sessionName.value)
  showNewSessionModal.value = false
}

function goToCreateWarband() {
  showNewSessionModal.value = false
  store.navigateTo('WARBANDS')
}

function confirmRestartSession(id: string, name: string) {
  sessionToRestartId.value = id
  sessionToRestartName.value = name
  showRestartDialog.value = true
}

function executeRestart() {
  if (sessionToRestartId.value) {
    store.restartSession(sessionToRestartId.value)
  }
  showRestartDialog.value = false
}

function confirmDeleteSession(id: string, name: string) {
  sessionToDeleteId.value = id
  sessionToDeleteName.value = name
  showDeleteDialog.value = true
}

function executeDelete() {
  if (sessionToDeleteId.value) {
    store.deleteSession(sessionToDeleteId.value)
  }
  showDeleteDialog.value = false
}
</script>

<style scoped>
.screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  padding-top: max(16px, env(safe-area-inset-top));
  padding-bottom: max(20px, env(safe-area-inset-bottom));
  overflow-y: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-titles {
  display: flex;
  flex-direction: column;
}

.sub-title {
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.8px;
}

.main-title {
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.top-action-bar {
  margin-bottom: 16px;
}

.btn-add {
  width: 100%;
  height: 48px;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.5px;
  gap: 8px;
}

.btn-add .icon {
  font-size: 20px;
  font-weight: bold;
}

.sessions-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.session-card {
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: border-color 0.2s, transform 0.15s;
}

.session-card:hover {
  border-color: var(--border-hover);
}

.session-card.session-active {
  border-left: 4px solid var(--tzeentch-gold);
}

.session-card-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.session-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.session-name {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.turn-badge {
  background-color: var(--khorne-red);
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  padding: 3px 8px;
  border-radius: 6px;
  letter-spacing: 0.5px;
  box-shadow: var(--shadow-red);
  white-space: nowrap;
}

.session-subtitle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.warband-tag {
  color: var(--tzeentch-gold);
  font-weight: 700;
}

.date-tag {
  font-size: 11px;
  opacity: 0.8;
}

.session-status-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 8px 10px;
  background-color: var(--bg-surface-elevated);
  border-radius: 8px;
  border: 1px solid var(--border-subtle);
  align-items: center;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
}

.status-indicator .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-indicator .dot.alive {
  background-color: var(--bonus-green);
}

.status-indicator .dot.dead {
  background-color: var(--malus-red);
}

.rolls-indicator {
  margin-left: auto;
  color: var(--text-secondary);
  font-size: 11px;
}

.session-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-resume {
  width: 100%;
  height: 44px;
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 800;
  box-shadow: var(--shadow-gold);
}

.btn-resume:hover {
  background-color: var(--tzeentch-gold-hover);
}

.secondary-actions {
  display: flex;
  gap: 8px;
}

.btn-secondary {
  flex: 1;
  height: 34px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  gap: 6px;
  border: 1px solid var(--border-color);
  background-color: transparent;
  color: var(--text-secondary);
}

.btn-restart:hover {
  color: var(--tzeentch-gold);
  border-color: var(--tzeentch-gold);
}

.btn-delete:hover {
  color: var(--malus-red);
  border-color: var(--malus-red);
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.7;
}

.empty-title {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 6px;
}

.empty-text {
  color: var(--text-secondary);
  font-size: 14px;
  max-width: 300px;
  margin-bottom: 20px;
  line-height: 1.4;
}

/* Modal styling */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: var(--dialog-overlay);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1000;
}

.modal-card {
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 22px;
  max-width: 380px;
  width: 100%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
}

.modal-title {
  color: var(--text-primary);
  font-size: 17px;
  font-weight: 800;
  margin-bottom: 8px;
}

.modal-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.4;
  margin-bottom: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.field-label {
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 700;
}

.text-input, .select-input {
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
  color: var(--text-primary);
  font-size: 14px;
  width: 100%;
}

.text-input:focus, .select-input:focus {
  border-color: var(--tzeentch-gold);
}

.select-input option {
  background-color: var(--bg-surface);
  color: var(--text-primary);
}

.no-warbands-alert {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 16px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-primary {
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 800;
}

.btn-muted {
  background-color: var(--bg-muted);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 700;
}

.btn-khorne {
  background-color: var(--khorne-red);
  color: #fff;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 800;
}
</style>

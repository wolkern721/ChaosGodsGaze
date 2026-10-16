<template>
  <div class="screen warbands-screen">
    <!-- Header -->
    <header class="header">
      <div class="header-titles">
        <span class="sub-title">WARHAMMER : THE OLD WORLD</span>
        <h1 class="main-title">MES BANDES DU CHAOS</h1>
      </div>
      <SettingsButton @click="store.showSettings = true" />
    </header>

    <!-- Top Action: Create Warband Button -->
    <div class="top-action-bar">
      <button class="btn btn-primary btn-add" @click="showCreateModal = true">
        <span class="icon">+</span>
        <span>NOUVELLE BANDE</span>
      </button>
    </div>

    <!-- Warbands List -->
    <div v-if="store.warbands.length > 0" class="warbands-list">
      <div
        v-for="warband in store.warbands"
        :key="warband.id"
        class="warband-card"
      >
        <!-- Card Header -->
        <div class="card-header">
          <div class="warband-title-row">
            <h2 class="warband-name">{{ warband.name }}</h2>
            <span class="warband-badge">
              {{ warband.characters.length }} {{ warband.characters.length > 1 ? 'champions' : 'champion' }}
            </span>
          </div>

          <!-- Preview of champions in warband -->
          <p v-if="warband.characters.length > 0" class="warband-preview">
            {{ warband.characters.map((c) => c.name).join(' • ') }}
          </p>
          <p v-else class="warband-preview empty">
            Aucun champion dans cette bande.
          </p>
        </div>

        <!-- Card Actions -->
        <div class="card-actions">
          <!-- Start game button -->
          <button
            class="btn btn-play"
            :disabled="warband.characters.length === 0"
            :title="warband.characters.length === 0 ? 'Ajoutez au moins un guerrier pour batailler' : 'Lancer une partie'"
            @click="openStartBattleModal(warband.id)"
          >
            <span>⚔️ Lancer une Partie</span>
          </button>

          <!-- Edit roster button -->
          <button
            class="btn btn-edit-roster"
            @click="store.openWarbandSetup(warband.id)"
          >
            <span>👥 Gérer la Liste</span>
          </button>

          <!-- Utility buttons: Duplicate & Delete -->
          <div class="utility-row">
            <button
              class="btn btn-util btn-duplicate"
              title="Dupliquer rapidement cette bande"
              @click="handleDuplicate(warband.id)"
            >
              <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
              </svg>
              <span>Dupliquer</span>
            </button>

            <button
              class="btn btn-util btn-delete"
              title="Supprimer cette bande"
              @click="confirmDelete(warband.id, warband.name)"
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
      <div class="empty-icon">🛡️</div>
      <h3 class="empty-title">Aucune bande créée</h3>
      <p class="empty-text">Créez votre première bande de guerre pour configurer vos champions du Chaos.</p>
      <button class="btn btn-primary" @click="showCreateModal = true">
        Créer une Bande
      </button>
    </div>

    <!-- Modal: Créer une Bande -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal-card">
        <h3 class="modal-title">NOUVELLE BANDE DU CHAOS</h3>
        <div class="field-group">
          <label class="field-label">Nom de la bande</label>
          <input
            v-model="newWarbandName"
            type="text"
            placeholder="Ex: Légion de Khorne"
            class="text-input"
            autofocus
            @keyup.enter="handleCreateWarband"
          />
        </div>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showCreateModal = false">Annuler</button>
          <button class="btn btn-primary" :disabled="!newWarbandName.trim()" @click="handleCreateWarband">
            Créer et Configurer
          </button>
        </div>
      </div>
    </div>

    <!-- Modal: Lancer une Bataille -->
    <div v-if="showStartBattleModal" class="modal-overlay" @click.self="showStartBattleModal = false">
      <div class="modal-card">
        <h3 class="modal-title">LANCER UNE BATAILLE</h3>
        <p class="modal-desc">
          Une nouvelle session de jeu indépendante sera créée avec les caractéristiques de base de vos champions.
        </p>
        <div class="field-group">
          <label class="field-label">Nom de la partie / adversaire</label>
          <input
            v-model="newBattleName"
            type="text"
            placeholder="Ex: vs Empire 1500pts"
            class="text-input"
            @keyup.enter="handleStartBattle"
          />
        </div>
        <div class="modal-actions">
          <button class="btn btn-muted" @click="showStartBattleModal = false">Annuler</button>
          <button class="btn btn-primary" @click="handleStartBattle">
            ⚔️ Commencer
          </button>
        </div>
      </div>
    </div>

    <!-- Modal: Confirmer la suppression -->
    <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
      <div class="modal-card">
        <h3 class="modal-title">SUPPRIMER LA BANDE ?</h3>
        <p class="modal-desc">
          Êtes-vous sûr de vouloir supprimer « {{ warbandToDeleteName }} » ? Les parties existantes créées avec cette bande ne seront pas effacées.
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
import { ref } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'

const store = useGameStore()

const showCreateModal = ref(false)
const newWarbandName = ref('')

const showStartBattleModal = ref(false)
const targetWarbandId = ref('')
const newBattleName = ref('')

const showDeleteDialog = ref(false)
const warbandToDeleteId = ref('')
const warbandToDeleteName = ref('')

function handleCreateWarband() {
  if (!newWarbandName.value.trim()) return
  store.createWarband(newWarbandName.value)
  newWarbandName.value = ''
  showCreateModal.value = false
}

function handleDuplicate(warbandId: string) {
  store.duplicateWarband(warbandId)
}

function openStartBattleModal(warbandId: string) {
  const warband = store.warbands.find((w) => w.id === warbandId)
  if (!warband) return
  targetWarbandId.value = warbandId
  newBattleName.value = `${warband.name} - Bataille ${store.sessions.length + 1}`
  showStartBattleModal.value = true
}

function handleStartBattle() {
  if (!targetWarbandId.value) return
  store.startNewGameFromWarband(targetWarbandId.value, newBattleName.value)
  showStartBattleModal.value = false
}

function confirmDelete(id: string, name: string) {
  warbandToDeleteId.value = id
  warbandToDeleteName.value = name
  showDeleteDialog.value = true
}

function executeDelete() {
  if (warbandToDeleteId.value) {
    store.deleteWarband(warbandToDeleteId.value)
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

.warbands-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.warband-card {
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: border-color 0.2s, transform 0.15s;
}

.warband-card:hover {
  border-color: var(--border-hover);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.warband-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.warband-name {
  color: var(--tzeentch-gold);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.warband-badge {
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 12px;
  white-space: nowrap;
}

.warband-preview {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.warband-preview.empty {
  font-style: italic;
  opacity: 0.7;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-play {
  width: 100%;
  height: 42px;
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 800;
  box-shadow: var(--shadow-gold);
}

.btn-play:hover:not(:disabled) {
  background-color: var(--tzeentch-gold-hover);
}

.btn-play:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  filter: grayscale(0.5);
}

.btn-edit-roster {
  width: 100%;
  height: 38px;
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
}

.btn-edit-roster:hover {
  border-color: var(--border-hover);
  background-color: var(--bg-muted);
}

.utility-row {
  display: flex;
  gap: 8px;
}

.btn-util {
  flex: 1;
  height: 34px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  gap: 6px;
  border: 1px solid var(--border-color);
}

.btn-duplicate {
  background-color: transparent;
  color: var(--text-secondary);
}

.btn-duplicate:hover {
  color: var(--tzeentch-gold);
  border-color: var(--tzeentch-gold);
}

.btn-delete {
  background-color: transparent;
  color: var(--text-secondary);
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
  max-width: 280px;
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
  margin-bottom: 18px;
}

.field-label {
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 700;
}

.text-input {
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
  color: var(--text-primary);
  font-size: 15px;
}

.text-input:focus {
  border-color: var(--tzeentch-gold);
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

<template>
  <main class="app-root" :data-theme="store.isDarkTheme ? 'dark' : 'light'">
    <!-- Top Navigation Tabs (Visible when in Warbands or Sessions list) -->
    <nav v-if="store.currentScreen === 'WARBANDS' || store.currentScreen === 'SESSIONS'" class="top-nav-tabs">
      <button
        class="nav-tab"
        :class="{ active: store.currentScreen === 'WARBANDS' }"
        @click="store.navigateTo('WARBANDS')"
      >
        <span class="tab-icon">🛡️</span>
        <span class="tab-title">Mes Bandes</span>
        <span v-if="store.warbands.length > 0" class="tab-badge">{{ store.warbands.length }}</span>
      </button>

      <button
        class="nav-tab"
        :class="{ active: store.currentScreen === 'SESSIONS' }"
        @click="store.navigateTo('SESSIONS')"
      >
        <span class="tab-icon">⚔️</span>
        <span class="tab-title">Mes Parties</span>
        <span v-if="store.sessions.length > 0" class="tab-badge active-badge">{{ store.sessions.length }}</span>
      </button>
    </nav>

    <!-- Screens routing based on currentScreen -->
    <WarbandsView v-if="store.currentScreen === 'WARBANDS'" />
    <GameSessionsView v-else-if="store.currentScreen === 'SESSIONS'" />
    <CharacterSetupView v-else-if="store.currentScreen === 'SETUP'" />
    <GameMonitoringView v-else-if="store.currentScreen === 'MONITORING'" />
    <DashboardView v-else-if="store.currentScreen === 'DASHBOARD'" />

    <!-- Global Settings Modal -->
    <div v-if="store.showSettings" class="modal-overlay" @click.self="store.showSettings = false">
      <div class="modal-card">
        <h3 class="modal-title">Paramètres</h3>

        <!-- Theme toggle -->
        <div class="setting-item">
          <span>{{ store.isDarkTheme ? 'Mode nuit' : 'Mode jour' }}</span>
          <label class="switch">
            <input
              type="checkbox"
              :checked="store.isDarkTheme"
              @change="(e) => store.toggleTheme((e.target as HTMLInputElement).checked)"
            />
            <span class="slider"></span>
          </label>
        </div>

        <!-- Keep screen awake toggle -->
        <div class="setting-item">
          <div class="setting-text">
            <span>Garder l'écran allumé</span>
            <small class="setting-desc">Empêche la mise en veille pendant la partie</small>
          </div>
          <label class="switch">
            <input
              type="checkbox"
              :checked="store.keepScreenAwake"
              @change="(e) => void store.toggleKeepScreenAwake((e.target as HTMLInputElement).checked)"
            />
            <span class="slider"></span>
          </label>
        </div>

        <div class="modal-actions">
          <button class="close-btn" @click="store.showSettings = false">Fermer</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useGameStore } from './stores/gameStore'
import WarbandsView from './views/WarbandsView.vue'
import GameSessionsView from './views/GameSessionsView.vue'
import CharacterSetupView from './views/CharacterSetupView.vue'
import GameMonitoringView from './views/GameMonitoringView.vue'
import DashboardView from './views/DashboardView.vue'

const store = useGameStore()

function handleVisibilityChange() {
  if (document.visibilityState === 'visible' && store.keepScreenAwake) {
    void store.requestWakeLock()
  }
}

onMounted(async () => {
  await store.loadState()
  document.documentElement.setAttribute('data-theme', store.isDarkTheme ? 'dark' : 'light')
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  void store.releaseWakeLock()
})
</script>

<style scoped>
.app-root {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  overflow: hidden;
}

/* Top Tab Bar */
.top-nav-tabs {
  display: flex;
  background-color: var(--bg-surface);
  border-bottom: 1px solid var(--border-color);
  padding: 8px 12px 0;
  padding-top: max(8px, env(safe-area-inset-top));
  gap: 8px;
  flex-shrink: 0;
}

.nav-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  background: transparent;
  color: var(--text-secondary);
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 6px 6px 0 0;
}

.nav-tab:hover {
  color: var(--text-primary);
  background-color: var(--bg-surface-elevated);
}

.nav-tab.active {
  color: var(--tzeentch-gold);
  border-bottom-color: var(--tzeentch-gold);
  background-color: var(--bg-surface-elevated);
}

.tab-icon {
  font-size: 16px;
}

.tab-badge {
  background-color: var(--bg-muted);
  color: var(--text-primary);
  font-size: 11px;
  font-weight: 800;
  padding: 2px 7px;
  border-radius: 10px;
}

.nav-tab.active .tab-badge {
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
}

.tab-badge.active-badge {
  background-color: var(--khorne-red);
  color: #fff;
}

.nav-tab.active .tab-badge.active-badge {
  background-color: var(--khorne-red);
  color: #fff;
  box-shadow: var(--shadow-red);
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
  border-radius: 10px;
  padding: 20px;
  max-width: 360px;
  width: 100%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
}

.modal-title {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  margin-bottom: 20px;
  gap: 12px;
}

.setting-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.setting-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.3;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.close-btn {
  background: transparent;
  color: var(--tzeentch-gold);
  font-size: 14px;
  font-weight: bold;
  padding: 8px 12px;
}

/* Switch styling */
.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 26px;
  flex-shrink: 0;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--bg-muted);
  border: 1px solid var(--border-color);
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: var(--text-secondary);
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--tzeentch-gold);
  border-color: var(--tzeentch-gold);
}

input:checked + .slider:before {
  transform: translateX(22px);
  background-color: #000;
}
</style>

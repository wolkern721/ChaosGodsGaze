<template>
  <main class="app-root" :data-theme="store.isDarkTheme ? 'dark' : 'light'">
    <!-- Screens routing based on currentScreen -->
    <CharacterSetupView v-if="store.currentScreen === 'SETUP'" />
    <GameMonitoringView v-else-if="store.currentScreen === 'MONITORING'" />
    <DashboardView v-else-if="store.currentScreen === 'DASHBOARD'" />

    <!-- Global Settings Modal -->
    <div v-if="store.showSettings" class="modal-overlay" @click.self="store.showSettings = false">
      <div class="modal-card">
        <h3 class="modal-title">Paramètres</h3>

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

        <div class="modal-actions">
          <button class="close-btn" @click="store.showSettings = false">Fermer</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useGameStore } from './stores/gameStore'
import CharacterSetupView from './views/CharacterSetupView.vue'
import GameMonitoringView from './views/GameMonitoringView.vue'
import DashboardView from './views/DashboardView.vue'

const store = useGameStore()

onMounted(() => {
  store.loadState()
  document.documentElement.setAttribute('data-theme', store.isDarkTheme ? 'dark' : 'light')
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
}

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
  border-radius: 8px;
  padding: 20px;
  max-width: 320px;
  width: 100%;
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
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
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

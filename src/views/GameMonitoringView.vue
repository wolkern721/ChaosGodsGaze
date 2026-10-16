<template>
  <div class="screen monitoring-screen">
    <!-- Header -->
    <header class="header">
      <div class="turn-info">
        <span class="sub-title">MONITORING DE LA PARTIE</span>
        <h1 class="turn-title">TOUR EN COURS : {{ store.currentTurn }}</h1>
      </div>
      <div class="header-actions">
        <SettingsButton @click="store.showSettings = true" />
        <button class="quit-btn" @click="store.quitGame">Quitter</button>
      </div>
    </header>

    <div class="status-heading">
      <span>Statut des combattants :</span>
    </div>

    <!-- Fighter List -->
    <div class="fighters-list">
      <div
        v-for="(char, index) in store.characterList"
        :key="index"
        class="fighter-row"
        :class="{
          'dead-row': store.deadCharactersIndices.includes(index),
          'stupid-row': char.isStupid && !store.deadCharactersIndices.includes(index)
        }"
        @click="store.selectCharacter(index)"
      >
        <div class="fighter-info">
          <div class="fighter-name-line">
            <span class="fighter-name">{{ char.name.toUpperCase() }}</span>
            <span v-if="store.deadCharactersIndices.includes(index)" class="badge badge-dead">
              DÉCÉDÉ
            </span>
            <span v-else-if="char.isStupid" class="badge badge-stupid">
              STUPIDE
            </span>
          </div>

          <div class="stats-line">
            <template v-if="store.deadCharactersIndices.includes(index)">
              <span class="dead-notice">Mort au combat — Aucun attribut disponible.</span>
            </template>
            <template v-else>
              <MonitoringStatLabel label="CC" :current-value="char.baseCC + char.permCC" :base-value="char.baseCC" />
              <MonitoringStatLabel label="F" :current-value="char.baseF + char.permF" :base-value="char.baseF" />
              <MonitoringStatLabel label="E" :current-value="char.baseE + char.tempE" :base-value="char.baseE" />
              <MonitoringStatLabel label="I" :current-value="char.baseI + char.tempI" :base-value="char.baseI" />
              <MonitoringStatLabel label="A" :current-value="char.baseA + char.permA" :base-value="char.baseA" />
              <MonitoringStatLabel label="Cd" :current-value="char.baseCd + char.permCd" :base-value="char.baseCd" />
            </template>
          </div>
        </div>

        <div class="fighter-action">
          <span :class="store.deadCharactersIndices.includes(index) ? 'action-dead' : 'action-active'">
            {{ store.deadCharactersIndices.includes(index) ? 'Gérer ›' : 'Lancer Dés ›' }}
          </span>
        </div>
      </div>
    </div>

    <!-- Next Turn Button -->
    <footer class="footer-actions">
      <button class="btn btn-next-turn" @click="store.nextTurn">
        PASSER AU TOUR SUIVANT ›
      </button>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'
import MonitoringStatLabel from '../components/MonitoringStatLabel.vue'

const store = useGameStore()
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

.turn-info {
  display: flex;
  flex-direction: column;
}

.sub-title {
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.turn-title {
  color: var(--tzeentch-gold);
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.quit-btn {
  background: transparent;
  color: var(--khorne-red);
  border: 1px solid rgba(139, 0, 0, 0.4);
  font-size: 13px;
  font-weight: 700;
  padding: 6px 14px;
  border-radius: 6px;
  transition: all 0.15s ease;
}

.quit-btn:hover {
  background: var(--khorne-red);
  color: #fff;
  border-color: var(--khorne-red);
}

.status-heading {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

.fighters-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 16px;
}

.fighter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease;
}

.fighter-row:hover {
  border-color: var(--border-hover);
  transform: translateY(-1px);
}

.dead-row {
  background-color: var(--dead-bg);
  border-color: rgba(139, 0, 0, 0.5);
}

.stupid-row {
  border-color: #D32F2F;
}

.fighter-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.fighter-name-line {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fighter-name {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 700;
}

.dead-row .fighter-name {
  color: var(--text-secondary);
}

.badge {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
}

.badge-dead {
  background-color: var(--khorne-red);
  color: #fff;
}

.badge-stupid {
  background-color: var(--stupid-bg);
  color: #D32F2F;
  border: 1px solid #D32F2F;
}

.stats-line {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.dead-notice {
  color: var(--text-secondary);
  font-size: 13px;
  font-style: italic;
}

.fighter-action {
  margin-left: 12px;
  white-space: nowrap;
}

.action-active {
  color: var(--tzeentch-gold);
  font-weight: 700;
  font-size: 14px;
}

.action-dead {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 14px;
}

.footer-actions {
  display: flex;
}

.btn-next-turn {
  width: 100%;
  height: 54px;
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0.5px;
  border-radius: 10px;
  box-shadow: var(--shadow-gold);
}

.btn-next-turn:hover {
  background-color: var(--tzeentch-gold-hover);
}
</style>

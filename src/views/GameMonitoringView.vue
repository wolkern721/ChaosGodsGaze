<template>
  <div v-if="session" class="screen monitoring-screen">
    <!-- Header -->
    <header class="header">
      <div class="header-top">
        <button class="back-btn" @click="store.quitSessionToSessionsList">
          ‹ Mes Parties
        </button>
        <div class="header-actions">
          <SettingsButton @click="store.showSettings = true" />
          <button class="quit-btn" @click="store.quitSessionToSessionsList">Quitter</button>
        </div>
      </div>

      <div class="turn-info">
        <div class="session-meta">
          <span class="session-title">{{ session.name }}</span>
          <span class="warband-subtitle">🛡️ {{ session.warbandName }}</span>
        </div>
        <h1 class="turn-title">TOUR EN COURS : {{ store.currentTurn }}</h1>
      </div>
    </header>

    <div class="status-heading">
      <span>Combattants ({{ session.characterList.length - store.deadCharactersIndices.length }} en vie) :</span>
    </div>

    <!-- Fighter List -->
    <div class="fighters-list">
      <div
        v-for="(char, index) in session.characterList"
        :key="char.id || index"
        class="fighter-row"
        :class="{
          'dead-row': store.deadCharactersIndices.includes(index),
          'stupid-row': char.isStupid && !store.deadCharactersIndices.includes(index),
          'rolled-row': session.currentTurnRolls[index] !== undefined && !store.deadCharactersIndices.includes(index)
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
            <span v-if="session.currentTurnRolls[index] !== undefined && !store.deadCharactersIndices.includes(index)" class="badge badge-roll">
              🎲 D6 = {{ session.currentTurnRolls[index] }}
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
  <div v-else class="screen">
    <p>Aucune session de jeu active.</p>
    <button class="btn btn-primary" @click="store.navigateTo('SESSIONS')">
      Retour à la liste des parties
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useGameStore } from '../stores/gameStore'
import SettingsButton from '../components/SettingsButton.vue'
import MonitoringStatLabel from '../components/MonitoringStatLabel.vue'

const store = useGameStore()
const session = computed(() => store.activeSession)
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
  flex-direction: column;
  margin-bottom: 16px;
  gap: 8px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn {
  background: transparent;
  color: var(--tzeentch-gold);
  font-size: 15px;
  font-weight: 700;
  padding: 4px 0;
}

.turn-info {
  display: flex;
  flex-direction: column;
}

.session-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 4px;
}

.session-title {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.warband-subtitle {
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 600;
}

.turn-title {
  color: var(--tzeentch-gold);
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quit-btn {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  font-size: 13px;
  font-weight: 700;
  padding: 5px 12px;
  border-radius: 6px;
  transition: all 0.15s ease;
}

.quit-btn:hover {
  background: var(--bg-surface-elevated);
  border-color: var(--border-hover);
  color: var(--text-primary);
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

.rolled-row {
  border-left: 3px solid var(--bonus-green);
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
  flex-wrap: wrap;
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

.badge-roll {
  background-color: rgba(76, 175, 80, 0.15);
  color: var(--bonus-green);
  border: 1px solid var(--bonus-green);
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

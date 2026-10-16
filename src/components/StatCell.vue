<template>
  <div
    class="stat-cell"
    :class="{
      'is-modified': isModified,
      'is-bonus': !isDead && currentValue > baseValue,
      'is-malus': !isDead && currentValue < baseValue,
      'is-dead': isDead
    }"
  >
    <span class="cell-label">{{ label }}</span>

    <!-- Main current value -->
    <span class="cell-value">
      {{ currentValue }}
    </span>

    <!-- Difference / Base indicator tag -->
    <div v-if="!isDead && isModified" class="diff-badge">
      <span class="diff-sign">{{ diffText }}</span>
      <span class="diff-base">({{ baseValue }})</span>
    </div>
    <div v-else class="base-value-placeholder">
      <span class="base-text">{{ isDead ? '—' : baseValue }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  label: string
  currentValue: number
  baseValue: number
  isDead: boolean
}>()

const isModified = computed(() => {
  return props.currentValue !== props.baseValue && !props.isDead
})

const diffText = computed(() => {
  const diff = props.currentValue - props.baseValue
  return diff > 0 ? `+${diff}` : `${diff}`
})
</script>

<style scoped>
.stat-cell {
  flex: 1;
  min-width: 0;
  background-color: var(--bg-surface-elevated);
  border: 1.5px solid var(--border-color);
  border-radius: 8px;
  padding: 8px 2px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-card);
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

.cell-label {
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.cell-value {
  font-size: 22px;
  font-weight: 900;
  line-height: 1.1;
  margin: 3px 0;
  color: var(--text-primary);
  transition: color 0.2s ease;
}

/* Base placeholder under value */
.base-value-placeholder {
  height: 16px;
  display: flex;
  align-items: center;
}

.base-text {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
  opacity: 0.6;
}

/* Diff Badge (+1, -1) */
.diff-badge {
  display: flex;
  align-items: center;
  gap: 2px;
  height: 16px;
  padding: 0 4px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 800;
}

.diff-base {
  font-size: 10px;
  opacity: 0.75;
  font-weight: 600;
}

/* Bonus styling */
.stat-cell.is-bonus {
  background: linear-gradient(180deg, var(--bg-surface-elevated) 0%, rgba(76, 175, 80, 0.15) 100%);
  border-color: var(--bonus-green);
  box-shadow: 0 0 12px rgba(76, 175, 80, 0.35);
}

.stat-cell.is-bonus .cell-label {
  color: var(--bonus-green);
}

.stat-cell.is-bonus .cell-value {
  color: var(--bonus-green);
  text-shadow: 0 0 10px rgba(76, 175, 80, 0.4);
}

.stat-cell.is-bonus .diff-badge {
  background-color: var(--bonus-green);
  color: #fff;
}

/* Malus styling */
.stat-cell.is-malus {
  background: linear-gradient(180deg, var(--bg-surface-elevated) 0%, rgba(229, 115, 115, 0.15) 100%);
  border-color: var(--malus-red);
  box-shadow: 0 0 12px rgba(229, 115, 115, 0.35);
}

.stat-cell.is-malus .cell-label {
  color: var(--malus-red);
}

.stat-cell.is-malus .cell-value {
  color: var(--malus-red);
  text-shadow: 0 0 10px rgba(229, 115, 115, 0.4);
}

.stat-cell.is-malus .diff-badge {
  background-color: var(--malus-red);
  color: #fff;
}

/* Dead styling */
.stat-cell.is-dead {
  background-color: var(--dead-bg);
  border-color: var(--border-color);
  opacity: 0.6;
}

.stat-cell.is-dead .cell-value,
.stat-cell.is-dead .cell-label {
  color: var(--text-secondary);
}
</style>

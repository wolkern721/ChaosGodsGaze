<template>
  <div
    class="stat-cell"
    :class="{ modified: isModified }"
    :style="cellStyle"
  >
    <span class="cell-label">{{ label }}</span>
    <span
      class="cell-value"
      :class="{
        'is-dead': isDead,
        'is-bonus': !isDead && currentValue > baseValue,
        'is-malus': !isDead && currentValue < baseValue,
        'is-neutral': !isDead && currentValue === baseValue
      }"
    >
      {{ currentValue }}
    </span>
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

const cellStyle = computed(() => {
  if (isModified.value) {
    const borderColor = props.currentValue > props.baseValue
      ? 'var(--bonus-green)'
      : 'var(--malus-red)'
    return {
      borderColor: borderColor
    }
  }
  return {}
})
</script>

<style scoped>
.stat-cell {
  flex: 1;
  min-width: 0;
  background-color: var(--bg-surface);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 8px 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.cell-label {
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: bold;
}

.cell-value {
  font-size: 20px;
  font-weight: bold;
  margin-top: 2px;
}

.cell-value.is-dead {
  color: var(--text-secondary);
}

.cell-value.is-bonus {
  color: var(--bonus-green);
}

.cell-value.is-malus {
  color: var(--malus-red);
}

.cell-value.is-neutral {
  color: var(--text-primary);
}
</style>

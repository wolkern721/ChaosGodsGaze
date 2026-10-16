<template>
  <div class="stat-label">
    <span class="label">{{ label }}:</span>
    <span
      class="value"
      :class="{
        'is-bonus': currentValue > baseValue,
        'is-malus': currentValue < baseValue,
        'is-neutral': currentValue === baseValue,
        'is-bold': currentValue !== baseValue
      }"
    >
      {{ currentValue }}
    </span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  currentValue: number
  baseValue: number
}>()
</script>

<style scoped>
.stat-label {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  gap: 2px;
}

.label {
  color: var(--text-secondary);
}

.value {
  padding-left: 2px;
}

.value.is-bonus {
  color: var(--bonus-green);
}

.value.is-malus {
  color: var(--malus-red);
}

.value.is-neutral {
  color: var(--text-primary);
}

.value.is-bold {
  font-weight: bold;
}
</style>

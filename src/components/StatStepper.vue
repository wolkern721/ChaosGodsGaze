<template>
  <div class="stat-stepper" :class="{ focused: isFocused }">
    <span class="stepper-label">{{ label }}</span>

    <button
      type="button"
      class="step-btn step-up"
      @click="step(1)"
      tabindex="-1"
      aria-label="Augmenter"
    >
      <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 8l-6 6 1.41 1.41L12 10.83l4.59 4.58L18 14z" />
      </svg>
    </button>

    <input
      :value="modelValue"
      type="number"
      inputmode="numeric"
      class="stepper-input"
      @input="handleInput"
      @focus="isFocused = true"
      @blur="isFocused = false"
    />

    <button
      type="button"
      class="step-btn step-down"
      @click="step(-1)"
      tabindex="-1"
      aria-label="Diminuer"
    >
      <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
        <path d="M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z" />
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  label: string
  modelValue: number | string
  min?: number
  max?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: number): void
}>()

const isFocused = ref(false)

function step(delta: number) {
  const current = Number(props.modelValue) || 0
  const next = current + delta
  const min = props.min ?? 1
  const max = props.max ?? 10
  if (next >= min && next <= max) {
    emit('update:modelValue', next)
  }
}

function handleInput(event: Event) {
  const val = Number((event.target as HTMLInputElement).value)
  if (!isNaN(val)) {
    emit('update:modelValue', val)
  }
}
</script>

<style scoped>
.stat-stepper {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: var(--bg-surface-elevated);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 8px 4px;
  min-width: 0;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  user-select: none;
}

.stat-stepper:hover {
  border-color: var(--border-hover);
}

.stat-stepper.focused {
  border-color: var(--tzeentch-gold);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.25);
}

.stepper-label {
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.5px;
  color: var(--tzeentch-gold);
  margin-bottom: 4px;
  text-transform: uppercase;
}

.step-btn {
  background: var(--bg-surface);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  width: 100%;
  height: 34px;
  border-radius: 6px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
  touch-action: manipulation;
}

.step-btn:hover {
  color: var(--tzeentch-gold);
  border-color: var(--tzeentch-gold);
  background-color: var(--bg-muted);
}

.step-btn:active {
  background-color: var(--tzeentch-gold);
  color: #0F0F12;
  transform: scale(0.95);
}

.stepper-input {
  width: 100%;
  text-align: center;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 800;
  padding: 4px 0;
  outline: none;
}
</style>

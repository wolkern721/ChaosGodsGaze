import type { ChaosCharacter } from '../types/character'

export const EyeOfTheGodsRules = {
  applyRoll(char: ChaosCharacter, roll: number): string {
    switch (roll) {
      case 1:
        if (!char.isStupid) {
          char.isStupid = true
          return 'Stupide ! Le personnage devient Stupide.'
        } else {
          char.permCd -= 1
          return 'Déjà stupide ! Malus cumulatif de -1 en Commandement.'
        }
      case 2:
        char.tempI += 1
        return '+1 en Initiative (Temporaire)'
      case 3:
        char.tempE += 1
        return '+1 en Endurance (Temporaire)'
      case 4:
        char.permCC += 1
        return '+1 en Capacité de Combat (Permanent)'
      case 5:
        char.permA += 1
        return '+1 Attaque (Permanent)'
      case 6:
        char.permF += 1
        char.permCd += 1
        return '+1 en Force et +1 en Commandement (Permanent)'
      default:
        return 'Jet invalide'
    }
  },

  cancelRoll(character: ChaosCharacter, roll: number): void {
    switch (roll) {
      case 1:
        character.isStupid = false
        break
      case 2:
        character.permCC = Math.max(0, character.permCC - 1)
        break
      case 3:
        character.permF = Math.max(0, character.permF - 1)
        break
      case 4:
        character.tempE = Math.max(0, character.tempE - 1)
        break
      case 5:
        character.tempI = Math.max(0, character.tempI - 1)
        break
      case 6:
        character.permA = Math.max(0, character.permA - 1)
        character.permCd -= 1
        break
    }
  }
}

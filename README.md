# Chaos Gods Gaze (CGG)

Compagnon de jeu pour **Warhammer: The Old World / Battle**, facilitant le suivi de la règle du **Regard des Dieux** (*Eye of the Gods*) pour les champions du Chaos.

---

## ⚡ Caractéristiques
- **Progressive Web App (PWA)** : Installable sur Android, iOS et Desktop, avec mode plein écran natif et support hors-ligne (100% offline).
- **Stack moderne** : Vue 3, TypeScript, Pinia, Vite & `vite-plugin-pwa`.
- **Thème Grimdark** : Mode Nuit (Khorne Red, Tzeentch Gold) et Mode Jour avec bascule instantanée.
- **Suivi en direct** : Gestion du tour par tour, des statistiques Warhammer (CC, F, E, I, A, Cd) et application immédiate des effets du D6.

---

## 🚀 Développement Local

### Prérequis
- [Node.js](https://nodejs.org/) (version 20+)
- npm

### Commandes
```bash
# Installation des dépendances
npm install

# Démarrer le serveur de développement
npm run dev

# Compiler pour la production (build PWA)
npm run build

# Prévisualiser le build de production
npm run preview
```

---

## 🌐 Déploiement

Le projet se déploie automatiquement sur **GitHub Pages** lors d'un push sur la branche `develop` via GitHub Actions (`.github/workflows/deploy-pages.yml`).

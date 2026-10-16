# Chaos Gods Gaze (CGG)

Compagnon de jeu pour **Warhammer: The Old World / Battle**, facilitant le suivi de la règle du **Regard des Dieux** (*Eye of the Gods*) pour les champions du Chaos.

---

## ⚡ Caractéristiques
- **Progressive Web App (PWA)** : Installable sur Android, iOS et Desktop, avec mode plein écran natif et support hors-ligne (100% offline).
- **Stack moderne** : Vue 3, TypeScript, Pinia, Vite & `vite-plugin-pwa`.
- **Multi-listes & Multi-parties** : Créez et gérez plusieurs bandes de guerre réutilisables, dupliquez-les en un clic, et lancez/sauvegardez plusieurs batailles indépendantes sans perte de progression.
- **Thème Grimdark** : Mode Nuit (Khorne Red, Tzeentch Gold) et Mode Jour avec bascule instantanée.
- **Suivi en direct** : Gestion du tour par tour, des statistiques Warhammer (CC, F, E, I, A, Cd) et application immédiate des effets du D6 (lancer animé ou saisie manuelle).
- **Anti-veille (Wake Lock)** : Maintient l'écran allumé pendant vos parties de jeu de figurines.

---

## 📱 Installation de l'application (PWA)

L'application est une **Progressive Web App (PWA)**. Elle ne nécessite aucun téléchargement via un store applicatif et fonctionne **100% hors-ligne** une fois installée ou visitée une première fois.

### 🤖 Sur Android (Chrome, Edge, Firefox, Brave)
1. Ouvrez l'adresse de l'application dans votre navigateur mobile.
2. Une bannière en bas de l'écran ou un pop-up vous proposera généralement **« Ajouter Chaos Gods Gaze à l'écran d'accueil »** ou **« Installer l'application »**.
3. Si la bannière n'apparaît pas :
   - Appuyez sur le menu du navigateur (**⋮** en haut à droite).
   - Sélectionnez **Installer l'application** (ou **Ajouter à l'écran d'accueil**).
4. L'application apparaît dans votre tiroir d'applications et sur votre écran d'accueil avec son icône dédiée, et s'ouvre en plein écran sans l'interface du navigateur (WebAPK native).

---

### 🍏 Sur iOS (iPhone / iPad via Safari)
> [!NOTE]
> Sur iOS, Apple impose d'utiliser le navigateur **Safari** pour installer une PWA sur l'écran d'accueil.

1. Ouvrez l'adresse de l'application dans **Safari**.
2. Appuyez sur le bouton de **Partage** (icône du carré avec une flèche vers le haut $\lceil\uparrow\rceil$ au bas de l'écran).
3. Faites défiler le menu vers le bas et sélectionnez **Sur l'écran d'accueil** (ou *Add to Home Screen*).
4. Confirmez en appuyant sur **Ajouter** en haut à droite.
5. L'icône de l'application est désormais sur votre écran d'accueil. En la lançant, elle s'exécute dans une fenêtre autonome en plein écran, sans la barre d'adresse ni les onglets Safari.

---

### 💻 Sur Desktop (Windows, macOS, Linux — Chrome / Edge / Brave)
1. Rendez-vous sur l'adresse de l'application depuis votre navigateur de bureau.
2. Dans la barre d'adresse, cliquez sur l'icône d'installation située à droite (icône d'écran avec une flèche vers le bas ou symbole `+`).
3. Ou cliquez sur le menu du navigateur (**⋮**) puis **Installer Chaos Gods Gaze…**
4. L'application est installée comme une application bureau indépendante avec sa propre fenêtre et son raccourci système.

---

## 🚀 Développement Local

### Prérequis
- [Node.js](https://nodejs.org/) (version 20+)
- npm

### Commandes
```bash
# Installation des dépendances
npm install

# Démarrer le serveur de développement (HMR actif)
npm run dev

# Compiler pour la production (build PWA + Service Worker)
npm run build

# Prévisualiser le build de production localement
npm run preview
```

---

## 🌐 Déploiement

Le projet se déploie automatiquement sur **GitHub Pages** lors d'un push sur la branche `develop` via le workflow GitHub Actions (`.github/workflows/deploy-pages.yml`).

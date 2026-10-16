import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  base: './',
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['icons/*.png', 'screenshots/*.png'],
      manifest: {
        id: 'org.wolkern.cgg',
        name: 'Chaos Gods Gaze',
        short_name: 'CGG',
        description: 'Compagnon de jeu Warhammer pour la règle du Regard des Dieux',
        theme_color: '#F3F0E8',
        background_color: '#F3F0E8',
        display: 'standalone',
        orientation: 'any',
        scope: './',
        start_url: './',
        categories: ['games', 'utilities'],
        icons: [
          {
            src: 'icons/72x72-CGG.png',
            sizes: '72x72',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/96x96-CGG.png',
            sizes: '96x96',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/128x128-CGG.png',
            sizes: '128x128',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/144x144-CGG.png',
            sizes: '144x144',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/152x152-CGG.png',
            sizes: '152x152',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/192x192-CGG.png',
            sizes: '192x192',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/384x384-CGG.png',
            sizes: '384x384',
            type: 'image/png',
            purpose: 'maskable any'
          },
          {
            src: 'icons/512x512-CGG.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'maskable any'
          }
        ],
        screenshots: [
          {
            src: 'screenshots/screenshot-mobile.png',
            sizes: '512x512',
            type: 'image/png',
            form_factor: 'narrow',
            label: 'Monitoring des champions du Chaos'
          },
          {
            src: 'screenshots/screenshot-desktop.png',
            sizes: '512x512',
            type: 'image/png',
            form_factor: 'wide',
            label: 'Tableau de bord et lancer de dés'
          }
        ]
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,png,svg,ico}']
      }
    })
  ]
})

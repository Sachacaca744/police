# Mod Système de recherche (Forge 1.20.1)

Compilation : voir les explications dans la conversation (GitHub Actions, dossier `recherche-mod`).
Jar produit : `build/libs/recherche-1.0.0.jar`.

## Fonctionnement
- Frapper un joueur / villageois / policier : 1 étoile. Tuer : +1 étoile (minimum 2). Frapper un policier : minimum 2 étoiles. Tuer un policier : +2 (minimum 3).
- Étoiles affichées en haut à droite (clignotent rouge/bleu). Max 5.
- Des policiers (étoiles + 1) apparaissent, te suivent et te tirent dessus. Sans crime, ils ne tirent jamais.
- Perdre les policiers de vue 40 s = -1 étoile. Mourir ou quitter le jeu = recherche remise à 0.
- Pistolet : craft (3 lingots de fer en haut, 1 lingot + 1 bâton au milieu, 1 bâton en bas), clic droit pour tirer, munitions = pépites de fer (gratuit en créatif).
- Créatif/spectateur : jamais recherché.
- `/recherche set <0-5>` (op) pour tester.

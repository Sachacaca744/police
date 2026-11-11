# Mod Métiers (Forge 1.20.1)

## Obtenir le .jar (compilation automatique via GitHub)
1. Crée un dépôt GitHub (vide) et envoie-y tout le contenu de ce dossier
   (y compris le dossier caché `.github`). Le plus simple : `git init`, `git add .`, `git commit`, `git push`.
2. Onglet **Actions** > "Build mod" > attends le point vert (~3-5 min).
3. En bas de la page du run : **Artifacts > metiers-mod** = un .zip contenant `metiers-1.0.0.jar`.
4. Mets le .jar dans `.minecraft/mods` avec **Forge 1.20.1 (47.x)**.

(En local : JDK 17 + Gradle 8.1.1, puis `gradle build` -> `build/libs/metiers-1.0.0.jar`.)

## Utilisation
- **P** ou **/metier** (ou **/métier**) : menu des métiers. Changement possible toutes les 2 h.
- **/metier vehicule** : renvoie ton véhicule (policier / chauffeur) s'il a été perdu.
- **/metier reset** : (op) supprime le délai de 2 h.
- Conduite : clic droit sur le véhicule, Z/S avancer/reculer, Q/D tourner, Shift pour descendre.
  Frapper son propre véhicule le supprime.

## Métiers
- Pompier : immunisé feu/lave. Policier : ne blesse pas les villageois, voiture de police, +50% dégâts sur criminels.
- Médecin : Shift + clic droit pour soigner. Commerçant : réductions villageois + 32 émeraudes.
- Chômeur : 3 pains / 10 min. Civil : rien.
- Dans Civil : Chauffeur routier (camion, perdu au changement), Criminel (rapide), Agriculteur (récoltes x2), Mineur (célérité).

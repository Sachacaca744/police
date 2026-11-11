package com.metiers.job;

public enum Job {
    CIVIL("civil", "Civil", "Un simple citoyen. Aucun avantage particulier."),
    CHOMEUR("chomeur", "Chômeur", "Allocation : 3 pains toutes les 10 minutes."),
    POMPIER("pompier", "Pompier", "Immunisé au feu et à la lave. Kit : seau d'eau + hache."),
    POLICIER("policier", "Policier", "Ne peut pas blesser les villageois. Voiture de police gratuite. +50% de dégâts sur les criminels."),
    MEDECIN("medecin", "Médecin", "Shift + clic droit sur quelqu'un pour le soigner."),
    COMMERCANT("commercant", "Commerçant", "Réductions chez les villageois. Démarre avec 32 émeraudes."),
    CHAUFFEUR("chauffeur", "Chauffeur routier", "Camion gratuit (perdu si tu changes de métier)."),
    CRIMINEL("criminel", "Criminel", "Rapide, mais les policiers te frappent plus fort."),
    AGRICULTEUR("agriculteur", "Agriculteur", "Récoltes mûres : drops doublés. Kit de fermier."),
    MINEUR("mineur", "Mineur", "Célérité permanente. Kit de mineur.");

    public final String id;
    public final String display;
    public final String description;

    Job(String id, String display, String description) {
        this.id = id;
        this.display = display;
        this.description = description;
    }

    public static Job byId(String id) {
        for (Job j : values()) {
            if (j.id.equals(id)) return j;
        }
        return null;
    }
}

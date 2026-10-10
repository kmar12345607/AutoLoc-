\# Stratégie de fetch et de cascade — AutoLoc



\## Principes retenus



\- \*\*Côté propriétaire\*\* : toujours le côté `@ManyToOne` (il porte la clé étrangère). Le côté inverse utilise `mappedBy` et ne crée aucune colonne.

\- \*\*Fetch\*\* : `LAZY` partout. `@ManyToOne` et `@OneToOne` sont `EAGER` par défaut, on les force à `LAZY` pour ne pas charger toute une chaîne d'objets liés. Les collections restent `LAZY`.

\- \*\*Cascade\*\* : uniquement quand l'enfant ne peut pas vivre sans son parent (composition) ou quand il est créé en même temps que lui. Jamais de `REMOVE` sur une entité qui survit à son parent.

\- \*\*Lombok\*\* : `@Getter`/`@Setter` ciblés, pas de `@Data`, pour éviter les boucles infinies de `toString`/`hashCode` sur les associations bidirectionnelles.



\## Tableau des associations



| Association | Fetch | Cascade | Justification |

|---|---|---|---|

| Contrat → Paiement | LAZY | ALL + orphanRemoval | Un paiement n'existe que rattaché à son contrat (composition) : supprimer le contrat supprime ses paiements, et retirer un paiement de la liste le supprime en base. |

| Agence → Vehicule | LAZY | Aucune | Un véhicule survit à la suppression de son agence : il peut être réaffecté. La liste n'est chargée que si on en a besoin. |

| Agence → Employe | LAZY | Aucune | Un employé existe indépendamment de son agence (mutation possible). Aucune suppression en cascade. |

| Vehicule ↔ Equipement | LAZY | Aucune | Les équipements (GPS, siège bébé...) sont partagés entre véhicules : supprimer un véhicule ne doit pas supprimer un équipement utilisé ailleurs. On utilise un `Set` pour éviter les doublons et les réinsertions complètes de la table de jointure `vehicule\_equipement`. |

| Client → Reservation | LAZY | PERSIST | Enregistrer un nouveau client avec ses premières réservations les enregistre aussi. Pas de `REMOVE` : l'historique des réservations ne doit pas disparaître avec le client. |

| Reservation → Vehicule | LAZY | Aucune | Une réservation référence un véhicule qui existe indépendamment d'elle. `ManyToOne` forcé à LAZY (EAGER par défaut). |

| Reservation ↔ Contrat | LAZY | ALL (côté Reservation) | Le contrat est issu de la réservation et n'a pas de sens sans elle (composition). Le côté propriétaire est `Contrat` (colonne de clé étrangère). Remarque : côté inverse d'un `OneToOne`, Hibernate charge en pratique l'objet lié sans amélioration de bytecode. |

| Vehicule → Maintenance | LAZY | PERSIST | Une maintenance est créée en même temps que son véhicule lors de l'enregistrement, mais l'historique de maintenance ne doit pas être supprimé automatiquement avec le véhicule. |



\## Conséquence du choix LAZY



Accéder à une collection `LAZY` hors transaction lève une `LazyInitializationException`. Elle sera traitée par des requêtes dédiées (Atelier 7), pas en repassant à `EAGER`.


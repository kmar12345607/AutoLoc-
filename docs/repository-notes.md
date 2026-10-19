# Notes — Couche Repository (Spring Data JPA)

## A. Choix d'interface

Les neuf repositories sont dans `tn.esprit.autoloc.repository`, nommés `I…Repository`, et étendent tous `JpaRepository<Entité, Long>` (clé primaire `Long`, générée par MySQL en `IDENTITY`).

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, `findAll` renvoie une `List`, tri et pagination disponibles. |
| IEmployeRepository | JpaRepository<Employe, Long> | Idem. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | Idem ; tri et pagination utiles pour une liste de véhicules. |
| IEquipementRepository | JpaRepository<Equipement, Long> | Idem. |
| IClientRepository | JpaRepository<Client, Long> | Idem. |
| IReservationRepository | JpaRepository<Reservation, Long> | Idem. |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. Créé d'abord avec `CrudRepository`, puis passé à `JpaRepository`. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Créé pour lire les paiements. Un paiement fait partie du contrat (composition) : le code métier crée ou retire un paiement à travers le `Contrat`. |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, mêmes avantages que les autres. |

**Pourquoi `JpaRepository` plutôt que les autres interfaces ?**
- `Repository` : interface marqueur sans méthode, il faudrait recopier chaque signature.
- `CrudRepository` : `findAll` renvoie un `Iterable`, moins pratique qu'une `List`.
- `PagingAndSortingRepository` : tri et pagination seulement, sans CRUD depuis Spring Data 3.
- `JpaRepository` réunit CRUD, `List`, tri, pagination et méthodes JPA (`flush`, `saveAndFlush`, `getReferenceById`).

**Précaution** : les suppressions en lot (`deleteAllInBatch`, `deleteAllByIdInBatch`) contournent le contexte de persistance. La cascade et l'`orphanRemoval` ne s'appliquent pas : supprimer ainsi des contrats laisserait des paiements orphelins, ou ferait échouer la requête sur la clé étrangère. On utilise `deleteById` / `delete`, qui passent par le contexte de persistance.

**Rappels sur `save`** : identifiant `null` → `persist` (INSERT) ; identifiant renseigné → `merge` (SELECT puis UPDATE si des champs ont changé), et il faut utiliser l'objet retourné. `deleteById` ne lève pas d'exception si l'identifiant n'existe pas.

## B. Anomalies SonarQube for IDE

Analyse faite sur une classe de démonstration `SonarDemo` (supprimée ensuite, elle ne figure pas dans le commit). Le panneau Findings a relevé 5 anomalies.

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
|---|---|---|
| (3, 7) Import `java.util.Date` jamais utilisé | java:S1128 — *Unnecessary imports should be removed* : un import inutile nuit à la lisibilité, peut créer des conflits de noms et alourdit la complétion. | Import supprimé. |
| (9, 8) `System.out.println("test")` | java:S106 — *Standard outputs should not be used directly to log anything* : la sortie standard n'est ni formatée, ni configurable, ni niveau de log. | Remplacé par un logger Lombok : `@Slf4j` et `log.info("test")`. |
| (8, 12) Variable locale `inutile` jamais utilisée | java:S1481 — *Unused local variables should be removed* : code mort qui complique la lecture. | Variable supprimée. |
| (8, 20) Affectation `inutile = 5` sans effet | java:S1854 — *Unused assignments should be removed* : valeur jamais lue, calcul inutile. | Disparaît avec la suppression de la variable. |
| (0, 0) Utilisation de `java.util.Date` | java:S2143 — *"java.time" classes should be used for dates and times* : `Date` est source d'erreurs, notamment en multithread ; l'API `java.time` est immuable et thread-safe. | Disparaît avec la suppression de l'import ; on utilise `LocalDate` / `LocalDateTime` comme dans les entités. |

Après correction, l'onglet Findings est vide.

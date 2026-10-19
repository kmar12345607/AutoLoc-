# Notes — Couche Service, IoC et injection de dépendances

## A. Modes d'injection comparés

| Critère | Constructeur (retenu) | Attribut (`@Autowired`) |
|---|---|---|
| Champ `final` possible ? | Oui | Non |
| Dépendance visible dans la signature de la classe ? | Oui, c'est un paramètre du constructeur | Non, elle est cachée dans un champ privé |
| Utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : impossible de créer l'objet sans fournir le repository | Non : `new` laisse le champ à `null` (NullPointerException à l'appel) |
| Signalé par SonarQube for IDE ? | Non | Oui (règle java:S6813 : préférer l'injection par constructeur) |

## B. Services

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| AgenceServiceImpl | IAgenceRepository | Constructeur | Champ `final`, dépendance typée par interface ; refuse nom, ville ou adresse vides. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Idem ; refuse nom, prénom ou rôle manquant. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Idem ; refuse un tarif journalier absent ou négatif. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Idem ; refuse un libellé vide. |
| ClientServiceImpl | IClientRepository | Constructeur | Idem ; refuse nom, prénom ou e-mail vides. |
| ReservationServiceImpl | IReservationRepository | Constructeur | Idem ; refuse une date de fin antérieure à la date de début. |
| ContratServiceImpl | IContratRepository | Constructeur | Idem ; refuse un montant total négatif ; la suppression d'un contrat supprime ses paiements par cascade. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Lecture seule : un paiement fait partie du contrat et se gère à travers lui. |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Idem ; refuse une date de fin antérieure à la date de début. |

Remarque : pour les entités liées par des associations (Reservation, Vehicule, Employe, Maintenance, Contrat), `update` ne recopie que les attributs simples ; la gestion des liens est traitée au Chapitre 9.

## C. Messages d'erreur du conteneur

**Message 1 — bean introuvable** : `NoSuchBeanDefinitionException`, enveloppée dans une `UnsatisfiedDependencyException`. Le constructeur de `ContratController` réclame un bean de type `IContratService`, et le conteneur n'en trouve aucun.
- Causes probables côté `ContratServiceImpl` : l'annotation `@Service` a été oubliée, donc la classe n'est pas détectée ; ou la classe se trouve en dehors du package scanné `tn.esprit.autoloc`.
- Correction : ajouter `@Service` sur la classe et vérifier qu'elle est bien dans un sous-package de `tn.esprit.autoloc`.

**Message 2 — bean ambigu** : `NoUniqueBeanDefinitionException`. Le constructeur de `NotificationController` attend un seul bean, mais `emailNotificateur` et `smsNotificateur` conviennent tous les deux.
- Correction 1 : placer `@Primary` sur la **classe** du bean à privilégier par défaut (ex. `EmailNotificateur`).
- Correction 2 : placer `@Qualifier("emailNotificateur")` sur le **paramètre du constructeur** du contrôleur, pour désigner explicitement le bean voulu.

**Message 3 — dépendance circulaire** : `BeanCurrentlyInCreationException` entre `clientServiceImpl` et `reservationServiceImpl`.
- Avec l'injection par constructeur, il faut disposer de B complètement construit pour créer A, et de A pour créer B. Aucune des deux instances ne peut exister en premier, donc le cycle ne peut pas être contourné.
- Piste de conception : extraire ce que les deux services partagent dans un troisième service dont ils dépendent tous les deux, ou faire dépendre l'un des services du repository de l'autre plutôt que de son service.

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction |
|---|---|---|
| java:S6813 | Injection par attribut (`@Autowired` sur un champ) : dépendance cachée, champ non `final`, `null` si l'objet est créé avec `new` hors conteneur. | Retour à l'injection par constructeur avec `@RequiredArgsConstructor` et un champ `final`. |
| java:S1128 | Import inutilisé `lombok.RequiredArgsConstructor`, devenu inutile après le retrait de l'annotation pour l'injection par attribut. | Annotation remise lors du retour au constructeur : l'import est de nouveau utilisé. |
| java:S112 | Une `RuntimeException` générique est levée via `throw new RuntimeException` dans `findById` : elle ne se distingue pas d'une autre erreur et ne porte pas de sens métier. | Retour à `ResourceNotFoundException`, l'exception métier explicite, avec `orElseThrow`. |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'apparaît nulle part dans notre code. C'est le conteneur Spring qui l'exécute au démarrage (`SpringApplication.run`) : il détecte la classe grâce à `@Service`, crée d'abord le proxy de `IContratRepository`, puis appelle le constructeur en le lui passant.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour limiter le couplage : le contrôleur ne connaît que le contrat de l'interface. On peut changer l'implémentation, ou la remplacer par une fausse dans des tests, sans modifier le contrôleur.

3. **Pourquoi un service singleton doit-il rester sans état ?**
   Une seule instance sert toutes les requêtes en même temps, sur plusieurs threads. Avec un attribut modifiable comme `private Contrat contratCourant`, une requête écraserait la valeur d'une autre : un utilisateur pourrait alors consulter ou modifier le contrat d'un autre.

4. **Pourquoi `update` charge-t-il l'entité existante avant `save` ?**
   Envoyer directement l'objet reçu à `save` remplacerait toutes les colonnes : les attributs absents deviendraient `null`. De plus, avec `orphanRemoval`, une liste de paiements vide supprimerait tous les paiements du contrat. On charge donc l'existant et on ne recopie que les attributs modifiables.

5. **Différences.**
   - `@Component` se met sur une classe que l'on écrit et que le scan détecte. `@Bean` se met sur une méthode d'une classe `@Configuration`, pour une classe qu'on ne peut pas annoter (bibliothèque, JDK) ou une construction particulière.
   - `@Primary` désigne, sur la classe, le candidat par défaut pour tous les consommateurs. `@Qualifier` choisit un bean précis par son nom, à l'endroit de l'injection.
   - `IPaiementService` n'expose ni création, ni modification, ni suppression : un paiement est une composition du contrat (`cascade = ALL`, `orphanRemoval`), donc ces opérations passent par le contrat pour garder les données cohérentes.
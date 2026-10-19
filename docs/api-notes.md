New-Item -ItemType Directory -Force docs | Out-Null

$p = Join-Path (Get-Location).Path 'docs\\api-notes.md'

$t = @'

\# Notes — Atelier 5 : API REST, Postman et Swagger



\## A. Endpoints



Les 400 viennent de la validation (`@Valid`, contraintes des DTO) ou de `IllegalArgumentException` ; les 404 de `ResourceNotFoundException`. Le contrôleur ne produit aucun des deux lui-même : `GlobalExceptionHandler` les traduit. Un POST sur `/api/paiements` répond 405 (lecture seule).



| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |

|---|---|---|---|---|

| AgenceController | GET /api/agences | IAgenceService.findAll() | 200 | — |

| AgenceController | GET /api/agences/{id} | IAgenceService.findById(id) | 200 | 404 |

| AgenceController | POST /api/agences | IAgenceService.create(entité) | 201 | 400 |

| AgenceController | PUT /api/agences/{id} | IAgenceService.update(id, entité) | 200 | 404, 400 |

| AgenceController | DELETE /api/agences/{id} | IAgenceService.deleteById(id) | 204 | 404 |

| EmployeController | GET /api/employes | IEmployeService.findAll() | 200 | — |

| EmployeController | GET /api/employes/{id} | IEmployeService.findById(id) | 200 | 404 |

| EmployeController | POST /api/employes | IEmployeService.create(entité) | 201 | 400 |

| EmployeController | PUT /api/employes/{id} | IEmployeService.update(id, entité) | 200 | 404, 400 |

| EmployeController | DELETE /api/employes/{id} | IEmployeService.deleteById(id) | 204 | 404 |

| VehiculeController | GET /api/vehicules | IVehiculeService.findAll() | 200 | — |

| VehiculeController | GET /api/vehicules/{id} | IVehiculeService.findById(id) | 200 | 404 |

| VehiculeController | POST /api/vehicules | IVehiculeService.create(entité) | 201 | 400 |

| VehiculeController | PUT /api/vehicules/{id} | IVehiculeService.update(id, entité) | 200 | 404, 400 |

| VehiculeController | DELETE /api/vehicules/{id} | IVehiculeService.deleteById(id) | 204 | 404 |

| EquipementController | GET /api/equipements | IEquipementService.findAll() | 200 | — |

| EquipementController | GET /api/equipements/{id} | IEquipementService.findById(id) | 200 | 404 |

| EquipementController | POST /api/equipements | IEquipementService.create(entité) | 201 | 400 |

| EquipementController | PUT /api/equipements/{id} | IEquipementService.update(id, entité) | 200 | 404, 400 |

| EquipementController | DELETE /api/equipements/{id} | IEquipementService.deleteById(id) | 204 | 404 |

| ClientController | GET /api/clients | IClientService.findAll() | 200 | — |

| ClientController | GET /api/clients/{id} | IClientService.findById(id) | 200 | 404 |

| ClientController | POST /api/clients | IClientService.create(entité) | 201 | 400 |

| ClientController | PUT /api/clients/{id} | IClientService.update(id, entité) | 200 | 404, 400 |

| ClientController | DELETE /api/clients/{id} | IClientService.deleteById(id) | 204 | 404 |

| ReservationController | GET /api/reservations | IReservationService.findAll() | 200 | — |

| ReservationController | GET /api/reservations/{id} | IReservationService.findById(id) | 200 | 404 |

| ReservationController | POST /api/reservations | IReservationService.create(entité) | 201 | 400 |

| ReservationController | PUT /api/reservations/{id} | IReservationService.update(id, entité) | 200 | 404, 400 |

| ReservationController | DELETE /api/reservations/{id} | IReservationService.deleteById(id) | 204 | 404 |

| ContratController | GET /api/contrats | IContratService.findAll() | 200 | — |

| ContratController | GET /api/contrats/{id} | IContratService.findById(id) | 200 | 404 |

| ContratController | POST /api/contrats | IContratService.create(entité) | 201 | 400 |

| ContratController | PUT /api/contrats/{id} | IContratService.update(id, entité) | 200 | 404, 400 |

| ContratController | DELETE /api/contrats/{id} | IContratService.deleteById(id) | 204 | 404 |

| ContratController | POST /api/contrats/{id}/paiements | IContratService.addPaiement(id, paiement) | 201 | 404, 400 |

| ContratController | PUT /api/contrats/{id}/paiements/{idPaiement} | IContratService.updatePaiement(...) | 200 | 404, 400 |

| ContratController | DELETE /api/contrats/{id}/paiements/{idPaiement} | IContratService.removePaiement(...) | 204 | 404 |

| MaintenanceController | GET /api/maintenances | IMaintenanceService.findAll() | 200 | — |

| MaintenanceController | GET /api/maintenances/{id} | IMaintenanceService.findById(id) | 200 | 404 |

| MaintenanceController | POST /api/maintenances | IMaintenanceService.create(entité) | 201 | 400 |

| MaintenanceController | PUT /api/maintenances/{id} | IMaintenanceService.update(id, entité) | 200 | 404, 400 |

| MaintenanceController | DELETE /api/maintenances/{id} | IMaintenanceService.deleteById(id) | 204 | 404 |

| PaiementController | GET /api/paiements | IPaiementService.findAll() | 200 | — |

| PaiementController | GET /api/paiements/{id} | IPaiementService.findById(id) | 200 | 404 |



\## B. Annotations Jackson



Le projet expose des \*\*DTO\*\* (`web.dto`, records) et non les entités JPA : aucune boucle de sérialisation n'est possible, donc aucune annotation Jackson n'a été nécessaire sur les entités. Les associations sont exprimées par des identifiants.



| Association | Choix retenu | Justification |

|---|---|---|

| Contrat ↔ Paiement (composition) | `ContratDto.paiements` (liste de `PaiementDto`) et `PaiementDto.contratId` | Le paiement se crée dans le JSON de son contrat (cascade) ; le lien retour est un simple identifiant, en lecture seule. Équivalent d'une paire `@JsonManagedReference` / `@JsonBackReference`. |

| Agence → Vehicule / Employe (côté inverse) | Absents de `AgenceDto` | Équivalent d'un `@JsonIgnore` sur la liste inverse. Le véhicule désigne son agence par `agenceId`. |

| Reservation → Client, Vehicule | `clientId`, `vehiculeId` | Un `@JsonBackReference` ici ferait ignorer la propriété à la lecture, et le POST créerait une réservation sans client. |



\*\*`@JsonBackReference` ou `@JsonIgnore` ?\*\* `@JsonManagedReference` / `@JsonBackReference` forment une paire : la partie « managed » est sérialisée, la partie « back » est omise à la sérialisation mais \*\*reconstruite à la désérialisation\*\* (le paiement retrouve son contrat). `@JsonIgnore` supprime la propriété dans les deux sens : adapté à une liste inverse que l'on ne veut jamais voir ni recevoir dans le JSON, l'enfant désignant son parent par un identifiant.



\## C. Postman et Swagger



\- \*\*Postman\*\* sert à tester : requêtes rejouables, assertions (`pm.test`), variables d'environnement (`baseUrl`, `contratId`...). Exemple rencontré : « Consulter un contrat inexistant » doit renvoyer 404, et le scénario « Créer un contrat avec ses paiements » vérifie que 2 paiements sont créés en cascade.

\- \*\*Swagger UI\*\* sert à documenter et explorer : le contrat OpenAPI est généré depuis le code (`/api/v3/api-docs`), avec groupes (`@Tag`), résumés (`@Operation`) et réponses (`@ApiResponse`). Exemple : la section \*\*Schemas\*\* décrit `ContratDto` automatiquement.

\- Ils sont complémentaires : Postman vérifie le comportement, Swagger garde la documentation synchronisée avec le code.



\## D. Anomalies SonarQube



Analyse de `web.controller` avec SonarQube for IDE : 43 anomalies dans 8 fichiers, toutes de la même règle. Aucun Security Hotspot ni Taint Vulnerability.



| Règle | Explication | Correction |

|---|---|---|

| `java:S1710` — Annotation repetitions should not be wrapped (Maintainability), 43 occurrences | Avant Java 8, plusieurs annotations identiques devaient être regroupées dans une annotation conteneur (`@ApiResponses({...})`). Depuis Java 8, `@ApiResponse` est répétable : on l'écrit directement, plusieurs fois, ce qui est plus lisible. | Suppression du wrapper `@ApiResponses({ ... })` et de son import dans les 8 contrôleurs ; les `@ApiResponse` sont placées directement sur la méthode. Nouvelle analyse : 0 anomalie. |



\## E. Questions de compréhension



1\. \*\*Composant d'entrée\*\* : la `DispatcherServlet`. Le `HandlerMapping` retrouve la méthode du contrôleur à partir du verbe HTTP et de l'URL (`@GetMapping`, `@PostMapping`...).

2\. \*\*Dépendance par interface\*\* : le contrôleur reste mince et découplé : il ne dépend que du contrat `IContratService`, pas de l'implémentation (remplaçable) ni du repository (accès aux données, qui relève de la couche service).

3\. \*\*Codes\*\* : 201 pour une création (une ressource existe désormais), 204 pour une suppression (succès sans corps). Une ressource absente est une erreur du client (404), pas une défaillance du serveur : `ResourceNotFoundException` est traduite par `GlobalExceptionHandler` au lieu de remonter en 500.

4\. \*\*Paiement en lecture seule\*\* : un paiement est une composition du contrat, qui le gère (cascade). `PaiementController` n'expose que des GET ; un POST sur `/api/paiements` reçoit \*\*405 Method Not Allowed\*\*.

5\. \*\*Exposition des entités\*\* : couplage de l'API au schéma de base, champs internes visibles, boucles de sérialisation sur les associations bidirectionnelles, proxys LAZY non sérialisables, risque d'écrire des champs non voulus. Contrat.paiements relève d'une composition (l'enfant se crée dans le JSON du parent) donc paire managed/back ; Agence.vehicules est une liste inverse, créée ailleurs, donc `@JsonIgnore`. Les DTO suppriment ces problèmes.

'@

\[System.IO.File]::WriteAllText($p, $t, (New-Object System.Text.UTF8Encoding($false)))


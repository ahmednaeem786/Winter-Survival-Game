# Winter Survival Game
```                                                                             
`7MMF'     A     `7MF'`7MMF'`7MN.   `7MF'MMP""MM""YMM `7MM"""YMM  `7MM"""Mq.  
  `MA     ,MA     ,V    MM    MMN.    M  P'   MM   `7   MM    `7    MM   `MM. 
   VM:   ,VVM:   ,V     MM    M YMb   M       MM        MM   d      MM   ,M9  
    MM.  M' MM.  M'     MM    M  `MN. M       MM        MMmmMM      MMmmdM9   
    `MM A'  `MM A'      MM    M   `MM.M       MM        MM   Y  ,   MM  YM.   
     :MM;    :MM;       MM    M     YMM       MM        MM     ,M   MM   `Mb. 
      VF      VF      .JMML..JML.    YM     .JMML.    .JMMmmmmMMM .JMML. .JMM.
```

# Team Members

Muhamad Shafy Dimas Rafarrel [34476911] 

(Req1 & Req5)

Reynard Andyti Putra Kaban [35050756]

(Req2)

Ahmed Bhuri [35446420] 

(Req3 & Req4)

## Contribution Log
https://docs.google.com/spreadsheets/d/1DXegmezGLiO4IL6PpUdu7QzbckEFhFKcXCJRfnY2vF8/edit?usp=sharing

## REQ5: Stateful Creatures (HD Requirement)

### Chimera - Shapeshifting Beast
I created a Chimera that transforms between 4 elemental states. Each state has different stats, attacks, and behaviors.

### The 4 States

#### Default State (C)
- **Weapon:** BearClaw (35 dmg, 75% hit)
- **Behavior:** Attacks nearby enemies, wanders randomly
- **Special:** None (base form)

#### Fire State (F)
- **Weapon:** FlameBreath (80 dmg, 65% hit)
- **Special Effects:**
    - Creates Fire terrain on 2 random tiles around target when attacking
    - Applies **BurnEffect** status to target (5 damage/turn for 5 turns)
    - Status effects stack if multiple burns are applied
- **Behavior:** Aggressive movement

#### Ice State (I)
- **Weapon:** IceShard (50 dmg, 85% hit)
- **Special Effects:**
    - +5 max HP to chimera when entering this state
    - +5 max HP to tamer (if tamed) when entering this state
    - Applies **FrostBiteEffect** status to target (WARMTH -2/turn for 4 turns)
    - Status effects stack if multiple frostbites are applied
- **Behavior:** Moves to defensive positions with fewer exits

#### Poison State (P)
- **Weapon:** VenomedStrike (45 dmg, 80% hit)
- **Special Effects:**
    - Applies **PoisonEffect** status to target (-2 HP/turn for 3 turns)
    - Status effects stack additively
    - Displays poison tracking messages ("X is poisoned! (Y turns remaining)")
    - Removes dead actors from map when health reaches 0
- **Behavior:** Stalks enemies methodically, prefers positions near unpoisoned targets

### State Transitions (Predetermined)
The next state is always predetermined, NOT random. Here's the transition map:
```
Default -> Fire (60% after 3 turns)
Fire -> Ice (60% after 3 turns)
Ice -> Poison (40% after 3 turns) OR Default (30% after 4 turns alone)
Poison -> Fire (50% after 4 turns) OR Default (30% after 5 turns without attacking)
```
Each state can only go to specific next states. The percentages just determine WHEN it transitions, not WHERE it goes.

### Status Effect System
All status effects (Burn, Frostbite, Poison) are implemented using the `StatusEffect` interface and managed through `StatusRecipientRegistry`:

- **BurnEffect:** Direct HP damage each turn
- **FrostBiteEffect:** Reduces WARMTH attribute using engine's attribute system
- **PoisonEffect:** Direct HP damage each turn with visual tracking
- All effects are **stackable** - multiple applications add their effects together
- Effects automatically expire after their duration ends

### SOLID Principles

#### Single Responsibility
- Each state class only handles its own behavior
- Chimera coordinates states but doesn't implement behavior
- Status effects are delegated to dedicated `StatusEffect` classes

#### Open/Closed
- Can add new states without changing the `ChimeraState` interface or `Chimera` class
- Can add new status effects without modifying existing states

#### Liskov Substitution
- Any `ChimeraState` implementation works interchangeably in `Chimera`
- All states can be substituted without breaking behavior

#### Interface Segregation
- `ChimeraState` interface only has methods all states need
- Optional buff method (`applyBuffsToAllies`) has default implementation

#### Dependency Inversion
- `Chimera` depends on `ChimeraState` interface, not concrete state classes
- Attack actions depend on `StatusEffect` interface, not concrete implementations

### Taming
Chimeras can be tamed with Apple or YewBerry. When tamed:

- Follows the player
- Defends player from nearby enemies
- Still transforms between states normally
- Ice buff affects player too (+5 max HP)
- Uses state-specific attacks when assisting in combat

### Implementation Details

**Packages:**
- **States:** `game.states` package (`ChimeraState` interface + 4 implementations)
- **Creature:** `game.actors.Chimera` (extends `TameableAnimal`)
- **Weapons:** `game.weapons` (`BearClaw`, `FlameBreath`, `IceShard`, `VenomedStrike`)
- **Status Effects:** `game.status` (`StatusEffect` interface, `BurnEffect`, `FrostBiteEffect`, `PoisonEffect`)

**Design Patterns:**
- **State Pattern:** Behaviors encapsulated in state objects instead of giant if/else blocks
- **Strategy Pattern:** Each state has custom attack actions (FireAttackAction, IceAttackAction, PoisonAttackAction)
- **Registry Pattern:** `StatusRecipientRegistry` manages status effect application

### Testing the Chimera

1. **Spawn a chimera** and watch it transition between states (look for state transition messages)
2. **Observe special effects:**
  - Fire: Look for "Fire spreads around the target!" and burn status messages
  - Ice: Check for max health increase messages and frostbite status
  - Poison: Watch for "X is poisoned! (Y turns remaining)" messages
3. **Tame a chimera** with Apple/YewBerry and verify it follows you and assists in combat

# REQ5: The Questmaster NPC - Dynamic Quest System

## Overview
A stationary NPC called the Questmaster is located in the forest. This NPC uses the Gemini API to generate quest dialogue and offers players random quests with item rewards.

## Core Features

### The Questmaster NPC
* Fixed location in the forest map
* Players can interact to:
  * Accept new quests
  * Check current quest progress
  * Turn in completed quests for rewards
* Uses Gemini API to generate unique dialogue for each quest

### Quest Types
The Questmaster offers three types of quests:

1. **Hunter's Challenge**
  * Objective: Kill X number of creatures (wolves, bears, or deer)
  * Rewards: Combat items (Poisoned Axe, Enhanced Bow, Special Arrows)

2. **Gatherer's Task**
  * Objective: Collect X quantity of items (berries, hazelnuts, apples)
  * Rewards: Utility items (Enhanced Bottle, Bedroll)

3. **Explorer's Journey**
  * Objective: Visit specific locations
  * Rewards: Travel items (TeleportCube, Special Torch)

### Quest System Mechanics
* Questmaster randomly selects quest type, targets, and quantities
* Player can have one active quest at a time
* Quest progress is tracked automatically as player plays
* Rewards are given upon quest completion

## Example Scenario
```
Player: *Approaches Questmaster*
Questmaster: [AI-generated greeting]
"Ah, brave wanderer! I sense you seek purpose in these woods. I offer you three trials:"

1. "Slay 3 wolves to prove your might - a poisoned axe shall be your reward"
2. "Gather 5 yew berries from the depths of the forest - a magical bedroll awaits"
3. "Visit the frozen tundra and return with proof - a special torch I shall grant"

[Player selects quest 1]

Questmaster: [AI-generated quest acceptance dialogue]
"May your blade strike true. Return when the wolves have fallen."

[Quest tracking begins]
```

## New Classes Required

### Questmaster NPC
* **Class:** `Questmaster`
* **Package:** `game.actors`
* **Description:** The NPC that offers quests to players. Utilizes the Gemini API to generate quest dialogue and manages quest interactions.

### Quest Management
* **Class:** `Quest`
* **Package:** `game.quest`
* **Description:** Represents a quest with properties such as title, description, objectives, and rewards.

* **Class:** `QuestService`
* **Package:** `game.quest`
* **Description:** Interface for generating quests. Implemented by classes that interact with the Gemini API.

* **Class:** `GeminiQuestGenerator`
* **Package:** `game.quest`
* **Description:** Implements QuestService and uses the Gemini API to generate quests based on player context.

### Quest Objectives
* **Class:** `QuestObjective`
* **Package:** `game.quest`
* **Description:** Represents the specific objectives of a quest (e.g., kill a certain number of creatures, gather items).

* **Class:** `QuestReward`
* **Package:** `game.quest`
* **Description:** Represents the rewards given upon quest completion (e.g., items, weapons).

### Quest Tracking
* **Class:** `QuestTracker`
* **Package:** `game.quest`
* **Description:** Manages active quests for the player, tracks progress, and verifies completion.

### Actions
* **Class:** `QuestAction`
* **Package:** `game.actions`
* **Description:** Represents the action of interacting with the Questmaster to start or complete quests.

Full setup guide (API, keys) is documented here:
- `docs/design/assignment3/REQ5/Questmaster-API.md`

## What’s Implemented Now (Final Design)

- Provider selection and fallback
  - Uses `GeminiQuestGenerator` when a key is present (`GEMINI_API_KEY`). When not present or on API error, falls back to `LocalQuestGenerator` for fully offline play.
  - Factory wired at: `src/game/quest/QuestServiceFactory.java`.


- Quest generation, structure and safety
  - Quests have title, description and at least one objective. If an AI response omits objectives, a default VISIT route is injected so progress is always visible and deterministic.
  - VISIT objectives default to the canonical route `[Cave, Tundra, Meadow]` and require ordered completion.
  - COLLECT objectives are capped to a maximum of 5 items to keep pacing tight.


- Progress tracking hooks
  - KILL: counted when the player defeats the correct species (see `src/game/actions/AttackAction.java`).
  - COLLECT: counted on pickup via a tracked pickup action (see `src/game/actions/TrackedPickUpAction.java`).
  - VISIT: recorded each turn based on current ground – by class name OR tile symbol (`C`=Cave, `_`=Tundra, `w`=Meadow). Logic in `src/game/actors/Player.java`.


- Readable, multi‑line progress and offers
  - Quest offers wrap long descriptions (~78 chars) and list VISIT steps one per line.
  - Progress view prints a checklist for VISIT: `[x]` done, `[>]` current, `[ ]` pending.
  - Implemented in `src/game/actions/QuestAction.java` and `src/game/quest/QuestObjective.java`.


- Claim flow and inventory effects
  - When claiming a completed COLLECT quest, the required number of collected items is removed from the player’s inventory (extras remain). See `src/game/actions/QuestAction.java`.


- Reward distribution (mixing AI names with in‑game items)
  - Fuzzy mapping converts common AI names into concrete items:
    - “poison/yew” → Yewberry‑coated Axe; “frost/snow/ice” → Snow‑coated Axe
    - “arrow(s)/bow” → Bow; “torch/flame” → Torch; “teleport/cube/portal” → Teleport Cube
    - “bottle/flask/canteen” → Bottle; “bed/bedroll/sleep” → Bedroll
  - Unknown rewards still grant a useful random item (e.g., Bow, Torch, Teleport Cube, infused Axe, Bottle).
  - Implemented in `src/game/quest/SimpleRewardDistributor.java`.


- Naming/structure changes
  - The previous placeholder generator `GPTQuestGenerator` has been renamed to `LocalQuestGenerator` to better reflect its role as an offline provider.


### Unit Tests (coverage and how to run)

- Location: `src/test/java`
  - `game/quest/VisitProgressTest.java` — ordered, out‑of‑order, and duplicate VISIT cases.
  - `game/quest/KillAndCollectProgressTest.java` — typical/boundary/edge for KILL and COLLECT (including caps).
  - `game/quest/GeminiQuestGeneratorTest.java` — parses VISIT and KILL from stubbed JSON; falls back on client error (no network).
  - `game/actions/QuestClaimConsumptionTest.java` — consumes exactly the required collected items on claim.
- Run all tests with Maven: `mvn test`
  - Tests are deterministic (no network/time), use small fixtures, and assert behaviours (not internals).

### Troubleshooting

- “No progress shown” on VISIT: ensure you stand on the correct tiles in order — `C` (Cave), `_` (Tundra), `w` (Meadow). The progress view shows a checklist with `[x]/[>]/[ ]` markers.
- Engine NPEs in tests: maps must belong to a `World` before adding actors; tests use a minimal `World` for this.

### Class Index (implemented for REQ5)

- Core domain (package `game.quest`)
  - `Quest`, `QuestObjective`, `QuestReward`, `QuestStatus`, `ObjectiveType` — quest data model and lifecycle helpers.
  - `QuestTracker` — progress book‑keeping; exposes `recordKill/recordCollect/recordVisit` used by actions and Player.
  - `QuestParticipant`, `QuestParticipantRegistry` — decouple actions from concrete `Player` via registry/adapter.
  - `QuestService` — abstraction for quest providers.
  - `LocalQuestGenerator` — offline provider (renamed from GPTQuestGenerator).
  - `GeminiClient`, `HttpGeminiClient`, `ApiConfig` — API client + configuration (JDK HttpClient; no external deps).
  - `GeminiQuestGenerator` — AI provider; builds prompt → parses JSON → ensures safe defaults (adds VISIT if missing).
  - `QuestServiceFactory` — selects Gemini vs local, based on `GEMINI_API_KEY`.
  - `RewardDistributor` — abstraction; `SimpleRewardDistributor` — fuzzy maps AI names to in‑game items; mixes unknowns.


- Actions and actors
  - `game.actions.QuestAction` — offer/progress/claim; wraps long text, renders VISIT checklist, consumes collected items.
  - `game.actions.TrackedPickUpAction` — extends engine pickup; records COLLECT quest progress.
  - `game.actions.AttackAction` (touched) — records KILL progress when a target dies.
  - `game.actors.Questmaster` — stationary NPC; attaches `QuestAction` using `QuestServiceFactory`.
  - `game.actors.Player`  — logs VISIT via class name or tile symbol; owns a `QuestTracker` and registers as participant.

### How It Works (end‑to‑end)

- Interaction
  - Stand adjacent to `Q` and choose “Talk to the Questmaster”. If no active quest exists, one is generated and offered.
  - The offer shows a wrapped description and objectives. VISIT steps are listed one per line.


- Progress
  - KILL is counted on death; COLLECT on item pickup; VISIT when standing on `C` (Cave), `_` (Tundra), `w` (Meadow) in that order.
  - Talk again to see a checklist with `[x]` completed, `[>]` current, `[ ]` pending.


- Claim
  - When all objectives complete, claiming will remove exactly the required number of collected items and grant mapped rewards.


- Safety & defaults
  - If an AI payload is malformed or omits objectives, a default VISIT route is inserted so players always see progress.
  - COLLECT quantities are clamped to 1..5; KILL objectives accept only the named species.

### Configuration & Environment

- Required (for AI quests): `GEMINI_API_KEY` (run configuration → Environment variables)
- Optional:
  - `GEMINI_API_VERSION` (default `v1`)
  - `GEMINI_MODEL` (recommended `gemini-2.5-flash`)

### SOLID

- Dependency Inversion — actions depend on `QuestService` and `RewardDistributor`; Gemini code is behind `GeminiClient`.
- Interface Segregation — `QuestParticipant` provides only `getQuestTracker` to consumers; no `instanceof` checks.
- Single Responsibility — generation, tracking, interaction, and reward mapping are cleanly separated classes.
- Open/Closed — add new providers, rewards, or objective types without modifying existing consumers.


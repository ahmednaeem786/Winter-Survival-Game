# FIT2099 Assignment (Semester 2, 2025)
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

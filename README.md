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
The 4 States
Default State (C)

Weapon: BearClaw (35 dmg, 75% hit)
Behavior: Attacks nearby enemies, wanders randomly

### Fire State (F)

Weapon: FlameBreath (80 dmg, 65% hit)

Special: Creates Fire terrain on 2 tiles around target when attacking (this is the "new behavior" like deer spawning)

Behavior: Aggressive movement

### Ice State (I)

Weapon: IceShard (50 dmg, 85% hit)

Special: +5 max HP to chimera (and tamer if tamed) when entering this state

Behavior: Moves to defensive positions

### Poison State (P)

Weapon: VenomedStrike (45 dmg, 80% hit)

Special: Applies poison DOT (-2 HP/turn for 3 turns, stacks)

Behavior: Stalks enemies, tracks who's poisoned

### State Transitions (Predetermined)
The next state is always predetermined, NOT random. Here's the transition map:
```
Default -> Fire (60% after 3 turns)
Fire -> Ice (60% after 3 turns)
Ice -> Poison (40% after 3 turns) OR Default (30% after 4 turns alone)
Poison -> Fire (50% after 4 turns) OR Default (30% after 5 turns without attacking)
```
Each state can only go to specific next states. The percentages just determine WHEN it transitions, not WHERE it goes.

## SOLID Principles
#### Single Responsibility:
Each state class only handles its own behavior. Chimera just coordinates states.

#### Open/Closed:
Can add new states without changing the ChimeraState interface or Chimera class.

#### Liskov Substitution:
Any ChimeraState implementation works interchangeably in Chimera.

#### Interface Segregation:
ChimeraState interface only has methods all states need. Optional buff method has default implementation.

#### Dependency Inversion:
Chimera depends on ChimeraState interface, not concrete state classes.
Taming
Chimeras can be tamed with Apple or YewBerry. When tamed:

Follows the player
Defends player from nearby enemies
Still transforms between states normally
Ice buff affects player too

## Implementation
States: game.states package (ChimeraState interface + 4 implementations)
Creature: game.actors.Chimera (extends TameableAnimal)
Weapons: game.weapons (BearClaw, FlameBreath, IceShard, VenomedStrike)

Used State Pattern so behaviors are encapsulated in state objects instead of giant if/else blocks in Chimera.
# REQ5: The Questmaster NPC – Dynamic Quest System (HD)

This document explains how to run the Questmaster feature with Gemini API, how the design applies SOLID principles, and what to expect at runtime.

## What You Get

- A stationary Questmaster NPC on the Forest map who offers dynamic quests.
- When a Gemini API key is present, quests (title/description/objectives/rewards) are AI‑generated; otherwise a local generator is used.
- Quest types:
    - Hunter’s Challenge: kill X Wolves/Bears/Deer
    - Gatherer’s Task: collect X YewBerry/Hazelnut/Apple (capped at 5)
    - Explorer’s Journey: visit Cave → Tundra → Meadow in order
- Progress tracking is automatic while you play. On claim:
    - COLLECT: removes the required number of collected items from your inventory
    - Rewards are distributed; unknown reward names still grant useful in‑game items

## Prerequisites

- Java 11+ (uses `java.net.http.HttpClient`)
- Internet access to `https://generativelanguage.googleapis.com`
- IntelliJ IDEA (or any IDE) to set environment variables easily

## Get and Configure a Gemini API Key

1. Open Google AI Studio → API keys → Create API key, then copy the key.
2. In IntelliJ: Run → Edit Configurations… → select your Application run config
3. Add Environment variables:
    - `GEMINI_API_KEY=YOUR_KEY`
    - Optional: `GEMINI_API_VERSION=v1` (default `v1`)
    - Optional: `GEMINI_MODEL=gemini-2.5-flash`
4. Apply and Run.

Without a key, the Questmaster still works using a local generator.

## How to Use In‑Game

1. Start the game normally.
2. Find the Questmaster (`Q`) in the Forest.
3. Choose “Talk to the Questmaster” to:
    - Accept a new quest (if none active)
    - See quest progress (if active)
    - Claim rewards for completed quests; required collected items are consumed

## Design Rationale (SOLID + DRY)

- Dependency Inversion
    - `QuestAction` depends on `QuestService` and `RewardDistributor` interfaces, not on a concrete generator/provider.
    - `GeminiClient` abstracts HTTP calls; `HttpGeminiClient` is a small JDK‑only implementation.
  

- Interface Segregation / Registry
    - `QuestParticipant` and `QuestParticipantRegistry` decouple actions from `Player` and avoid `instanceof` checks.


- Single Responsibility
    - `GeminiQuestGenerator`: prompt building + parsing + fallback
    - `QuestTracker`: progress bookkeeping
    - `SimpleRewardDistributor`: maps AI reward names to concrete game items and mixes unknown rewards with useful items
    - `QuestAction`: orchestrates interaction, claiming, and display


- Open/Closed
    - New providers or reward rules can be added without changing consumers, only by wiring via `QuestServiceFactory`.


- DRY
    - Shared helpers for parsing and reward mapping; fuzzy mapping avoids large `switch` trees and covers novel reward names.

## Implementation Files

- NPC and actions:
    - `src/game/actors/Questmaster.java`
    - `src/game/actions/QuestAction.java`
- Quest model and tracking:
    - `src/game/quest/Quest.java`, `QuestObjective.java`, `QuestReward.java`, `QuestTracker.java`
- Providers and API:
    - `src/game/quest/QuestService.java`, `GeminiQuestGenerator.java`, `GPTQuestGenerator.java`
    - `src/game/quest/QuestServiceFactory.java`, `ApiConfig.java`
    - `src/game/quest/GeminiClient.java`, `HttpGeminiClient.java`
- Rewards:
    - `src/game/quest/RewardDistributor.java`, `SimpleRewardDistributor.java`

## Notes and Safety

- Use environment variables or IDE run config.
- The system falls back to a local quest generator if the API is unavailable.
- Collect quests are capped at 5 items to keep play pacing reasonable; on claim, the required items are removed from inventory.

## Troubleshooting

- If quests look like the familiar local generator templates, the API key may not be set. Verify environment variables in your run config.
- If network is restricted, you will still receive local quests.
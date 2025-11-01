package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Questmaster;
import game.quest.model.ObjectiveType;
import game.quest.model.QuestStatus;
import game.quest.provider.LocalQuestGenerator;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;
import game.quest.core.QuestTracker;
import game.quest.core.QuestService;
import game.quest.core.QuestParticipant;
import game.quest.core.QuestParticipantRegistry;
import game.quest.reward.RewardDistributor;
import game.quest.reward.SimpleRewardDistributor;

import java.util.List;

/**
 * Action to interact with the Questmaster. Generates a quest if none active
 * and reports progress when one exists. Rewards are wired in later commits.
 */
public class QuestAction extends Action {

    private final Questmaster questmaster;
    private final QuestService questService;
    private final RewardDistributor rewardDistributor;

    /**
     * DI-friendly constructor. Pass a {@link QuestService} (e.g., Gemini-backed) from outside.
     */
    public QuestAction(Questmaster questmaster, QuestService questService) {
        this.questmaster = questmaster;
        this.questService = questService == null ? new LocalQuestGenerator() : questService;
        this.rewardDistributor = new SimpleRewardDistributor();
    }

    /**
     * Convenience constructor using a default placeholder generator.
     */
    public QuestAction(Questmaster questmaster) {
        this(questmaster, new LocalQuestGenerator());
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        QuestParticipant participant = QuestParticipantRegistry.get(actor);
        if (participant == null) {
            return String.format("%s regards you in silence.", questmaster);
        }
        QuestTracker tracker = participant.getQuestTracker();

        // If any completed quests exist and unclaimed, auto-claim and report
        String claimMsg = claimCompletedIfAny(tracker, participant, actor);
        if (claimMsg != null) {
            return claimMsg;
        }

        List<Quest> active = tracker.getActive();
        if (active.isEmpty()) {
            Quest newQuest = questService.generateQuest();
            tracker.add(newQuest);
            return formatQuestOffer(newQuest);
        }

        // Ensure VISIT objectives are present for display if a generated quest omitted them
        for (Quest q : active) {
            ensureVisitObjectivePresent(q);
        }

        return formatProgress(active);
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Talk to the Questmaster";
    }

    private String formatQuestOffer(Quest newQuest) {
        StringBuilder sb = new StringBuilder();
        sb.append("The Questmaster intones: A task for you...\n");
        sb.append("Quest: ").append(newQuest.getTitle()).append("\n");
        sb.append(wrap(newQuest.getDescription(), 78)).append("\n");
        for (QuestObjective o : newQuest.getObjectives()) {
            sb.append(" - Objective: ").append(o.getType()).append(" ");
            if (o.getType() == ObjectiveType.VISIT) {
                // Render ordered locations on separate lines for readability
                sb.append("\n");
                java.util.List<String> locs = o.getOrderedLocations();
                for (int i = 0; i < locs.size(); i++) {
                    sb.append("     ").append(i+1).append(") ").append(locs.get(i)).append("\n");
                }
            } else {
                sb.append(o.getTarget()).append(" x").append(o.getRequiredAmount()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String formatProgress(List<Quest> active) {
        StringBuilder sb = new StringBuilder();
        sb.append("The Questmaster nods. Your progress: \n");
        for (Quest q : active) {
            sb.append("[").append(q.getTitle()).append("]\n");
            for (QuestObjective o : q.getObjectives()) {
                String prog = o.progressText();
                // indent multi-line progress
                String indented = prog.replace("\n", "\n   ");
                sb.append("   ").append(o.getType()).append(": ").append(indented).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String claimCompletedIfAny(QuestTracker tracker, QuestParticipant participant, Actor asActor) {
        StringBuilder sb = new StringBuilder();
        boolean any = false;
        for (Quest q : tracker.getCompleted()) {
            if (q.getStatus() == QuestStatus.COMPLETED) {
                // Consume collected items for COLLECT objectives
                String consumeMsg = consumeCollectedItems(asActor, q);
                String msg = rewardDistributor.distribute(q, participant, asActor);
                q.markClaimed();
                if (!any) {
                    sb.append("The Questmaster smiles. You have completed quests!\n");
                }
                any = true;
                if (!consumeMsg.isBlank()) sb.append(consumeMsg).append("\n");
                sb.append(msg).append("\n");
            }
        }
        return any ? sb.toString().trim() : null;
    }

    /**
     * Removes collected items from the actor's inventory equal to the required amount
     * for each COLLECT objective in the quest.
     */
    private String consumeCollectedItems(Actor asActor, Quest q) {
        StringBuilder sb = new StringBuilder();
        for (QuestObjective o : q.getObjectives()) {
            if (o.getType() == ObjectiveType.COLLECT) {
                String target = o.getTarget();
                int toRemove = o.getRequiredAmount();
                if (target == null || target.isBlank() || toRemove <= 0) continue;
                int removed = 0;
                // Work on a copy since engine inventory is unmodifiable
                java.util.List<edu.monash.fit2099.engine.items.Item> items = new java.util.ArrayList<>(asActor.getItemInventory());
                for (edu.monash.fit2099.engine.items.Item it : items) {
                    if (removed >= toRemove) break;
                    if (it.getClass().getSimpleName().equalsIgnoreCase(target)) {
                        asActor.removeItemFromInventory(it);
                        removed++;
                    }
                }
                if (removed > 0) {
                    sb.append("Consumed ").append(removed).append(" ").append(target).append(removed==1?"":"s").append(" for quest completion.\n");
                }
            }
        }
        return sb.toString().trim();
    }

    // If a quest has no objectives, synthesize a default VISIT route (Cave, Tundra, Meadow)
    private void ensureVisitObjectivePresent(Quest q) {
        if (q.getObjectives().isEmpty()) {
            java.util.List<String> route = java.util.Arrays.asList("Cave", "Tundra", "Meadow");
            q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", route.size(), route));
        } else {
            for (QuestObjective o : q.getObjectives()) {
                if (o.getType() == ObjectiveType.VISIT && o.getOrderedLocations().isEmpty()) {
                    java.util.List<String> route = java.util.Arrays.asList("Cave", "Tundra", "Meadow");
                    // Add a replacement VISIT objective only if none has steps
                    q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", route.size(), route));
                    break;
                }
            }
        }
    }

    // Wrap text at word boundaries to keep lines short in terminal output
    private static String wrap(String text, int width) {
        if (text == null) return "";
        String[] words = text.split("\\s+");
        StringBuilder out = new StringBuilder();
        int lineLen = 0;
        for (String w : words) {
            if (lineLen == 0) {
                out.append(w);
                lineLen = w.length();
            } else if (lineLen + 1 + w.length() <= width) {
                out.append(' ').append(w);
                lineLen += 1 + w.length();
            } else {
                out.append('\n').append(w);
                lineLen = w.length();
            }
        }
        return out.toString();
    }
}


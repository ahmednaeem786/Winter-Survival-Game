package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Questmaster;
import game.quest.GPTQuestGenerator;
import game.quest.Quest;
import game.quest.QuestObjective;
import game.quest.QuestTracker;
import game.quest.QuestService;
import game.quest.QuestParticipant;
import game.quest.QuestParticipantRegistry;
import game.quest.RewardDistributor;
import game.quest.SimpleRewardDistributor;

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
        this.questService = questService == null ? new GPTQuestGenerator() : questService;
        this.rewardDistributor = new SimpleRewardDistributor();
    }

    /**
     * Convenience constructor using a default placeholder generator.
     */
    public QuestAction(Questmaster questmaster) {
        this(questmaster, new GPTQuestGenerator());
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
        sb.append(newQuest.getDescription()).append("\n");
        for (QuestObjective o : newQuest.getObjectives()) {
            sb.append(" - Objective: ").append(o.getType()).append(" ").append(o.getTarget())
                    .append(" x").append(o.getRequiredAmount()).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatProgress(List<Quest> active) {
        StringBuilder sb = new StringBuilder();
        sb.append("The Questmaster nods. Your progress: \n");
        for (Quest q : active) {
            sb.append("[").append(q.getTitle()).append("]\n");
            for (QuestObjective o : q.getObjectives()) {
                sb.append("   ").append(o.getType()).append(": ").append(o.progressText()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String claimCompletedIfAny(QuestTracker tracker, QuestParticipant participant, Actor asActor) {
        StringBuilder sb = new StringBuilder();
        boolean any = false;
        for (Quest q : tracker.getCompleted()) {
            if (q.getStatus() == game.quest.QuestStatus.COMPLETED) {
                String msg = rewardDistributor.distribute(q, participant, asActor);
                q.markClaimed();
                if (!any) {
                    sb.append("The Questmaster smiles. You have completed quests!\n");
                }
                any = true;
                sb.append(msg).append("\n");
            }
        }
        return any ? sb.toString().trim() : null;
    }
}

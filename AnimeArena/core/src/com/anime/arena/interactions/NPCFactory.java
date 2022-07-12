package com.anime.arena.interactions;

import com.anime.arena.objects.NPCObject;
import com.anime.arena.objects.TrainerObject;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.Counter;
import com.anime.arena.tools.Movement;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NPCFactory {

    public NPCFactory() {

    }

    public NPCObject createNPC(String npcID, String overworld, PlayScreen screen, int x, int y) {
        Sprite npcSprite = new Sprite(screen.getNPCAtlas().findRegion(overworld));
        FileHandle file = Gdx.files.internal("scripts/" + npcID + ".txt");
        String text = file.readString();
        String[] lines = text.split("\r\n");
        Counter index = new Counter();
        MovementScript moveScript = null;
        Event interactionEvent = null;
        List<List<Integer>> npcEmojiSwitches = new ArrayList<List<Integer>>();
            if (lines[index.getCounter()].equals("MOVESCRIPT")) {
                moveScript = createMoveScript(lines, index.getCounter() + 1);
                index.increment(4); //Skip past movement scriptStatus, movements, and end movement linese
            }
            if (lines[index.getCounter()].equals("EMOJI_SWITCHES")) {
                index.increment();
                npcEmojiSwitches = getNPCEmojiSwitches(lines, index);
            }
            if (lines[index.getCounter()].equals("INTERACT")) {
                index.increment();
                interactionEvent = createInteractionEvent(lines, index, screen);
            }
        return new NPCObject(x, y, screen, npcSprite, interactionEvent, moveScript, npcEmojiSwitches);
    }

    private List<List<Integer>> getNPCEmojiSwitches(String[] lines, Counter index) {
        List<List<Integer>> displayQuestSwitches = new ArrayList<List<Integer>>();
        while (!lines[index.getCounter()].equals("END_EMOJI_SWITCHES")) {
            String emojiSwitchesLine = lines[index.getCounter()];
            String[] emojiSwitchesArray = emojiSwitchesLine.split(",");
            List<Integer> intEmojiList = new ArrayList<Integer>();
            for (String em : emojiSwitchesArray) {
                intEmojiList.add(Integer.parseInt(em));
            }
            displayQuestSwitches.add(intEmojiList);
            index.increment();
        }
        index.increment();
        return displayQuestSwitches;
    }

    public TrainerObject createTrainer(String trainerID, String overworld, PlayScreen screen, int x, int y) {
        Sprite npcSprite = new Sprite(screen.getNPCAtlas().findRegion(overworld));
        FileHandle file = Gdx.files.internal("scripts/trainer/" + trainerID + ".txt");
        String text = file.readString();
        String[] lines = text.split("\r\n");
        Counter index = new Counter();
        MovementScript moveScript = null;

        //Trainer Events populated from the scripts/trainer/{trainerID}.txt
        Event beforeBattleEvent = null; //The event that executes when talking to a trainer before battling them.
        Event afterDefeatEvent = null; //Ex: Talking to the trainer after they've been defeated.

        if (lines[index.getCounter()].equals("MOVESCRIPT")) {
            moveScript = createMoveScript(lines, index.getCounter() + 1);
            index.increment(4); //Skip past movement scriptStatus, movements, and end movement linese
        }
        String trainerTitle = lines[index.getCounter()];
        String trainerName = lines[index.getCounter() + 1];
        int money = Integer.parseInt(lines[index.getCounter() + 2]);
        String battleSprite = lines[index.getCounter() + 3];
        String encounterBGM = lines[index.getCounter() + 4];
        String battleBGM = lines[index.getCounter() + 5];
        String afterBattleText = lines[index.getCounter() + 6]; //The text the trainer says after beating them. Money is given afterwards.

        String tileVision = lines[index.getCounter() + 7];
        String[] tileVisionList = tileVision.split(",");
        int upVision = 0;
        int downVision = 0;
        int leftVision = 0;
        int rightVision = 0;
        if (tileVisionList.length == 4) {
            upVision = Integer.parseInt(tileVisionList[0]);
            rightVision = Integer.parseInt(tileVisionList[1]);
            downVision = Integer.parseInt(tileVisionList[2]);
            leftVision = Integer.parseInt(tileVisionList[3]);
        } else {
            Gdx.app.log("ERROR", "Tile Vision List is not equal to 4 - Size: " + tileVisionList.length + ", TrainerID: " + trainerID);
        }
        index.increment(8);
        if (lines[index.getCounter()].equals("POKEMON_TEAM")) {
            index.increment();
            populatePokemonTeam(lines, index);
        }
        if (lines[index.getCounter()].equals("START_TEXT_BEFORE")) {
            index.increment();
            beforeBattleEvent = createInteractionEvent(lines, index, screen);
        }
        Gdx.app.log("AFter start text before", "" + lines[index.getCounter()]);
        if (lines[index.getCounter()].equals("AFTER_DEFEAT")) {
            index.increment();
            afterDefeatEvent = createInteractionEvent(lines, index, screen);
        }
        return new TrainerObject(x, y, screen, npcSprite, moveScript, Integer.parseInt(trainerID), trainerTitle, trainerName, money, battleSprite,
                encounterBGM, battleBGM, upVision, rightVision, downVision, leftVision, afterBattleText, beforeBattleEvent, afterDefeatEvent);
    }

    private void populatePokemonTeam(String[] lines, Counter index) {
        //TODO: Change function to return a Pokemon team. Populate it from the values in the lines.
    }

    private Event createInteractionEvent(String[] lines, Counter index, PlayScreen screen) {
        if (lines[index.getCounter()].equals("TEXTBOX")) {
            TextBoxFactory tbf = new TextBoxFactory(screen);
            QuestFactory qf = new QuestFactory();
            index.increment();
            String nextLine = lines[index.getCounter()];
            TextBox tb = tbf.createTextBox(nextLine);
            index.increment();

            if (lines[index.getCounter()].equals("OPTION")) {
                index.increment();
                tb.addOption("Yes", createInteractionEvent(lines, index, screen));
                index.increment(); //Increment past ENDOPTION
                if (lines[index.getCounter()].equals("OPTION")) {
                    index.increment();
                    tb.addOption("No", createInteractionEvent(lines, index, screen));
                    index.increment(2); //Increment past ENDOPTION AND ENDTEXTBOX
                    return tb;
                } else {
                    //Should not go here. Option should always be the next step after the first OPTION
                }
            } else if (lines[index.getCounter()].equals("ENDTEXTBOX")) {
                index.increment();
                if (lines[index.getCounter()].equals("ENDINTERACT")) {
                    tb.setNextEvent(null);
                } else {
                    tb.setNextEvent(createInteractionEvent(lines, index, screen));
                }
                return tb;
            }
        } else if (lines[index.getCounter()].equals("TEXTBOX_JINGLE")) {
            TextBoxFactory tbf = new TextBoxFactory(screen);
            QuestFactory qf = new QuestFactory();
            index.increment();
            String jingle = lines[index.getCounter()];
            index.increment();
            String nextLine = lines[index.getCounter()];
            TextBox tb = tbf.createJingleTextBox(nextLine, jingle);
            index.increment();
            if (lines[index.getCounter()].equals("ENDTEXTBOX")) {
                index.increment();
                if (lines[index.getCounter()].equals("ENDINTERACT")) {
                    tb.setNextEvent(null);
                } else {
                    tb.setNextEvent(createInteractionEvent(lines, index, screen));
                }
                return tb;
            }
        } else if (lines[index.getCounter()].equals("SWITCH_EVENT")) {
            index.increment();
            int switchID = Integer.parseInt(lines[index.getCounter()]);
            index.increment(2); //Skip past SWITCH_TRUE_EVENT
            Event trueEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_SWITCH_TRUE_EVENT and SWITCH_FALSE_EVENT
            Event falseEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_SWITCH_FALSE_EVENT and END_SWITCH_EVENT
            return new SwitchConditionEvent(screen, switchID, trueEvent, falseEvent);
        } else if (lines[index.getCounter()].equals("QUEST_SWITCH_EVENT")) {
            index.increment();
            int questID = Integer.parseInt(lines[index.getCounter()]);
            index.increment(2); //Skip past QUEST_SWITCH_COMPLETE_EVENT
            Event completeEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_QUEST_SWITCH_COMPLETE_EVENT & QUEST_SWITCH_IN_PROGRESS_EVENT
            Event inprogressEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_QUEST_SWITCH_IN_PROGRESS_EVENT & QUEST_SWITCH_FINISHED_EVENT
            Event finishedEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_QUEST_SWITCH_FINISHED_EVENT & QUEST_NOT_TAKEN_EVENT
            Event notTakenEvent = createInteractionEvent(lines, index, screen);
            index.increment(2); //Skip past END_QUEST_NOT_TAKEN_EVENT & END_QUEST_SWITCH_EVENT
            return new QuestEvent(screen, questID, completeEvent, inprogressEvent, notTakenEvent, finishedEvent);
        } else if (lines[index.getCounter()].equals("COMPLETE_QUEST_EVENT")) {
            index.increment();
            int questID = Integer.parseInt(lines[index.getCounter()]);
            index.increment(2); //Skip past END_COMPLETE_QUEST_EVENT
            Event completeQuestEvent = new CompleteQuestEvent(screen, questID);
            completeQuestEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return completeQuestEvent;
        } else if (lines[index.getCounter()].equals("START_QUEST_EVENT")) {
            index.increment();
            int questID = Integer.parseInt(lines[index.getCounter()]);

        } else if (lines[index.getCounter()].equals("HEAL_POKEMON")) {
            Event healEvent = new HealPartyEvent(screen);
            index.increment();
            healEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return healEvent;
        } else if (lines[index.getCounter()].equals("GIVE_MONEY")) {
            index.increment();
            int moneyAmount = Integer.parseInt(lines[index.getCounter()]);
            index.increment(2);
            Event moneyEvent = new MoneyEvent(screen, moneyAmount);
            moneyEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return moneyEvent;
        } else if (lines[index.getCounter()].equals("GIVE_ITEM")) {
            index.increment();
            int itemID = Integer.parseInt(lines[index.getCounter()]);
            index.increment();
            int amount = Integer.parseInt(lines[index.getCounter()]);
            index.increment(2);
            Event itemEvent = new GiveItemEvent(screen, itemID, amount);
            itemEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return itemEvent;
        } else if (lines[index.getCounter()].equals("TOGGLE_SWITCH_EVENT")) {
            index.increment();
            int switchID = Integer.parseInt(lines[index.getCounter()]);
            Event toggleSwitchEvent = new ToggleSwitchEvent(screen, switchID);
            index.increment(2); //Skip past END_TOGGLE_SWITCH_EVENT
            toggleSwitchEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return toggleSwitchEvent;
        } else if (lines[index.getCounter()].equals("SET_SWITCH_TRUE")) {
            index.increment();
            String switches = lines[index.getCounter()];
            Event switchTrueEvent = new SwitchTrueEvent(screen, switches);
            index.increment(2); //Skip past END_SET_SWITCH_TRUE
            switchTrueEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return switchTrueEvent;
        } else if (lines[index.getCounter()].equals("SET_SWITCH_FALSE")) {
            index.increment();
            String switches = lines[index.getCounter()];
            Event switchFalseEvent = new SwitchFalseEvent(screen, switches);
            index.increment(2); //Skip past END_SET_SWITCH_FALSE
            switchFalseEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return switchFalseEvent;
        } else if (lines[index.getCounter()].equals("IF")) {
            IfEvent ifEvent = new IfEvent(screen);
            index.increment();
            String conditions = lines[index.getCounter()];
            String[] ifConditionArray = conditions.split(",");
            index.increment(2); //Skip past THEN
            ifEvent.addConditionalEvent(new ConditionalEvent(ifConditionArray, createInteractionEvent(lines, index, screen)));
            while (!lines[index.getCounter()].equals("END_IF")) {
                if (lines[index.getCounter()].equals("ELSEIF")) {
                    index.increment();
                    conditions = lines[index.getCounter()];
                    index.increment();
                    String[] conditionsArray = conditions.split(",");
                    ifEvent.addConditionalEvent(new ConditionalEvent(conditionsArray, createInteractionEvent(lines, index, screen)));
                } else if (lines[index.getCounter()].equals("ELSE")) {
                    index.increment();
                    ifEvent.setDefaultEvent(createInteractionEvent(lines, index, screen));
                }
            }
            index.increment();
            ifEvent.appendAfterIfEvent(createInteractionEvent(lines, index, screen));
            return ifEvent;
        } else if (lines[index.getCounter()].equals("SELECT_STARTER")) {
            Event starterEvent = new StarterPokemonEvent(screen);
            index.increment();
            starterEvent.setNextEvent(createInteractionEvent(lines, index, screen));
            return starterEvent;
        }

        return null;
    }

    private MovementScript createMoveScript(String[] lines, int index) {
        int scriptStatus = Integer.parseInt(lines[index]);
        index++; //Get the next line, the movements

        String[] movements = lines[index].split(",");
        List<Movement> movementList = new ArrayList<Movement>();
        for (String movementString : movements) {
            if (movementString.equals("UP")) {
                movementList.add(Movement.MOVE_UP);
            } else if (movementString.equals("LEFT")) {
                movementList.add(Movement.MOVE_LEFT);
            } else if (movementString.equals("DOWN")) {
                movementList.add(Movement.MOVE_DOWN);
            } else if (movementString.equals("RIGHT")) {
                movementList.add(Movement.MOVE_RIGHT);
            } else if (movementString.equals("LOOK_UP")) {
                movementList.add(Movement.LOOK_UP);
            } else if (movementString.equals("LOOK_LEFT")) {
                movementList.add(Movement.LOOK_LEFT);
            } else if (movementString.equals("LOOK_DOWN")) {
                movementList.add(Movement.LOOK_DOWN);
            } else if (movementString.equals("LOOK_RIGHT")) {
                movementList.add(Movement.LOOK_RIGHT);
            }
        }
        return new MovementScript(movementList, scriptStatus);
    }


}

package com.anime.arena.objects;

import com.anime.arena.interactions.*;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.Counter;
import com.anime.arena.tools.Movement;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class EventObject extends WorldObject {
    private Event event;

    public EventObject(String eventScript, int x, int y, PlayScreen screen) {
        super(x, y, screen);
        initScript(eventScript);
        this.visible = false;
    }

    private void initScript(String eventScript) {
        FileHandle file = Gdx.files.internal("scripts/event/" + eventScript + ".txt");
        String text = file.readString();
        String[] lines = text.split("\r\n");
        Counter index = new Counter();
        event = createEvent(lines, index, screen);
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

    private Event createEvent(String[] lines, Counter index, PlayScreen screen) {
        if (lines[index.getCounter()].equals("IF")) {
            return createIfEvent(lines, index, screen);
        } else if (lines[index.getCounter()].equals("PLAYER_MOVESCRIPT")) {
            return createPlayerMovementEvent(lines, index, screen);
        } else if (lines[index.getCounter()].equals("WAIT_PLAYER")) {
            return createWaitPlayerEvent(lines, index, screen);
        } else if (lines[index.getCounter()].equals("TEXTBOX")) {
            TextBoxFactory tbf = new TextBoxFactory(screen);
            QuestFactory qf = new QuestFactory();
            index.increment();
            String nextLine = lines[index.getCounter()];
            TextBox tb = tbf.createTextBox(nextLine);
            index.increment();

            if (lines[index.getCounter()].equals("OPTION")) {
                index.increment();
                tb.addOption("Yes", createEvent(lines, index, screen));
                index.increment(); //Increment past ENDOPTION
                if (lines[index.getCounter()].equals("OPTION")) {
                    index.increment();
                    tb.addOption("No", createEvent(lines, index, screen));
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
                    tb.setNextEvent(createEvent(lines, index, screen));
                }
                return tb;
            }
        }
        return null;
    }

    private Event createWaitPlayerEvent(String[] lines, Counter index, PlayScreen screen) {
        index.increment(1);
        Event waitPlayer =  new WaitPlayerEvent(screen);
        waitPlayer.setNextEvent(createEvent(lines, index, screen));
        return waitPlayer;
    }

    private Event createPlayerMovementEvent(String[] lines, Counter index, PlayScreen screen) {
        Event moveScript = new PlayerMovementEvent(screen, createMoveScript(lines, index.getCounter() + 1));
        index.increment(4); //Skip past movement scriptStatus, movements, and end movement lines
        moveScript.setNextEvent(createEvent(lines, index, screen));
        return moveScript;
    }

    private Event createIfEvent(String[] lines, Counter index, PlayScreen screen) {
        IfEvent ifEvent = new IfEvent(screen);
        index.increment();
        String conditions = lines[index.getCounter()];
        String[] ifConditionArray = conditions.split(",");
        index.increment(2); //Skip past THEN
        ifEvent.addConditionalEvent(new ConditionalEvent(ifConditionArray, createEvent(lines, index, screen)));
        while (!lines[index.getCounter()].equals("END_IF")) {
            if (lines[index.getCounter()].equals("ELSEIF")) {
                index.increment();
                conditions = lines[index.getCounter()];
                index.increment();
                String[] conditionsArray = conditions.split(",");
                ifEvent.addConditionalEvent(new ConditionalEvent(conditionsArray, createEvent(lines, index, screen)));
            } else if (lines[index.getCounter()].equals("ELSE")) {
                index.increment();
                ifEvent.setDefaultEvent(createEvent(lines, index, screen));
            }
        }
        index.increment();
        ifEvent.appendAfterIfEvent(createEvent(lines, index, screen));
        return ifEvent;
    }

    public Event getEvent() {
        return event;
    }

}

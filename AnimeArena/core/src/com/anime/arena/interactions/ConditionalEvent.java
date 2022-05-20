package com.anime.arena.interactions;

import com.anime.arena.objects.PlayerSwitches;

import java.util.ArrayList;
import java.util.List;

public class ConditionalEvent {
    private List<Integer> conditions;
    private Event event;
    public ConditionalEvent(String[] conditionArray, Event event) {
        initConditionList(conditionArray);
        this.event = event;
    }

    private void initConditionList(String[] conditionArray) {
        this.conditions = new ArrayList<Integer>();
        for (String condition : conditionArray) {
            conditions.add(Integer.parseInt(condition));
        }
    }

    public Event getEvent() {
        return event;
    }

    /**
     * Return if this conditional event is valid
     * @param switches
     * @return
     */
    public boolean isTrue(PlayerSwitches switches) {
        for (Integer condition : conditions) {
            if (!switches.isActive(condition)) {
                return false;
            }
        }
        return true;
    }
}

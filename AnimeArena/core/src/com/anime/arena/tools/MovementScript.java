package com.anime.arena.tools;

import com.anime.arena.objects.Direction;

import java.util.ArrayList;
import java.util.List;

public class MovementScript {
    private List<Movement> movements;
    private List<Movement> reversedMovement;
    private int scriptStatus; //0 = Plays once, 1 = looping, 2 = reverse the script once complete and looping
    private boolean isReversing;
    private boolean doneMoving;
    private int currentMovement; //index of the current movement
    public MovementScript(List<Movement> movement, int scriptStatus) {
        this.movements = movement;
        this.isReversing = false;
        this.currentMovement = 0;
        this.doneMoving = false;
        this.scriptStatus = scriptStatus;
        if (scriptStatus == 2) {
            initReversedMovement();
        }
    }

    public void resetMovementScript() {
        this.currentMovement = 0;
        this.doneMoving = false;
    }

    public Direction getInitialDirection() {
        if (movements.size() > 0) {
            if (movements.get(0) == Movement.MOVE_DOWN || movements.get(0) == Movement.LOOK_DOWN) {
                return Direction.DOWN;
            } else if (movements.get(0) == Movement.MOVE_UP || movements.get(0) == Movement.LOOK_UP) {
                return Direction.UP;
            } else if (movements.get(0) == Movement.MOVE_LEFT || movements.get(0) == Movement.LOOK_LEFT) {
                return Direction.LEFT;
            } else if (movements.get(0) == Movement.MOVE_RIGHT || movements.get(0) == Movement.LOOK_RIGHT) {
                return Direction.RIGHT;
            }
        }
        return Direction.DOWN;

    }

    private void initReversedMovement() {
        List<Movement> reversedMovement = new ArrayList<Movement>();
        for (int i = movements.size() - 1; i >= 0; i--) {
            reversedMovement.add(movements.get(i).getReversedMovement());
        }
        this.reversedMovement = reversedMovement;
    }

    public Movement getMovement() {
        if (!doneMoving) {
            if (isReversing && reversedMovement.size() > 0) {
                return reversedMovement.get(currentMovement);
            } else if (movements.size() > 0) {
                return movements.get(currentMovement);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isMoving() {
        if (!doneMoving) {
            if (isReversing) {
                if (reversedMovement.get(currentMovement) == Movement.MOVE_UP ||
                        reversedMovement.get(currentMovement) == Movement.MOVE_LEFT ||
                        reversedMovement.get(currentMovement) == Movement.MOVE_RIGHT ||
                        reversedMovement.get(currentMovement) == Movement.MOVE_DOWN) {
                    return true;
                }
            } else {
                if (movements.get(currentMovement) == Movement.MOVE_UP ||
                        movements.get(currentMovement) == Movement.MOVE_LEFT ||
                        movements.get(currentMovement) == Movement.MOVE_RIGHT ||
                        movements.get(currentMovement) == Movement.MOVE_DOWN) {
                    return true;
                }
            }
        }
        return false;
    }

    public Movement getNextMovement() {
        if (isReversing && currentMovement == reversedMovement.size() - 1) {
            return movements.get(0);
        } else if (!isReversing && currentMovement == movements.size() - 1 && scriptStatus == 2) {
            return reversedMovement.get(0);
        } else if (currentMovement == movements.size() - 1 && scriptStatus == 1) {
            return movements.get(0);
        } else if (currentMovement < movements.size() - 1){
            if (isReversing) {
                return reversedMovement.get(currentMovement + 1);
            } else {
                return movements.get(currentMovement + 1);
            }
        } else {
            return null;
        }
    }

    public boolean hasSameNextMovement() {
        Movement nextMovement = getNextMovement();
        if (nextMovement != null) {
            if (getMovement().getValue() == nextMovement.getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNextMovement() {
        return getNextMovement() != null;
    }

    public void setNextMovement() {
        if (isReversing && currentMovement == reversedMovement.size() - 1) {
            isReversing = false;
            currentMovement = 0;
        } else if (!isReversing && currentMovement == movements.size() - 1 && scriptStatus == 2) {
            isReversing = true;
            currentMovement = 0;
        } else if (currentMovement == movements.size() - 1 && scriptStatus == 1) {
            currentMovement = 0;
        } else if (currentMovement < movements.size() - 1) {
            currentMovement++;
        } else if (currentMovement == movements.size() - 1 && scriptStatus == 0) {
            doneMoving = true;
        }
    }

    public boolean isDoneMoving() {
        return doneMoving;
    }






}

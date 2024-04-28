package com.anime.arena.battle.ui;

public class CustomBattleText {
    private String text;
    private boolean hasOptions;
    private CustomBattleText nextBattleText;
    private CustomBattleText nextBattleTextForYes;
    private CustomBattleText nextBattleTextForNo;
    private CustomBattleTextController.CustomBattleTextResult yesResult;
    private CustomBattleTextController.CustomBattleTextResult noResult;
    private CustomBattleTextController.CustomBattleTextResult defaultResult; //used when it's not an option text box and it's the last one.

    private boolean selectedYes;
    private boolean selectedOption;

    public CustomBattleText(String text) {
        this.text = text;
        this.hasOptions = false;
        this.selectedOption = false;
    }

    public CustomBattleText(String text, boolean hasOptions) {
        this.text = text;
        this.hasOptions = hasOptions;
        this.selectedOption = false;
    }

    public void setSelectedOption(boolean option) {
        this.selectedYes = option;
        selectedOption = true;
    }

    public boolean hasSelectedOption() {
        return this.selectedOption;
    }

    public boolean isSelectedYes() {
        return this.selectedYes;
    }

    public void removeSelectedOption() {
        selectedOption = false;
    }

    public boolean hasOptions() {
        return hasOptions;
    }

    public void setNextBattleText(CustomBattleText nextBattleText) {
        this.nextBattleText = nextBattleText;
    }

    public void setNextBattleTextForYes(CustomBattleText nextBattleText) {
        this.nextBattleTextForYes = nextBattleText;
    }

    public void setNextBattleTextForNo(CustomBattleText nextBattleText) {
        this.nextBattleTextForNo = nextBattleText;
    }

    public CustomBattleText getNextBattleText() {
        return nextBattleText;
    }

    public CustomBattleText getNextBattleTextForYes() {
        return nextBattleTextForYes;
    }

    public CustomBattleText getNextBattleTextForNo() {
        return nextBattleTextForNo;
    }

    public CustomBattleTextController.CustomBattleTextResult getYesResult() {
        return yesResult;
    }

    public void setYesResult(CustomBattleTextController.CustomBattleTextResult yesResult) {
        this.yesResult = yesResult;
    }

    public CustomBattleTextController.CustomBattleTextResult getNoResult() {
        return noResult;
    }

    public void setNoResult(CustomBattleTextController.CustomBattleTextResult noResult) {
        this.noResult = noResult;
    }

    public CustomBattleTextController.CustomBattleTextResult getDefaultResult() {
        return defaultResult;
    }
    public void setDefaultResult(CustomBattleTextController.CustomBattleTextResult defaultResult) {
        this.defaultResult = defaultResult;
    }

    public String getText() {
        return text;
    }

    public int getTextLength() {
        return text.length();
    }
}

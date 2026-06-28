package com.trojan.proficiency.perk;

public record PerkUnlockResult(
        Status status,
        String message
) {

    public enum Status {
        SUCCESS,
        PERK_NOT_FOUND,
        ALREADY_UNLOCKED,
        LEVEL_REQUIRED,
        PARENT_REQUIRED,
        INSUFFICIENT_POINTS
    }

    public boolean success() {
        return status == Status.SUCCESS;
    }
}

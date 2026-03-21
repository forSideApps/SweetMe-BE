package dev.sweetme.domain.enums;

public enum CareerLevel {
    JUNIOR("신입"),
    EXPERIENCED("경력");

    private final String displayName;

    CareerLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

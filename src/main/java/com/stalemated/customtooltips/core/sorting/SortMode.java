package com.stalemated.customtooltips.core.sorting;

public enum SortMode {
    CREATION_DATE,
    NAME_AND_TAG,
    DISABLED_FIRST;

    public SortMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}

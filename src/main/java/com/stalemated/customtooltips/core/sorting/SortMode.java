package com.stalemated.customtooltips.core.sorting;

import com.google.gson.annotations.SerializedName;

public enum SortMode {
    @SerializedName(value = "MANUAL_ORDER", alternate = "CREATION_DATE")
    MANUAL_ORDER,
    NAME_AND_TAG,
    DISABLED_FIRST;

    public SortMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}

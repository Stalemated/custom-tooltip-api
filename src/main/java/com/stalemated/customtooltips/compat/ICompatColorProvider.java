package com.stalemated.customtooltips.compat;

public interface ICompatColorProvider {
    /**
     * @param originalColor The original border color set by the external mod
     * @return The overridden color, or originalColor if no override is needed.
     */
    int overrideBorderColor(int originalColor);
}

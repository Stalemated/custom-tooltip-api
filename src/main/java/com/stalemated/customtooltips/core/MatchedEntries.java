package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import java.util.List;

public record MatchedEntries(List<TooltipEntry> shown, boolean waitingForKey, boolean hidesVanillaLines) {
}

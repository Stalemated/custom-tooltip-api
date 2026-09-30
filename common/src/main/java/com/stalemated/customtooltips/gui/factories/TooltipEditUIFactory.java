package com.stalemated.customtooltips.gui.factories;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.lib.compat.yacl.controller.builder.AdvancedColorControllerBuilder;
import com.stalemated.lib.compat.yacl.controller.builder.SimpleStringDropdownControllerBuilder;
import com.stalemated.lib.util.color.ColorUtils;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TooltipEditUIFactory {

    public static <T> Option<T> buildOption(
            TooltipEntry entry,
            String nameKey, T defaultValue,
            Function<TooltipEntry, T> getter,
            BiConsumer<TooltipEntry, T> setter,
            Function<Option<T>, ControllerBuilder<T>> controllerBuilder) {

        var option = Option.<T>createBuilder()
                .name(Text.translatable(nameKey))
                .description(OptionDescription.of(Text.translatable(nameKey + ".description")))
                .binding(defaultValue, () -> getter.apply(entry), val -> setter.accept(entry, val))
                .controller(controllerBuilder)
                .build();

        option.addEventListener((opt, event) -> {
            if (TooltipEditScreen.previewEntry != null) {
                setter.accept(TooltipEditScreen.previewEntry, opt.pendingValue());
                TooltipEditScreen.previewEntry.invalidateCaches();
            }
        });
        return option;
    }

    public static <T> Option<T> buildOption(
            TooltipEntry entry,
            String nameKey, String secondDescKey, T defaultValue,
            Function<TooltipEntry, T> getter,
            BiConsumer<TooltipEntry, T> setter,
            Function<Option<T>, ControllerBuilder<T>> controllerBuilder) {

        var option = Option.<T>createBuilder()
                .name(Text.translatable(nameKey))
                .description(OptionDescription.of(Text.translatable(nameKey + ".description"), Text.translatable(secondDescKey)))
                .binding(defaultValue, () -> getter.apply(entry), val -> setter.accept(entry, val))
                .controller(controllerBuilder)
                .build();

        option.addEventListener((opt, event) -> {
            if (TooltipEditScreen.previewEntry != null) {
                setter.accept(TooltipEditScreen.previewEntry, opt.pendingValue());
                TooltipEditScreen.previewEntry.invalidateCaches();
            }
        });
        return option;
    }

    public static Option<Boolean> buildBoolean(
            TooltipEntry entry,
            String nameKey, boolean defaultValue,
            Function<TooltipEntry, Boolean> getter, BiConsumer<TooltipEntry, Boolean> setter) {
        return buildOption(entry, nameKey, defaultValue, getter, setter, TickBoxControllerBuilder::create);
    }

    public static Option<String> buildTextColor(
            TooltipEntry entry,
            String nameKey, String secondDescKey, int index,
            Function<TooltipEntry, List<TextColor>> listGetter) {

        return buildOption(entry, nameKey, secondDescKey, "white",
                e -> {
                    List<TextColor> list = listGetter.apply(e);
                    return list.size() > index && list.get(index) != null ? ColorUtils.toHexString(list.get(index)) : "white";
                },
                (e, val) -> {
                    List<TextColor> list = listGetter.apply(e);
                    while (list.size() <= index) list.add(ColorUtils.resolveTextColor("white"));
                    list.set(index, ColorUtils.resolveTextColor(val));
                },
                AdvancedColorControllerBuilder::create
        );
    }

    public static Option<String> buildColor(
            TooltipEntry entry,
            String nameKey, int index, String defaultHex,
            Function<TooltipEntry, List<Color>> listGetter) {

        return buildOption(entry, nameKey, defaultHex,
                e -> {
                    List<Color> list = listGetter.apply(e);
                    return list.size() > index && list.get(index) != null ? ColorUtils.toRGBAHexString(list.get(index)) : defaultHex;
                },
                (e, val) -> {
                    List<Color> list = listGetter.apply(e);
                    while (list.size() <= index) list.add(ColorUtils.parseRGBAToAWT(defaultHex));
                    list.set(index, ColorUtils.parseRGBAToAWT(val));
                },
                opt -> AdvancedColorControllerBuilder.create(opt).alpha(true)
        );
    }

    public static Option<String> buildIdentifier(
            TooltipEntry entry,
            String nameKey, String defaultId,
            Function<TooltipEntry, Identifier> getter, BiConsumer<TooltipEntry, Identifier> setter) {

        return buildOption(entry, nameKey, defaultId,
                e -> getter.apply(e) != null ? getter.apply(e).toString() : defaultId,
                (e, val) -> {
                    Identifier id = Identifier.tryParse(val);
                    setter.accept(e, id != null ? id : Identifier.of(defaultId));
                },
                StringControllerBuilder::create
        );
    }

    public static Option<String> buildIdentifierDropdown(
            TooltipEntry entry,
            String nameKey, String defaultId,
            List<String> availableValues,
            Function<TooltipEntry, Identifier> getter, BiConsumer<TooltipEntry, Identifier> setter) {

        return buildOption(entry, nameKey, defaultId,
                e -> getter.apply(e) != null ? getter.apply(e).toString() : defaultId,
                (e, val) -> {
                    Identifier id = Identifier.tryParse(val);
                    setter.accept(e, id != null ? id : Identifier.of(defaultId));
                },
                opt -> SimpleStringDropdownControllerBuilder.create(opt)
                        .values(availableValues)
                        .formatValue(Text::literal)
        );
    }
}

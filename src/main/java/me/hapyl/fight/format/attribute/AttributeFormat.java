package me.hapyl.fight.format.attribute;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.util.TypeConverter;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum AttributeFormat {

    COOLDOWN {
        @Override
        String format(@Nonnull TypeConverter obj) {
            return "&l♻ &fᴄᴏᴏʟᴅᴏᴡɴ: " + tickToString(obj);
        }
    },

    FIRE_RATE {
        @Override
        String format(@Nonnull TypeConverter obj) {
            return "&6🏹 &fғɪʀᴇ ʀᴀᴛᴇ: " + tickToString(obj);
        }
    },

    MAX_AMMO {
        @Override
        String format(@Nonnull TypeConverter obj) {
            return "&4&l\uD83D\uDD3C &fᴍᴀx ᴀᴍᴍᴏ: " + obj.toInt();
        }
    },

    DAMAGE {
        @Override
        String format(@Nonnull TypeConverter obj) {
            return "&4⚔ &fᴅᴀᴍᴀɢᴇ: %.1f".formatted(obj.toDouble());
        }
    },

    MAX_DISTANCE {
        @Override
        String format(@Nonnull TypeConverter obj) {
            return "&b&l\uD83C\uDFAF &fᴍᴀx ᴅɪsᴛᴀɴᴄᴇ: %.0f blocks".formatted(obj.toDouble());
        }
    };

    String format(@Nonnull TypeConverter obj) {
        return obj.toString();
    }

    private static String tickToString(TypeConverter obj) {
        final int anInt = obj.toInt();

        return (anInt == -1 || anInt == 1) ? "Dynamic" : (Tick.round(anInt) + "s");
    }

    public static void formatAll(@Nonnull Object object, @Nonnull ItemBuilder builder) {
        final List<String> list = formatAll(object);

        if (list.isEmpty()) {
            return;
        }

        builder.addLore();
        builder.addLore("&e&lᴀᴛᴛʀɪʙᴜᴛᴇs:");

        for (String string : list) {
            builder.addLore(" " + string);
        }
    }

    @Nonnull
    public static List<String> formatAll(@Nonnull Object object) {
        final List<String> list = new ArrayList<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            final AttributeField annotation = field.getAnnotation(AttributeField.class);

            if (annotation == null) {
                continue;
            }

            field.setAccessible(true);

            try {
                final Object value = field.get(object);

                if (value == null) {
                    continue;
                }

                list.add(annotation.type().format(TypeConverter.from(value)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }

}

package me.hapyl.fight.command;

import me.hapyl.fight.GVar;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimpleAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class GVarCommand extends SimpleAdminCommand {
    public GVarCommand(String name) {
        super(name);

        addCompleterHandler(2, (player, arg, args) -> {
            if (arg.isBlank() || arg.isEmpty()) {
                return "&c&nEnter a value!";
            }
            return "&a&nUsing &l" + arg;
        });

        addCompleterHandler(3, (player, arg, args) -> {
            return "&aType: &l&n" + switch (arg.toLowerCase()) {
                case "int" -> "Integer";
                case "long" -> "Long";
                case "double" -> "Double";
                case "float" -> "Float";
                default -> "String";
            };
        });
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            Chat.sendMessage(sender, "&4Error! &cExpecting a variable name as string, got nothing.");
            return;
        }

        final String name = args[0];
        final Object object = GVar.getRaw(name);

        if (args.length == 1) {
            if (object == null) {
                Chat.sendMessage(sender, "&4Error! &cThere is no GVar named '%s'!".formatted(name));
                return;
            }

            Chat.sendMessage(
                    sender,
                    "&2Success! &aGVar '%s' has value of '%s' of type '%s'.".formatted(
                            name,
                            object,
                            object.getClass().getSimpleName()
                    )
            );
            return;
        }

        String toType = "";

        if (args.length == 2) {
            if (object == null) {
                Chat.sendMessage(sender, "&4Error! &cInvalid usage, value is not set, must provide type!");
                return;
            }

            if (object instanceof Integer) {
                toType = "int";
            }
            else if (object instanceof Long) {
                toType = "long";
            }
            else if (object instanceof Double) {
                toType = "double";
            }
            else if (object instanceof Float) {
                toType = "float";
            }
        }
        else if (args.length == 3) {
            toType = args[2].toLowerCase();
        }

        final String newValue = args[1];

        switch (toType) {
            case "int" -> trySet(sender, name, newValue, Validate::getInt);
            case "long" -> trySet(sender, name, newValue, Validate::getLong);
            case "double" -> trySet(sender, name, newValue, Validate::getDouble);
            case "float" -> trySet(sender, name, newValue, Validate::getFloat);
            default -> trySet(sender, name, newValue, t -> t);
        }
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }

        return completerSort(GVar.listNames(), args);
    }

    private <T> void trySet(CommandSender sender, String name, String obj, Function<String, T> function) {
        try {
            final T newValue = function.apply(obj);

            if (newValue == null) {
                Chat.sendMessage(sender, "&4Error! &cConverted value is null!");
                return;
            }

            final Object oldValue = GVar.getRaw(name);

            if (oldValue != null) {
                final String oldValueTypeName = oldValue.getClass().getSimpleName();
                final String newValueTypeName = newValue.getClass().getSimpleName();

                if (!oldValueTypeName.equalsIgnoreCase(newValueTypeName)) {
                    Chat.sendMessage(
                            sender,
                            "&4Error! &cThe old value has a different type! Expected '%s', got '%s'!".formatted(
                                    oldValueTypeName,
                                    newValueTypeName
                            )
                    );
                    return;
                }
            }

            GVar.set(name, newValue);
            Chat.sendMessage(sender, "&2Success! &aSet GVar '%s' to '%s'!".formatted(name, newValue));
        } catch (Exception e) {
            Chat.sendMessage(sender, "&4Error! &cCould not convert types, see console!");
            e.printStackTrace();
        }
    }

}

package bg.sofia.uni.fmi.mjt.crypto.factory;

import bg.sofia.uni.fmi.mjt.crypto.commands.Command;

public interface CommandFactory {
    Command createCommand(String[] arguments);
}

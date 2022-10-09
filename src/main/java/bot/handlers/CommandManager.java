package bot.handlers;

import bot.roulette.RouletteGame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

    private final RouletteGame game;

    public CommandManager(RouletteGame game) {
        this.game = game;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        String command = event.getCommandString().substring(1);

        switch (command) {
            case "play" -> game.attemptToPlay(event);
            case "star" -> game.showCurrentStar(event);
        }
    }
}

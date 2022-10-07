package bot.handlers;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        String command = event.getCommandString().substring(1);
        switch (command) {
            case "play" -> handlePlay(event);
            case "star" -> handleStar(event);
        }
    }

    private void handlePlay(SlashCommandEvent event) {
        RouletteGame game = new RouletteGame(event);
        game.play();
    }

    private void handleStar(SlashCommandEvent event) {
        RouletteGame.showCurrentStar(event);
    }
}

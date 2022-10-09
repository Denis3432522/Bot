package bot.handlers;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuildReadyHandler extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        registerCommands(event);
    }

    private void registerCommands(GuildReadyEvent event) {
        if(!event.getGuild().getId().equals("852597879548674098"))
            return;

        List<CommandData> commands = new ArrayList<>();
        commands.add(new CommandData("play", "Play and get a chance to become a new astonishing star"));
        commands.add(new CommandData("star", "Take a look at the current star"));

        event.getGuild().updateCommands().addCommands(commands).queue();
        System.out.println("Commands successfully registered!");
    }
}

package bot;

import bot.handlers.CommandManager;
import bot.handlers.GuildReadyHandler;
import bot.roulette.Backupper;
import bot.roulette.Checker;
import bot.roulette.RouletteGame;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class Bot {

    public Bot() throws LoginException, InterruptedException {
        Dotenv config = Dotenv.configure().load();

        JDABuilder builder = JDABuilder.create(config.get("TOKEN"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(new GuildReadyHandler());
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        JDA jda = builder.build();

        String guild_id = Objects.requireNonNull(config.get("GUILD_ID"));

        Guild guild = getGuild(jda, guild_id);

        RouletteGame rouletteGame = new RouletteGame(guild);
        jda.addEventListener(new CommandManager(rouletteGame));
        Checker checker = new Checker(new Backupper(Integer.parseInt(Objects.requireNonNull(config.get("BACK_UP_INTERVAL")))), rouletteGame, 1000);

        new Thread(checker).start();

        System.out.println("Roulette game successfully started!");
    }

    private Guild getGuild(JDA jda,@NotNull String guild_id) throws InterruptedException {
        Guild guild;

        while (true) {
            Thread.sleep(100);
            guild = jda.getGuildById(guild_id);

            if(guild != null && guild.isLoaded())
                return guild;
        }
    }
}


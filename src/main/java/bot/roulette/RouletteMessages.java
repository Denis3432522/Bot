package bot.roulette;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class RouletteMessages {

    static MessageEmbed getDefaultEmbedMessage(String msg) {
        return new EmbedBuilder()
                .setColor(new Color(46, 255, 168))
                .appendDescription("**" + msg + "**")
                .build();
    }

    static MessageEmbed getAlreadyWonMessage() {
        String msg = "Вы не можете сыграть снова, так как вы действующая звезда";

        return getDefaultEmbedMessage(msg);
    }

    static MessageEmbed getTryLaterMessage(String cooldown) {
        String msg = "К сожалению, вы уже сыграли. Возвращайтесь через " + cooldown + ".";

        return getDefaultEmbedMessage(msg);
    }

    static MessageEmbed getLossMessage(String name, int number, String cooldown) {
        String msg = "К сожалению, " + name + ", вы проиграли с числом " + number + ". Следующая попытка будет доступна через " + cooldown + ".";

        return getDefaultEmbedMessage(msg);
    }

    static MessageEmbed getVictoryMessage(String userId, int number) {
        String msg = "Поздравляем, " + userId + "! Теперь ты новая звезда с числом " + number + "!";

        return getDefaultEmbedMessage(msg);
    }
}

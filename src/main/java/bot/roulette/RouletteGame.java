package bot.roulette;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class RouletteGame {
    private final String GUILD_ID;
    private final Star star;
    private final RouletteMemberCooldowns cooldowns;
    private final Role starRole;
    private final Guild guild;

    public RouletteGame(@NotNull Guild guild) {
        Dotenv cfg = Dotenv.configure().load();
        GUILD_ID = Objects.requireNonNull(cfg.get("GUILD_ID"));
        String STAR_ROLE_ID = Objects.requireNonNull(cfg.get("STAR_ROLE_ID"));
        this.star = Star.getInstance();
        this.cooldowns = RouletteMemberCooldowns.getInstance();
        this.guild = guild;
        starRole = Objects.requireNonNull(guild.getRoleById(STAR_ROLE_ID));
    }

    public void attemptToPlay(SlashCommandEvent event) {
        String userId = event.getUser().getId();

        if(star.amIStar(userId)) {
            alreadyWon(event);
        }
        else if(cooldowns.isEligibleToPlay(userId)) {
            cooldowns.setMemberCooldown(userId);
            playRoulette(event);
        }
        else {
            tryLater(event);
        }
    }

    private void playRoulette(SlashCommandEvent event) {
        event.deferReply().queue();

        int number = new Random().nextInt(1_000_000);

        if(star.ifNewStar(number, event.getUser().getId())) {
            congratulation(event, number);
        } else
            lose(event, number);
    }

    private void lose(SlashCommandEvent event, int number) {
        MessageEmbed msg = RouletteMessages.getLossMessage(event.getUser().getName(), number, convertSecondsToTime(cooldowns.COOLDOWN_TIME));
        event.getHook().sendMessageEmbeds(msg).queue();
    }

    private void congratulation(SlashCommandEvent event, int number) {
        swapStarRole(event);
        MessageEmbed msg = RouletteMessages.getVictoryMessage(event.getUser().getName(), number);
        event.getHook().sendMessageEmbeds(msg).queue();
    }

    private void tryLater(SlashCommandEvent event) {
        int rawCooldown = cooldowns.getCooldownByUserId(event.getUser().getId());
        String cooldown = convertSecondsToTime(rawCooldown);
        MessageEmbed msg = RouletteMessages.getTryLaterMessage(cooldown);

        event.replyEmbeds(msg).setEphemeral(true).queue();
    }

    private void alreadyWon(SlashCommandEvent event) {
        MessageEmbed msg = RouletteMessages.getAlreadyWonMessage();
        event.replyEmbeds(msg).setEphemeral(true).queue();
    }

    private static String convertSecondsToTime(int secs) {

        int seconds = secs % 60;
        secs /= 60;

        int minutes = secs % 60;
        secs /= 60;

        int hours = secs % 24;
        secs /= 24;

        int days = secs;

        var time = new StringBuilder();
        time.append((days != 0) ? days + "дн, " : "");
        time.append((hours != 0) ? hours + "ч, " : "");
        time.append((minutes != 0) ? minutes + "мин, " : "");
        time.append((seconds != 0) ? seconds + "сек, " : "");
        return time.delete(time.length()-2, time.length()).toString();
    }

    private void swapStarRole(SlashCommandEvent event) {
        removeRoleFromPrevStar();
        addRoleToNewStar(event);
    }

    private void removeRoleFromPrevStar() {
        String prevStarId = star.getPreviousStarId();

        if(prevStarId == null)
            return;

        Member prevStarMember = guild.getMemberById(prevStarId);
        guild.removeRoleFromMember(prevStarMember, starRole).queue();
    }

    public void checkStarTimeLeft() {
        if(star.isStarPresent() && star.getTimeLeft() == 0) {
            star.resetStar();
            removeRoleFromPrevStar();
        }
    }

    private void addRoleToNewStar(SlashCommandEvent event) {
        Member starMember = event.getJDA().getGuildById(GUILD_ID).getMemberById(star.getStarId());
        event.getGuild().addRoleToMember(starMember, starRole).queue();
    }

    public void showCurrentStar(SlashCommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        if(star.isStarPresent()) {
            String starTimeLeft = convertSecondsToTime(star.getTimeLeft());
            String starId = star.getStarId();
            String number =  String.valueOf(star.getStarNumber());
            String avatarUrl = event.getJDA().getUserById(starId).getAvatarUrl();

            builder.setAuthor(event.getJDA().getUserById(starId).getAsTag(), null, avatarUrl);
            builder.addField(new MessageEmbed.Field("Число", number, false));
            builder.addField(new MessageEmbed.Field("Сбросится автоматически через:", starTimeLeft, false));
        } else {
            builder.appendDescription("**На данный момент действующей звезды нет. Станьте им!**");
        }
        builder.setColor(new Color(46, 255, 168));
        event.replyEmbeds(builder.build()).queue();
    }

    private static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}

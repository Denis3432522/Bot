package bot.handlers;

import bot.cache.RouletteMemberCooldowns;
import bot.cache.Star;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.Random;

public class RouletteGame {
    private static final String GUILD_ID = "852597879548674098";
    private static final String STAR_ROLE_ID = "1027242781770530947";
    private final SlashCommandEvent event;
    private final String userTag;

    RouletteGame(SlashCommandEvent event) {
        this.event = event;
        this.userTag = event.getUser().getAsTag();
    }

    void play() {
        if(Star.isStarPresent() && Star.getStarTag().equals(userTag))
            alreadyWon();
        else if(RouletteMemberCooldowns.isEligibleToPlay(userTag))
            playRoulette(); // setCooldown()
        else
            tryLater();
    }

    private static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private void alreadyWon() {
        String msg = "Вы не можете сыграть снова, так как вы действующая звезда";
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(46, 255, 168));
        builder.appendDescription("**" + msg + "**");
        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
    }

    private void playRoulette() {
        event.deferReply().queue();

        int number = new Random().nextInt(1_000_000);

        if(Star.ifNewStar(number, userTag)) {
            congratulation(number);
        } else
            lose(number);
    }

    private void lose(int number) {
        String cooldown = convertSecondsToTime(RouletteMemberCooldowns.COOLDOWN);
        String msg = "К сожалению, " + event.getUser().getName() + ", вы проиграли с числом " + number + ". Следующая попытка будет доступна через " + cooldown + ".";
        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription("**" + msg + "**");
        builder.setColor(new Color(46, 255, 168));
        event.getHook().sendMessageEmbeds(builder.build()).queue();
    }

    private void congratulation(int number) {
        String msg = "Поздравляем, " + event.getUser().getName() + "! Теперь ты новая звезда с числом " + number + "!";
        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription("**" + msg + "**");
        builder.setColor(new Color(46, 255, 168));
        swapStarRole();
        event.getHook().sendMessageEmbeds(builder.build()).queue();
    }

    private void tryLater() {
        int rawMemberCooldown = RouletteMemberCooldowns.getCooldownByName(userTag);
        String memberCooldown = convertSecondsToTime(rawMemberCooldown);
        String msg = "К сожалению, вы уже сыграли. Возвращайтесь через " + memberCooldown + ".";

        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription("**" + msg + "**");
        builder.setColor(new Color(46, 255, 168));

        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
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

    private void swapStarRole() {
        Role role = event.getJDA().getGuildById(GUILD_ID).getRoleById(STAR_ROLE_ID);

        removeRoleFromPrevStar(role);
        addRoleToNewStar(role);
    }

    private void removeRoleFromPrevStar(Role role) {
        String prevStarTag = Star.getPreviousStarTag();
        if(prevStarTag == null)
            return;

        Member prevStarMember = event.getJDA().getGuildById(GUILD_ID).getMemberByTag(prevStarTag);
        event.getGuild().removeRoleFromMember(prevStarMember, role).queue();
    }

    private void addRoleToNewStar(Role role) {
        Member starMember = event.getJDA().getGuildById(GUILD_ID).getMemberByTag(Star.getStarTag());
        System.out.println(starMember.getUser().getName());
        event.getGuild().addRoleToMember(starMember, role).queue();
    }

    public static void showCurrentStar(SlashCommandEvent event) {

        EmbedBuilder builder = new EmbedBuilder();
        if(Star.isStarPresent()) {
            String starTimeLeft = convertSecondsToTime(Star.getTimeLeft());
            String starTag = Star.getStarTag();
            String number =  String.valueOf(Star.getStarNumber());
            String avatarUrl = event.getJDA().getUserByTag(starTag).getAvatarUrl();

            builder.setAuthor(starTag, null, avatarUrl);
            builder.addField(new MessageEmbed.Field("Число", number, false));
            builder.addField(new MessageEmbed.Field("Сбросится автоматически через:", starTimeLeft, false));
        } else {
            builder.setColor(new Color(46, 255, 168));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {}
            builder.appendDescription("**На данный момент действующей звезды нет. Станьте им!**");
        }
        event.replyEmbeds(builder.build()).queue();
    }
}

package unused;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Controller implements EventListener {
    int counter = 0;

    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
        if(event instanceof MessageReceivedEvent messenger) {
            Message message = messenger.getMessage();
            String msg = message.getContentDisplay();
            String author = messenger.getAuthor().getName();
            System.out.println(++counter);
            //System.out.println("Author: " + author);
//            System.out.println("Message: " + msg);

            if(author.equals("GFDapp")) {
                Text text = new Text();
                message.reply(text.createMessage()).queueAfter(5000, TimeUnit.MILLISECONDS);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

class Text {
    List<String> verbs = List.of("lie", "smoke", "jump", "dress", "cook", "start", "look", "listen", "laugh", "whistle", "accept", "agree", "beam", "murder", "wonder", "wander", "giggle", "cry", "decide", "fear", "imagine", "impress");
    List<String> past = List.of("a long time ago", "a while back", "recently", "at last meeting", "yesterday", "just recently", "one second ago", "2 days ago", "before leaving", "after starting", "after going", "after sleeping", "before taking it", "before listening", "10 minutes ago", "2 month ago", "3 weeks ago");
    List<String> future = List.of("a few minutes later", "soon", "in evening", "on friday", "tonight", "3 minutes later", "before leaving", "after meeting", "on saturday", "on monday", "in a morning", "5 months later", "after abandoning", "before quiting", "before learning", "after breaking", "as soon as possible", "some minutes later", "some days later", "not soon");
    List<String> places = List.of("in a forest", "at home", "under a bridge", "at work", "at school", "in a kitchen", "with a wife", "with a husband", "on a coach", "at a meeting", "at a corner", "in front of it", "sitting", "quickly", "with a cup of tea", "right on a road", "in a elevator", "with him", "with us", "with her");

    enum Time {PAST, FUTURE, PRESENT};

    String createMessage() {
        String msg;

        if(isQuestion()) {
            msg = getQuestion();
        } else {
            msg = getSentence();
        }

        return msg;
    }

    private int getRandom(int from, int to) {
        Random random = new Random();
        return random.nextInt(to-from+1) + from;
    }

    private boolean isQuestion() {
        return getRandom(0, 1) == 1;
    }

    private Time getTime() {
        int r = getRandom(0, 2);
        return r == 2 ? Time.FUTURE : r == 1 ? Time.PAST : Time.PRESENT;
    }

    private String getVerb() {
        int index = getRandom(0, verbs.size()-1);
        return verbs.get(index);
    }

    private String getPlace() {
        int index = getRandom(0, places.size()-1);
        return places.get(index);
    }

    private String getTimePhrase(Time time) {
        if(Time.PAST == time) {
            int ind = getRandom(0, past.size()-1);
            return past.get(ind);
        } else if (Time.FUTURE == time) {
            int ind = getRandom(0, future.size()-1);
            return future.get(ind);
        } else {
            int ind = getRandom(0, 1);
            return ind == 1 ? "now" : "";
        }
    }

    private String getVerbWithTime(Time time) {
        String verb = getVerb();
        if(Time.FUTURE == time) return "will " + verb;
        if(Time.PAST == time) return verb.matches(".*[euioa]") ? verb + "d" : verb.endsWith("y") ? verb.substring(0, verb.length()-1) + "id" : verb + "ed";
        return verb;
    }

    private String getQuestion() {
        var res = new StringBuilder();
        Time time = getTime();

        if(Time.FUTURE == time) res.append("Will");
        else if(Time.PAST == time)res.append("Did");
        else res.append("Do");

        res.append(" ");
        res.append("you");
        res.append(" ");

        res.append(getVerb());
        res.append(" ");

        res.append(getPlace());
        res.append(" ");

        res.append(getTimePhrase(time));
        res.append("?");

        return res.toString();
    }

    private String getSentence() {
        var res = new StringBuilder();
        Time time = getTime();

        res.append("you");
        res.append(" ");

        res.append(getVerbWithTime(time));
        res.append(" ");

        res.append(getPlace());
        res.append(" ");

        res.append(getTimePhrase(time));

        return res.toString();
    }
}


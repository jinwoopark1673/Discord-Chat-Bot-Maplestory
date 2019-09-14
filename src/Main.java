import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Main {

    /** Responsible for setting up arduino serial and other listeners.
     *  Loops forever to execute run() in gameInteraction class. */
    public static void main(String[] args) throws Exception {
        GUI gui= new GUI();
        gui.editText("Please wait");

        gameScreenAudit gsa = new gameScreenAudit();

        jscomm rxtx = new jscomm("COM" + gui.getPortName());

        Rectangle gameRec = gsa.getGameRec();
        for (int i = 0; i < gameRec.x / 10; i++) {
            rxtx.write(1);
        }
        for (int i = 0; i < gameRec.x % 10; i++) {
            rxtx.write(0);
        }
        for (int i = 0; i < gameRec.y / 10; i++) {
            rxtx.write(4);
        }
        for (int i = 0; i < gameRec.y % 10; i++) {
            rxtx.write(3);
        }
        TimeUnit.MILLISECONDS.sleep(100);

        jdaListener jdaListener = new jdaListener(publicChatDiscordID);
        jdaListener.listener listener = jdaListener.listener;
        JDA jda = discordUtilities.initJDA(new JDABuilder(AccountType.BOT).addEventListener(jdaListener), botToken);

        gameInteraction gi = new gameInteraction(jda, gsa, rxtx, listener, gui);
        System.out.println("starting lil");
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
                gi.run();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    // Discord Bot Token
    static String botToken = "--Your--Bot--Token--";
    /** Check the discord text channel ids by using Main for discordUtilities class. */
    // Discord ID for a text channel stores the messages the bot character received as a private message in-game.
    static String inquiryDiscordID = "--Discord--ID--";
    // Discord ID for a text channel stores all the commands executed.
    static String executeHistoryDiscordID = "--Discord--ID--";
    // Discord ID for a text channel that will act as a public chat.
    static String publicChatDiscordID = "--Discord--ID--";
    // Discord ID for a text channel that stores all the in-game user permissions.
    static String userPermmisionsID = "--Discord--ID--";
    // Discord ID for a text channel that stores the schedule and locations of free mvp buffs, conveniently called lil here.
    static String lilMessageID = "--Discord--ID--";
    // Discord ID for a text channel that stores admin messages that will periodically be printed in-game.
    static String adminAlarmID = "--Discord--ID--";
    // Discord ID for a text channel that directly executes any input command (executed as in-game bot).
    static String adminCommandsID = "--Discord--ID--";
}

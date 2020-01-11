import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    /** Responsible for setting up arduino serial and other listeners.
     *  Loops forever to execute run() in gameInteraction class. */
    public static void main(String[] args) throws Exception {
        File settings = new File("./settings.txt");
        GUI gui;
        if (settings.exists()) {
            Scanner scanner = new Scanner(new File("./settings.txt"));
            ArrayList<String> inputs = new ArrayList<>();
            while (scanner.hasNextLine()) {
                inputs.add(scanner.nextLine());
            }
            if (inputs.size() < 3) {
                inputs.add("");
                inputs.add("");
                inputs.add("");
            }
            gui = new GUI("Kalpago", inputs.get(0), inputs.get(1), inputs.get(2));
        } else {
            gui= new GUI("Kalpago", "", "", "");
        }
        String[] userSettings = gui.getUserSettings();
        PrintWriter writer2 = new PrintWriter("./settings.txt", "UTF-8");
        writer2.println(userSettings[0]);
        writer2.println(userSettings[1]);
        writer2.println(userSettings[2]);
        writer2.close();

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
        jda.setRequestTimeoutRetry(true);

        gameInteraction gi = new gameInteraction(jda, gsa, rxtx, listener, gui);
        System.out.println("starting lil");
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
                gi.run();
            } catch (Exception e) {
                PrintWriter writer = new PrintWriter("error_log.txt", "UTF-8");
                writer.println(e.toString());
                e.printStackTrace();
                for (StackTraceElement st : e.getStackTrace()) {
                    writer.println("\t at " + st.toString());
                }
                writer.close();

                jda.shutdown();
                jda = discordUtilities.initJDA(new JDABuilder(AccountType.BOT).addEventListener(jdaListener), botToken);
                jda.setRequestTimeoutRetry(true);
                gi.replaceJDA(jda);
            }
        }
    }

    // Discord Bot Token
    static String botToken = "--Your Discord Bot ID--";
    /** Please check Main at discordUtilities class for text channel ids. */
    // Discord ID for a text channel stores the messages the bot character received as a private message in-game.
    static String inquiryDiscordID = "--Channel ID--";
    // Discord ID for a text channel stores all the commands executed.
    static String executeHistoryDiscordID = "--Channel ID--";
    // Discord ID for a text channel that will act as a public chat.
    static String publicChatDiscordID = "--Channel ID--";
    // Discord ID for a text channel that stores all the in-game user permissions.
    static String userPermmisionsID = "--Channel ID--";
    // Discord ID for a text channel that stores the schedule and locations of free mvp buffs, conveniently called lil here.
    static String lilMessageID = "--Channel ID--";
    // Discord ID for a text channel that stores admin messages that will periodically be printed in-game.
    static String adminAlarmID = "--Channel ID--";
    // Discord ID for a text channel that directly executes any input command (executed as in-game bot).
    static String adminCommandsID = "--Channel ID--";
}

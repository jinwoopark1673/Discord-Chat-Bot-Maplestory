import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;

import javax.security.auth.login.LoginException;
import java.util.List;

/** Takes care of the discord setups and discord writes. */
public class discordUtilities {

    /** Executing this main prints all the text channel names and their id
     *  that the bot can access. */
    public static void main(String[] args) {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        JDA jda = discordUtilities.initJDA(builder, Main.botToken);
        List<TextChannel> l = jda.getTextChannels();
        for (TextChannel k : l) {
            System.out.println(k.getName() + ": " + k.getId());
        }
        System.exit(0);
    }

    public static JDA initJDA (JDABuilder builder, String token) {
        builder.setToken(token);
        try {
            JDA jda = builder.build();
            jda.awaitReady();
            return jda;
        } catch (LoginException e) {
            System.out.println("Error! Login to discord server failed.");
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("Error! Login interrupted.");
            System.exit(1);
        }
        return null;
    }

    public static void sendMessage (JDA jda, String textChannelID, String message) {
        jda.getTextChannelById(textChannelID).sendMessage(message).queue();
    }

    public static void sendMessage (TextChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessage (List<TextChannel> channelList, String message) {
        for (TextChannel channel : channelList) {
            channel.sendMessage(message).queue();
        }
    }

    public static void sendPrivateMessage (User user, String message) {
        user.openPrivateChannel().queue((PrivateChannel channel) -> channel.sendMessage(message).queue());
    }
}

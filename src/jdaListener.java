import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;

/** Listens to a discrod channel and saves all the inputs. */
public class jdaListener extends ListenerAdapter {

    public jdaListener(String channelID) {
        this.channelID = channelID;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        if (channel.getId().equals(channelID) && !author.isBot()) {
            synchronized (listener) {
                if (event.getMember().getNickname() == null) {
                    listener.history.add(event.getMember().getEffectiveName() + " : " + message.getContentRaw());
                } else {
                    listener.history.add(event.getMember().getNickname() + " : " + message.getContentRaw());
                }
            }
            message.delete().queue();
        }
    }

    public class listener {
        public ArrayList<String> history = new ArrayList<>();

        public boolean hasMessage() {
            return history.size() != 0;
        }
    }

    private String channelID;
    public listener listener = new listener();
}

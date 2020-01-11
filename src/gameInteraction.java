import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import org.joda.time.LocalDateTime;

import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/** Responsible for reading the chat screen and run any commands;
 *  sends and receives message from Discord channels in each iteration from Main class. */
public class gameInteraction {

    /** Message to beg for flag missions. */
    private final String flagMessage = "[알림] 플래그 참여좀 해주세요ㅜㅠㅜㅠㅜㅠㅜㅜㅠㅜㅜㅠㅠㅜㅠㅜㅜㅠㅠㅠㅜㅠㅜㅠㅜㅜㅠㅠㅜㅜㅠㅜㅠㅜㅠㅜㅠㅜ";

    /** Alarm messages for 11:45 pm.*/
    private final String toDoMessage = "[알림] 몬파, 심볼일퀘, 유니온 코인, 마일리지적립 까먹지마새오!";
    private final String toDoMondayMessage = "[알림] 길축, 심볼일퀘, 유니온 코인, 마일리지적립!!! 'ㅅ'";
    private final String toDoWednesdayMessage = "[알림] 몬파, 결정석판매, 심볼일퀘, 유니온 코인, 마일리지적립 체크하새오!";
    private final String toDoSundayMessage = "[알림] 일요일몬파, 심볼일퀘, 유니온경쿠, 유니온 코인, 마일리지적립 받으새오!!";

    /** Display message at help command run. */
    private final String helpMessage = "[커맨드 목록] [!릴경][!릴경설정][!시간][!도움말][!디코주소]";
    private final String linkMessage = "[디코 가입] _____Address______";

    /** User permission describes the level of control that each user gets.
     *  Used for Actions class and various functions. in gameInteraction class.*/
    enum userPermission {
        DEVELOPER(0, "DEVELOPER"), ADMIN(2, "ADMIN"), AUTH(4, "AUTH"), MEMBER(6, "MEMBER"), BANNED(8, "BANNED"), RMP(10, "RMP");

        private int level;
        private String name;

        userPermission(int l, String name) {
            level = l;
            this.name = name;
        }

        public int getLevel() {
            return level;
        }

        public String getString() {
            return name;
        }
    }

    public gameInteraction (JDA jda, gameScreenAudit gsa, jscomm rxtx, jdaListener.listener listener, GUI gui) {
        _jda = jda;
        _gsa = gsa;
        _rxtx = rxtx;
        _listener = listener;
        _toDoList = new scheduler() {{
            add(Arrays.asList(0, 12), flagMessage);
            add(Arrays.asList(0, 19), flagMessage);
            add(Arrays.asList(0, 21), flagMessage);
            add(Arrays.asList(30, 23), toDoMessage);
            add(Arrays.asList(45, 23), toDoMessage);
            add(Arrays.asList(55, 23), toDoMessage);
            add(Arrays.asList(30, 23, 1), toDoMondayMessage);
            add(Arrays.asList(45, 23, 1), toDoMondayMessage);
            add(Arrays.asList(55, 23, 1), toDoMondayMessage);
            add(Arrays.asList(30, 23, 3), toDoWednesdayMessage);
            add(Arrays.asList(45, 23, 3), toDoWednesdayMessage);
            add(Arrays.asList(55, 23, 3), toDoWednesdayMessage);
            add(Arrays.asList(30, 23, 7), toDoSundayMessage);
            add(Arrays.asList(45, 23, 7), toDoSundayMessage);
            add(Arrays.asList(55, 23, 7), toDoSundayMessage);
        }};
        _lilAlarms = new HashSet<>();
        _userPrivilege = new HashMap<>() {{
            /** Hardcoded values for ownership. */
            // put("--Your--Owner--Character--Name--", 0);
            put("admin", 0);
        }};
        Actions a = new Actions(this);

        /** Maps user inputs to user commands. */
        _userCommands =  new HashMap<>() {{
            Actions.Action e = a.new editPermissionAction();
            put("!edit", e);
            Actions.Action q = a.new exitSystemAction();
            put("!quit", q);
            Actions.Action p = a.new pauseAction();
            put("!pause", p);
            Actions.Action r = a.new resumeAction();
            put("!resume", r);
            Actions.Action au = a.new authAction();
            put("!auth", au);
            Actions.Action rs = a.new resetPermissionAction();
            put("!rs", rs);
            put("!reset", rs);
            Actions.Action rmp = a.new rmpAction();
            put("!rmp", rmp);
            Actions.Action b = a.new banAction();
            put("!밴", b);
            put("!ban", b);
            Actions.Action ls = a.new lilSetAction();
            put("!릴경설정", ls);
            put("!릴경추가", ls);
            put("!경뿌설정", ls);
            put("!경뿌추가", ls);
            put("!add", ls);
            put("!set", ls);
            Actions.Action al = a.new alarmAction();
            put("!알람설정", al);
            put("!알람", al);
            put("!alarm", al);
            Actions.Action lc = a.new lilCallAction();
            put("!릴경", lc);
            put("!릴경좀", lc);
            put("!릴경?", lc);
            put("!ㄹ", lc);
            put("!ㄹㄱ", lc);
            put("!ㄺ", lc);
            Actions.Action h = a.new helpAction();
            put("!도움", h);
            put("!헬프", h);
            put("!가이드", h);
            put("!도움말", h);
            put("!명령어", h);
            put("!h", h);
            put("!help", h);
            Actions.Action t = a.new timeAction();
            put("!시간", t);
            put("!타임", t);
            put("!t", t);
            put("!time", t);
            Actions.Action ty = a.new typeAction();
            put("!type", ty);
            Actions.Action la = a.new linkAction();
            put("!디코", la);
            put("!디코주소", la);
        }};
        _messageHistoryTime = new long[3];
        adminMsgPeriod = gui.getPeriod();
        adminMsgAlert = new LocalDateTime().plusMinutes(adminMsgPeriod);
        myName = gui.getMyName();
        System.out.println(myName);
        lilMessage = getDiscordLilMessage();
        System.out.println(lilMessage);
        getDiscordMemberPermission();
        gui.editText("Starting bot");
    }

    public void run() {
        LocalDateTime currentTime = new LocalDateTime();
        if (previousMinute != currentTime.getMinuteOfHour()) {
            previousMinute = currentTime.getMinuteOfHour();
            lilAlarmed = false;
            toDoAlarmed = false;
        }
        if (!adminMessageAlarmed && currentTime.getHourOfDay() == adminMsgAlert.getHourOfDay() && currentTime.getMinuteOfHour() == adminMsgAlert.getMinuteOfHour()) {
            List<Message> adminAlarm = _jda.getTextChannelById(Main.adminAlarmID).getHistory().retrievePast(100).complete();
            for (int i = adminAlarm.size() - 1; i >= 0; i--) {
                copyClipboard(adminAlarm.get(i).getContentRaw());
                sendEnterPasteEnter();
            }
            adminMessageAlarmed = true;
        } else if (adminMessageAlarmed && currentTime.getMinuteOfHour() != adminMsgAlert.getMinuteOfHour()) {
            adminMsgAlert = currentTime.plusMinutes(adminMsgPeriod - 1);
            adminMessageAlarmed = false;
        }
        if (!lilAlarmed && _lilAlarms.contains(currentTime.plusMinutes(1).getMinuteOfHour())) { // Need to run lil alarm
            lilAlarmed = true;
            runLilAlarm();
        } else if (!toDoAlarmed && !_toDoList.get(currentTime).equals("")) { // Need to run one of to-Do alarms
            toDoAlarmed = true;
            runToDoAlarm(_toDoList.get(currentTime));
        } else {
            ArrayList<String> listUpdates = _gsa.update();
            ArrayList<String> sl = new ArrayList<>();
            for (String update : listUpdates) {
                String[] chat = parseChat(update);
                String type = chat[0];
                String userName = chat[1];
                String msg = chat[2];
                String identifier = chat[3];
                System.out.println(chat[1] + " : " + chat[2]);
                if (!paused || hasPermission(userName, userPermission.ADMIN)) {
                    // Send to discord
                    if (type.equals("public")) {
                        sl.add(userName + " : " + msg.trim());
                    }
                    // Execute the command
                    if (!userName.equals(myName) && _userCommands.containsKey(identifier) && (type.equals("public") || hasPermission(userName, userPermission.ADMIN))) {
                        Actions.Action command = _userCommands.get(identifier);
                        if (command.isAcceptedUserType(userName)) {
                            command.execute(chat);
                            discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, userName + " : " + msg);
                        } else {
                            discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, "Rejected: " + userName + " : " + msg);
                        }
                    } else if (type.equals("private")) {
                        discordUtilities.sendMessage(_jda, Main.inquiryDiscordID, userName + " : " + msg);
                    }
                } else if (identifier.equals("!resume")) {
                    Actions.Action command = _userCommands.get(identifier);
                    if (command.isAcceptedUserType(userName)) {
                        command.execute(chat);
                        discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, userName + " : " + msg);
                    } else {
                        discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, "Rejected (r): " + userName + " : " + msg);
                    }
                } else if (_userCommands.containsKey(identifier)) {
                    discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, "Rejected (p): " + userName + " : " + msg);
                }
            }
            int charCount = 0;
            StringBuilder sb = new StringBuilder();
            for (String s : sl) {
                if (charCount + s.length() > 2000) {
                    discordUtilities.sendMessage(_jda, Main.publicChatDiscordID, sb.toString());
                    sb = new StringBuilder();
                    charCount = 0;
                }
                sb.append(s);
                sb.append('\n');
                charCount += s .length()+ 1;
            }
            if (charCount != 0) {
                discordUtilities.sendMessage(_jda, Main.publicChatDiscordID, sb.toString());
            }
        }
        // Send discord updates to game
        ArrayList<String> updateList = new ArrayList<>();
        synchronized (_listener) {
            if (_listener.hasMessage()) {
                updateList = new ArrayList<>(_listener.history);
                _listener.history = new ArrayList<>();
            }
        }
        if (updateList.size() != 0) {
            for (String x : updateList) {
                copyClipboard(x);
                sendEnterPasteEnter();
            }
        }
        List<Message> adminCommands = _jda.getTextChannelById(Main.adminCommandsID).getHistory().retrievePast(100).complete();
        for (Message m : adminCommands) {
            m.delete().queue();
            String[] chat = parseChat("admin : " + m.getContentRaw());
            if (_userCommands.containsKey(chat[3])) {
                Actions.Action command = _userCommands.get(chat[3]);
                command.execute(chat);
                discordUtilities.sendMessage(_jda, Main.executeHistoryDiscordID, "admin : " + m.getContentRaw());
            }
        }
    }

    public String getDiscordLilMessage() {
        List<Message> pinned = _jda.getTextChannelById(Main.lilMessageID).getPinnedMessages().complete();
        if (pinned.size() != 0) {
            return pinned.get(0).getContentRaw();
        } else {
            return "";
        }
    }

    public void getDiscordMemberPermission() {
        List<Message> next = _jda.getTextChannelById(Main.userPermmisionsID).getHistory().retrievePast(100).complete();
        List<Message> permissions = new ArrayList<>(next);
        String latestID = "";
        if (next.size() == 100) {
            latestID = next.get(99).getId();
            next = _jda.getTextChannelById(Main.userPermmisionsID).getHistoryBefore(latestID, 100).complete().getRetrievedHistory();
            permissions.addAll(next);
        }
        for (int i = permissions.size() - 1; i >= 0; i--) {
            String message = permissions.get(i).getContentRaw();
            String[] split = message.split(":", 2);
            givePermission(split[0], split[1], false);
        }
    }

    public int getLevel(String name) {
        if (_userPrivilege.containsKey(name)) {
            return _userPrivilege.get(name);
        } else {
            return userPermission.MEMBER.getLevel();
        }
    }

    public boolean hasPermission(String name, userPermission up) {
        if (_userPrivilege.containsKey(name)) {
            return _userPrivilege.get(name) <= up.level;
        } else {
            return 6 <= up.level;
        }
    }

    public void givePermission(String name, String level, boolean save) {
        if (!_userPrivilege.containsKey(name) || _userPrivilege.get(name) != 0) {
            level = level.toLowerCase();
            switch (level) {
                case "developer":
                    _userPrivilege.put(name, userPermission.DEVELOPER.getLevel());
                    break;
                case "admin":
                    _userPrivilege.put(name, userPermission.ADMIN.getLevel());
                    break;
                case "auth":
                    _userPrivilege.put(name, userPermission.AUTH.getLevel());
                    break;
                case "member":
                    _userPrivilege.put(name, userPermission.MEMBER.getLevel());
                    break;
                case "banned":
                    _userPrivilege.put(name, userPermission.BANNED.getLevel());
                    break;
                case "rmp":
                    _userPrivilege.put(name, userPermission.RMP.getLevel());
                    break;
            }
            if (save) {
                discordUtilities.sendMessage(_jda, Main.userPermmisionsID, name + ":" + level.toUpperCase());
            }
        }
    }

    public void givePermission(String name, userPermission up) {
        if (!_userPrivilege.containsKey(name) || _userPrivilege.get(name) != 0) {
            _userPrivilege.put(name, up.getLevel());
            discordUtilities.sendMessage(_jda, Main.userPermmisionsID, name + ":" + up.getString());
        }
    }

    public void pauseBot() {
        paused = true;
    }

    public void resumeBot() {
        paused = false;
    }

    public void runTypeCall(String message) {
        copyClipboard(message);
        sendEnterPasteEnter();
    }

    private void runLilAlarm() {
        String msg = "[" + new LocalDateTime().getMinuteOfHour() + "분이애오!] " + lilMessage;
        copyClipboard(msg + new String(new char[70 - msg.length()]).replace("\0", "@"));
        sendEnterPasteEnter();
    }

    private void runToDoAlarm(String message) {
        copyClipboard(message);
        sendEnterPasteEnter();
    }

    public void runLilSet(String message) {
        String[] split = message.split(" ", 2);
        System.out.println(split.length);
        if (split.length == 2) {
            lilMessage = message.split(" ", 2)[1];
            _jda.getTextChannelById(Main.lilMessageID).getPinnedMessages().complete().get(0).editMessage(lilMessage).queue();
        } else if (split.length == 1) {
            lilMessage = "";
            _jda.getTextChannelById(Main.lilMessageID).getPinnedMessages().complete().get(0).editMessage("없음").queue();
        }
    }

    public void runAlarmSet(HashSet<Integer> times) {
        _lilAlarms = times;
    }

    public void runHelpCall() {
        copyClipboard(helpMessage);
        sendEnterPasteEnter();
    }

    public void runLinkCall() {
        copyClipboard(linkMessage);
        sendEnterPasteEnter();
    }

    public void runLilCall() {
        if (!lilMessage.equals("")) {
            copyClipboard("[" + new LocalDateTime().getMinuteOfHour() + "분] " + lilMessage);
            sendEnterPasteEnter();
        } else {
            copyClipboard("[릴경]"  + new String[]{"ㅁㄹㅁㄹ","ㅁㄻㄻㄻㄹ", "몰라요", "없을걸?", "모르겠는데?"}[ThreadLocalRandom.current().nextInt(0 ,5)]);
            sendEnterPasteEnter();
        }
    }

    public void runTimeCall() {
        LocalDateTime dt = new LocalDateTime();
        String dayOfWeek;
        switch (dt.getDayOfWeek()) {
            case 1: dayOfWeek = "월요일"; break;
            case 2: dayOfWeek = "화요일"; break;
            case 3: dayOfWeek = "수요일"; break;
            case 4: dayOfWeek = "목요일"; break;
            case 5: dayOfWeek = "금요일"; break;
            case 6: dayOfWeek = "토요일"; break;
            default: dayOfWeek = "일요일";
        }
        copyClipboard(String.format("[시간] %d년 %d월 %d일 %s %d시 %d분입니다",
                dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dayOfWeek, dt.getHourOfDay(), dt.getMinuteOfHour()));
        sendEnterPasteEnter();
    }

    /** 0th: public/private, 1st: user name, 2nd: message, 3rd: identifier. */
    private String[] parseChat(String chat) {
        String[] result = new String[5];
        int publicLoc = chat.indexOf(":");
        int privateLoc = chat.indexOf(">>");
        String[] split;
        if (privateLoc == -1 || (publicLoc != -1 && publicLoc < privateLoc)) {
            result[0] = "public";
            split = chat.split(":", 2);
            result[1] = split[0].trim();
        } else {
            result[0] = "private";
            split = chat.split(">>", 2);
            result[1] = split[0].trim().split("\\[", 2)[0];
        }
        if (split.length == 1) {
            result[2] = "";
            result[3] = "null";
            return result;
        }
        result[2] = split[1].trim();
        result[3] = result[2].split(" ", 2)[0].toLowerCase();
        return result;
    }

    private void sendEnterPasteEnter() {
        try {
            TimeUnit.MILLISECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lastMsg.equals(currentMsg)) {
            _sameMessageCount += 1;
        } else {
            _sameMessageCount = 0;
        }
        if (_sameMessageCount >= 2) {
            _sameMessageCount = 0;
            copyClipboard('.' + currentMsg);
        }
        while (System.currentTimeMillis() < _messageHistoryTime[2] + 3000) {
            try {
                TimeUnit.MILLISECONDS.sleep(_messageHistoryTime[2] + 3000 - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _messageHistoryTime[2] = _messageHistoryTime[1];
        _messageHistoryTime[1] = _messageHistoryTime[0];
        _messageHistoryTime[0] = System.currentTimeMillis();
        System.out.println(currentMsg);
        _rxtx.writeWait(7);
    }

    private void copyClipboard(String msg) {
        try {
            msg = msg.replaceAll("\"", "").trim();
            lastMsg = currentMsg;
            currentMsg = msg;
            Runtime.getRuntime().exec("cmd.exe /C echo|set/p=\"" + msg + "\"|clip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceJDA(JDA jda) {
        this._jda = jda;
    }

    private boolean lilAlarmed;
    private boolean toDoAlarmed;
    private boolean adminMessageAlarmed;
    private int previousMinute;
    private boolean paused;
    private String lilMessage;
    private String myName;
    private String currentMsg = "";
    private String lastMsg = "";
    private int adminMsgPeriod;
    private LocalDateTime adminMsgAlert;

    private long[] _messageHistoryTime;
    private int _sameMessageCount;
    private HashMap<String, Actions.Action> _userCommands;
    private jdaListener.listener _listener;

    /** 0 to 5. */
    private HashMap<String, Integer> _userPrivilege;
    private scheduler _toDoList;
    private HashSet<Integer> _lilAlarms;
    private JDA _jda;
    private gameScreenAudit _gsa;
    private jscomm _rxtx;
}

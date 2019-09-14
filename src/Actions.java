/** Helper class that stores action for each command.
 *  Used in gameInteraction class. */
public class Actions {

    public Actions(gameInteraction gi) {
        _gi = gi;
    }

    /** An interface for Action class java polymorphism.*/
    public interface Action {
        boolean isAcceptedUserType(String name);
        void execute(String[] strs);
    }

    public class exitSystemAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Exiting system");
            System.exit(0);
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.ADMIN);
        }
    }

    public class pauseAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Pausing bot");
            _gi.pauseBot();
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.ADMIN);
        }
    }

    public class resumeAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Resuming bot");
            _gi.resumeBot();
        }

        public boolean isAcceptedUserType(String name) { // admin
            return _gi.hasPermission(name, gameInteraction.userPermission.ADMIN);
        }
    }

    public class authAction implements Action {
        public void execute(String[] strs) {
            String chat = strs[2];
            String[] split = chat.split(" ");
            for (int i = 1; i < split.length; i++) {
                _gi.givePermission(split[i], gameInteraction.userPermission.AUTH);
            }
        }

        public boolean isAcceptedUserType(String name) { // admin
            return _gi.hasPermission(name, gameInteraction.userPermission.ADMIN);
        }
    }

    public class editPermissionAction implements Action {
        public void execute(String[] strs) {
            String[] chatSplit = strs[2].split(" ");
            if (chatSplit.length >= 3) {
                String user = chatSplit[1];
                String level = chatSplit[2];
                _gi.givePermission(user, level, true);
            }
        }

        public boolean isAcceptedUserType(String name) { // owner
            return _gi.hasPermission(name, gameInteraction.userPermission.DEVELOPER);
        }
    }

    public class resetPermissionAction implements Action {
        public void execute(String[] strs) {
            String name = strs[1];
            String chat = strs[2];
            String[] split = chat.split(" ");
            if (_gi.hasPermission(name, gameInteraction.userPermission.ADMIN)) {
                for (int i = 1; i < split.length; i++) {
                    if (_gi.getLevel(name) > 2) {
                        _gi.givePermission(split[i], gameInteraction.userPermission.MEMBER);
                    }
                }
            } else {
                for (int i = 1; i < split.length; i++) {
                    if (_gi.getLevel(name) > 4 && _gi.getLevel(split[i]) != 10) {
                        _gi.givePermission(split[i], gameInteraction.userPermission.MEMBER);
                    }
                }
            }
        }

        public boolean isAcceptedUserType(String name) { // auth
            return _gi.hasPermission(name, gameInteraction.userPermission.AUTH);
        }
    }

    public class rmpAction implements Action {
        public void execute(String[] strs) {
            String chat = strs[2];
            String[] split = chat.split(" ");
            for (int i = 1; i < split.length; i++) {
                _gi.givePermission(split[i], gameInteraction.userPermission.RMP);
            }
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.ADMIN);
        }
    }

    public class banAction implements Action {
        public void execute(String[] strs) {
            String chat = strs[2];
            String[] split = chat.split(" ");
            for (int i = 1; i < split.length; i++) {
                _gi.givePermission(split[i], gameInteraction.userPermission.BANNED);
            }
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.AUTH);
        }
    }

    public class lilSetAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Setting lil message to " + strs[2]);
            _gi.runLilSet(strs[2]);
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.MEMBER);
        }
    }

    public  class alarmAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Set alarm period to " + strs[2]);
            _gi.runAlarmSet(strs[2]);
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.MEMBER);
        }
    }

    public class lilCallAction implements Action {
        public void execute(String[] strs) {
            System.out.println("Sending lil message");
            if (System.currentTimeMillis() - lilLastCalled > 5000) {
                _gi.runLilCall();
                lilLastCalled = System.currentTimeMillis();
            }
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.BANNED);
        }
    }

    public class helpAction implements Action {
        public void execute(String[] strs) {
            System.out.println("help called");
            if (System.currentTimeMillis() - helpLastCalled > 5000) {
                _gi.runHelpCall();
                helpLastCalled = System.currentTimeMillis();
            }
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.BANNED);
        }
    }

    public class timeAction implements Action {
        public void execute(String[] strs) {
            System.out.println("time called");
            if (System.currentTimeMillis() - timeLastCalled > 5000) {
                _gi.runTimeCall();
                timeLastCalled = System.currentTimeMillis();
            }
        }

        public boolean isAcceptedUserType(String name) {
            return _gi.hasPermission(name, gameInteraction.userPermission.BANNED);
        }
    }

    private gameInteraction _gi;
    private long lilLastCalled;
    private long helpLastCalled;
    private long timeLastCalled;
}
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/** Helper class for scheduling alarms.
 *  Used in gameInteraction class. */
public class scheduler {

    public scheduler() {
        _scheduleList = new ArrayList<>();
        currentID = 0;
    }

    public int add(List<Integer> items, String message) {
        _scheduleList.add(new schedule(items, message, currentID));
        _scheduleList.sort(Comparator.comparingInt((schedule o) -> o.size * -1));
        currentID += 1;
        return currentID - 1;
    }

    public void delete(int id) {
        for (schedule s : _scheduleList) {
            if (s.getID() == id) {
                _scheduleList.remove(s);
                return;
            }
        }
    }

    public void delete(HashSet<Integer> ids) {
        for (schedule s : _scheduleList) {
            if (ids.contains(s.getID())) {
                _scheduleList.remove(s);
            }
        }
    }

    public String get(LocalDateTime dt) {
        for (schedule s : _scheduleList) {
            if (s.equals(dt)) {
                return s.getMessage();
            }
        }
        return "";
    }

    private class schedule {
        private int year;
        private int month;
        private int day;
        /** 0 if day of week does not matter. 1 if Monday ... 7 if Sunday. */
        private int dayOfWeek;
        private int hour;
        private int minute;
        private int size;
        private String message;
        private int id;

        public schedule(List<Integer> items, String msg, int ID) {
            if (items.size() != 0) {
                switch (items.size()) {
                    default:
                        year = items.get(5);
                    case 5:
                        month = items.get(4);
                    case 4:
                        day = items.get(3);
                    case 3:
                        dayOfWeek = items.get(2);
                    case 2:
                        hour = items.get(1);
                    case 1:
                        minute = items.get(0);
                }
            }
            size = items.size() > 5 ? 5: items.size();
            message = msg;
            id = ID;
        }

        public boolean equals(LocalDateTime dt) {
            switch (size) {
                case 6:
                    if (dt.getYear() != year) {
                        return false;
                    }
                case 5:
                    if (dt.getMonthOfYear() != month) {
                        return false;
                    }
                case 4:
                    if (dt.getDayOfMonth() != day) {
                        return false;
                    }
                case 3:
                    if (dayOfWeek != 0 && dt.getDayOfWeek() != dayOfWeek) {
                        return false;
                    }
                case 2:
                    if (dt.getHourOfDay() != hour) {
                        return false;
                    }
                case 1:
                    if (dt.getMinuteOfHour() != minute) {
                        return false;
                    }
                    return true;
            }
            return false;
        }

        public int getID() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }

    private int currentID;
    private ArrayList<schedule> _scheduleList;
}

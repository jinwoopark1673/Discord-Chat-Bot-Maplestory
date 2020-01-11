import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/** Helper class that is responsible for cleaning the raw capture of the screen
 *  and used fontTree class to parse it.
 *  Used in gameInteraction class. */
public class gameScreenAudit extends Thread {

    // Pixel length of the chat.
    final private int chatLength = 380;
    final private int numLines = 29;
    final private int black = Color.BLACK.getRGB();
    final private int white = Color.WHITE.getRGB();
    final private int orange =  new Color(255, 163, 25).getRGB(); // friend
    final private int green1 = new Color(25, 255, 25).getRGB(); // private
    final private int green2 = new Color(178, 255, 132).getRGB(); // union
    final private int yellow = new Color(255, 255, 25).getRGB(); // Item Link

    public gameScreenAudit() throws AWTException, IOException {
        _capture = new capture();
        _gameRec = _capture.getRect();
        _font = new fontTree("fonttree.txt");
        update();
    }

    /** Reads the chat and converts it to an ArrayList. */
    public ArrayList<String> update() {
        _previousList = _chatList;
        BufferedImage chat = getChat(numLines);
        ArrayList<String> readings = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            BufferedImage line = getLine(chat, i);
            String reading = _font.readString(line, new HashSet<>(Arrays.asList(green1, green2, yellow)));
            readings.add(reading);
        }
        _chatList = new ArrayList<>();
        for (int i = numLines - 1; i >= 0; i--) {
            if (readings.get(i).length() >= 3 && !isExtended(readings.get(i))) {
                if (i > 0 && readings.get(i - 1).length() >= 3 && isExtended(readings.get(i - 1))) {
                    _chatList.add(readings.get(i).trim() + readings.get(i - 1).trim());
                    i -= 1;
                } else {
                    _chatList.add(readings.get(i).trim());
                }
            }
        }
        return getChanges();
    }

    /** Compares the chat with previous chat and finds changes. */
    public ArrayList<String> getChanges() {
        if (_previousList == null || _previousList.size() == 0 || _chatList.size() == 0) {
            return _chatList;
        }
        for (int i = _chatList.size() - 1; i >= 0; i--) {
            int j = 0;
            while (i >= j) {
                if (_previousList.size() - 1 - j < _previousList.size() && !_chatList.get(i - j).equals(_previousList.get(_previousList.size() - 1 - j))) {
                    break;
                }
                if (i == j) {
                    ArrayList<String> result = new ArrayList<>();
                    for (int k = i + 1; k < _chatList.size(); k++) {
                        result.add(_chatList.get(k));
                    }
                    return result;
                }
                j += 1;
            }
        }
        return _chatList;
    }

    public Rectangle getGameRec() {
        return _gameRec;
    }

    private boolean isExtended(String s) {
        return s.charAt(0) == ' ' && s.charAt(1) == ' ' && s.charAt(2) == ' ';
    }

    public BufferedImage getLine(BufferedImage get, int linePos){
        return get.getSubimage(0, get.getHeight() - 13 - linePos * 13, get.getWidth(),13);
    }

    public BufferedImage getChat(int numLines) {
        return _capture.takeCapture(new Rectangle(_gameRec.x + 4, _gameRec.y + 775 - 13 * (numLines), chatLength, 13 * (numLines - 1) + 13));
    }

    private fontTree _font;
    private capture _capture;
    private Rectangle _gameRec;
    public ArrayList<String> _chatList;
    private ArrayList<String> _previousList;
}

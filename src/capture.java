import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.SocketTimeoutException;

/** Responsible for converting the game chat screen to a BufferedImage for fontTree class to read.
 *  Used in gameScreenAudit class. */
public class capture {

    public capture() throws AWTException{

        initialize();
    }

    public BufferedImage takeCapture(Rectangle rec) {
        return _robot.createScreenCapture(rec);
    }

    public BufferedImage takeCapture() {
        return _robot.createScreenCapture(_rec);
    }

    /** Finds the game window and records its location and size. */
    public void initialize() throws AWTException {
        String windowName = "MapleStory";
        int[] rect = {0, 0, 0, 0};

        User32.INSTANCE.GetWindowRect(User32.INSTANCE.FindWindow(null, windowName), rect);

        _rec = new Rectangle();
        _rec.x = rect[0];
        _rec.y = rect[1];
        _rec.width = rect[2] - rect[0];
        _rec.height = rect[3] - rect[1];
        System.out.println(_rec);
        _robot = new Robot();
    }

    public Rectangle getRect() {
        return _rec;
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
        HWND FindWindow(String lpClassName, String lpWindowName);
        boolean SetForegroundWindow(HWND hWnd);
        HWND FindWindowEx(HWND hwndParent, HWND hwndChildAfter, String lpszClass, String lpszWindow);
        int GetWindowText(HWND hWnd, char[] lpString, int nMaxCount);
        int GetWindowRect(HWND handle, int[] rect);
    }

    public void setFocusTo(String screen) {
        User32.INSTANCE.SetForegroundWindow(GetWindowHandle(screen.toUpperCase(), null));
    }


    public HWND GetWindowHandle(String strSearch, String strClass) {
        char[] lpString = new char[512];
        String strTitle;
        int iFind;
        HWND hWnd = User32.INSTANCE.FindWindow(strClass, null);
        while(hWnd != null) {
            User32.INSTANCE.GetWindowText(hWnd, lpString, 512);
            strTitle = new String(lpString);
            strTitle = strTitle.toUpperCase();
            System.out.println(strTitle);
            iFind = strTitle.indexOf(strSearch);
            if(iFind != -1) {
                return hWnd;
            }
            hWnd = User32.INSTANCE.FindWindowEx(null, hWnd, strClass, null);
        }
        return hWnd;
    }

    private Rectangle _rec;
    private Robot _robot;
}

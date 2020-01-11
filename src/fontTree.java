import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/** Responsible for converting a BufferedImage of single chat line to a string.
 *  Used in gameScreenAudit class. */
public class fontTree {
    final private int black = Color.BLACK.getRGB();

    private BufferedImage koreanA;
    private BufferedImage koreanU;
    private BufferedImage koreanYA;
    private BufferedImage koreanYO;
    private BufferedImage koreanE;
    private BufferedImage koreanI;
    private BufferedImage mmm;
    private BufferedImage nnn;
    private BufferedImage bracketBegin;
    private BufferedImage bracketEnd;

    public static void main(String[] args) throws Exception {
        fontTree f = new fontTree();

        StringBuilder sb = new StringBuilder();
        generate(f._treeFinal, sb);
        PrintWriter writer = new PrintWriter("dddddddd.txt", "UTF-8");
        writer.println(sb.toString());
        writer.close();
    }

    /** Parses fonttree file as a binary tree. */
    public fontTree(String ___) throws IOException {
        _treeFinal = new Node();
        Node pointer = _treeFinal;
        InputStream fonttree = this.getClass().getResourceAsStream("fonttree.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(fonttree, StandardCharsets.UTF_8));
        String text = in.readLine();
        for (char c : text.toCharArray()) {
            if (c == 'ㅐ') {
                pointer = pointer.parent;
            } else if (c == 'ㅏ') {
                if (pointer.zero == null) {
                    pointer.zero = new Node(pointer);
                }
                pointer = pointer.zero;
            } else if (c == 'ㅓ') {
                if (pointer.one == null) {
                    pointer.one = new Node(pointer);
                }
                pointer = pointer.one;
            } else {
                pointer.key = c;
            }
        }
        koreanA = ImageIO.read(this.getClass().getResource("koreanA.png"));
        koreanU = ImageIO.read(this.getClass().getResource("koreanU.png"));
        koreanYA = ImageIO.read(this.getClass().getResource("koreanYA.png"));
        koreanYO = ImageIO.read(this.getClass().getResource("koreanYO.png"));
        koreanE = ImageIO.read(this.getClass().getResource("koreanE.png"));
        koreanI = ImageIO.read(this.getClass().getResource("koreanI.png"));
        mmm = ImageIO.read(this.getClass().getResource("m.png"));
        nnn = ImageIO.read(this.getClass().getResource("n.png"));
        bracketBegin = ImageIO.read(this.getClass().getResource("bbegin.png"));
        bracketEnd = ImageIO.read(this.getClass().getResource("bend.png"));
    }

    /** Creates BufferedImage for each character and appends them to the binary tree. */
    public fontTree() {
        _treeFinal = new Node();
        Set<Character> sb = new HashSet<>();
        for (int i = 0x0001; i <= 0x0007; i++) {
            sb.add((char) i);
        }
        sb.add((char) 0x000b);
        sb.add((char) 0x000c);
        for (int i = 0xac00; i < 0xd7a3; i++) {
            sb.add((char) i);
        }
        for (int i = 0x000e; i <= 0x001f; i++) {
            sb.add((char) i);
        }
        for (int i = 0x0021; i <= 0x007f; i++) {
            if (i != 0x0060 && i != 0x006d && i != 0x006e /**&& i != 0x006d*/ && i != 0x007c) {
                sb.add((char) i);
            }
        }
        for (int i = 0x3131; i <= 0x3163; i++) {
            if (i != 0x314f && i != 0x3153 && i != 0x3151 && i != 0x3155 && i != 0x3150 && i != 0x3163) {
                sb.add((char) i);
            }
        }
        for (char s: sb) {
            if (s == '|') {
                appendTree(stringToBufferedImage("" + s, false), _treeFinal, 0, 0, 'l');
            } else if (s != '\n') {
                appendTree(stringToBufferedImage("" + s, false), _treeFinal, 0, 0, s);
            }
        }
        for (char s: sb) {
            if (s == '|') {
                appendTree(stringToBufferedImage("" + s, true), _treeFinal, 0, 0, 'l');
            } else if (s != '\n' && s != '옙' && s != '예' && s != '켸' && s != '엡' && s != '렙') {
                appendTree(stringToBufferedImage("" + s, true), _treeFinal, 0, 0, s);
            }
        }
        appendTree(stringToBufferedImage(" ", false), _treeFinal, 0, 0, ' ');
    }

    /** Converts the font tree to a string in StringBuilder. */
    public static void generate(Node n, StringBuilder sb) {
        if (n.key != '\0') {
            sb.append(n.key);
        }
        if (n.zero != null) {
            sb.append('ㅏ');
            generate(n.zero, sb);
            sb.append('ㅐ');
        }
        if (n.one != null) {
            sb.append('ㅓ');
            generate(n.one, sb);
            sb.append('ㅐ');
        }
    }

    private void appendTree(BufferedImage img, Node node, int x, int y, char result) {
        if (x == img.getWidth()) {
            if (node.key == '\0') {
                node.key = result;
            }
        } else {
            if (img.getRGB(x, y) == black) {
                if (node.one == null) {
                    node.one = new Node();
                }
                if (y == img.getHeight() - 1) {
                    appendTree(img, node.one, x + 1, 0, result);
                } else {
                    appendTree(img, node.one, x, y + 1, result);
                }
            } else {
                if (node.zero == null) {
                    node.zero = new Node();
                }
                if (y == img.getHeight() - 1) {
                    appendTree(img, node.zero, x + 1, 0, result);
                } else {
                    appendTree(img, node.zero, x, y + 1, result);
                }
            }
        }
    }

    private boolean hasColor(BufferedImage img, HashSet<Integer> color) {
        for (int i = 0; i < img.getWidth() && i < 30; i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if (color.contains(img.getRGB(i, j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String readString_imageSearch(BufferedImage img, BufferedImage target, HashSet<Integer> color, String target_char, int i) {
        if (i + target.getWidth() <= img.getWidth()) {
            to:
            for (int j = 0; j < target.getWidth(); j++) {
                for (int k = 0; k < target.getHeight(); k++) {
                    if ((!color.contains(img.getRGB(i + j, k)) && target.getRGB(j, k) == black) || (color.contains(img.getRGB(i + j, k)) && target.getRGB(j, k) != black)) {
                        break to;
                    }
                    if (j == target.getWidth() - 1 && k == target.getHeight() - 1) {
                        String result = "";
                        if (i != 0) {
                            result += readString(img.getSubimage(0, 0, i, img.getHeight()), color);
                        }
                        result += target_char;
                        if (img.getWidth() - i - target.getWidth() != 0) {
                            result += readString(img.getSubimage(i + target.getWidth(), 0, img.getWidth() - i - target.getWidth(), img.getHeight()), color);
                        }
                        return result;
                    }
                }
            }
        }
        return "";
    }

    public String checkItemHover(BufferedImage img, HashSet<Integer> color) {
        boolean begin = false;
        String beforeBracket = "";
        for (int i = 0; i < img.getWidth(); i++) {
            if (!begin) {
                if (i + bracketBegin.getWidth() <= img.getWidth()) {
                    loop:
                    for (int m = 0; m < bracketBegin.getWidth(); m++) {
                        for (int n = 0; n < bracketBegin.getHeight(); n++) {
                            if ((!color.contains(img.getRGB(i + m, n)) && bracketBegin.getRGB(m, n) == black)
                                    || (color.contains(img.getRGB(i + m, n)) && bracketBegin.getRGB(m, n) != black)) {
                                break loop;
                            }
                            if (m == bracketBegin.getWidth() - 1 && n == bracketBegin.getHeight() - 1) {
                                beforeBracket = readString(img.getSubimage(0, 0, i, img.getHeight()), color);
                                begin = true;
                                break loop;
                            }
                        }
                    }
                }
            } else {
                if (i + bracketEnd.getWidth() <= img.getWidth()) {
                    loop:
                    for (int m = 0; m < bracketEnd.getWidth(); m++) {
                        for (int n = 0; n < bracketEnd.getHeight(); n++) {
                            if ((!color.contains(img.getRGB(i + m, n)) && bracketEnd.getRGB(m, n) == black)
                                    || (color.contains(img.getRGB(i + m, n)) && bracketEnd.getRGB(m, n) != black)) {
                                break loop;
                            }
                            if (m == bracketEnd.getWidth() - 1 && n == bracketEnd.getHeight() - 1) {
                                String afterBracket = readString(img.getSubimage(i + bracketEnd.getWidth(), 0, img.getWidth() - i - bracketEnd.getWidth(), img.getHeight()), color);
                                return beforeBracket + "[]" + afterBracket;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public String readString(BufferedImage img, HashSet<Integer> color) {
        if (!hasColor(img, color)) {
            return "";
        }
        /*
        String checkItemHover = checkItemHover(img, color);
        if (!checkItemHover.isEmpty()) {
            return checkItemHover;
        }*/
        /** Lazy image search for 0, n, m because they are used in fonttree.txt. */
        for (int i = 0; i < img.getWidth(); i++) {
            String result_a = readString_imageSearch(img, koreanA, color, "ㅏ", i);
            if (!result_a.isEmpty()) {
                return result_a;
            }
            String result_u = readString_imageSearch(img, koreanU, color, "ㅓ", i);
            if (!result_u.isEmpty()) {
                return result_u;
            }
            String result_ya = readString_imageSearch(img, koreanYA, color, "ㅑ", i);
            if (!result_ya.isEmpty()) {
                return result_ya;
            }
            String result_yo = readString_imageSearch(img, koreanYO, color, "ㅕ", i);
            if (!result_yo.isEmpty()) {
                return result_yo;
            }
            String result_e = readString_imageSearch(img, koreanE, color, "ㅐ", i);
            if (!result_e.isEmpty()) {
                return result_e;
            }
            String result_i = readString_imageSearch(img, koreanI, color, "ㅣ", i);
            if (!result_i.isEmpty()) {
                return result_i;
            }
            String result_m = readString_imageSearch(img, mmm, color, "m", i);
            if (!result_m.isEmpty()) {
                return result_m;
            }
            String result_n = readString_imageSearch(img, nnn, color, "n", i);
            if (!result_n.isEmpty()) {
                return result_n;
            }
        }
        StringBuilder sb = new StringBuilder();
        int x = 0;
        int y = 0;
        int saveX;
        Node tree = _treeFinal;
        while (x < img.getWidth()) {
            saveX = x;
            while (x < img.getWidth()) {
                if (color.contains(img.getRGB(x, y))) {
                    if (tree.key == '\0' && tree.one == null) {
                        x = saveX + 1;
                        y = 0;
                        break;
                    }
                    tree = tree.one;
                    if (y == img.getHeight() - 1) {
                        x += 1;
                        y = 0;
                        if (tree.key != '\0') {
                            sb.append(tree.key);
                            break;
                        }
                    } else {
                        y += 1;
                    }
                } else {
                    if (tree.key == '\0' && tree.zero == null) {
                        x = saveX + 1;
                        y = 0;
                        break;
                    }
                    tree = tree.zero;
                    if (y == img.getHeight() - 1) {
                        x += 1;
                        y = 0;
                        if (tree.key != '\0') {
                            sb.append(tree.key);
                            break;
                        }
                    } else {
                        y += 1;
                    }
                }
            }
            tree = _treeFinal;
        }
        return sb.toString();
    }

    public BufferedImage stringToBufferedImage(String s, boolean isBold) {
        java.awt.image.BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = img.getGraphics();
        Font f;
        f = new Font("돋움", Font.PLAIN, 11);
        g.setFont(f);
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        Rectangle2D rect = f.getStringBounds(s, frc);
        g.dispose();
        img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
        g = img.getGraphics();
        g.setColor(Color.black);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        if (isBold) {
            if (s.equals("]")) {
                try {
                    img = ImageIO.read(this.getClass().getResource("rightBold.png"));
                } catch (Exception e) {
                    System.exit(1);
                }
            } else {
                for (int i = 1; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        if (img.getRGB(i, j) == black) {
                            img.setRGB(i - 1, j, black);
                        }
                    }
                }
            }
        }
        return img;
    }

    private class Node {
        char key;
        Node zero, one, parent;

        public Node() {
            zero = one = null;
        }

        public Node(Node p) {
            zero = one = null;
            parent = p;
        }
    }

    private Node _treeFinal;
}

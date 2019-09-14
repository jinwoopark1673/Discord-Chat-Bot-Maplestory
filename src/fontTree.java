import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Responsible for converting a BufferedImage of single chat line to a string.
 *  Used in gameScreenAudit class. */
public class fontTree {
    final private int black = Color.BLACK.getRGB();

    /** Parses fonttree file as a binary tree. */
    public fontTree(String location) throws Exception {
        _treeFinal = new Node();
        Node pointer = _treeFinal;
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(location), StandardCharsets.UTF_8));
        String text = in.readLine();
        for (char c : text.toCharArray()) {
            if (c == '0') {
                pointer = pointer.parent;
            } else if (c == 'm') {
                if (pointer.zero == null) {
                    pointer.zero = new Node(pointer);
                }
                pointer = pointer.zero;
            } else if (c == 'n') {
                if (pointer.one == null) {
                    pointer.one = new Node(pointer);
                }
                pointer = pointer.one;
            } else {
                pointer.key = c;
            }
        }
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
            if (i != 0x0060 && i != 0x0030 && i != 0x006e && i != 0x006d && i != 0x007c) {
                sb.add((char) i);
            }
        }
        for (int i = 0x3131; i <= 0x3163; i++) {
            sb.add((char) i);
        }
        for (char s: sb) {
            if (s != '\n') {
                appendTree(stringToBufferedImage("" + s, false), _treeFinal, 0, 0, s);
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
            sb.append('m');
            generate(n.zero, sb);
            sb.append('0');
        }
        if (n.one != null) {
            sb.append('n');
            generate(n.one, sb);
            sb.append('0');
        }
    }

    private void appendTree(BufferedImage img, Node node, int x, int y, char result) {
        if (x == img.getWidth()) {
            node.key = result;
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

    public String readString(BufferedImage img, HashSet<Integer> color) {
        if (!hasColor(img, color)) {
            return "";
        }
        /** Lazy image search for 0, n, m because they are used in fonttree.txt. */
        try {
            BufferedImage zero = ImageIO.read(new File("0.png"));
            BufferedImage n = ImageIO.read(new File("n.png"));
            BufferedImage m = ImageIO.read(new File("m.png"));
            for (int i = 0; i < img.getWidth(); i++) {
                String result_zero = readString_imageSearch(img, zero, color, "0", i);
                String result_n = readString_imageSearch(img, n, color, "n", i);
                String result_m = readString_imageSearch(img, m, color, "m", i);
                if (!result_zero.isEmpty()) {
                    return result_zero;
                }
                if (!result_n.isEmpty()) {
                    return result_n;
                }
                if (!result_m.isEmpty()) {
                    return result_m;
                }
            }
        } catch (IOException e) {
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

    public char exactSearch(BufferedImage img) {
        int x = 0;
        int y = 0;
        Node tree = _treeFinal;
        while (x != img.getWidth()) {
            if (img.getRGB(x, y) == black) {
                tree = tree.one;
                if (y == img.getHeight() - 1) {
                    x += 1;
                    y = 0;
                } else {
                    y += 1;
                }
            } else {
                tree = tree.zero;
                if (y == img.getHeight() - 1) {
                    x += 1;
                    y = 0;
                } else {
                    y += 1;
                }
            }
        }
        return tree.key;
    }

    public BufferedImage stringToBufferedImage(String s, boolean isBold) {
        java.awt.image.BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = img.getGraphics();
        Font f;
        if (isBold) {
            f = new Font("돋움", 1, 11);
        } else {
            f = new Font("돋움", Font.PLAIN, 11);
        }
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Simple GUI for the execution of the program without command prompt. */
public class GUI extends JFrame {

    public GUI() {
        JFrame frame = new JFrame("Kalpago");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Start");
        listener l= new listener();
        button.addActionListener(l);
        JTextField textbox = new JTextField("Your Character Name",16);
        JTextField textboxPort = new JTextField("Arduino Port Num. Example: 6",16);
        textField = new JTextArea("Please press Start");
        JPanel p = new JPanel();
        p.add(textbox);
        p.add(textboxPort);
        p.add(button);
        p.add(textField);
        frame.add(p);
        frame.setVisible(true);
        try {
            while (!l.pressed) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myName = textbox.getText();
        portName = textboxPort.getText();
    }

    class listener implements ActionListener {
        public volatile boolean pressed = false;

        public void actionPerformed(ActionEvent e) {
            pressed = true;
        }
    }

    public void editText(String text) {
        textField.setText(text);
    }

    public String getPortName() {
        return portName;
    }

    public String getMyName() {
        return myName;
    }

    private JTextArea textField;
    private String myName = "";
    private String portName = "";
}

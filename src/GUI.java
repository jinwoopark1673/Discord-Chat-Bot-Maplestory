import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Simple GUI for the execution of the program without command prompt. */
public class GUI extends JFrame {

    public GUI(String name, String characterName, String portNum, String adminPeriod) {
        super(name);
        JFrame frame = this;
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Start");
        listener l= new listener();
        button.addActionListener(l);
        JLabel characterNameTextArea = new JLabel("Name: ");
        JTextField textbox;
        if (characterName.isEmpty()) {
            textbox = new JTextField("Your Character Name",20);
        } else {
            textbox = new JTextField(characterName,20);
        }
        JLabel portNumTextArea = new JLabel("Port: ");
        JTextField textboxPort;
        if (portNum.isEmpty()) {
            textboxPort = new JTextField("Arduino Port Num. Ex: 6",21);
        } else {
            textboxPort = new JTextField(portNum,21);
        }
        JLabel adminMsgPeriodTextArea= new JLabel("Admin Msg Period: ");
        JTextField adminMsgPeriod;
        if (adminPeriod.isEmpty()) {
            adminMsgPeriod = new JTextField("Msg Period (Min). Ex, 20",14);
        } else {
            adminMsgPeriod = new JTextField(adminPeriod,14);
        }
        textField = new JLabel("Please press Start");
        JPanel p = new JPanel();
        p.add(characterNameTextArea);
        p.add(textbox);
        p.add(portNumTextArea);
        p.add(textboxPort);
        p.add(adminMsgPeriodTextArea);
        p.add(adminMsgPeriod);
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
        button.setEnabled(false);
        myName = textbox.getText();
        portName = textboxPort.getText();
        period = Integer.parseInt(adminMsgPeriod.getText());
        userSettings = new String[] {myName, portName, adminMsgPeriod.getText()};
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

    public int getPeriod() {
        return period;
    }

    public String[] getUserSettings() {
        return userSettings;
    }

    private JLabel textField;
    private String myName = "";
    private String portName = "";
    private int period;
    private String[] userSettings;
}

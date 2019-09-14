import com.fazecast.jSerialComm.SerialPort;

/** Responsible for setting up the SerialPort connection and writing and reading from it. */
public class jscomm {

    public jscomm(String portName) {
        for (SerialPort c : SerialPort.getCommPorts()) {
            if (c.getSystemPortName().equals(portName)) {
                comPort = c;
            }
        }
        if (comPort == null) {
            System.exit(1);
        }
        comPort.openPort();
    }

    public void write(int b) {
        comPort.writeBytes(new byte[]{(byte) b}, 1);
    }

    public void writeWait(int b) {
        if (b >= 0 && b < 256) {
            if (comPort.bytesAvailable() != 0) {
                comPort.readBytes(new byte[comPort.bytesAvailable()], comPort.bytesAvailable());
            }
            comPort.writeBytes(new byte[]{(byte) b}, 1);
            long currentTime = System.currentTimeMillis();
            boolean timedOut = false;
            try {
                while (comPort.bytesAvailable() == 0) {
                    if (System.currentTimeMillis() - currentTime > 5000) {
                        timedOut = true;
                        break;
                    }
                    Thread.sleep(50);
                }
                if (timedOut) {
                    System.out.println("Timed out");
                    System.exit(1);
                } else {
                    comPort.readBytes(new byte[comPort.bytesAvailable()], comPort.bytesAvailable());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SerialPort comPort;
}


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Side");
    private JTextPane messageShowArea = new JTextPane();
    private StyledDocument doc;
    private JTextField messageTextArea = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Server() throws IOException {
        server = new ServerSocket(7775);
        System.out.println("Server is ready to accept connection...");
        socket = server.accept();
        System.out.println("Connection established.");

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        createGUI();
        handleEvents();
        startReading();
    }

    public void createGUI() {
        this.setTitle("Server Messenger");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageTextArea.setFont(font);
        messageShowArea.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        heading.setIcon(new ImageIcon("images5.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        messageTextArea.setHorizontalAlignment(SwingConstants.CENTER);
        messageTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageShowArea.setEditable(false);
        doc = messageShowArea.getStyledDocument();

        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);

        JScrollPane jScrollPane = new JScrollPane(messageShowArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(messageTextArea, BorderLayout.SOUTH);
        this.add(jScrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public void handleEvents() {
        messageTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String msg = messageTextArea.getText().trim();
                    if (!msg.isEmpty()) {
                        appendMessage("Me", msg, true); // Left-aligned (server's own message)
                        out.println(msg);
                        out.flush();
                        messageTextArea.setText("");

                        if (msg.equalsIgnoreCase("exit")) {
                            try {
                                JOptionPane.showMessageDialog(null, "You terminated the chat");
                                messageTextArea.setEnabled(false);
                                socket.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    messageTextArea.requestFocus();
                }
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (!socket.isClosed()) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        messageTextArea.setEnabled(false);
                        socket.close();
                        break;
                    }
                    appendMessage("Client", msg, false); // Right-aligned
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start();
    }

    private void appendMessage(String sender, String message, boolean leftAlign) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrSet, leftAlign ? StyleConstants.ALIGN_LEFT : StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(attrSet, 16);
        StyleConstants.setForeground(attrSet, Color.BLACK);
        StyleConstants.setSpaceAbove(attrSet, 5);
        StyleConstants.setSpaceBelow(attrSet, 5);
        StyleConstants.setBold(attrSet, true);

        try {
            doc.setParagraphAttributes(doc.getLength(), 1, attrSet, false);
            doc.insertString(doc.getLength(), sender + ": " + message + "\n", attrSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("This is the server...");
        new Server();
    }
}

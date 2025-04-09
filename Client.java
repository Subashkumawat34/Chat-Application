import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading=new JLabel("Client Side");
    private JTextArea messageShowArea=new JTextArea();
    private JTextField messageTextArea=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);

    public Client() throws IOException {
        System.out.println("sending request to server");
        socket=new Socket("127.0.0.1",7775);
        System.out.println("connection done.");

        br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out=new PrintWriter(socket.getOutputStream());

        createGUI();
        handleEvents();
        startReading();
//        startWriting();


    }
    public void createGUI(){
        this.setTitle("Client Messanger");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for components
        heading.setFont(font);
        messageTextArea.setFont(font);
        messageShowArea.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageShowArea.setEditable(false);
        heading.setIcon(new ImageIcon("images5.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        messageTextArea.setHorizontalAlignment(SwingConstants.CENTER);
        messageTextArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);

        JScrollPane jScrollPane=new JScrollPane(messageShowArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(messageTextArea,BorderLayout.SOUTH);
        this.add(jScrollPane,BorderLayout.CENTER);

        this.setVisible(true);
    }
    public void handleEvents(){
        messageTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {

                if(e.getKeyCode()==10){
                    String msg=messageTextArea.getText();
                    messageShowArea.append("Me : "+msg+"\n");
                    out.println(msg);
                    out.flush();
                    messageTextArea.setText("");
                    if(msg.equals("exit")){
                        try {
                            JOptionPane.showMessageDialog(null,"I terminated the chat");
                            messageTextArea.setEnabled(false);
                            socket.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    messageTextArea.requestFocus();
                }
            }
        });
    }


    public void startReading(){
        Runnable r1=()->{
            System.out.println("reader started");
            try {
                while (!socket.isClosed()) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        JOptionPane.showMessageDialog(this,"Server terminated the chat");
                        messageTextArea.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageShowArea.append("Server : " + msg+ "\n");
                }
            }catch(Exception e){
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("this is client...");
        new Client();
    }
}

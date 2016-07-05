import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @Date   : 2016. 7. 3.
 */

/**
 * @author : GT
 *
 */
public class ClientEx extends JFrame implements ActionListener {
	private Socket socket = null;

	private BufferedReader in = null;
	private BufferedWriter out = null;

	private Receiver receiver = null;

	private JScrollPane scrollPane = null;

	public ClientEx() {
		super("ClientEx");
		this.setSize(500, 500);
		Container c = this.getContentPane();

		receiver = new Receiver();

		scrollPane = new JScrollPane(receiver);
		c.add(scrollPane, BorderLayout.CENTER);

		JTextField textField = new JTextField();
		c.add(textField, BorderLayout.SOUTH);
		textField.addActionListener(this);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

		this.tryToConnect();

		Thread th = new Thread(receiver);
		th.start();
	}

	private String getTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("[aa hh:mm:ss] ");
		String time = sdf.format(calendar.getTime());

		return time;
	}

	private void tryToConnect() {
		try {
			socket = new Socket(InetAddress.getByName("localhost"), ServerEx.PORT_NUM); //
			receiver.append(getTime() + "서버에 접속되었습니다." + "\n");

			setBuffered();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}

	private void setBuffered() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JTextField tf = (JTextField) e.getSource();
		String sendMsg = tf.getText();
		try {
			// @빈문자 입력시@
			out.write(sendMsg + "\n");
			out.flush();

			receiver.append(getTime() + "나> " + sendMsg + "\n");
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

			tf.setText("");
		} catch (IOException e1) {
		}
	}

	class Receiver extends JTextArea implements Runnable {

		public Receiver() {
			this.setEditable(false);
		}

		@Override
		public void run() {
			while (true) {
				try {
					String receiveMsg = in.readLine();
					this.append(getTime() + "상대> " + receiveMsg + "\n");
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
				} catch (IOException e) {
				}
			}
		}

	}

	public static void main(String[] args) {
		new ClientEx();

	}
}

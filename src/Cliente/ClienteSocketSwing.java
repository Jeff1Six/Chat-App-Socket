package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteSocketSwing extends JFrame {

	private Date datatual;
	private JTextArea taEditor = new JTextArea("Digite sua mensagem");
	private JTextArea taVisor = new JTextArea();
	private JList liUsuarios = new JList();
	private PrintWriter escritor;
	private BufferedReader leitor;
	private JScrollPane scrollTaVisor = new JScrollPane(taVisor);

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");


	public ClienteSocketSwing(){
		setTitle("Chat com Sockets");
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		liUsuarios.setBackground(Color.lightGray);
		taEditor.setBackground(Color.gray);


		taEditor.setPreferredSize(new Dimension(400,40));
		taVisor.setEditable(false);
		liUsuarios.setPreferredSize(new Dimension(100,140));


		add(taEditor , BorderLayout.SOUTH);
		add(scrollTaVisor, BorderLayout.CENTER);
		add(new JScrollPane(liUsuarios), BorderLayout.WEST);


		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		iniciarEscritor();
		String[] usuarios = new String[]{};
		preencherListadeUsuarios(usuarios);

	}

	private void preencherListadeUsuarios(String[] usuarios) {
		DefaultListModel modelo = new DefaultListModel();
		liUsuarios.setModel(modelo);
		for (String usuario : usuarios){
			modelo.addElement(usuario);
		}
	}

	private void iniciarEscritor() {
		taEditor.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					if (taVisor.getText().isEmpty()) {
						return;
					}
					Object usuario = liUsuarios.getSelectedValue();
					if(usuario != null){
						datatual = new Date();
						taVisor.append("[ " + sdf.format(datatual)+ " ] EU: ");
						taVisor.append(taEditor.getText());
						taVisor.append("\n");

						escritor.println(Comandos.MENSAGEM + usuario);
						escritor.println(taEditor.getText());

						e.consume();
					}else {
						if(taVisor.getText().equalsIgnoreCase(Comandos.SAIR)){
							System.exit(0);
						}
						JOptionPane.showMessageDialog(ClienteSocketSwing.this, "Selecione um usuario");
						return ;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
	}



	public void iniciarChat() {
		try {
			final Socket cliente = new Socket ("127.0.107" , 8084);
			escritor = new PrintWriter(cliente.getOutputStream(), true);
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
		}catch (UnknownHostException e) {
			System.out.println("O endereco passado e invalido");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println("Servico Indisponivel");
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ClienteSocketSwing cliente = new ClienteSocketSwing();
		cliente.iniciarChat();
		cliente.iniciarEscritor();
		cliente.iniciarLeitor();
		cliente.atualizarListaUsuarios();


	}

	private void atualizarListaUsuarios() {
		escritor.println(Comandos.LISTA_USUARIOS);
	}

	private void iniciarLeitor() {
		try {
			while(true) {
				String mensagem = leitor.readLine();
				if(mensagem == null || mensagem.isEmpty())
					continue;
				//recebe o texto
				if(mensagem.equals(Comandos.LISTA_USUARIOS)){
					String[] usuarios = leitor.readLine().split(",");
					preencherListadeUsuarios(usuarios);
				}else if (mensagem.equals(Comandos.LOGIN)){
					String login = JOptionPane.showInputDialog("Qual o seu login ? ");
					escritor.println(login);
				}else if (mensagem.equals(Comandos.LOGIN_NEGADO)){
					JOptionPane.showMessageDialog(ClienteSocketSwing.this, "O login e invalido");
				}else if (mensagem.equals(Comandos.LOGIN_ACEITO)){
					atualizarListaUsuarios();
				}else{
					taVisor.append(mensagem);
					taVisor.append("\n");
					taVisor.setCaretPosition(taVisor.getDocument().getLength());
				}
			}
		} catch (IOException e) {
			System.out.println("Nao e possivel ler a mensagem do servidor");
			e.printStackTrace();
		}
	}

	private DefaultListModel getListaUsuarios() {
		return (DefaultListModel) liUsuarios.getModel();
	}

}

import db.DB;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServidorSocket {


	public static PreparedStatement st;
	public static Connection conn;
	public static ResultSet rs;



	public static void main(String[] args) {

		ServerSocket servidor = null;

		try {
			System.out.println("Iniciando o servidor");
			servidor = new ServerSocket(8084);
			System.out.println("Servidor iniciado");
			while(true) {
				Socket cliente = servidor.accept();
				new GerenciadorDeClientes(cliente);
			}
			
			
		} catch (IOException e) {
			System.err.println("Deu erro na conexao ! Servidor finalizado !");
			try {
				if(servidor != null)
					servidor.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

}

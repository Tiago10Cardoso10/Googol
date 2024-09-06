package hello_callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Ficheiro que representa o(s) Cliente(s).
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public class Cliente extends UnicastRemoteObject implements IClient {
	/**
	 * Ligação à gateway através de RMI.
	 */
	private IGateway h;

	/**
	 * Conecta-se à gateway.
	 * @throws RemoteException
	 */
	public Cliente() throws RemoteException {
		super();
	}

	/**
	 * Conecta o cliente à gateway e verifica a opção escolhido pelo mesmo. Caso saia, a conexão é encerrada.
	 */
	/*
	public String run(int escolha) {
		String resposta;
		try {
			System.out.println("Client Conectado - Selecionou a opcão " + escolha + "\n");

			switch (escolha) {
				case 0:
					return "\n\n\n  -> OBRIGADO! A fechar................";
				case 1:
					enviar_url();
					break;
				case 2:
					enviar_palavra();
					break;
				case 3:
					status();
					break;
				case 4:
					procura_link();
					break;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "Erro no menu do Cliente";
	}
	*/

	/*
	public void run(int escolha) {
		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Client\n");
			while (true) {
				//Scanner scanner = new Scanner(System.in);
				//int escolha;
				do {
					menu();
					System.out.print("Escolha uma opção:");
					// Verifica se a entrada é um número inteiro

					while (!scanner.hasNextInt()) {
						System.out.print("Opção inválida. Por favor, selecione novamente: ");
						scanner.next(); // Limpa o buffer de entrada
					}
					escolha = scanner.nextInt();

					if (escolha > 4 || escolha < 0) {
						System.out.println("Opção escolhida inválida. Selecione novamente.");
					}
					System.out.println();
				} while (escolha != 0 && (escolha > 4 || escolha < 1));
				if (escolha == 0) {
					System.out.println("\n\n\n  -> OBRIGADO! A fechar................");
					break;
				}
				switch (escolha) {
					case 1:
						enviar_url();
						break;
					case 2:
						enviar_palavra();
						break;
					case 3:
						status();
						break;
					case 4:
						procura_link();
						break;
				}
			}
		} catch (Exception ex) {
		}
		System.exit(0);
	}*/

	/**
	 * Imprime no output as opções de escolha.
	 */
	/*
	public void menu() {
		System.out.println("----- Menu -----");
		System.out.println(" 1-> Indexar novo URL");
		System.out.println(" 2-> Páginas que contenham um conjunto de termos ordenados por importância");
		System.out.println(" 3-> Página de Administração");
		System.out.println(" 4-> Pesquisa página - Recebe links da mesma");
		System.out.println(" 0-> Sair");
	}*/

	/**
	 * Envia o url para a gateway para que este possa ser indexado.
	 * @throws RemoteException
	 */
	public String enviar_url(String mensagem,IGateway h) throws RemoteException {
		/*
		System.out.print("\n> URL:");
		String a;
		Scanner sc = new Scanner(System.in);
		a = sc.nextLine();
		*/
		try {
			Document doc = Jsoup.connect(mensagem).get();
			h.pesquisa(mensagem);
		} catch (Exception e) {
			return "Link inválido.";
		}
		return "Link enviado com sucesso";
	}

	/**
	 * Envia a(s) palavra(s) para a gateway, e devolve os links gerados pela(s) mesma(s).
	 * @throws RemoteException
	 */
	public String enviar_palavra(String mensagem,IGateway h) throws RemoteException {
		/*
		System.out.print("\n> Palavra:");
		Scanner sc = new Scanner(System.in);
		String a = sc.nextLine();
		*/


		String palavras = h.procura_palavra(mensagem);
		/*
		System.out.println("Palavra Pesquisada:" + a);
		if(!palavras.equals("")){
			palavras = palavras.replace("[", "").replace("]", "");
			String[] pedidos = palavras.split(", ");
			int grupoAtual = 0;
			int totalGrupos = (int) Math.ceil((double) pedidos.length / 10);
			while (grupoAtual < totalGrupos) {
				int inicio = grupoAtual * 10;
				int fim = Math.min((grupoAtual + 1) * 10, pedidos.length);
				for (int i = inicio; i < fim; i++) {
					System.out.println(pedidos[i]);
				}
				System.out.println("Pressione Enter para ver o próximo grupo...");
				sc.nextLine();
				grupoAtual++;
			}
		} else {
			System.out.println("Não existem ocorrencias de " + a);
		}
		System.out.println();
		 */

		if(!palavras.equals("")){
			String[] words = palavras.split(", ");

			// Usar um HashSet para remover duplicatas
			Set<String> uniqueWords = new HashSet<>();
			for (String word : words) {
				uniqueWords.add(word);
			}

			// Construir a string sem duplicatas
			StringBuilder result = new StringBuilder();
			for (String word : uniqueWords) {
				if (result.length() > 0) {
					result.append(", ");
				}
				result.append(word);
			}
			return result.toString();
		} else {
			return "Nao existem ocorrencias de " + mensagem;
		}

	}

	/**
	 * Envia o link pedido pelo cliente para a gateway, e devolve os links ligados a este.
	 * @throws IOException
	 */
	public String procura_link(String mensagem,IGateway h) throws IOException {
		/*
		System.out.print("\n> Link:");
		Scanner sc = new Scanner(System.in);
		String a = sc.nextLine();
		*/
		try {
			Document doc = Jsoup.connect(mensagem).get();
			String palavras = h.procura_link(mensagem);

			/*
			System.out.println("Link Pesquisado: " + a);
			if(!palavras.equals("")){
				String[] pedidos = palavras.split(", ");
				int grupoAtual = 0;
				int totalGrupos = (int) Math.ceil((double) pedidos.length / 10);
				while (grupoAtual < totalGrupos) {
					int inicio = grupoAtual * 10;
					int fim = Math.min((grupoAtual + 1) * 10, pedidos.length);
					for (int i = inicio; i < fim; i++) {
						System.out.println(pedidos[i]);
					}
					System.out.println("Pressione Enter para ver o próximo grupo...");
					sc.nextLine();
					grupoAtual++;
				}
			} else {
				System.out.println("O link ainda não foi visitado sendo ele: " + a);
			}
			*/
			if(!palavras.equals("")){

				return palavras;
			} else {
				return "Nao existem ocorrencias do link " + mensagem;
			}

		} catch (Exception e) {
			return "Nao e possivel aceder ao link " + mensagem;
		}

	}

	/**
	 * Devolve as estatísticas atuais do motor de busca(NºClientes, NºDownloaders, Links Visitados).
	 * @throws RemoteException
	 */
	public void status(String mensagem) throws RemoteException {

		System.out.print("\n> Verificar:");
		Scanner sc = new Scanner(System.in);
		String a = sc.nextLine();
		a.toLowerCase();
		while (!a.equals("sair")){
			if (a.equals("clientes")) {
				int nr = h.nr_clientes();
				System.out.println("Existem " + nr + " Clientes ativos no momento");
				break;
			} else if(a.equals("downloaders")){
				int nr = h.nr_downloaders();
				System.out.println("Existem " + nr + " Downloaders ativos no momento");
				break;
			} else if(a.equals("links visitados")){
				int nr = h.nr_links();
				System.out.println("Existem " + nr + " Links já visitados,sendo eles:");
				String links = h.mostra_linksV();
				String[] pedidos = links.split(", ");
				for (int i = 0; i < pedidos.length; i++) {
					System.out.println(pedidos[i]);
				}
				break;
			} else {
				System.out.println("Não foi possivel ver status de " + a );
				System.out.println("Na próxima tente usar: clientes/downloaders/links visitados/sair");
				break;
			}
		}
		System.out.println();
	}

	/**
	 * Cria um novo cliente quando executada.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Cliente c = new Cliente();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

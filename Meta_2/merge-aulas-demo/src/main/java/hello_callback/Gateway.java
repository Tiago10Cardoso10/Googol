package hello_callback;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Ficheiro que representa a Gateway.
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public class Gateway extends UnicastRemoteObject implements IGateway {
	/**
	 * Ligação aos Barrels através de RMI.
	 */
	private IBarrels b;
	/**
	 * Lista que contém os clientes.
	 */
	public ArrayList<IClient> clientes;

	public HashMap<String,Integer> top_pesquisas = new HashMap<>();
	/**
	 * Lista que contém os Downloaders.
	 */
	public ArrayList<IDownloaders> downloaders;
	/**
	 * Lista que contém os urls para análise.
	 */
	public ArrayList<String> urls;
	/**
	 * Lista de urls que já foram visitados.
	 */
	public HashSet<String> urls_visitados;

	/**
	 * Cria a ligação da gateway.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public Gateway() throws RemoteException, NotBoundException {
		super();
		clientes = new ArrayList<>();
		downloaders = new ArrayList<>();

		urls = new ArrayList<>();
		urls_visitados = new HashSet<>();


		LocateRegistry.createRegistry(1099);

		try {
			Naming.rebind("XPTO", (IGateway) this);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		run();
	}

	public void addString(String teste) {
		if (top_pesquisas.containsKey(teste)) {
			int count = top_pesquisas.get(teste);
			top_pesquisas.put(teste, count + 1);
		} else {
			top_pesquisas.put(teste, 1);
		}
	}

	// Método para obter o top 10 palavras mais frequentes
	public String getTop10Words() {
		// Cria uma lista a partir das entradas do mapa
		List<Map.Entry<String, Integer>> entryList = new ArrayList<>(top_pesquisas.entrySet());

		// Ordena a lista pelas contagens em ordem decrescente
		entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

		// StringBuilder para construir a string do top 10
		StringBuilder top10 = new StringBuilder();
		int count = 0;

		// Adiciona as 10 palavras mais frequentes à string
		for (Map.Entry<String, Integer> entry : entryList) {
			if (count == 10) {
				break;
			}
			if (count > 0) {
				top10.append(", ");
			}
			top10.append(entry.getKey() + "-" + entry.getValue());
			count++;
		}
		
		return top10.toString();
	}


	/**
	 * Conecta-se ao Barrel, caso não seja possível o sistema não inicia.
	 * @throws RemoteException
	 */
	public void run() throws RemoteException {
		String a;
		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Hello Server ready.\n");
			b = (IBarrels) LocateRegistry.getRegistry(8000).lookup("XPTOA");
			adiciona_liks(b.links_visitados());
		} catch (Exception re) {
			System.out.println("Não foi possivel conectar ao Barrel. Certifique-se que existe 1 ativo\n");
			System.exit(0);
		}
	}

	/**
	 * Adiciona os urls já visitados quando o sistema inicia.
	 * @param urls
	 * @throws RemoteException
	 */
	public void adiciona_liks(String urls) throws RemoteException {
		if(urls.equals("")){
		} else {
			urls = urls.replace("[", "").replace("]", "");
			String[] pedidos = urls.split(", ");
			for(int i=0; i < pedidos.length;i++){
				if(verifica_url(pedidos[i]) == 1){
					urls_visitados.add(pedidos[i]);
				}
			}
		}
	}

	/**
	 * Verifica o tamanho da lista urls_visitados.
	 * @return Tamanho da lista.
	 * @throws RemoteException
	 */
	public Integer nr_links() throws RemoteException {
		if(urls_visitados.isEmpty()){
			return 0;
		} else {
			return urls_visitados.size();
		}
	}

	/**
	 * Guarda os links já visitados.
	 * @return String com os links já visitados.
	 * @throws RemoteException
	 */
	public String mostra_linksV() throws RemoteException{
		StringBuilder resultado = new StringBuilder();
		for (String link :urls_visitados ) {
			resultado.append(link + ", ");
		}
		return resultado.toString();
	}

	/**
	 * Verifica se o url já foi visitado, caso não tenha sido adiciona à lista de urls para analisar.
	 * @param s
	 * @throws RemoteException
	 */
	public void pesquisa(String s) throws RemoteException {
		if(verifica_url(s) == 0){
			System.out.println("O URL " + s + " já foi visitado");
		} else {
			urls.add(s);
		}
	}

	/**
	 * Informa que o cliente se conectou e adiciona-o à lista de clientes.
	 * @param c
	 * @throws RemoteException
	 */
	public void subscribe(IClient c) throws RemoteException {
		System.out.print("Client subscribed\n");
		clientes.add(c);
	}

	/**
	 * Verifica o nº de clientes na lista.
	 * @return Nº de clientes.
	 * @throws RemoteException
	 */
	public Integer nr_clientes() throws RemoteException {
		return clientes.size();
	}


	/**
	 * Informa que o downloader se conectou e adiciona-o à lista de downloaders.
	 * @param d
	 * @throws RemoteException
	 */
	public void downloader_active (IDownloaders d) throws RemoteException {
		System.out.print("Downloader active\n");
		downloaders.add(d);
	}

	/**
	 * Verifica o nº de downloaders na lista.
	 * @return Nº de downloaders.
	 * @throws RemoteException
	 */
	public Integer nr_downloaders() throws RemoteException {
		return downloaders.size();
	}



	/**
	 * Verifica se a lista de urls a ser analisados está vazia.
	 * @return True or False.
	 * @throws RemoteException
	 */
	public boolean existe_url() throws RemoteException{
		if(urls.isEmpty()){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Adiciona os novos urls à lista de urls a ser analisados.
	 * @param lista_elementos
	 * @throws RemoteException
	 */
	public void separa_lista(String lista_elementos) throws RemoteException {
		String[] partes = lista_elementos.split("\\|");
		for (String parte : partes) {
			String url = parte.trim();
			urls.add(url);
		}
	}

	/**
	 * Verifica se o url já está na lista de urls_visitados.
	 * @param s
	 * @return True or False.
	 * @throws RemoteException
	 */
	public int verifica_url(String s) throws RemoteException {
		if(urls_visitados.contains(s)){
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Envia o url a ser analisado para o Downloader até encontrar um disponível.
	 * @throws RemoteException
	 */
	public void enviar_url () throws RemoteException {
		boolean urlEnviada = false;
		while (!urlEnviada) {
			if(!downloaders.isEmpty()) {
				for (IDownloaders downloader : downloaders) {
					try {
						int i = 0;
						while (downloaders.get(i).quantos_urls() != 0) {
							if ((i + 1) < downloaders.size()) {
								downloader = downloaders.get(i + 1);
								i++;
							}
							//System.out.println("Downloader ocupado. Tentando próximo downloader...");
						}
						if (!urls.isEmpty()) {
							String temp = urls.get(0);
							urls.remove(0);
							if (verifica_url(temp) == 1) {
								if (temp.startsWith("http")) {
									//System.out.println("Link enviado para Downloader -" + temp);
									String lista_elementos = downloader.recebe_url(temp);
									separa_lista(lista_elementos);
								}
								urls_visitados.add(temp);
							}
						}
						urlEnviada = true;
						break;

					} catch (RemoteException e) {
						// Se houver uma exceção RemoteException, significa que o downloader está ocupado.
						// Portanto, tentamos enviar a URL para o próximo downloader.
						System.out.println("Downloader indisponível. Tentando próximo downloader...");
					}
				}
			} else {
				System.out.println("Não existem Downloaders Conectados");
			}
			// Verificar se estão ocupados ou n existem
			if (!urlEnviada) {
				System.out.println("Todos os downloaders estão ocupados. Aguardando...");
				try {
					// Aguarda 1 segundo antes de verificar novamente
					Thread.sleep(1000);
					enviar_url();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Envia para o Barrel a(s) palavra(s) pedida(s) pelo Cliente.
	 * @param s
	 * @return String com os urls da(s) palavra(s).
	 * @throws RemoteException
	 */
	public String procura_palavra(String s) throws RemoteException {
		StringBuilder resultado = new StringBuilder();
		resultado.append(b.ver_palavra(s));
		return resultado.toString();
	}

	/**
	 * Envia para o Barrel o url pedido pelo Cliente.
	 * @param s
	 * @return String com os urls ligados ao url pedido.
	 * @throws RemoteException
	 */
	public String procura_link(String s) throws RemoteException {
		StringBuilder resultado = new StringBuilder();
		resultado.append(b.ver_link(s));
		return resultado.toString();
	}

	/**
	 * Cria a gateway quando executada.
	 * @param args
	 */
	public static void main(String args[])  {
		try {
			Gateway h = new Gateway();
		} catch (RemoteException | NotBoundException e) {
			throw new RuntimeException(e);
		}
	}
}

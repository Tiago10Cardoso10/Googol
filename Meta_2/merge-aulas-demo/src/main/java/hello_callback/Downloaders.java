package hello_callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Ficheiro que representa o(s) Downloader(s)(Webcrawler).
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public class Downloaders extends UnicastRemoteObject implements IDownloaders {
    /**
     * Ip usado no Multicast.
     */
    public String MULTICAST_ADDRESS = "224.3.2.1";
    /**
     * Porta usada na conexão Multicast.
     */
    public int PORT = 4321;
    /**
     * Ligação à gateway através de RMI.
     */
    private IGateway h;
    /**
     * Contém o url que está a ser analisado.
     */
    public ArrayList<String> meus_urls;
    /**
     * Contém os urls do link a ser analisado que não são repetidos.
     */
    public ArrayList<String> todos_urls;

    /**
     * Conecta-se à gateway.
     * @throws RemoteException
     */
    Downloaders() throws RemoteException {
        super();
        meus_urls = new ArrayList<>();
        todos_urls = new ArrayList<>();
        try {
            h = (IGateway) LocateRegistry.getRegistry(1099).lookup("XPTO");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        run();

    }

    /**
     * Conecta-se à gateway e verifica se existem urls disponíveis para analisar no dicionário da mesma.
     */
    public void run() {
        String a;
        try (Scanner sc = new Scanner(System.in)) {
            // subscribe on gateway
            h.downloader_active((IDownloaders) this);
            System.out.println("Downloader\n");
            while (true){
                existe();
            }
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    /**
     * Verifica se existem urls novos no dicionário da gateway.
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void existe() throws RemoteException, InterruptedException {
        while(h.existe_url()){
            h.enviar_url();
        }
    }

    /**
     * Calcula o número de urls existentes no dicionário do downloader.
     * @return Nº de urls no dicionário do Downloader.
     * @throws RemoteException
     */
    public int quantos_urls() throws RemoteException{
        return meus_urls.size();
    }

    /**
     * Recebe urls para analisar através do jsoup.
     * @param s
     * @return Lista de urls para adicionar ao dicionário na gateway, ou null.
     * @throws RemoteException
     */
    public String recebe_url(String s) throws RemoteException {
        if(s.startsWith("http")) {
            System.out.println("Recebi link: " + s);
            meus_urls.add(s);
            String lista = palavras(s);
            lista = separa_lista(lista,s);
            return lista;
        }
        return null;
    }

    /**
     * Analisa o url com o método jsoup.
     * @param s
     * @return Lista de palavras:url, link:url.
     * @throws RemoteException
     */
    public String palavras(String s) throws RemoteException {
        StringBuilder resultado = new StringBuilder();
        try {
            Document doc = Jsoup.connect(s).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());
            while (tokens.hasMoreElements()) {
                String elemento = tokens.nextToken().toLowerCase();
                resultado.append(elemento).append(" : ").append(s).append(" | ");
            }
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String url = link.attr("abs:href");
                if (url.startsWith("http")) {
                    if(verifica_url(url) == 1){
                        todos_urls.add(url);
                        resultado.append(url).append(" | ");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Não é possivel aceder ao link.");
        }
        meus_urls.remove(s);
        todos_urls.clear();
        return resultado.toString();
    }

    /**
     * Separa a string devolvida pelo método jsoup.
     * @param lista_elementos
     * @param link
     * @return Novos urls para adicionar na gateway.
     * @throws RemoteException
     */
    public String separa_lista(String lista_elementos, String link) throws RemoteException {
        String[] palavrasEUrls = lista_elementos.split(" \\| ");
        StringBuilder links_urls = new StringBuilder();
        for (int i = 0; i < palavrasEUrls.length; i++) {
            String[] elem_url = palavrasEUrls[i].split(" : ");
            if (elem_url.length >= 2) {
                String palavra = elem_url[0].trim();
                String url = elem_url[1];
                String elemento = palavra + " : " + url;
                envia_string(elemento);
            } else {
                String elemento = elem_url[0] + " : " + link;
                envia_string(elemento);
                links_urls.append(elem_url[0]).append(" | ");
            }
        }
        return links_urls.toString();
    }

    /**
     * Verifica se o url já existe na lista de urls repetidos do Downloader.
     * @param s
     * @return True or False.
     */
    public int verifica_url(String s){
        if(todos_urls.contains(s)){
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Envia através de Multicast os resultados do método jsoup para os Barrels.
     * @param s
     * @throws RemoteException
     */
    public void envia_string(String s) throws RemoteException{
            try (MulticastSocket socket = new MulticastSocket()) {
                InetAddress mcastaddr = InetAddress.getByName(MULTICAST_ADDRESS);
                byte[] buffer = s.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mcastaddr, PORT);
                socket.send(packet);
            } catch (IOException var9) {
            }
    }

    /**
     * Cria um novo Downloader quando executada.
     * @param args
     */
    public static void main(String args[]) {
        /*
         * System.getProperties().put("java.security.pDownloadersolicy", "policy.all");
         * System.setSecurityManager(new RMISecurityManager());
         */
        try {
            Downloaders d = new Downloaders();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}





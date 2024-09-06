package hello_callback;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * Ficheiro que representa o(s) Barrel(s).
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public class Barrels extends Thread implements IBarrels, Serializable {

    /**
     * Ip usado no Multicast.
     */
    private String MULTICAST_ADDRESS = "224.3.2.1";
    /**
     * Porta usada na conexão Multicast.
     */
    private int PORT = 4321;
    /**
     * Lista que contém palavra/links:urls que a contém.
     */
    public HashMap<String, ArrayList<String>> dicionario;
    /**
     * Cópia do dicionario.
     */
    public HashMap<String, ArrayList<String>> dicionario2 = new HashMap<>();;
    /**
     * Cria a ligação do(s) Barrel(s).
     * @throws RemoteException
     */
    public Barrels() throws RemoteException {
        super();
        dicionario = new HashMap<>();
        try {
            Registry r = LocateRegistry.createRegistry(8000);
            r.rebind("XPTOA", this);
            run();
        } catch (IOException e) {
            run();
        }
    }

    /**
     * Recebe informação através da conexão Multicast e adiciona ao dicionário e ao ficheiro de texto.
     */
    public void run() {
        MulticastSocket socket = null;
        long counter = 0;
        try {
            socket = new MulticastSocket(PORT);
            socket.setSoTimeout(50);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            System.out.println("Barrel Criado");
            le_ficheiro();
            if(!dicionario2.isEmpty()){
                dicionario.putAll(dicionario2);
                dicionario2.clear();
            }
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                    String[] elem_url = message.split(" : ");
                    String palavra = elem_url[0].trim();
                    String url = elem_url[1];
                    if (dicionario.containsKey(palavra)) {
                        // Se a chave existe, adiciona o valor à lista existente
                        ArrayList<String> listaExistente = dicionario.get(palavra);
                        if (!listaExistente.contains(url)) {
                            listaExistente.add(url);
                        }
                    } else {
                        // Se a chave não existe, cria uma nova lista e adiciona o valor a ela
                        ArrayList<String> novaLista = new ArrayList<>();
                        novaLista.add(url);
                        dicionario.put(palavra, novaLista);
                    }
                    // Adicionando ao arquivo sem reescrever
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt"))) {
                        for (Map.Entry<String, ArrayList<String>> entry : dicionario.entrySet()) {
                            String chave = entry.getKey();
                            ArrayList<String> valores = entry.getValue();
                            writer.write(chave + " : " + valores + "\n");
                        }
                    } catch (IOException e) {
                        System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
                    }
                } catch (SocketTimeoutException e) {
                    // Ignorando timeouts de socket
                }
                try {
                    sleep(0);
                } catch (InterruptedException e) {
                    // Ignorando interrupções
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    /**
     * Lê o ficheiro dados.txt e adiciona a informação no dicionario2.
     */

    public void le_ficheiro() {
        try (BufferedReader reader = new BufferedReader(new FileReader("dados.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    String[] partes = linha.split(" : ");
                    if (partes.length == 2) {
                        String chave = partes[0];
                        String valor = partes[1];
                        String conteudo = valor.substring(1, valor.length() - 1);
                        // Dividir a string com base na vírgula e remover espaços em branco
                        String[] array = conteudo.split(",\\s*");
                        // Converter array para ArrayList
                        ArrayList<String> listaUrls = new ArrayList<>(Arrays.asList(array));
                        dicionario2.put(chave,listaUrls);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica a existência da(s) palavra(s) no dicionario2.
     * @param s
     * @return Urls que contém a(s) palavra(s) pedida(s) pelo cliente.
     * @throws RemoteException
     */
    public String ver_palavra(String s) throws RemoteException {
        le_ficheiro();
        StringBuilder resultado = new StringBuilder();
        String[] pedidos = s.split(" ");
        HashMap<String,Integer> urls = new HashMap<>();
        HashMap<String,Integer> urls_nrU = new HashMap<>();
        ArrayList<Integer> ordenado_urls = new ArrayList<>();
        for (String pedido : pedidos) {
            if (dicionario2.containsKey(pedido)) {
                ArrayList<String> valores = dicionario2.get(pedido);
                for (String valor : valores) {
                    if (urls.containsKey(valor)) {
                        urls.put(valor, urls.get(valor) + 1);
                    } else {
                        urls.put(valor, 1);
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : urls.entrySet()) {
            String chave = entry.getKey();
            Integer valor = entry.getValue();
            if(valor == pedidos.length){
                urls_nrU.put(chave,nr_link(chave));
                ordenado_urls.add(nr_link(chave));
            }
        }
        Collections.sort(ordenado_urls, Collections.reverseOrder());
        for (Integer i : ordenado_urls) {
            for (Map.Entry<String, Integer> entry : urls_nrU.entrySet()) {
                String chave = entry.getKey();
                Integer valor = entry.getValue();
                if (valor.equals(i)) {
                    resultado.append(chave + ", ");
                }
            }
        }
        dicionario2.clear();
        return resultado.toString();
    }

    /**
     * Verifica a existência do url no dicionario2.
     * @param s
     * @return Urls ligados ao url pedido.
     * @throws RemoteException
     */
    public String ver_link(String s) throws RemoteException {
        le_ficheiro();
        StringBuilder resultado = new StringBuilder();
        for (Map.Entry<String, ArrayList<String>> entry : dicionario2.entrySet()) {
            String chave = entry.getKey();
            ArrayList<String> valores = entry.getValue();
            for (String valor : valores) {
                if (valor.contains(s) && chave.contains("http")) {
                        resultado.append(chave).append(", ");
                        break;
                }
            }
        }
        dicionario2.clear();
        return resultado.toString();
    }

    /**
     * Calcula o nº de urls que um url contém.
     * @param s
     * @return Nº de urls.
     * @throws RemoteException
     */
    public int nr_link(String s) throws RemoteException {
        le_ficheiro();
        int count = 0;
        if(!dicionario2.isEmpty()){
            for (Map.Entry<String, ArrayList<String>> entry : dicionario2.entrySet()) {
                String chave = entry.getKey();
                ArrayList<String> valores = entry.getValue();
                for (String valor : valores) {
                    if (valor.contains(s) && chave.contains("http")) {
                        count++;
                        break;
                    }
                }
            }
        }
        dicionario2.clear();
        return count;
    }

    /**
     * Adiciona os urls visitados guardados no ficheiro de texto ao dicionário de urls visitados da gateway.
     * @return Urls visitados anteriormente.
     * @throws RemoteException
     */
    public String links_visitados() throws RemoteException{
        le_ficheiro();
        StringBuilder resultado = new StringBuilder();
        ArrayList<String> urls_visitados = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : dicionario2.entrySet()) {
            ArrayList<String> valores = entry.getValue();
            for (String valor : valores) {
                if(!urls_visitados.contains(valor)){
                    urls_visitados.add(valor);
                }
            }
        }
        for (String ar: urls_visitados){
            resultado.append(ar + ", ");
        }
        dicionario2.clear();
        return resultado.toString();
    }

    /**
     * Cria o(s) Barrel(s) quando executada.
     * @param args
     * @throws RemoteException
     */
    public static void  main(String[] args) throws RemoteException {
        Barrels b = new Barrels();

        b.start();
    }
}

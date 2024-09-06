package hello_callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface da Gateway.
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public interface IGateway extends Remote {

    /**
     * Declaração da função.
     * @return Tamanho da lista.
     * @throws RemoteException
     */
    public Integer nr_links() throws RemoteException;

    /**
     * Declaração da função.
     * @return String com os links já visitados.
     * @throws RemoteException
     */
    public String mostra_linksV() throws RemoteException;

    /**
     * Declaração da função.
     * @param s
     * @throws java.rmi.RemoteException
     */
    public void pesquisa(String s) throws java.rmi.RemoteException;

    /**
     * Declaração da função.
     * @param client
     * @throws RemoteException
     */
    public void subscribe(IClient client) throws RemoteException;

    /**
     * Declaração da função.
     * @return Nº de clientes.
     * @throws RemoteException
     */
    public Integer nr_clientes() throws RemoteException;

    /**
     * Declaração da função.
     * @param downloaders
     * @throws RemoteException
     */
    public void downloader_active (IDownloaders downloaders) throws RemoteException;

    /**
     * Declaração da função.
     * @return Nº de downloaders.
     * @throws RemoteException
     */
    public Integer nr_downloaders() throws RemoteException;

    /**
     * Declaração da função.
     * @return True or False.
     * @throws RemoteException
     */
    public boolean existe_url() throws RemoteException;

    /**
     * Declaração da função.
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void enviar_url () throws RemoteException, InterruptedException;

    /**
     * Declaração da função.
     * @param s
     * @return String com os urls da(s) palavra(s).
     * @throws RemoteException
     */
    public String procura_palavra(String s) throws RemoteException;

    /**
     * Declaração da função.
     * @param s
     * @return String com os urls ligados ao url pedido.
     * @throws RemoteException
     */
    public String procura_link(String s) throws RemoteException;

    public void addString(String palava) throws RemoteException;
    public String getTop10Words() throws RemoteException;
}
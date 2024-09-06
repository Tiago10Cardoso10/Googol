package hello_callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface do(s) Downloader(s).
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public interface IDownloaders extends Remote {

    /** Declaração da função.
     * @return Nº de urls no dicionário do Downloader.
     * @throws java.rmi.RemoteException
     */
    public int quantos_urls() throws java.rmi.RemoteException;

    /**
     * Declaração da função.
     * @param s
     * @return Lista de urls para adicionar ao dicionário na gateway, ou null.
     * @throws java.rmi.RemoteException
     */
    public String recebe_url(String s) throws java.rmi.RemoteException;

    /**
     * Declaração da função.
     * @param s
     * @throws RemoteException
     */
    public void envia_string(String s) throws RemoteException;
}

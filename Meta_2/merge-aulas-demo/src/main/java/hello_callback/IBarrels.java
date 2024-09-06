package hello_callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface do(s) Barrel(s).
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */
public interface IBarrels extends Remote {
    /**
     * Declaração da função.
     * @param s
     * @return Urls que contém a(s) palavra(s) pedida(s) pelo cliente.
     * @throws RemoteException
     */
    public String ver_palavra(String s) throws RemoteException;

    /**
     * Declaração da função.
     * @param s
     * @return Urls ligados ao url pedido.
     * @throws RemoteException
     */
    public String ver_link(String s) throws RemoteException;

    /**
     * Declaração da função.
     * @return Urls visitados anteriormente.
     * @throws RemoteException
     */
    public String links_visitados() throws RemoteException;




}

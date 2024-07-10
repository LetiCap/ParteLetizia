package ClimateMonitoring;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;

public interface ServerInterface extends Remote {
    public boolean registrazione(String id, LinkedList<String> inserimenti) throws RemoteException;

    public void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite) throws RemoteException;

    public  boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili) throws RemoteException;

    public  LinkedList<String> mostraElementiDisponibili(String tabella, String centro, String nomeColonnaDoveRicercare, boolean ricercaLibera) throws RemoteException;

    public boolean login(String id, String password) throws RemoteException;

    public void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote) throws RemoteException;

    public void statisticaParametri(String parametroScelto, String elementoScelto) throws RemoteException;
}

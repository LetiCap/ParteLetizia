package ClimateMonitoring;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;

public interface ServerInterface extends Remote {
    boolean registrazione(String id, LinkedList<String> inserimenti) throws RemoteException;

    void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite) throws RemoteException;

     boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili) throws RemoteException;

      LinkedList<String> mostraElementiDisponibili(String tabella, String nomeColonnaDoveRicercare, boolean ricercaLibera) throws RemoteException;

    boolean login(String id, String password) throws RemoteException;

     void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote) throws RemoteException;

    void statisticaParametri( String elementoScelto) throws RemoteException;

    LinkedList<Result>ricercaTramiteNome(String nome)throws RemoteException;

    LinkedList<Result> ricercaTramiteStato(String statoAppartenenza)throws RemoteException;

    LinkedList<Result> cercaAreaGeograficaCoordinate(double latitudine, double longitudine)throws RemoteException;
}

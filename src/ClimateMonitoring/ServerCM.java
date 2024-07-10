package ClimateMonitoring;/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Map;

/*
public class Utente {

    private ClimateMonitoring.GestioneUtente ClimateMonitoring.GestioneUtente = new ClimateMonitoring.GestioneUtente();
    private ClimateMonitoring.GestioneCentri centro = new ClimateMonitoring.GestioneCentri();


    public void registrazione(String id, LinkedList<String> inserimenti) {
           if(ClimateMonitoring.GestioneUtente .controlloId(id)){
               ClimateMonitoring.GestioneUtente.RichiestaDatiPerRegistrazione(inserimenti);
           }
    }

    public void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite) {
           ClimateMonitoring.GestioneUtente.registraCentroAree(inserimenti,lonlatInserite);
    }



    public boolean richiestaInserimentoCentro(String centro) {
          return ClimateMonitoring.GestioneUtente.richiestaInserimentoCentro(centro);
    }

    public boolean login(String id, String password) {
           return ClimateMonitoring.GestioneUtente.login(id,password);
    }


    public void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote) {
           ClimateMonitoring.GestioneUtente.inserisciParametriClimatici(longlatScelta,  MappavaluNote);
    }


    public void statisticaParametri(String parametroScelto, String elementoScelto ) {
        centro.restitutoreMode(parametroScelto,elementoScelto);

    }

}


 */

public class ServerCM extends UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private GestioneUtente ute = new GestioneUtente();
    private GestioneCentri centro = new GestioneCentri();
    private DatabaseConnection db = new DatabaseConnection();


    public ServerCM() throws RemoteException {
        super();
    }

    @Override
    public synchronized boolean registrazione(String id, LinkedList<String> inserimenti) throws RemoteException{
        if (ute.controlloId(id)) {
            ute.RichiestaDatiPerRegistrazione(inserimenti);
            return true;
        }return false;
    }

    @Override
    public synchronized void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite)throws RemoteException {
        ute.registraCentroAree(inserimenti,lonlatInserite);
    }

    @Override
    public synchronized boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili)throws RemoteException {
        return ute.richiestaInserimentoCentro(centro,elementiDisponibili);
    }


    @Override
    public synchronized LinkedList<String> mostraElementiDisponibili(String tabella, String centro, String nomeColonnaDoveRicercare, boolean ricercaLibera){
        return db.mostraElementiDisponibili(tabella, centro, nomeColonnaDoveRicercare, ricercaLibera);
    }

    @Override
    public  synchronized boolean login(String id, String password)throws RemoteException {

        return ute.login(id,password);
    }

    @Override
    public synchronized void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote)throws RemoteException {
        ute.inserisciParametriClimatici(longlatScelta,  MappavaluNote);
    }

    @Override
    public synchronized void statisticaParametri(String parametroScelto, String elementoScelto)throws RemoteException {
        centro.restitutoreMode(parametroScelto,elementoScelto);

    }



    public static void main(String[] args) {
        try {
            ServerCM utente = new ServerCM();
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("Server", utente);
            System.out.println("Server running");
        } catch (RemoteException e) {
            System.out.println("Server creation failed");
            e.printStackTrace();
        }
    }

}

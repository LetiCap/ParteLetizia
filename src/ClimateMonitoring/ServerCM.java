/*Tahir Agalliu	753550 VA
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

    private GestioneUtente GestioneUtente = new GestioneUtente();
    private GestioneCentri centro = new GestioneCentri();


    public void registrazione(String id, LinkedList<String> inserimenti) {
           if(GestioneUtente .controlloId(id)){
               GestioneUtente.RichiestaDatiPerRegistrazione(inserimenti);
           }
    }

    public void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite) {
           GestioneUtente.registraCentroAree(inserimenti,lonlatInserite);
    }



    public boolean richiestaInserimentoCentro(String centro) {
          return GestioneUtente.richiestaInserimentoCentro(centro);
    }

    public boolean login(String id, String password) {
           return GestioneUtente.login(id,password);
    }


    public void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote) {
           GestioneUtente.inserisciParametriClimatici(longlatScelta,  MappavaluNote);
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


    public ServerCM() throws RemoteException {
        super();
    }

    @Override
    public synchronized void registrazione(String id, LinkedList<String> inserimenti) throws RemoteException{
        if (ute.controlloId(id)) {
            ute.RichiestaDatiPerRegistrazione(inserimenti);
        }
    }

    @Override
    public synchronized void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite)throws RemoteException {
        ute.registraCentroAree(inserimenti,lonlatInserite);
    }

    @Override
    public synchronized boolean richiestaInserimentoCentro(String centro)throws RemoteException {
        return ute.richiestaInserimentoCentro(centro);
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

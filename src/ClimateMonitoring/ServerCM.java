package ClimateMonitoring;/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;


public class ServerCM extends UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private GestioneUtente ute = new GestioneUtente();
    private GestioneCentri centro = new GestioneCentri();
    private DatabaseConnection db= new DatabaseConnection();
    private JFrame frame;
    private JTextField dbHostField;
    private JTextField dbUsernameField;
    private JPasswordField dbPasswordField;
    private static String[] NomiColonneParametriPAR = {"vento_val" ,"umidita_val", "precipitazioni_val" ,"pressione_val" ,"temperatura_val" ,"altitudineghiacchi_val" ,"massaghiacci_val" };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes","umidita_notes",
            "precipitazioni_notes" , "pressione_notes",
            "temperatura_notes",
            "altitudineghiacchi_notes","massaghiacci_notes" };


    public ServerCM() throws RemoteException {
        super();

        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Server Configuration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 200));
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        dbHostField = new JTextField("jdbc:postgresql://localhost:5432/");
        //provare a togliere utente e mettere sotto il settimg dellurl
        dbUsernameField = new JTextField("postgres");
        dbPasswordField = new JPasswordField("6313");

        panel.add(new JLabel("Database URL:"));
        panel.add(dbHostField);
        panel.add(new JLabel("Database Username:"));
        panel.add(dbUsernameField);
        panel.add(new JLabel("Database Password:"));
        panel.add(dbPasswordField);

        JButton saveButton = new JButton("Save Configuration");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dbUrl = dbHostField.getText().trim();
                String dbUsername = dbUsernameField.getText().trim();
                String dbPassword = new String(dbPasswordField.getPassword());

                db.setConnectionDetails(dbUrl, dbUsername, dbPassword);
                try {
                    db.connect();
                    db.setConnectionDetails("jdbc:postgresql://localhost:5432/climatemonitoring", dbUsername, dbPassword);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                startRMIServer();
                frame.setVisible(false);
            }
        });

        panel.add(saveButton);
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void startRMIServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(1097);
            registry.rebind("Server", this);
            System.out.println("Server CM started on RMI registry.");

            // Optionally: Connect to the database using the saved credentials


        } catch (RemoteException e) {
            System.err.println("Error starting the RMI server or connecting to database: " + e.getMessage());
        }
    }

    @Override
    public synchronized boolean registrazione(String id, LinkedList<String> inserimenti) throws RemoteException{
        if (ute.controlloId(id,db)) {
            ute.RichiestaDatiPerRegistrazione(inserimenti,db);
            return true;
        }return false;
    }

    @Override
    public synchronized void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite)throws RemoteException {
        ute.registraCentroAree(inserimenti,lonlatInserite, db);
    }

    @Override
    public synchronized boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili)throws RemoteException {
        return ute.richiestaInserimentoCentro(centro,elementiDisponibili,db);
    }


    @Override
    public synchronized LinkedList<String> mostraElementiDisponibili(String tabella,  String nomeColonnaDoveRicercare, boolean ricercaLibera){
        return db.mostraElementiDisponibili(tabella, nomeColonnaDoveRicercare, ricercaLibera);
    }

    @Override
    public  synchronized boolean login(String id, String password)throws RemoteException {
        return ute.login(id,password,db);
    }

    @Override
    public synchronized void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote)throws RemoteException {
        ute.inserisciParametriClimatici(longlatScelta,  MappavaluNote, db);
    }

    @Override
    public synchronized void statisticaParametri( String elementoScelto)throws RemoteException {
        centro.restitutoreMode(elementoScelto,db);

    }
    @Override
    public synchronized  LinkedList<Result>ricercaTramiteNome(String nome){
        return db.cercaAreaGeograficaNomeCitta(nome);
    }

    @Override
    public synchronized LinkedList<Result> ricercaTramiteStato(String statoAppartenenza) {
        return db.ricercaTramiteStato(statoAppartenenza);
    }

    @Override
    public LinkedList<Result> cercaAreaGeograficaCoordinate(double latitudine, double longitudine) {
        return db.cercaAreaGeograficaCoordinate(latitudine,longitudine);
    }
    @Override
    public String getNote(String cityName, String colonna){
        return db.getInfoCity(cityName,colonna,false);
    }
    @Override
    public String getModa(String cityName,String colonna){
        return db.getInfoCity(cityName,colonna,true);
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

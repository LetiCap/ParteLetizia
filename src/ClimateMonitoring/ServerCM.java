package ClimateMonitoring;
/*Tahir Agalliu	753550 VA
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


    /**
     * La classe <strong>ServerCM</strong> rappresenta il server per l'applicazione Climate Monitoring.
     * <p>
     * Questa classe implementa l'interfaccia {@link ServerInterface} e gestisce le operazioni di connessione
     * al database e le richieste dei client tramite RMI (Remote Method Invocation).
     * </p>
     * <p>
     * La classe estende {@link UnicastRemoteObject} e implementa {@link ServerInterface}.
     * </p>
     *
     * @see ServerInterface
     * @see DatabaseConnection
     * @see GestioneUtente
     * @see ClientCM
     * @author Letizia Capitanio
     * @author Tahir Agalliu
     */
    public class ServerCM extends UnicastRemoteObject implements ServerInterface {
        private static final long serialVersionUID = 1L;
        private GestioneUtente utente = new GestioneUtente();
        private DatabaseConnection db= new DatabaseConnection();
        private JFrame frame;
        private JTextField dbHostField;
        private JTextField dbUsernameField;
        private JPasswordField dbPasswordField;


        /**
         * Costruttore della classe <strong>ServerCM</strong>.
         * <p>
         * Inizializza il server e crea l'interfaccia grafica per la configurazione del database.
         * </p>
         *
         * @throws RemoteException Se si verifica un errore durante la creazione dell'oggetto remoto.
         */
        public ServerCM() throws RemoteException {
            super();
            initializeGUI();
        }


        /**
         * Inizializza l'interfaccia grafica e la prima connessione con il database.
         * <p>
         * Crea una finestra con campi di input per l'URL del database, il nome utente e la password, e un pulsante
         * per salvare la configurazione. All'azione di clic sul pulsante, salva i dettagli del database e avvia
         * il server RMI.
         * </p>
         */
        private void initializeGUI() {
            frame = new JFrame("Server Configuration");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(new Dimension(400, 200));
            frame.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            dbHostField = new JTextField("jdbc:postgresql://localhost:5432/");
            //provare a togliere utente e mettere sotto il setting dellurl
            dbUsernameField = new JTextField("postgres");
            dbPasswordField = new JPasswordField("");

            panel.add(new JLabel("Database URL:"));
            panel.add(dbHostField);
            panel.add(new JLabel("Database Username:"));
            panel.add(dbUsernameField);
            panel.add(new JLabel("Database Password:"));
            panel.add(dbPasswordField);

            JButton saveButton = new JButton("Save Configuration");
            saveButton.addActionListener(e -> {
                String dbUrl = dbHostField.getText().trim();
                String dbUsername = dbUsernameField.getText().trim();
                String dbPassword = new String(dbPasswordField.getPassword());

                if(dbUrl.isEmpty() || dbUsername.isEmpty() || dbPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Inserisci tutti i valori correttamente");
                }else{
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

        /**
         * Crea un registro RMI sulla porta 1099 e associa l'istanza del server al registro.
         * Se si verifica un errore durante l'avvio del server, viene stampato un messaggio di errore.
         *
         */
        private void startRMIServer() {
            try {
                Registry registry = LocateRegistry.createRegistry(1099);
                registry.rebind("Server", this);
                System.out.println("Server CM started on RMI registry.");
            } catch (RemoteException e) {
                System.err.println("Error starting the RMI server or connecting to database: " + e.getMessage());
            }
        }

        @Override
        public synchronized boolean registrazione(String id, LinkedList<String> inserimenti) throws RemoteException{
            if (utente.controlloId(id,db)) {
                utente.RichiestaDatiPerRegistrazione(inserimenti,db);
                return true;
            }return false;
        }

        @Override
        public synchronized void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite)throws RemoteException {
            utente.registraCentroAree(inserimenti,lonlatInserite, db);
        }

        @Override
        public synchronized boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili)throws RemoteException {
            return utente.richiestaInserimentoCentro(centro,elementiDisponibili,db);
        }


        @Override
        public synchronized LinkedList<String> mostraElementiDisponibili(String tabella,  String nomeColonnaDoveRicercare, boolean ricercaLibera){
            return db.mostraElementiDisponibili(tabella, nomeColonnaDoveRicercare, ricercaLibera);
        }

        @Override
        public  synchronized boolean login(String id, String password)throws RemoteException {
            return utente.login(id,password,db);
        }

        @Override
        public synchronized void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote)throws RemoteException {
            utente.inserisciParametriClimatici(longlatScelta,  MappavaluNote, db);}

        @Override
        public synchronized  LinkedList<Result>ricercaTramiteNome(String nome){return db.cercaAreaGeograficaNomeCitta(nome);}

        @Override
        public synchronized LinkedList<Result> ricercaTramiteStato(String statoAppartenenza) {return db.ricercaTramiteStato(statoAppartenenza);}

        @Override
        public LinkedList<Result> cercaAreaGeograficaCoordinate(double latitudine, double longitudine) {return db.cercaAreaGeograficaCoordinate(latitudine,longitudine);}

        @Override
        public String getNote(String cityName, String colonna){return db.getInfoCity(cityName,colonna,false);}

        @Override
        public String getModa(String cityName,String colonna){return db.getInfoCity(cityName,colonna,true);}



        public static void main(String[] args) {
            try {
                new ServerCM();
            } catch (RemoteException e) {
                System.out.println("Server creation failed");
                e.printStackTrace();
            }
        }

    }

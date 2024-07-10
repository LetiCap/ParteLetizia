/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/

import java.io.Serializable;
import java.util.*;
    /**
     *
     *La classe <strong>GestioneUtente</strong> si occupa degli aspetti relativi ai dati dell'utente.
     *Sia le fasi per registrarsi all'interno del programma <strong>Climate Monitoring</strong>,
     *sia le fasi per fare il login.
     *@author Letizia Capitanio
     */



public class GestioneUtente  implements Serializable {
    private String id;
    private String Password;
    private String NomeCognome;
    private String CodiceFiscale;
    private String Email;
    private GestioneCentri gestioneCentri;

    private final String[]  NomiColonneUtente = {"Password", "NomeCognome", "CodiceFiscale", "Email"};
    private final String[] NomiColonneCentro = {"Civico","CAP","Comune",  "Via/Piazza", "Provincia" };
    private final String[] OutputSchermoUtente = {"la Password", "Nome e Cognome", "il codice Fiscale", "l'Email"};
    private final String[] OutputSchermoCentro = {"il comune", " provincia sottoforma di sigla, ad esempio per Novara-->NO", "la via o la piazza", "il numero civico", "il CAP"};
    private String centro;
    private final Scanner scanner = new Scanner(System.in);
    private int quantitaAreeperilCentro;
    private final DatabaseConnection db = new DatabaseConnection();






    /*Metodo controllo password e id del login*/
    /**
     * Viene controllato che sia presente quella corrispondenza nel db <strong>OperatoriRegistrati</strong>.
     * @param id da controllare
     * @param password password associata all'id
     * @return true login effettuato, false rifiutato
     */

    protected boolean login(String id, String password) {


        // while (true) {
        // id = getInfoFromUser("user ID ");
        // String query = "SELECT .\"Userid\" FROM  WHERE .\"Userid\" = ('" + id + "')";
        //  ResultSet resultSet = statement.executeQuery(query);

        if (db.controlloSegiaPresente(id, "OperatoriRegistrati", "Userid")) {
            System.out.println("procedo con il login");
            // presente = false;
            this.id = id;
            // while (true) {
            //  password = getInfoFromUser("password ");
            //query = "SELECT * FROM OperatoriRegistrati WHERE OperatoriRegistrati.\"Userid\" = ('" + this.id + "') AND OperatoriRegistrati.\"Password\"=('" + password + "')";
            // resultSet = statement.executeQuery(query);
            String result = db.controlloPasswordUtente(this.id, password);
            if (result != null) {
                this.Password = password;
                this.centro = result;
                System.out.println("Accesso Effettuato");
                return true;
                // Uscita dal while controllo password
                // break;
            } else {
                System.out.println("Password incorretta");
                return false;
            }
            // }
            //uscita dal primo while di controllo id
            //  break;
        } else{
            System.out.println("Reinserire in quanto nessun id esistente.");
            return false;
         }
    }

    /*Metodo che verifica che l'id scelto in fase di registrazione sia gia presente oppure no*/

        /**
         * Metodo di controllo dell'id in fase di <strong>registrazione</strong>..
         * Viene controllato che <strong>non</strong> sia presente l'id nel db <strong>OperatoriRegistrati</strong>
         * @param id id da verificare
         * @return true se l'id non è gia presente, altrimenti false
         */
    protected boolean controlloId(String id) {
        // Connection connection = null;

        //    connection = DatabaseConnection.connect();
//            Statement statement = connection.createStatement();


      //  while (true) {
         //   String id = getInfoFromUser("user ID");

            //  String query = "SELECT OperatoriRegistrati.\"Userid\" FROM OperatoriRegistrati WHERE OperatoriRegistrati.\"Userid\" = ('" + id + "')";

                /*PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();*/

            //  ResultSet resultSet = statement.executeQuery(query);

            // Verifica se il ResultSet ha dei risultati
            if (db.controlloSegiaPresente(id, "OperatoriRegistrati", "Userid")) {
                // Se c'è almeno un record, significa che l'utente è già presente
                System.out.println("L'utente " + id + " è già presente. Riprovare");
                return false;
            } else {
                // Se non ci sono risultati, l'utente non è presente

                this.id = id;
                return true;
               // break;
                // scanner.close();
            }
      //  }

    }

    /*Metodo che richiede tutti i dati */
    /**
     *Metodo per la fase di <strong>registrazione</strong>.
     *È costituito da più metodi singoli. Usando questo metodo si facilita la parte di registrazione con un metodo solo.
     *Vengono passati i dati per la registrazione: Email, Codice Fiscale, Nome e Cognome
     * @param inserimenti lista con elementi da inserire associati all'utente
     */
    protected void RichiestaDatiPerRegistrazione(LinkedList<String> inserimenti)  {
        db.inserimentoinDB("OperatoriRegistrati", this.id, "Userid", null, null, "User");
        ControlloDatiPrimadiInserire( inserimenti,true);
        //richiestaInserimentoCentro();
    }


    /*Metodo a cui passi i dati dell'utente per la registrazione oppure per la registrazione del centro*/
    /**
     *Metodo per la fase di <strong>registrazione</strong>.
     *Metodo che passando i dati da inserire, gli inserisce al centro o all'utente
     * @param datiUtente è un booleano. se impostato su true, richiede i dati per l'utente, se false richiede i dati per registrare un nuovo centro
     * @param inserimenti lista con elementi da inserire
     */
    protected void ControlloDatiPrimadiInserire( LinkedList<String> inserimenti, boolean datiUtente) {
        String [] Nomicolonne;
      //  String [] OutputSchermo;
        if(datiUtente){
            Nomicolonne=NomiColonneUtente;
           // OutputSchermo=OutputSchermoUtente;
        }else{
            Nomicolonne=NomiColonneCentro;
            //OutputSchermo=OutputSchermoCentro;
        }
        Map<String, Object> dataMap = new HashMap<>();
        for (int i = 0; i < Nomicolonne.length; i++) {
           /* String input = getInfoFromUser(OutputSchermo[i]);

            if (Nomicolonne[i].equals("CodiceFiscale")) {
                while (input.length() != 16) {
                    System.out.println("Il numero di caratteri risulta insufficiente, si prega di inserire il codice fiscale > ");
                    input = scanner.nextLine();
                }
            }
            if (Nomicolonne[i].equals("Provincia")) {
                input = input.toUpperCase();
                while (input.length() != 2) {
                    System.out.println("Reinserire la provincia correttamente > ");
                    input = scanner.nextLine().toUpperCase();
                }
            }

            */
            dataMap.put(Nomicolonne[i], inserimenti.get(i));
        }
        if (datiUtente) {
            // Assegna i valori alle variabili della classe

            this.Password = dataMap.get("Password").toString();
            this.NomeCognome = dataMap.get("NomeCognome").toString();
            this.CodiceFiscale = dataMap.get("CodiceFiscale").toString();
            this.Email = dataMap.get("Email").toString();

            db.UpdateDataToDB(dataMap, "OperatoriRegistrati", this.id, "Userid");
        } else {
          //  String quantitaAreeString = inserimenti.getLast().toString();
           // this.quantitaAreeperilCentro = Integer.parseInt(quantitaAreeString);
            db.UpdateDataToDB(dataMap, "CentriMonitoraggio", this.centro, "NomeCentro");
        }
    }


    /*Metodo generico di richiesta dati. largomento viene immesso nella scritta che vedrà lutente insieme a inserisci*/
    /**
     *Metodo generale che richiede di inserire un dato. La stringa passata come parametro infulenza la scritta che vedrà l'utente.
     * Ad esempio il parametro inserito è Email, quindi questo metodo richiederà all'utente "inserisci Email >
     * @param  nomeDatodaInserire è la stringa che si aggiunge all'output "inserisci" per completare la richiesta.
     * @return il valore inserito da tastiera dall'utente, in formato <strong>String</strong>.
     */
/*
    protected String getInfoFromUser(String nomeDatodaInserire) {
        String inserimento;
        do {
            System.out.print("Inserisci " + nomeDatodaInserire + " > ");
               inserimento = scanner.nextLine();
            if (nomeDatodaInserire.isEmpty()) {
                System.out.println("Nessun inserimento... inserire nuovamente  ");
            }
        } while (inserimento.isEmpty());
        return inserimento;
    }
*/

    /*Metodo di richiesta specifica del centro. Vengono visualizzati i centri già registrati
     * l'utente puo scegliere tra quelli oppure registrarne uno nuovo*/

    /**
     * Metodo per la fase di <strong>registrazione</strong>.
     * Terzo metodo del metodo <strong>RichiestaDatiPerRegistrazione</strong>.
     * Vengono visualizzati i centri già registrati, se presenti. L'utente può sceglierne uno tra quelli presenti, oppure registrarne uno nuovo.
     * @param centro nome del centro a cui ci si vuole associare
     * @return true se il centro era già esistente, false se va creato
     */


    protected boolean richiestaInserimentoCentro(String centro)  {

        LinkedList<String> elementiDisponibili;
        String choice;
        elementiDisponibili = db.mostraElementiDisponibili("CentriMonitoraggio", null, "NomeCentro",false);
  /*      if (!elementiDisponibili.isEmpty()) {
            System.out.println("Lista centri disponibili :");
            for (String elemento : elementiDisponibili) {
                System.out.println(elemento);
            }
            System.out.print(" sceglierne uno oppure registrarne uno nuovo ? digitare 1 o 2 > ");
            choice = scanner.nextLine();
        } else {
            System.out.println("Nessun centro presente. Verrà registrato quello inserito");
            choice = "2";
        }
        */
        this.centro=centro;
       // if ((choice).equals("2")) {
         //   registraCentroAree();
        //} else if ((choice).equals("1")) {
         //   while(true){
          //      centro = getInfoFromUser("il nome del centro tra i nomi della lista");
                if(elementiDisponibili.contains(centro)){
                    db.UpdateDataToDB("NomeCentro", centro, id);
                    return true;
                   // break;
                }else{
                    db.inserimentoinDB("CentriMonitoraggio", centro, "NomeCentro", null, null, "centro");
                    db.UpdateDataToDB("NomeCentro", centro, id);
                    return false;
                    //far partire nella gui il metodo registracentroaree

            }
      //  }
    }

/*
    private void RichiestaDatidelCentrodaRegistrare() {
        ControlloDatiPrimadiInserire( false);
        gestioneCentri = new GestioneCentri();
        gestioneCentri.setCentroId(centro, id);
        gestioneCentri.inserimentoAree(quantitaAreeperilCentro);

    }

 */




    //metodo che fa scegliere le aree e fa inserire per il login

    /**
     * Metodo della sezione<strong>Login</strong>
     * Metodo che permette di inserire le valutazioni e le note, presenti nella Map, nell'area passata come argomento "longScelta"
     * Per poter utilizzare questo metodo è necessario non utilizzarlo da solo, ma soltanto in seguito ad aver richiamato il metodo <strong>login</strong>.
     * in alternativa usare direttamente il metodo <strong>Login</strong> che racchiude entrambi i metodi citati
     * @param longlatScelta
     * @param MappavaluNote
     */
    protected void inserisciParametriClimatici(String longlatScelta, Map<String, Object> MappavaluNote) {
        gestioneCentri = new GestioneCentri();
        gestioneCentri.setCentroId(centro, id);
        gestioneCentri.selezioneAreadiLavoroeInserimento(longlatScelta, MappavaluNote);

    }

    //metodo registrazione centro RICHIESTO
    //RegistrazioneNuovoCentroConRichiestaNomeCentro
    /**
     * metodo da usare dopo richiestaInserimentoCentro
     *Va ad inserire i dati passati come lista inserimenti al centro, e le aree passando le longitutidine e  latituidni di esse come lista
     * @param inserimenti dati del centro
     * @param lonlatInserite longitutidini e latituidini delle aree
     */
    /* fai visualizzare la lista di elementi
    modficare in modo che nella schermata ci sia un solo pulsante. prememndolo vieen fatto il controollo. se entra nell'if, fa ludate e restituisce true.
    se invece entra nellelse, restituisce false e dato che ce false, si apre una nuova schermata dove poi parte il metodo RichiestaDatidelCentrodaRegistrare
    in caso si faccia cosi, invertire i nomi dei metodi registracentroaree e richiestadatidelcentroda regsitrare
    richiestadatidelcentroda non è necessario che venga implememntato nel server*/
    protected void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite){
      //  centro = getInfoFromUser("il nome del centro");

        ControlloDatiPrimadiInserire( inserimenti,false);
       gestioneCentri = new GestioneCentri();
        gestioneCentri.setCentroId(centro, id);
      //  gestioneCentri.inserimentoAree(quantitaAreeperilCentro);
      gestioneCentri.inserimentoAree(lonlatInserite);


    }




    /*
    private boolean RichiestaDatidelCentrodaRegistrare(){
        String choice;
        while (true) {
            centro = getInfoFromUser("il nome del centro");
            if (db.controlloSegiaPresente(this.centro, "CentriMonitoraggio", "NomeCentro")) {
                System.out.println("Il Centro " + centro + " è già presente presente");
                db.UpdateDataToDB("NomeCentro", centro, id);
                return true;
            } else {


               return false;
            }
        }
    }



     */
}

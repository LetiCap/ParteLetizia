/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/

package ClimateMonitoring;


import java.io.Serializable;
import java.util.*;
    /**
     * La classe <strong>GestioneUtente</strong> gestisce le operazioni relative ai dati degli utenti all'interno del programma <strong>Climate Monitoring</strong>.
     * Questa classe include le funzionalità per la registrazione e il login degli utenti.
     * <p>
     * Le operazioni di registrazione includono la verifica della disponibilità dell'ID, l'inserimento dei dati dell'utente e l'associazione a un centro.
     * Le operazioni di login includono la verifica delle credenziali e la gestione dei parametri climatici.
     * </p>
     * @author Letizia Capitanio
     */

    public class GestioneUtente  implements Serializable {
        private String id;
        private GestioneCentri gestioneCentri;
        private final String[] NomiColonneUtente = {"Password", "NomeCognome", "CodiceFiscale", "Email"};
        private final String[] NomiColonneCentro = {"Civico", "CAP", "Comune", "Via/Piazza", "Provincia"};
        private String centro;


        /*Metodo controllo password e id del login*/

        /**
         * Verifica le credenziali dell'utente confrontando l'username e la password forniti con i dati presenti
         * nel database nella tabella <strong>OperatoriRegistrati</strong>.
         * <p>
         * Se l'username è trovato nel database, viene effettuato un ulteriore controllo per verificare se la password
         * corrisponde. In caso di successo, il metodo imposta il campo `centro` con il nome del centro associato all'utente
         * e ritorna {@code true}. Altrimenti, ritorna {@code false}.
         * </p>
         * @param id Username
         * @param password password associata all'username
         * @param db oggetto della classe DatabaseConnection
         * @return {@code true} se presente
         */
        protected boolean login(String id, String password, DatabaseConnection db) {
            if (db.controlloSegiaPresente(id, "OperatoriRegistrati", "Userid")) {
                System.out.println("nome presente, controllo password in corso..");
                this.id = id;
                String result = db.controlloPasswordUtente(this.id, password);
                if (result != null) {
                    this.centro = result;
                    System.out.println("Accesso Effettuato");
                    return true;
                    // Uscita dal while controllo password
                } else {
                    System.out.println("Password incorretta");
                    return false;
                }
            } else {
                System.out.println("Reinserire in quanto nessun id esistente.");
                return false;
            }
        }

        /*Metodo che verifica che l'id scelto in fase di registrazione sia gia presente oppure no*/

        /**
         * Metodo di controllo dell'id in fase di <strong>registrazione</strong>.
         * <p>
         * Viene controllato che <strong>non</strong> non sia presente l'id nel db <strong>OperatoriRegistrati</strong>*
         * </p>
         * @param id che si vuole registrare
         * @param db oggetto della classe DatabaseConnection
         * @return {@code true} se l'id non è gia presente, altrimenti {@code false}
         */
        protected boolean controlloId(String id, DatabaseConnection db) {
            // Verifica se il ResultSet ha dei risultati
            if (db.controlloSegiaPresente(id, "OperatoriRegistrati", "Userid")) {
                // Se c'è almeno un record, significa che l'utente è già presente
                System.out.println("L'utente " + id + " è già presente. Riprovare");
                return false;
            } else {
                // Se non ci sono risultati, l'utente non è presente
                this.id = id;
                return true;
            }
        }

        /*Metodo che richiede tutti i dati */

        /**
         * Metodo per la fase di <strong>registrazione</strong>.
         * È costituito da più metodi singoli. Usando questo metodo si facilita la parte di registrazione con un metodo solo.
         * Vengono passati i dati per la registrazione: Email, Codice Fiscale, Nome e Cognome attraverso la lista di Stringhe
         * @param inserimenti lista con elementi da inserire associati all'utente
         * @param db oggetto della classe DatabaseConnection
         * @author Letizia Capitanio
         */
        protected void RichiestaDatiPerRegistrazione(LinkedList<String> inserimenti, DatabaseConnection db) {
            db.inserimentoinDB("OperatoriRegistrati", this.id, "Userid", null, null, "User", null);
            InserimentoDatiAggiuntivi(inserimenti, true, db);

        }


        /*Metodo a cui passi i dati dell'utente per la registrazione oppure per la registrazione del centro*/

        /**
         * Metodo per la fase di <strong>registrazione</strong>.
         * Metodo che passando i dati da inserire, gli inserisce al centro o all'utente
         * @param datiUtente  è un booleano. se impostato su true, richiede i dati per l'utente, se false richiede i dati per registrare un nuovo centro
         * @param inserimenti lista con elementi da inserire
         * @param db oggetto della classe DatabaseConnection
         * @author Letizia Capitanio
         */
        protected void InserimentoDatiAggiuntivi(LinkedList<String> inserimenti, boolean datiUtente, DatabaseConnection db) {
            String[] Nomicolonne;
            if (datiUtente) {Nomicolonne = NomiColonneUtente;} else {Nomicolonne = NomiColonneCentro;}
            Map<String, Object> dataMap = new HashMap<>();
            for (int i = 0; i < Nomicolonne.length; i++) {
                dataMap.put(Nomicolonne[i], inserimenti.get(i));
            }
            if (datiUtente) {
                db.UpdateDataToDB(dataMap, "OperatoriRegistrati", this.id, "Userid");
            } else {
                db.UpdateDataToDB(dataMap, "CentriMonitoraggio", this.centro, "NomeCentro");
            }
        }

        /*Metodo di selezione,del centro tramite parametro. controllo se è presente tra quelli già registrati oppure no.
        * i primi dati del centro vengono registrati se nuovo*/

        /**
         * Metodo per la fase di <strong>registrazione</strong>.
         * Metodo che verifica se il centro a cui si vuole associare è già presente tra quelli registrati oppure no.
         * @param elementiDisponibili lista d centri già registrati
         * @param centro nome del centro a cui ci si vuole associare o registrare
         * @param db oggetto della classe DatabaseConnection
         * @return {@code true} se il centro era già esistente, {@code false} se va creato
         * @author Letizia Capitanio
         */
        protected boolean richiestaInserimentoCentro(String centro, LinkedList<String> elementiDisponibili, DatabaseConnection db) {
            this.centro = centro;
            if (elementiDisponibili.contains(centro)) {
                db.UpdateDataToDB( centro, id);
                return true;
            } else {
                db.inserimentoinDB("CentriMonitoraggio", centro, "NomeCentro", null, null, "centro", null);
                db.UpdateDataToDB( centro, id);
                return false;
            }
        }




        //metodo che fa scegliere le aree e fa inserire per il login

        /**
         * Metodo della sezione<strong>Login</strong>
         * <p>
         *     Metodo che permette di inserire le valutazioni e le note presenti nella Map, nell'area passata come argomento "longScelta"
         *     Per poter utilizzare questo metodo è necessario non utilizzarlo da solo, ma soltanto in seguito ad aver richiamato il metodo{@link #login(String, String, DatabaseConnection)}.
         * </p>
         * @param longlatScelta area scelta del proprio centro dove aggiungere valutazoni e note climatiche
         * @param mappaNoteValutazioni mappa con key con nome della colonna e value come valutazioni e note
         * @param db oggetto della classe DatabaseConnection
         * @author Letizia Capitanio
         */
        protected void inserisciParametriClimatici(String longlatScelta, Map<String, Object> mappaNoteValutazioni, DatabaseConnection db) {
            gestioneCentri = new GestioneCentri();
            gestioneCentri.setCentroId(centro, id);
            gestioneCentri.selezioneAreadiLavoroeInserimento(longlatScelta, mappaNoteValutazioni, db);

        }


        //inserisce i dati specifici del centro nuovo e registra le aree a se associate

        /**
         * metodo da usare dopo richiestaInserimentoCentro
         * Va ad inserire i dati specifici del centro, passati come lista, e le aree passando le longitutidine e  latituidni di esse come lista
         * @param inserimenti dati del centro
         * @param lonlatInserite nome delle aree associate al centro che si vogliono registrare
         * @param db oggetto della classe DatabaseConnection
         * @author Letizia Capitanio
         */
        protected void registraCentroAree(LinkedList<String> inserimenti, LinkedList<String> lonlatInserite, DatabaseConnection db) {
            InserimentoDatiAggiuntivi(inserimenti, false, db);
            gestioneCentri = new GestioneCentri();
            gestioneCentri.setCentroId(centro, id);
            gestioneCentri.inserimentoAree(lonlatInserite, db);
        }
    }

package ClimateMonitoring;
/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private String centro;

    public static void setConnectionDetails(String dbUrl, String dbUsername, String dbPassword) {
        URL = dbUrl;
        USERNAME = dbUsername;
        PASSWORD = dbPassword;
    }

    public static Connection connect() throws SQLException {
        if (URL == null || USERNAME == null || PASSWORD == null) {
            throw new SQLException("Database connection details are not set.");
        }

        try {
            createDatabase(); // Ensure the database exists
            Connection connection=  DriverManager.getConnection(URL, USERNAME, PASSWORD);

            return connection;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }

    private static boolean databaseExists() {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", USERNAME, PASSWORD)) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getCatalogs();
            while (rs.next()) {
                String dbName = rs.getString(1);
                System.out.println(dbName);
                if (dbName.equals("climatemonitoring")) {
                    System.out.println("caaz");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void createDatabase() throws SQLException {
        if (!databaseExists()) {
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", USERNAME, PASSWORD)) {
                String dbName = "climatemonitoring";
                try (Statement stmt = conn.createStatement()) {
                    String sql = "CREATE DATABASE " + dbName;
                    stmt.executeUpdate(sql);
                    System.out.println("Database created successfully");

                    executeTableCreationScript();
                }
            }
        }
    }

    private static void executeTableCreationScript() throws SQLException {
        Connection g= DriverManager.getConnection("jdbc:postgresql://localhost:5432/climatemonitoring", USERNAME, PASSWORD);
       // setConnectionDetails("jdbc:postgresql://localhost:5432/climatemonitoring",USERNAME,PASSWORD);
        try (InputStream scriptStream = DatabaseConnection.class.getResourceAsStream("/script");
             BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream));
             Statement statement = g.createStatement()) {

            StringBuilder script = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n"); // Aggiungi una nuova riga dopo ogni linea letta
            }

            // Dividi lo script in singole istruzioni SQL (separate da ";")
            String[] sqlStatements = script.toString().split(";");

            // Esegui ciascuna istruzione SQL
            for (String sql : sqlStatements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    statement.executeUpdate(trimmedSql);
                }
            }

            System.out.println("Script executed successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Other methods for interacting with the database, such as inserting data, querying, etc.




/**
     * Metodo che permette di inserire uno o piu valori contemporaneamente nella tabella passata come parametro
     * @param tabella nome della tabella dove inserire i dati
     * @param valore valore da inserire nella tabella
     * @param colonna colonna dove inserire il valore
     * @param valore2 valore da inserire nella tabella
     * @param colonna2 colonna dove inserire il valore 2
     * @param dato cosa visualizzare nella stampa di successo o insuccesso
     */
    protected void inserimentoinDB(String tabella, String valore, String colonna, String valore2, String colonna2, String dato, Map<String, Object> dataMap ) {
        Connection connection = null;
        try {
            connection = connect();

            String database="climatemonitoringdb";
            Statement statement = connection.createStatement();
            int esito;
            String query = null;
            switch (tabella) {
                case "CentriMonitoraggio", "OperatoriRegistrati":
                    query = "INSERT INTO \"" + tabella + "\" (\"" + colonna + "\") VALUES ('" + valore + "')";break;
                case "aree":
                    query = "INSERT INTO \"" + tabella + "\" (\"" + colonna + "\", \"" + colonna2 + "\") VALUES ('" + valore + "', '" + valore2 + "')";break;
                case "ParametriClimatici":
                    StringBuilder columns = new StringBuilder();
                    StringBuilder values = new StringBuilder();
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        if (!columns.isEmpty()) {
                            columns.append(", ");
                            values.append(", ");
                        }
                        columns.append("\"").append(entry.getKey()).append("\"");
                        values.append("'").append(entry.getValue()).append("'");
                    }

                    query = "INSERT INTO \"" + tabella + "\" (" + columns + ") VALUES (" + values + ")";
                    System.out.println("Eseguendo query: " + query);
                    break;

            }
            esito = statement.executeUpdate(query);

            if (esito == 1) {
                System.out.println("inserimento eseguito correttamente " + dato);
            }else
                System.out.println("inserimento non eseguito " + dato);
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            // Chiudi la connessione
            closeConnection(connection);
        }
    }

    /**
     *Metodo che aggiunge i dati presenti nella Map, alla tabella passata come argomento,
     *  nella colonna contenente l'argomento passato come valoreColonnaWhere.
     * @param dataMap Mappa contenente i valori con il nome della colonna dove vanno inseriti
     * @param tabella nome della tabella dove inserire i dati (update)
     * @param valoreColonnaWhere valore presente nella tabella, dove andare a inserire i dati, contenuti nella Mappa
     * @param nomeColonnaWhere nome della colonna in cui cercare per il WHERE
     */
    protected void UpdateDataToDB(Map<String, Object> dataMap, String tabella, String valoreColonnaWhere, String nomeColonnaWhere) {
        Connection connection = null;
        try {
            connection = connect();

            Statement statement = connection.createStatement();
            int esito;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String query = "UPDATE  \"" + tabella + "\" SET \"" + entry.getKey() + "\" = '" + entry.getValue() + "' WHERE \"" + nomeColonnaWhere + "\" = '" + valoreColonnaWhere + "'";
                System.out.println(query);
                esito = statement.executeUpdate(query);
                if (esito == 1)
                    System.out.println("inserimento eseguito correttamente ");
                else
                    System.out.println("inserimento non eseguito ");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     *Metodo che aggiunge "inserimento", nella "colonna", all'"id" alla tabella <strong>OperatoriRegostrati</strong>,
     * @param colonna nome della colonna dove si vuole aggiungere l'elemento
     * @param inserimento valore che si vuole aggiungere alla tabella
     * @param id valore dell'id presente nella tabella, dove andare a inserire il dato
     */
    protected void UpdateDataToDB(String colonna, String inserimento, String id) {
        Connection connection = null;
        try {
            connection = connect();

            Statement statement = connection.createStatement();
            int esito;

            String query = "UPDATE \"OperatoriRegistrati\" SET \"" + colonna + "\" = '" + inserimento + "' WHERE \"Userid\" = '" + id + "'";
            esito = statement.executeUpdate(query);
            System.out.println(query);
            if (esito == 1)
                System.out.println("inserimento eseguito correttamente ");
            else
                System.out.println("inserimento non eseguito ");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
    }


    /**
     * Metodo che restituisce se il valore passato come argomento, è presente nella colonna indicata nella tabella passata come argoemnto
     * @param valore Stringa da cercare nella tabella
     * @param tabella tabella dove cercare il valore
     * @param colonna nome della colonna dove cercare il valore
     * @return true se è già presente l'argomento valore. false se non è presente
     */
    protected boolean controlloSegiaPresente(String valore, String tabella, String colonna)  {
        Connection connection = null;
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            String query = "SELECT \"" + colonna + "\" FROM \"" + tabella + "\" WHERE \"" + colonna + "\" = '" + valore + "'";

            ResultSet resultSet = statement.executeQuery(query);
            System.out.println(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Metodo che serve in fase di login per verificare che la coppia id e password passati come argomento, siano presenti nella tabella <strong>OperatoriRegistrati</strong>
     * @param id id dell'utente teoricamente registrato
     * @param password password dell'id passato come argomento
     * @return in caso di utente registrato( quindi che il login abbia avuto successo) resitutisce il centro per cui lavora
     */
    public String controlloPasswordUtente(String id, String password)  {
        Connection connection = null;

        try {
            connection = connect();
            Statement statement = connection.createStatement();
            String query = "SELECT \"NomeCentro\" FROM \"OperatoriRegistrati\"  WHERE \"OperatoriRegistrati\".\"Userid\" = ('" + id + "') AND \"OperatoriRegistrati\".\"Password\"=('" + password + "')";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {


               System.out.println(this.centro = resultSet.getString("NomeCentro"));

            } else {
                // Nessun risultato trovato per l'id e la password specificati
                this.centro = null; // o qualsiasi valore di default desiderato
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
        return centro;
    }

    /**
     * Metodo che restituisce la lista degli elementi nella tabella passato come argomento.
     * Se si tratta della tabella <strong>CentroMonitoraggio</strong> non è necessario specificare l'argoemnto centro, ne il boolean. Va specificata come argomento "nomeColonnaDoveRicercare"
     * Se si tratta della tabella <strong>aree</strong> e si vuole restituire gli elementi di una colonna di un centro specifico, va specificato l'argoemnto "centro" e boolean come false
     * Se si tratta della tabella <strong>aree</strong> e si vogliono restituire i valori di una colonna, senza vincolo del centro, non va specificato quest'ultimo, ma va imposto come boolean true
     *
     * @param tabella                  nome della tabella dove estrarre i dati
     * @param nomeColonnaDoveRicercare nome della colonna di cui restituire i valori
     * @param ricercaLibera            impostare true se si tratta di una ricerca senza un WHERE, altrimenti false
     * @return lista dei valori della colonna
     */
    public  LinkedList<String> mostraElementiDisponibili(String tabella, String nomeColonnaDoveRicercare, boolean ricercaLibera) {
        Connection connection = null;
        LinkedList<String> listaElementi = new LinkedList<>();
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            String query = "";
            System.out.println("qui");
            switch (tabella) {
                case "CentriMonitoraggio":
                    query = "SELECT \"" + nomeColonnaDoveRicercare + "\" FROM \"" + tabella + "\"";break;
                case "aree":
                    if(ricercaLibera){
                        query = "SELECT \"" + nomeColonnaDoveRicercare + "\" FROM \"" + tabella + "\"";break;
                    }else
                        query = "SELECT  \"" + nomeColonnaDoveRicercare + "\" FROM  \"" + tabella + "\" WHERE \"NomeCentro\"='" + this.centro + "'";break;
            }
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                listaElementi.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
        return listaElementi;
    }

    /**
     * Metodo che restituisce in forma di Map le mode e le eventuali note di tutti i parametri, di un'area o di un centro in base all'argomento "campodiricerca",
     * dell'elemento dell'area o centro passato come argomento "parametroscelto"
     * @return map che restituisce le mode e le note
     */

    public Map<String, HashMap<String, String>>  RicercaMode(String parametroscelto, String[] nomiColonneParametriPAR, String[] nomiColonneParametriNOT) {
        Connection connection = null;
        String query;
        ResultSet resultSet;
        boolean stampatoIntestazione = false;
        Map<String, HashMap<String, String>> result = new HashMap<>();
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            for (String colonna : nomiColonneParametriPAR) {
                query = "SELECT " + colonna + ", COUNT(" + colonna + ") AS frequenza FROM \"ParametriClimatici\" WHERE  \"area\" = '" + parametroscelto + "'  GROUP BY " + colonna + " ORDER BY frequenza DESC LIMIT 1";
                resultSet = statement.executeQuery(query);
                HashMap<String, String> parResult = new HashMap<>();
                if (resultSet.next()) {

                    String valorePiuUsato = resultSet.getString(colonna);
                    if (valorePiuUsato != null) {
                        int frequenza = resultSet.getInt("frequenza");
                        //  System.out.println("Valore più usato in " + colonna + ": " + valorePiuUsato);
                        //   System.out.println("Frequenza: " + frequenza + "\n");
                        parResult.put("ValorePiuUsato", valorePiuUsato);
                        parResult.put("Frequenza", String.valueOf(frequenza));
                        result.put(colonna, parResult);
                    }
                }
            }
            for (String column : nomiColonneParametriNOT) {
                query = "SELECT \"" + column + "\" FROM \"ParametriClimatici\" WHERE  \"area\" = '" + parametroscelto + "'";
                resultSet = statement.executeQuery(query);
                HashMap<String, String> noteResult = new HashMap<>();
                LinkedList<String> listaNote = new LinkedList<>();
                // Stampa le note una sotto l'altra
                while (resultSet.next()) {
                    String note = resultSet.getString(column);
                    if (note != null) {
                        if (!stampatoIntestazione) {
                            //  System.out.println("Le note per " + column + " sono:");
                            stampatoIntestazione = true;
                        }
                        listaNote.add(note);
                        //  System.out.println("- " + note);
                    }
                }
                if (!listaNote.isEmpty()) {
                    noteResult.put("Note", listaNote.toString());
                    result.put(column, noteResult);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Chiudi la connessione
            DatabaseConnection.closeConnection(connection);
        }
        return result;

    }






    /**
     * Cerca aree geografiche basate sul <strong>nome della città</strong> fornito.
     * Inizialmente cerca una corrispondenza esatta con il nome della città. Se non trova risultati,
     * riduce gradualmente la lunghezza del nome della città e riprova la ricerca con il nome parziale.
     *
     * @param nomeCitta Il <strong>nome della città</strong> per cui cercare le aree geografiche.
     * @return Una lista di <strong>risultati</strong> che corrispondono al nome della città o ai nomi parziali della città.
     * @author Tahir Agalliu
     */
    public LinkedList<Result> cercaAreaGeograficaNomeCitta(String nomeCitta) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Connessione al database
            connection = connect();
            statement = connection.createStatement();

            // Query SQL iniziale per selezionare le aree geografiche con il nome della città esatto
            String query = "SELECT * FROM coordinatemonitoraggio WHERE name = '" + nomeCitta + "'";

            resultSet = statement.executeQuery(query);

            // Itera sui risultati della query e aggiungi i risultati alla lista
            while (resultSet.next()) {
                Result risultato = new Result(
                        resultSet.getInt("geoname"),
                        resultSet.getString("name"),
                        resultSet.getString("asciiname"),
                        resultSet.getString("countrycode"),
                        resultSet.getString("countryname"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")
                );
                risultati.add(risultato);
            }

            // Se non sono stati trovati risultati, riduci gradualmente la lunghezza del nome della città e riprova
            while (risultati.isEmpty() && nomeCitta.length() > 1) {
                // Riduci il nome della città di un carattere alla volta
                nomeCitta = nomeCitta.substring(0, nomeCitta.length() - 1);
                query = "SELECT * FROM coordinatemonitoraggio WHERE name LIKE '%" + nomeCitta + "%'";
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Result risultato = new Result(
                            resultSet.getInt("geoname"),
                            resultSet.getString("name"),
                            resultSet.getString("asciiname"),
                            resultSet.getString("countrycode"),
                            resultSet.getString("countryname"),
                            resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );
                    risultati.add(risultato);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestione delle eccezioni o errori di connessione al database
        } finally {
            // Chiusura delle risorse nel blocco finally per garantire che vengano sempre chiuse
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return risultati;
    }

    /**
     * Ricerca le aree geografiche basate sul <strong>nome dello stato</strong> di appartenenza fornito.
     * Prima cerca una corrispondenza esatta con il nome dello stato. Se non trova risultati,
     * riduce gradualmente la lunghezza del nome dello stato e riprova la ricerca con il nome parziale.
     *
     * @param statoAppartenenza Il <strong>nome dello stato</strong> per cui cercare le aree geografiche.
     * @return Una lista di <strong>risultati</strong> che corrispondono al nome dello stato o ai nomi parziali dello stato.
     * @author Tahir Agalliu
     */
    public LinkedList<Result> ricercaTramiteStato(String statoAppartenenza) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Connessione al database
            connection = connect();
            statement = connection.createStatement();

            // Query SQL iniziale per selezionare le aree geografiche con il nome dello stato esatto
            String query = "SELECT * FROM coordinatemonitoraggio WHERE countryname = '" + statoAppartenenza + "'";

            resultSet = statement.executeQuery(query);

            // Itera sui risultati della query e aggiungi i risultati alla lista
            while (resultSet.next()) {
                Result risultato = new Result(
                        resultSet.getInt("geoname"),
                        resultSet.getString("name"),
                        resultSet.getString("asciiname"),
                        resultSet.getString("countrycode"),
                        resultSet.getString("countryname"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")
                );
                risultati.add(risultato);
            }

            // Se non sono stati trovati risultati, riduci gradualmente la lunghezza del nome dello stato e riprova
            while (risultati.isEmpty() && statoAppartenenza.length() > 1) {
                // Riduci il nome dello stato di un carattere alla volta
                statoAppartenenza = statoAppartenenza.substring(0, statoAppartenenza.length() - 1);
                query = "SELECT * FROM coordinatemonitoraggio WHERE countryname LIKE '%" + statoAppartenenza + "%'";
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Result risultato = new Result(
                            resultSet.getInt("geoname"),
                            resultSet.getString("name"),
                            resultSet.getString("asciiname"),
                            resultSet.getString("countrycode"),
                            resultSet.getString("countryname"),
                            resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );
                    risultati.add(risultato);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestisci eventuali eccezioni o errori di connessione al database
        } finally {
            // Chiusura delle risorse nel blocco finally per garantire che vengano sempre chiuse
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return risultati;
    }

    /**
     * Cerca le aree geografiche in base alle <strong>coordinate di latitudine e longitudine</strong> fornite,
     * utilizzando vari intervalli di ricerca. Restituisce una lista di risultati che corrispondono alle coordinate
     * specificate. Se vengono trovati duplicati, restituisce solo il risultato del duplicato.
     *
     * @param latitudine La <strong>latitudine</strong> centrale per la ricerca.
     * @param longitudine La <strong>longitudine</strong> centrale per la ricerca.
     * @return Una lista di <strong>risultati</strong> che corrispondono alle coordinate specificate. Se ci sono duplicati, restituisce solo il risultato del duplicato.
     * @author Tahir Agalliu
     */
    public LinkedList<Result> cercaAreaGeograficaCoordinate(double latitudine, double longitudine) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        double[] range = {0.00001, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0}; // possibile anche aumento range di ricerca a scapito di precisione

        try {
            // Connessione al database
            connection = connect();
            statement = connection.createStatement();

            double latitudeMin, latitudeMax, longitudeMin, longitudeMax;
            for (int i = 0; i < range.length; i++) {
                // Calcola i limiti minimi e massimi per la ricerca in base all'intervallo corrente
                latitudeMin = latitudine - range[i];
                latitudeMax = latitudine + range[i];
                longitudeMin = longitudine - range[i];
                longitudeMax = longitudine + range[i];
                String query;

                // Costruzione della query basata sull'intervallo
                if (i == 0) {
                    query = "SELECT * FROM coordinatemonitoraggio WHERE latitude BETWEEN " + latitudeMin +
                            " AND " + latitudeMax + " AND longitude BETWEEN " + longitudeMin + " AND " + longitudeMax;
                } else {
                    query = "SELECT * FROM coordinatemonitoraggio WHERE latitude BETWEEN " + latitudeMin +
                            " AND " + latitudeMax + " OR longitude BETWEEN " + longitudeMin + " AND " + longitudeMax;
                }
                resultSet = statement.executeQuery(query);

                // Utilizza un Set per gestire i duplicati basati sul geoname
                Set<Integer> geonameSet = new HashSet<>();

                // Itera sui risultati della query
                while (resultSet.next()) {
                    int geoname = resultSet.getInt("geoname");
                    // Crea un oggetto Result per ogni riga
                    Result result = new Result(
                            geoname,
                            resultSet.getString("name"),
                            resultSet.getString("asciiname"),
                            resultSet.getString("countrycode"),
                            resultSet.getString("countryname"),
                            resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );

                    // Verifica se il geoname è già presente nel Set
                    if (!geonameSet.contains(geoname)) {
                        geonameSet.add(geoname);
                        risultati.add(result);
                    } else {
                        // Se troviamo un duplicato, restituiamo una lista con solo quel geoname
                        LinkedList<Result> risultatoSingolo = new LinkedList<>();
                        risultatoSingolo.add(result);
                        closeConnection(connection);
                        return risultatoSingolo;
                    }
                }

                // Se sono stati trovati risultati, restituiamo la lista completa
                if (!risultati.isEmpty()) {
                    return risultati;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestione delle eccezioni o errori di connessione al database
        } finally {
            // Chiusura delle risorse nel blocco finally per garantire che vengano sempre chiuse
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return risultati;
    }

    /**
     * Recupera informazioni climatiche per una città specificata dalla tabella <strong>"ParametriClimatici"</strong>.
     * Il tipo di informazione restituita dipende dal valore del parametro <strong>moda</strong>.
     *
     * @param cityName Il <strong>nome della città</strong> per cui recuperare le informazioni climatiche.
     * @param colonna Il <strong>nome della colonna</strong> da cui estrarre i dati. Se è {@code null} o vuoto, viene selezionata tutte le colonne.
     * @param moda Se {@code true}, calcola e restituisce la moda (valore più frequente) della colonna specificata.
     *             Se {@code false}, restituisce tutti i valori della colonna come una stringa separata da nuove righe.
     * @return Una <strong>stringa</strong> contenente le informazioni climatiche richieste. Se non ci sono informazioni, restituisce "<no info yet>".
     * @author Tahir Agalliu
     */
    public String getInfoCity(String cityName, String colonna, boolean moda) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        StringBuilder result = new StringBuilder();

        try {
            // Connessione al database
            connection = connect();
            statement = connection.createStatement();

            // Costruzione della query dinamicamente in base ai parametri
            if (moda) {
                // Costruzione della query per ottenere i valori della colonna specificata
                String query = "SELECT " + colonna +
                        " FROM \"ParametriClimatici\" " +
                        " WHERE area LIKE '%" + cityName + "%' ";

                List<Integer> valori = new ArrayList<>();
                resultSet = statement.executeQuery(query);

                // Estrazione dei valori e aggiunta alla lista se non nulli e diversi da zero
                while (resultSet.next()) {
                    int valore = resultSet.getInt(colonna);
                    if (!resultSet.wasNull() && valore != 0) {
                        valori.add(valore);
                    }
                }

                // Calcolo della moda
                Map<Integer, Integer> conteggi = new HashMap<>();
                for (int valore : valori) {
                    conteggi.put(valore, conteggi.getOrDefault(valore, 0) + 1);
                }

                int maxConteggio = 0;
                List<Integer> valoriPiuFrequenti = new ArrayList<>();

                // Trova i valori con il conteggio massimo
                for (Map.Entry<Integer, Integer> entry : conteggi.entrySet()) {
                    if (entry.getValue() > maxConteggio) {
                        maxConteggio = entry.getValue();
                        valoriPiuFrequenti.clear();
                        valoriPiuFrequenti.add(entry.getKey());
                    } else if (entry.getValue() == maxConteggio) {
                        valoriPiuFrequenti.add(entry.getKey());
                    }
                }

                // Gestione caso in cui non ci sono valori
                if (valoriPiuFrequenti.isEmpty()) {
                    return "<no info yet>";
                }

                // Seleziona un valore a caso tra i più frequenti
                Random random = new Random();
                int valorePiuFrequente = valoriPiuFrequenti.get(random.nextInt(valoriPiuFrequenti.size()));

                return "" + valorePiuFrequente;
            }

            // Costruzione della query per ottenere tutti i valori della colonna specificata
            StringBuilder queryBuilder = new StringBuilder("SELECT ");

            if (colonna != null && !colonna.isEmpty()) {
                queryBuilder.append("\"").append(colonna).append("\"");
            } else {
                queryBuilder.append("*"); // Se colonna è null o vuota, seleziona tutte le colonne
            }

            queryBuilder.append(" FROM \"ParametriClimatici\" WHERE area LIKE '%").append(cityName).append("%'");

            if (colonna != null && !colonna.isEmpty()) {
                queryBuilder.append(" AND \"").append(colonna).append("\" IS NOT NULL");
            }

            String query = queryBuilder.toString();
            resultSet = statement.executeQuery(query);

            // Estrazione dei valori e costruzione della stringa di risultato
            while (resultSet.next()) {
                if (!result.isEmpty()) {
                    result.append("\n");
                }
                String value = resultSet.getString(colonna);
                if (value == null) {
                    value = "<no info yet>";
                }
                result.append(value);
            }
        } catch (SQLException e) {
            // Stampa l'errore in caso di eccezione
            e.printStackTrace();
        } finally {
            try {
                // Chiusura delle risorse nel blocco finally per garantire che vengano sempre chiuse
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                closeConnection(connection);
            } catch (SQLException e) {
                // Stampa l'errore in caso di eccezione durante la chiusura delle risorse
                e.printStackTrace();
            }
        }

        // Gestione del caso in cui non ci sono risultati
        if (result.isEmpty()) {
            return "<no info yet>";
        }

        return result.toString();
    }
}
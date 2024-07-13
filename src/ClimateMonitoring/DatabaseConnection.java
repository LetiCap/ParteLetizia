package ClimateMonitoring;

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
                String dbName = "climatemonitoring111";
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
        setConnectionDetails("jdbc:postgresql://localhost:5432/climatemonitoring",USERNAME,PASSWORD);
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
    protected void inserimentoinDB(String tabella, String valore, String colonna, String valore2, String colonna2, String dato) {
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
                case "aree", "ParametriClimatici":
                    query = "INSERT INTO \"" + tabella + "\" (\"" + colonna + "\", \"" + colonna2 + "\") VALUES ('" + valore + "', '" + valore2 + "')";break;
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
        String centro = null;
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            String query = "SELECT \"NomeCentro\" FROM \"OperatoriRegistrati\"  WHERE \"OperatoriRegistrati\".\"Userid\" = ('" + id + "') AND \"OperatoriRegistrati\".\"Password\"=('" + password + "')";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {


                centro = resultSet.getString("NomeCentro");
            } else {
                // Nessun risultato trovato per l'id e la password specificati
                centro = null; // o qualsiasi valore di default desiderato
            }
            System.out.println("cazzi" + mostraElementiDisponibili("CentriMonitoraggio",null,"NomeCentro",true));
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
     * @param tabella nome della tabella dove estrarre i dati
     * @param centro nome del centro di cui vanno restituite le aree (può essere {@code null})
     * @param nomeColonnaDoveRicercare nome della colonna di cui restituire i valori
     * @param ricercaLibera impostare true se si tratta di una ricerca senza un WHERE, altrimenti false
     * @return lista dei valori della colonna
     */
    public  LinkedList<String> mostraElementiDisponibili(String tabella, String centro, String nomeColonnaDoveRicercare, boolean ricercaLibera) {
        Connection connection = null;
        LinkedList<String> listaElementi = new LinkedList<>();
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            String query = "";

            switch (tabella) {
                case "CentriMonitoraggio":
                    query = "SELECT \"" + nomeColonnaDoveRicercare + "\" FROM \"" + tabella + "\"";break;
                case "aree":
                    if(ricercaLibera){
                        query = "SELECT \"" + nomeColonnaDoveRicercare + "\" FROM \"" + tabella + "\"";break;
                    }else
                        query = "SELECT  \"" + nomeColonnaDoveRicercare + "\" FROM  \"" + tabella + "\" WHERE \"NomeCentro\"='" + centro + "'";break;
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
     * @param parametroscelto nome dell'area o del centro di cui si vogliono visualizzare le mode
     * @param nomiColonneParametriPAR nomi delle colonne delle valutazioni nella tabella <strong>ParametriClimatici</strong>
     * @param nomiColonneParametriNOT nomi delle colonne delle note nella tabella <strong>ParametriClimatici</strong>
     * @param campoDiricerca stringa centro o area
     * @return map che restituisce le mode e le note
     */
    public Map<String, HashMap<String, String>>  RicercaMode(String parametroscelto, String[] nomiColonneParametriPAR, String[] nomiColonneParametriNOT, String campoDiricerca) {
        Connection connection = null;
        String query;
        ResultSet resultSet;
        boolean stampatoIntestazione = false;
        Map<String, HashMap<String, String>> result = new HashMap<>();
        try {
            connection = connect();
            Statement statement = connection.createStatement();
            for (String colonna : nomiColonneParametriPAR) {
                query = "SELECT " + colonna + ", COUNT(" + colonna + ") AS frequenza FROM \"ParametriClimatici\" WHERE \"" + campoDiricerca + "\" = '" + parametroscelto + "'  GROUP BY " + colonna + " ORDER BY frequenza DESC LIMIT 1";
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
                query = "SELECT \"" + column + "\" FROM \"ParametriClimatici\" WHERE \"" + campoDiricerca + "\" = '" + parametroscelto + "'";
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




    public LinkedList<Result> cercaAreaGeograficaNomeCitta(String nomeCitta) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = connect();
            statement = connection.createStatement();

            // Query SQL iniziale per selezionare geoname dove name = nomeCitta
            String query = "SELECT * FROM CoordinateMonitoraggioDati WHERE name = '" + nomeCitta + "'";

            resultSet = statement.executeQuery(query);

            // Itera sui risultati della query e aggiungi i geoname alla lista risultati
            while (resultSet.next()) {
                Result risultato=new Result(resultSet.getInt("geoname"),resultSet.getString("name"),
                        resultSet.getString("asciiname"),resultSet.getString("countrycode")
                        ,resultSet.getString("countryname"), resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")
                );
                risultati.add(risultato);
            }

            // Se non ci sono risultati, riduci gradualmente la lunghezza di nomeCitta e riprova
            while (risultati.isEmpty() && nomeCitta.length() > 1) {
                nomeCitta = nomeCitta.substring(0, nomeCitta.length() - 1);
                query = "SELECT * FROM CoordinateMonitoraggioDati WHERE name LIKE '%" + nomeCitta + "%'";
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Result risultato=new Result(resultSet.getInt("geoname"),resultSet.getString("name"),
                            resultSet.getString("asciiname"),resultSet.getString("countrycode")
                            ,resultSet.getString("countryname"), resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );


                    risultati.add(risultato);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Gestisci eventuali eccezioni o errori di connessione al database
        } finally {
            // Chiudi resultSet, statement e connection nel blocco finally
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


    public LinkedList<Result> ricercaTramiteStato(String statoAppartenenza) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = connect();
            statement = connection.createStatement();

            // Query SQL iniziale per selezionare geoname dove name = nomeCitta
            String query = "SELECT * FROM CoordinateMonitoraggioDati WHERE countryname= '" + statoAppartenenza + "'";

            resultSet = statement.executeQuery(query);

            // Itera sui risultati della query e aggiungi i geoname alla lista risultati
            while (resultSet.next()) {
                Result risultato=new Result(resultSet.getInt("geoname"),resultSet.getString("name"),
                        resultSet.getString("asciiname"),resultSet.getString("countrycode")
                        ,resultSet.getString("countryname"), resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")
                );
                risultati.add(risultato);
            }

            // Se non ci sono risultati, riduci gradualmente la lunghezza di nomeCitta e riprova
            while (risultati.isEmpty() && statoAppartenenza.length() > 1) {
                statoAppartenenza = statoAppartenenza.substring(0, statoAppartenenza.length() - 1);
                query = "SELECT * FROM CoordinateMonitoraggioDati WHERE countryname LIKE '%" + statoAppartenenza + "%'";
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Result risultato=new Result(resultSet.getInt("geoname"),resultSet.getString("name"),
                            resultSet.getString("asciiname"),resultSet.getString("countrycode")
                            ,resultSet.getString("countryname"), resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );


                    risultati.add(risultato);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Gestisci eventuali eccezioni o errori di connessione al database
        } finally {
            // Chiudi resultSet, statement e connection nel blocco finally
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








    public LinkedList<Result> cercaAreaGeograficaCoordinate(double latitudine, double longitudine) {
        LinkedList<Result> risultati = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        double[] range = {0.00001, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0};

        try {
            connection = connect();
            statement = connection.createStatement();

            double latitudeMin, latitudeMax, longitudeMin, longitudeMax;
            for (int i = 0; i < range.length; i++) {
                // Applichiamo il range di ricerca
                latitudeMin = latitudine - range[i];
                latitudeMax = latitudine + range[i];
                longitudeMin = longitudine - range[i];
                longitudeMax = longitudine + range[i];
                String query;

                if (i == 0) {
                    query = "SELECT * FROM CoordinateMonitoraggioDati WHERE latitude BETWEEN " + latitudeMin +
                            " AND " + latitudeMax + " AND longitude BETWEEN " + longitudeMin + " AND " + longitudeMax;
                } else {
                    query = "SELECT * FROM CoordinateMonitoraggioDati WHERE latitude BETWEEN " + latitudeMin +
                            " AND " + latitudeMax + " OR longitude BETWEEN " + longitudeMin + " AND " + longitudeMax;
                }
                resultSet = statement.executeQuery(query);

                // Utilizziamo un Set per verificare i duplicati di geoname
                Set<Integer> geonameSet = new HashSet<>();

                // Itera sui risultati della query e aggiungi i Result alla lista risultati
                while (resultSet.next()) {
                    int geoname = resultSet.getInt("geoname");
                    // Creare un oggetto Result per ogni riga
                    Result result = new Result(geoname,resultSet.getString("name"),
                            resultSet.getString("asciiname"),resultSet.getString("countrycode")
                            ,resultSet.getString("countryname"), resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    );

                    // Verifica se il geoname è già presente nel Set
                    if (!geonameSet.contains(geoname)) {
                        geonameSet.add(geoname);
                        risultati.add(result);
                    } else {
                        // Se troviamo un duplicato, restituire una lista con solo quel geoname
                        LinkedList<Result> risultatoSingolo = new LinkedList<>();
                        risultatoSingolo.add(result);
                        closeConnection(connection);

                        return risultatoSingolo;
                    }
                }

                // Se abbiamo trovato risultati, restituiamo la lista completa
                if (!risultati.isEmpty()) {
                    return risultati;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestisci eventuali eccezioni o errori di connessione al database
        } finally {
            // Chiudi resultSet, statement e connection nel blocco finally
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

















}
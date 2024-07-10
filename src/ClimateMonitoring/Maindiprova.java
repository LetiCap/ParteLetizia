import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Maindiprova {

    public static void main(String[] args)  {


        Connection connect = null;
        try {
            new DatabaseConnection();
            connect = DatabaseConnection.connect();

            try {
                String[] elements = {"masella", "david masella", "1234567896", "dsc"};
                LinkedList<String> inserimenti = new LinkedList<>(Arrays.asList(elements));

                String[] centoelement = {"16","25635","grodel","riviera", "NO"};
                LinkedList<String> daticentro=  new LinkedList<>(Arrays.asList(centoelement));

                String[] longlat = {"15115 155", "2345"};
                LinkedList<String> lonlatInserite=  new LinkedList<>(Arrays.asList(longlat));

                Map<String, Object> MappavaluNote= new HashMap<>();
                MappavaluNote.put("vento_val", 5);
                MappavaluNote.put("umidita_val", 2);
                MappavaluNote.put("precipitazioni_val", 3);
                MappavaluNote.put("pressione_val", 5);
                MappavaluNote.put("temperatura_val", 5);
                MappavaluNote.put("altitudineghiacchi_val", 4);
                MappavaluNote.put("massaghiacci_val", 3);

                ServerCM u = new ServerCM();

                //u.login("luca","lu");
              //  u.registrazione("kl",  inserimenti);
              //  u.richiestaInserimentoCentro("addad");
               // u.registraCentroAree(daticentro, lonlatInserite) ;


                 /*
                boolean riuscito= u.login("mase", "masella");
                System.out.println(riuscito);

                 u.inserisciParametriClimatici("15115 155", MappavaluNote);
                 u.statisticaParametri("centro", "centrum" );

*/



            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("file non trovato");
            }




        } finally {

           DatabaseConnection.closeConnection(connect);
        }


    }

}

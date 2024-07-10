/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 *La classe <strong>GestioneCentri</strong> si occupa degli aspetti relativi ai centri di monitoraggio.
 *Sia le fasi per registrare il centro, sia l'aggiunta delle area associate.
 *@author Letizia Capitanio
 */

public class GestioneCentri {
    private String centro;
    private String id;
    private int centquantitàAree;
    private String lonlat;
    private String areaScelta;
    private GestioneUtente utente = new GestioneUtente();
    private static String[] NomiColonneAree = {"lonlat",  "NomeArea"};
    private static String[] OutputSchermoAree = {"longitudine e latitudine",  "il nome dell'area"};
    private static String[] OutputSchermoParametri = {"valutazione per il Vento",  "valutazione per l'Umidita","valutazione per le Precipitazioni", "valutazione per la Pressione", "valutazione per la Temperatura", "valutazione per l'altitudine dei ghiacciai","valutazione per la Massa dei Ghiacciai"};
    private static String[] OutputSchermoParametriNotes = {"note per il Vento",  "note per l'Umidita","note per le Precipitazioni", "note per la Pressione", "note per la Temperatura", "note per l'altitudine dei ghiacciai","note per la Massa dei Ghiacciai"};

    private static String[] NomiColonneParametriPAR = {"vento_val" ,"umidita_val", "precipitazioni_val" ,"pressione_val" ,"temperatura_val" ,"altitudineghiacchi_val" ,"massaghiacci_val" };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes","umidita_notes",
            "precipitazioni_notes" , "pressione_notes",
            "temperatura_notes",
            "altitudineghiacchi_notes","massaghiacci_notes" };
    private Scanner scanner= new Scanner(System.in);
    private DatabaseConnection db = new DatabaseConnection();
    private LinkedList<String> elementiDisponibili = new LinkedList<>();
    private LinkedList<String> longlatDisponibili = new LinkedList<>();


    /**
     * Metodo che imposta il centro e l'utente nella classe <strong>GestioneCentri</strong>
     * @param NomeCentro Stringa contenente il nome del centro dell'utente.
     * @param Userid Stringa contenente l'userId.
     */
    public void setCentroId(String NomeCentro, String Userid) {
        this.centro = NomeCentro;
        this.id = Userid;
    }


    /**
     * controlla che le longitutidine e latituidine non sono già presenti. in caso positivo, vengono inserite nella tabella <stron>aree</stron>
     * @param lonlatInserite lista contenente longitudini e latitutidini come unico formato delle aree
     */
    public void inserimentoAree(LinkedList<String> lonlatInserite)  {

        String input;
        Map<String, String> dataMap = new HashMap<>();

        for (int i = 0; i < lonlatInserite.size();) {
           // for(int j=0;j<2;j++) {
              // input = utente.getInfoFromUser(OutputSchermoAree[j]);
              //  if(NomiColonneAree[j].equals("lonlat")){
            // Ottieni l'elemento corrente
            String lonlat = lonlatInserite.get(i);
            if(db.controlloSegiaPresente(lonlat,"aree","lonlat")){
                        //System.out.print("Area già presente, reinserire >");
                       // input = scanner.nextLine().toUpperCase();
                lonlatInserite.remove(i);
            }else{
                db.inserimentoinDB("aree",lonlat ,"lonlat", centro, "NomeCentro","lonlat");
                i++; }// Passa all'elemento successivo solo se non viene rimosso

                   // lonlat=input;

              //  }else{
                //    dataMap.put(NomiColonneAree[j], input);
                //}
          //  }
          //  db.UpdateDataToDB(dataMap,"aree", lonlat,"lonlat");
        }


    }


    /**
     *Inserisce le note e le valutazioni contenute nell'argomento mappavaluNote nell'area passata come argomento
     * @param longlatScelta longitutdine e latitudine dell'area scelta
     * @param MappavaluNote mappa che contiene le note e le valutazioni da inserire
     */
    public void selezioneAreadiLavoroeInserimento(String longlatScelta, Map<String, Object> MappavaluNote) {


        String output;
        elementiDisponibili = db.mostraElementiDisponibili("aree", centro, "NomeArea",false );
        longlatDisponibili= db.mostraElementiDisponibili("aree", centro,"lonlat",false);
        for (String elemento : longlatDisponibili) {
            System.out.println(elemento);
        }
       // output= utente.getInfoFromUser("area scelta");
        /*
        while(!longlatDisponibili.contains(longlatScelta)) {
            System.out.println("reinserire perchè non presente ");
            output = utente.getInfoFromUser("area scelta");
        }


        areaScelta=output;
        String longlatScelto = "";
        int index = elementiDisponibili.indexOf(areaScelta);
        if (index != -1) {
            longlatScelto = longlatDisponibili.get(index);
        }

         */
        // Utilizza longlatScelto come necessario
        //System.out.println("La longitudine e latitudine per l'area scelta (" + areaScelta + ") è: " + longlatScelto);
        db.inserimentoinDB("ParametriClimatici",longlatScelta,"area",centro,"centro","long nei ParametriClimatici");
        MappavaluNote.put("datainserimento",RestitutoreDataOdierna());
        db.UpdateDataToDB(MappavaluNote,"ParametriClimatici",longlatScelta,"area");

    }


    private String RestitutoreDataOdierna(){
        LocalDate dataCorrente = LocalDate.now();
        // Definisci il formato "short"
        DateTimeFormatter formatoData = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());
        return dataCorrente.format(formatoData);
    }

/*
    private void aggiuntaParametriIn(Map<String, String> MappavaluNote, String longlatScelto)  {
        Map<String, String> dataMap = new HashMap<>();
       // String result;

        MappavaluNote.put("datainserimento", RestitutoreDataOdierna());

        for (int i = 0; i < OutputSchermoParametri.length; i++) {
            System.out.println("Vuoi inserire "+OutputSchermoParametri[i]+" ? (si/no)");
            result= scanner.nextLine();
            if(result.equals("si")){
                String outputPAR= utente.getInfoFromUser(OutputSchermoParametri[i]);
                dataMap.put(NomiColonneParametriPAR[i], outputPAR);
                System.out.println("Vuoi inserire "+OutputSchermoParametriNotes[i]+" ? (si/no)");
                result= scanner.nextLine();
                if(result.equals("si")){
                    String outputNOT= utente.getInfoFromUser(OutputSchermoParametriNotes[i]);
                    dataMap.put(NomiColonneParametriNOT[i], outputNOT);
                }
            }
        }


        db.UpdateDataToDB(dataMap,"ParametriClimatici",longlatScelto,"area");

    }

 */


    /**
     * Metodo per utenti registrati e non.
     * Stampo le mode per l'area o il centro scelto.
     * @param parametroScelto inserire area o centro in base a che tipo di elemento si vuole inserire
     * @param elementoScelto nome del centro, oppure longitutdine e latitudine dell'area
     */
    public void restitutoreMode(String parametroScelto, String elementoScelto) {

        LinkedList<String> elementiDisponibili;
       // String campoDiricerca = utente.getInfoFromUser("per quale valore di ricerca vuoi visualizzare [centro] o [area]");
        if(parametroScelto.equals("centro")){
            elementiDisponibili = db.mostraElementiDisponibili("CentriMonitoraggio", null, "NomeCentro",false);
        }else{
            elementiDisponibili = db.mostraElementiDisponibili("aree", null, "lonlat",true);
        }
        for (String elemento : elementiDisponibili) {
            System.out.println(elemento);
        }
       // String elementoScelto= utente.getInfoFromUser("per quale valore le valutazioni inserite");
        GestoreStampe( db.RicercaMode(elementoScelto,NomiColonneParametriPAR, NomiColonneParametriNOT,  parametroScelto ));

    }


    private void GestoreStampe(Map<String, HashMap<String, String>> contenitore){
        for (Map.Entry<String, HashMap<String, String>> entry : contenitore.entrySet()) {
            String colonna = entry.getKey();
            HashMap<String, String> risultatiColonna = entry.getValue();
            System.out.println("Colonna: " + colonna);
            if (risultatiColonna.containsKey("Note")) {
                System.out.println("Note:" );
                String note = risultatiColonna.get("Note").replace("]","").replace("[","");
                for (String nota : note.split(",")) {
                    System.out.println("- "+ nota.trim()); // Stampa ogni nota con il prefisso "-"
                }
            } else {
                // Se non ci sono note, stampa valore più usato e frequenza
                System.out.println("Valore più usato: " + risultatiColonna.get("ValorePiuUsato"));
                System.out.println("Frequenza: " + risultatiColonna.get("Frequenza"));
            }
            System.out.println();
        }
    }




}

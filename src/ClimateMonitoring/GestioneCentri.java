package ClimateMonitoring;/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 *La classe <strong>ClimateMonitoring.GestioneCentri</strong> si occupa degli aspetti relativi ai centri di monitoraggio.
 *Sia le fasi per registrare il centro, sia l'aggiunta delle area associate.
 *@author Letizia Capitanio
 */

public class GestioneCentri {
    private String centro;
    private String id;
    private static String[] NomiColonneParametriPAR = {
            "vento_val" ,"umidita_val",
            "precipitazioni_val" ,"pressione_val" ,
            "temperatura_val" ,"altitudineghiacchi_val" ,
            "massaghiacci_val" };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes","umidita_notes",
            "precipitazioni_notes" , "pressione_notes",
            "temperatura_notes",
            "altitudineghiacchi_notes","massaghiacci_notes" };



    /**
     * Metodo che imposta il centro e l'utente nella classe <strong>ClimateMonitoring.GestioneCentri</strong>
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
    public void inserimentoAree(LinkedList<String> lonlatInserite, DatabaseConnection db)  {
        for (int i = 0; i < lonlatInserite.size();) {
            // Ottieni l'elemento corrente
            String lonlat = lonlatInserite.get(i);
            System.out.println(lonlat);
            if(db.controlloSegiaPresente(lonlat,"aree","lonlat")){
                lonlatInserite.remove(i);
            }else{
                db.inserimentoinDB("aree",lonlat ,"lonlat", centro, "NomeCentro","lonlat", null);
                i++; }// Passa all'elemento successivo solo se non viene rimosso
        }
    }


    /**
     * Inserisce le note e le valutazioni contenute nell'argomento mappavaluNote nell'area passata come argomento
     *
     * @param longlatScelta longitutdine e latitudine dell'area scelta
     * @param MappavaluNote mappa che contiene le note e le valutazioni da inserire
     * @param db
     */
    public void selezioneAreadiLavoroeInserimento(String longlatScelta, Map<String, Object> MappavaluNote, DatabaseConnection db) {
        // Utilizza longlatScelto come necessario
        //System.out.println("La longitudine e latitudine per l'area scelta (" + areaScelta + ") è: " + longlatScelto);
        MappavaluNote.put("datainserimento",RestitutoreDataOdierna());
        MappavaluNote.put("area",longlatScelta);
        MappavaluNote.put("centro",centro);
        db.inserimentoinDB("ParametriClimatici",null,null,null,null,"long nei ParametriClimatici", MappavaluNote);

    }


    private String RestitutoreDataOdierna(){
        LocalDate dataCorrente = LocalDate.now();
        // Definisci il formato "short"
        DateTimeFormatter formatoData = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());
        return dataCorrente.format(formatoData);
    }


    /**
     * Metodo per utenti registrati e non.
     * Stampo le mode per l'area o il centro scelto.
     * @param elementoScelto nome del centro, oppure longitutdine e latitudine dell'area
     */
    public void restitutoreMode( String elementoScelto, DatabaseConnection db) {
        GestoreStampe( db.RicercaMode(elementoScelto,NomiColonneParametriPAR, NomiColonneParametriNOT ));

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

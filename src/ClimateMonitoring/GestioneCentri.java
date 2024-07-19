/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/
package ClimateMonitoring;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;



    /**
     * La classe <strong>GestioneCentri</strong> gestisce le operazioni relative ai centri di monitoraggio.
     * Questa classe include le funzionalità per registrare un centro, aggiungere aree associate e inserire parametri climatici.
     * <p>
     * I metodi permettono di gestire l'associazione di aree e parametri climatici a un centro di monitoraggio specifico,
     * e di assicurarsi che i dati inseriti non siano duplicati.
     * </p>
     *
     * @author Letizia Capitanio
     */
    public class GestioneCentri {
        private String centro;
        private String id;

        /**
         * Metodo che imposta il centro e l'utente nella classe <strong>GestioneCentri</strong>
         * @param NomeCentro Stringa contenente il nome del centro dell'utente.
         * @param Userid Stringa contenente l'userId.
         */
        protected void setCentroId(String NomeCentro, String Userid) {
            this.centro = NomeCentro;
            this.id = Userid;
        }
        /**
         * controlla che le aree scelte non siano già presenti. in caso positivo, vengono inserite nella tabella <stron>aree</stron>
         * @param lonlatInserite lista contenente longitudini e latitutidini come unico formato delle aree
         * @param db oggetto della classe DatabaseConnection
         */
        protected void inserimentoAree(LinkedList<String> lonlatInserite, DatabaseConnection db)  {
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
         * @param longlatScelta area scelta
         * @param MappavaluNote contiene le note e le valutazioni da inserire per quell'area
         * @param db oggetto della classe DatabaseConnection
         */
        protected void selezioneAreadiLavoroeInserimento(String longlatScelta, Map<String, Object> MappavaluNote, DatabaseConnection db) {
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

    }

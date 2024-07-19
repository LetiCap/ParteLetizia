/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/
package ClimateMonitoring.GUI;
import java.util.LinkedList;

/**
 * Interfaccia per poter passare una lista di elementi da un panel al panel precedente
 * @author Letizia Capitanio
 */
public interface BackButtonListener {
    void onBackButtonClicked(LinkedList<String> lonlatInserite);
}

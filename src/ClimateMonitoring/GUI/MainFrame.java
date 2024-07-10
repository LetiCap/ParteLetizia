import ClimateMonitoring.ServerInterface;

import javax.swing.*;

public class MainFrame extends JFrame {
    private ServerInterface server; // Riferimento al server

    // Costruttore che accetta il server come parametro
    public MainFrame(ServerInterface server) {
        this.server = server;
        initializeGUI(); // Metodo per inizializzare la GUI
    }

    private void initializeGUI() {
        // Inizializzazione della GUI
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Aggiungi qui altri componenti della tua GUI
    }
}

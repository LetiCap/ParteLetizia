package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;

public class VisualizzazionePanel  extends JPanel{
    public VisualizzazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new GridLayout(8, 2, 10, 10)); // Imposto il layout con margine di 10 pixel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiungo margine esterno

    }

}

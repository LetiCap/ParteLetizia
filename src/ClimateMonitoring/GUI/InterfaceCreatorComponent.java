package ClimateMonitoring.GUI;

import javax.swing.*;
import java.awt.*;

public class InterfaceCreatorComponent {
    public JLabel creatorTileWindow(String title){
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x2E86C1));
        return titleLabel;
    }
    public void modifyGridBagConstraints(GridBagConstraints gbc,int x,int y){
        gbc.gridx = x;
        gbc.gridy =y;

    }
    public JTextField  createNormaleField(int column){
            JTextField field = new JTextField(column);
            field.setFont(new Font("Serif", Font.PLAIN, 18));
            return field;
    }
    public JButton createButton(boolean backButton,String text){
        JButton returner = new JButton(text);
        if(backButton){
            returner.setFont(new Font("Serif", Font.BOLD, 18));
            returner.setBackground(new Color(0xE5050E));
            returner.setForeground(Color.WHITE);
            return returner;
        }
        returner.setFont(new Font("Serif", Font.BOLD, 18));
        returner.setBackground(new Color(0x5DADE2));
        returner.setForeground(Color.WHITE);
        return returner;
    }
}

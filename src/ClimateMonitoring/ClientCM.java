package ClimateMonitoring;


import ClimateMonitoring.GUI.MainFrame;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientCM extends UnicastRemoteObject implements ClientInterface {
    ServerInterface server;
    public ClientCM() throws RemoteException {
    }

    public void exec(){
        try {
            Registry reg = LocateRegistry.getRegistry(1099);
            server = (ServerInterface) reg.lookup("Server");


            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(server); // Passa il server alla ClimateMonitoring.GUI
                frame.setVisible(true);
            });



        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.err.println("Client terminates. Server not found.");
        }
    }

    public static void main(String[] args) {
        try {
            new ClientCM().exec();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}

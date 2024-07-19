package ClimateMonitoring;

import ClimateMonitoring.GUI.MainFrame;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * La classe <strong>ClientCM</strong> rappresenta il client per l'applicazione di Climate Monitoring.
 * <p>
 * Il client si connette a un server remoto tramite RMI (Remote Method Invocation).
 * </p>
 * <p>
 * La classe estende {@link UnicastRemoteObject} e implementa {@link ClientInterface}.
 * </p>
 *
 * @see ClientInterface
 * @see ServerInterface
 * @see MainFrame
 * @author Letizia Capitanio
 * @author Tahir Agalliu
 */
public class ClientCM extends UnicastRemoteObject implements ClientInterface {
    private ServerInterface server;
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

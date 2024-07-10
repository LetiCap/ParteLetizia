import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientCM extends UnicastRemoteObject implements ClientInterface {
    static ServerInterface server;
    public ClientCM() throws RemoteException {
    }

    public static void main(String[] args) {
        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(1099);
            server = (ServerInterface) reg.lookup("Server");
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(server); // Passa il server alla GUI
                frame.setVisible(true);
            });

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.err.println("Client terminates. Server not found.");
        }
    }
}

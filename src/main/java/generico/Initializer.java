package generico;

import generico.logger.LogToFile;

import java.util.logging.SimpleFormatter;

public class Initializer {

    //-- Constantes de conexão com o BD --//
    public static String controllerDb;
    public static String linkDb;
    public static String userDb;
    public static String passwordDb;

    //USERDB=TELECUSHMAN;PASSWORDDB=W@ke_Cu$h1006;DRIVEDB=jdbc:postgresql:;URLDB=192.168.17.174;PORT=5432;NOMEDB=Desenvolvimento
    public static void init(String path, String nomeArquivo, String controllerDb, String linkDb, String userDb, String passwordDb){
        LogToFile.init(path, nomeArquivo, "txt", new SimpleFormatter());
        Initializer.controllerDb = controllerDb;
        Initializer.linkDb = linkDb;
        Initializer.userDb = userDb;
        Initializer.passwordDb = passwordDb;
    }

}

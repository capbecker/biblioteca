package generico.logger;

import generico.Geral;

import java.io.IOException;
import java.util.logging.*;

/** Loger */
public class LogToFile {
    public static final Level TIPO_SEVERE = Level.SEVERE ;
    public static final Level TIPO_WARNING = Level.WARNING ;
    public static final Level TIPO_INFO = Level.INFO ;
    public static final Level TIPO_CONFIG = Level.CONFIG ;
    public static final Level TIPO_FINE = Level.FINE ;
    public static final Level TIPO_FINER = Level.FINER ;
    public static final Level TIPO_FINEST = Level.FINEST ;

    protected static final Logger logger = Logger.getLogger("MYLOG");
    protected static String nomeArquivo;
    protected static String extensao;
    protected static String path;
    protected static Formatter formatter;
    protected static Boolean inicializado = false;

    public static void init(String path, String nomeArquivo){
        init(path, nomeArquivo, "txt", new SimpleFormatter());
    }

    public static void init(String path, String nomeArquivo, Formatter formatter){
        init(path, nomeArquivo, "txt", formatter);
    }

    public static void init(String path, String nomeArquivo, String extensao){
        init(path, nomeArquivo, extensao, new SimpleFormatter());
    }

    public static void init(String path, String nomeArquivo, String extensao, Formatter formatter){
        LogToFile.path = path;
        LogToFile.nomeArquivo = nomeArquivo;
        LogToFile.extensao = extensao;
        LogToFile.formatter = formatter;
        inicializado = true;
    }

    public static void log(Throwable t, Level level) {
        log(t,null, level);
    }

    public static void log(Throwable t, String msg, Level level) {
        if (!inicializado) {
            throw new ExceptionInInitializerError("Logger não inicializado");
        }
        FileHandler fh = null;
        try {
            fh = new FileHandler(path+nomeArquivo+"."+extensao, true);
            fh.setFormatter(formatter);
            logger.addHandler(fh);
            logger.log(level, Geral.coalesce(msg,t.getMessage()), t);
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally {
            if (fh != null) fh.close();
        }
    }
}
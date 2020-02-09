package generico;

import java.lang.reflect.Array;
import java.util.*;

/** Classe de apoio */
public class Geral {

    /** Retorna o primeiro não nulo */
    public static <T> T coalesce(T ...campos) {
        for (T campo: campos) {
            if (campo!=null) {
                return campo;
            }
        }
        return null;
    } //coalesce

    /** Retorna o primeiro não "vazio", sendo considerado diferente de nulo e:
     *  ► Para String, diferente de "";
     *  ► Para Class<?>, diferente de Object;
     *  ► Para List e Map, deve ter ao menos um valor.
     **/
    public static <T> T coalesce2(T ...campos) {
        for (T campo: campos) {
            if (campo!=null
                    && !(campo instanceof String && ((String) campo).length()==0)
                    && !(campo instanceof Class<?> && (campo).equals(Object.class))
                    && !(campo instanceof List<?> && ((List)campo).size() == 0)
                    && !(campo instanceof Map && ((Map)campo).size() == 0)
            ) {
                return campo;
            }
        }
        return null;
    } //coalesce2

    /** Cria um map através de 2 lists */
    public static <T,Q> Map<T,Q> createMap (List<T> keys, List<Q> valores) {
        if (keys.size()!=valores.size()) {
            throw new ArrayIndexOutOfBoundsException("Quantidade de valores errados");
        }
        Map<T,Q> retorno = new HashMap();
        for (int i = 0; i<keys.size(); i++) {
            retorno.put(keys.get(i),valores.get(i));
        }
        return retorno;
    }

    /** True se o primeiro {PARAMETRO} esta presente em {CAMPOS}, false se não */
    public static <T> Boolean in(T parametro, T ...campos) {
        for (T campo: campos) {
            if (campo==parametro) {
                return true;
            }
        }
        return false;
    } //in

    public static <T> List<T> convertList(List<Object> origem, Class<T> clazz) {
        List<T> retorno = new ArrayList<>();
        Class<?> clazzOrigem = origem.get(0).getClass();
        origem.forEach(elem->
        {
            if (clazz.isInstance(new String())) {
                retorno.add((T) String.valueOf(elem));
                } else if (clazzOrigem.isInstance(new String())) {
                    if (clazz.isInstance(new Double(0.0))) {
                        retorno.add((T) Double.valueOf((String) elem));
                    } else if (clazz.isInstance(new Double(0.0))) {
                        retorno.add((T) Integer.valueOf((String) elem));
                    }
            }
        });

        return retorno;
    } //convertList

    /** Obtém o tamanho de um iterator */
    public static Integer sizeIterator(Iterator<?> it) {
        Integer size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }
        return size;
    }

    /** Converte um list para um array */
    public static <T> T[] listToArray(List<T> origem, Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        T[] t = (T[]) Array.newInstance(clazz.newInstance().getClass(),origem.size());
        return origem.toArray(t);
    }

    /** Obtém um array das classes dos elementos de uma lista*/
    public static Class<?>[] getClasses(List<?> listaObjects) {
        List<Class<?>> listaClasses = new ArrayList<>();
        listaObjects.forEach(o->listaClasses.add(o.getClass()));
        return listaClasses.toArray(new Class[0]);
    } //getClasses

    /** Retorna a string com a primeira letra maiuscula */
    public static String decapitalize(String origem) {
        if (origem == null || origem.length() == 0) {
            return origem;
        }
        return origem.substring(0,1).toLowerCase()+ origem.substring(1);
    }

    /** Retorna a string com a primeira letra maiuscula */
    public static String capitalize(String origem) {
        if (origem == null || origem.length() == 0) {
            return origem;
        }
        return origem.substring(0,1).toUpperCase()+ origem.substring(1);
    }

    /** Retorna a string com a primeira letra de cada palavra maiuscula */
    public static String capitalizeAll(String origem) {
        if (origem == null || origem.length() == 0) {
            return origem;
        }
        String[] control = origem.trim().split(" ");
        StringJoiner retorno = new StringJoiner(" ");
        for (String part: control) {
            retorno.add(part.substring(0,1).toUpperCase()+ part.substring(1));
        }
        return retorno.toString();
    }

    /**
     * Calcula o tempo passado e retorna uma String no formato:
     * "{hora} Horas, {minuto} Minutos e {segundo} Segundos"
     */
    public static String converteHoras(Integer hora, Integer min, Integer seg) {
        return converteHoras(Double.valueOf(Geral.coalesce(hora,0)),
                Double.valueOf(Geral.coalesce(min,0)), Double.valueOf(Geral.coalesce(seg,0)));
    }

    /**
     * Calcula o tempo passado e retorna uma String no formato:
     * "{hora} Horas, {minuto} Minutos e {segundo} Segundos"
     */
    public static String converteHoras(Double hora, Double min, Double seg) {
        Double curHora = Geral.coalesce(hora,0d);
        Double curMin = Geral.coalesce(min,0d);
        Double curSeg = Geral.coalesce(seg,0d);
        Integer intHora = curHora.intValue();
        Integer intMin = curMin.intValue();
        Integer intSeg = curSeg.intValue();
        Integer newHora;
        Integer newMin;
        Double partHora = 60*(curHora-intHora);
        Double partMin = 60*(curMin-intMin);
        intSeg += partMin.intValue();
        intMin += partHora.intValue();
        newMin = intSeg/60;
        intSeg -= newMin*60;
        intMin += newMin;
        newHora = intMin/60;
        intMin -= 60*newHora;
        intHora += newHora;
        return intHora+" Horas, "+intMin+" Minutos e "+intSeg+" Segundos";
    }
}
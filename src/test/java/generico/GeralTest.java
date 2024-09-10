package generico;

import com.sun.jdi.IntegerType;
import org.junit.Assert;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class GeralTest {

    // objetos para teste
    Integer[] vIntEmpty = {};
    Integer[] vInt = {null,0,1,2,3};
    List<Integer> lIntEmpty = Arrays.asList(vIntEmpty);
    List<Integer> lInt = Arrays.asList(vInt);

    String[] vStrEmpty = {};
    String[] vStr = {null, "","valor 1","valor 2","valor 3"};
    List<String> lStrEmpty = Arrays.asList(vStrEmpty);
    List<String> lStr = Arrays.asList(vStr);

    Map<String,Integer> mapEmpty = Map.of();
    Map<String,Integer> map = Map.of("1",1,"2",2,"3",3);

    @Test
    /** Retorna o primeiro não nulo */
    public void testCoalesce() {
        assertEquals(Integer.valueOf(0), Geral.coalesce(null,0,10,5));
        assertEquals("", Geral.coalesce(null,"","passou"));

        assertEquals(vIntEmpty, Geral.coalesce(vIntEmpty, vStrEmpty, vInt, vStr));
        assertEquals(vInt[1], Geral.coalesce(vInt));
        assertEquals(lInt, Geral.coalesce(lInt));
        assertEquals(mapEmpty, Geral.coalesce(mapEmpty, map));
    }

    @Test
    public void testCoalesce2() {
        assertEquals(Integer.valueOf(0), Geral.coalesce2(null,0,10,5));
        assertEquals("passou", Geral.coalesce2(null,"","passou"));

        assertEquals(vInt, Geral.coalesce2(vIntEmpty, vStrEmpty, vInt, vStr));
        assertEquals(vInt[1], Geral.coalesce2(vInt));
        assertEquals(lInt, Geral.coalesce2(lInt));
        assertEquals(map, Geral.coalesce2(mapEmpty, map));
    }

    @Test
    public void testCreateMap() {
        //coloquei esse linkedhashset por 2 motivos:
        //1- no ultimo teste, eu adiciono um elemento a mais em keys para causar a exception, entao precisei trocar o
        //   set para um hashset
        //2- o set nao segue a ordenação que criei o objeto, tao pouco o hashset.
        //   Utilizei o LinkedHashSet para adicionar todos os elementos do set seguindo a ordem que o set fornecesse.
        Set<String> keys = new LinkedHashSet <String>();
        keys.addAll(map.keySet());
        Collection<Integer> values = map.values();

        assertEquals(map, Geral.createMap(new ArrayList<>(keys), new ArrayList<>(values)));
        assertEquals(map, Geral.createMap(keys, values));

        assertEquals(map, Geral.createMap(keys.toArray(new String[0]),
                values.toArray(new Integer[values.size()])));

        keys.add("v4");
        Assert.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {Geral.createMap(keys, values);});
    }

    @Test
    public void testIn() {
        assertEquals(true, Geral.in(2,vInt));
        assertEquals(true, Geral.in(null,vInt));
        assertEquals(false, Geral.in(7,vInt));

        assertEquals(false, Geral.in(2,vIntEmpty));
        assertEquals(false, Geral.in(null,vIntEmpty));
    }

    @Test
    public void testConvertList() {
        List<Object> listObjects = new ArrayList<>(map.values());
        List<Integer> listInteger = new ArrayList<>(map.values());
        List<String> listString = new ArrayList<>(map.keySet());

        assertEquals(listInteger, Geral.convertList(listObjects, Integer.class));
        assertEquals(listString, Geral.convertList(listObjects, String.class));
    }

    @Test
    public void testSizeIterator() {
        assertEquals((Integer)5, Geral.sizeIterator(lInt.iterator()));
        assertEquals((Integer)3, Geral.sizeIterator(map.keySet().iterator()));
    }

    @Test
    public void testGetClasses() {
        List<Integer> listInteger = new ArrayList<>(map.values());
        List<String> listString = new ArrayList<>(map.keySet());
        List<Object> listObjects = new ArrayList<>();
        listObjects.addAll(listInteger);
        listObjects.addAll(listString);

        Class<?>[] retornoInteger = {Integer.class,Integer.class,Integer.class};
        Class<?>[] retornoString = {String.class,String.class,String.class};
        Class<?>[] retornoBoth = {Integer.class,Integer.class,Integer.class,String.class,String.class,String.class};

        assertEquals(retornoInteger, Geral.getClasses(listInteger));
        assertEquals(retornoString, Geral.getClasses(listString));
        assertEquals(retornoBoth, Geral.getClasses(listObjects));
    }


    @Test
    public void testCaptilze() {
        String decapitalize = "texto teste";
        String capitalize = "Texto teste";
        String invertCapitalize = "texto Teste";
        String capitalizeAll = "Texto Teste";

        assertEquals(decapitalize, Geral.decapitalize(capitalize));
        assertEquals(invertCapitalize, Geral.decapitalize(capitalizeAll))
        ;
        assertEquals(capitalize, Geral.capitalize(decapitalize));
        assertEquals(capitalizeAll, Geral.capitalize(invertCapitalize));

        assertEquals(decapitalize, Geral.deCapitalizeAll(capitalizeAll));
        assertEquals(capitalizeAll, Geral.capitalizeAll(decapitalize));
    }

    @Test
    public void testConvertHora() {
        //return intHora+" Horas, "+intMin+" Minutos e "+intSeg+" Segundos";
        String labelRetorno = "{0} Horas, {1} Minutos e {2} Segundos";
        MessageFormat mf = new MessageFormat(labelRetorno);

        assertEquals(mf.format(new Object[]{"1","2","0"}), Geral.converteHoras(1,2,0));
        assertEquals(mf.format(new Object[]{"1","2","0"}), Geral.converteHoras(0.0,62.0,0.0));
        assertEquals(mf.format(new Object[]{"1","2","0"}), Geral.converteHoras(0.0,60.0,120.0));
        assertEquals(mf.format(new Object[]{"1","2","0"}), Geral.converteHoras(0,0,3720));
    }
}
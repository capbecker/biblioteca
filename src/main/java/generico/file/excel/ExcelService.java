package generico.file.excel;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingRule;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingThreshold;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheetConditionalFormatting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class ExcelService {

    public static <T> File geraExcelSimples(List<T> listModel, String nome)
        throws InvocationTargetException, IllegalAccessException, IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nome);
        // Definindo alguns padroes de layoutfile2.
        sheet.setDefaultColumnWidth(15);
        sheet.setDefaultRowHeight((short)400);

        int rownum = 0;
        int cellnum = 0;
        Cell cell;
        Row row;
        //Configurando estilos de células (Cores, alinhamento, formatação, etc..)
        HSSFDataFormat numberFormat = workbook.createDataFormat();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(CellStyle.ALIGN_CENTER);
        textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(numberFormat.getFormat("#,##0.00"));
        numberStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        // Configurando Header
        row = sheet.createRow(rownum++);

        for(Method metodo :listModel.get(0).getClass().getDeclaredMethods()) {
            if (metodo.getName().startsWith("get")) {
                cell = row.createCell(cellnum++);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(metodo.getName().replaceFirst("get",""));
            }
        }

        for(Object model:listModel) {
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for(Method metodo :model.getClass().getDeclaredMethods()) {
                if (metodo.getName().startsWith("get")) {
                    cell = row.createCell(cellnum++);
                    cell.setCellStyle(textStyle);
                    cell.setCellValue(String.valueOf(metodo.invoke(model)));
                }
            }
        }
        File f = new File("/tmp/"+nome+".xls");
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(f);
        workbook.write(out);
        out.close();
        workbook.close();
        return f;
    } //geraExcelSimples

    public static <T> File geraExcelSimples(LinkedList<T> listModel, String nome)
            throws InvocationTargetException, IllegalAccessException, IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nome);
        // Definindo alguns padroes de layoutfile2.
        sheet.setDefaultColumnWidth(15);
        sheet.setDefaultRowHeight((short)400);

        int rownum = 0;
        int cellnum = 0;
        Cell cell;
        Row row;
        //Configurando estilos de células (Cores, alinhamento, formatação, etc..)
        HSSFDataFormat numberFormat = workbook.createDataFormat();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(CellStyle.ALIGN_CENTER);
        textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(numberFormat.getFormat("#,##0.00"));
        numberStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        // Configurando Header
        row = sheet.createRow(rownum++);

        for(Method metodo :listModel.get(0).getClass().getDeclaredMethods()) {
            if (metodo.getName().startsWith("get")) {
                cell = row.createCell(cellnum++);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(metodo.getName().replaceFirst("get",""));
            }
        }
//        sheet.createRow(rownum++);
//        sheet.createRow(rownum++);
//        sheet.createRow(rownum++);
//        sheet.addMergedRegion(new CellRangeAddress(2,2,1,10));
//
        for(Object model:listModel) {
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for(Method metodo :model.getClass().getDeclaredMethods()) {
                if (metodo.getName().startsWith("get")) {
                    cell = row.createCell(cellnum++);
                    cell.setCellStyle(textStyle);
                    cell.setCellValue(String.valueOf(metodo.invoke(model)));
                }
            }
        }
        File f = new File("/tmp/"+nome+".xls");
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(f);
        workbook.write(out);
        out.close();
        workbook.close();
        return f;
    } //geraExcelSimples

    public static Integer maxRowIterator(Iterator<Row> it) {
        Integer maxKey = 0;
        while (it.hasNext()) {
            Row curRow = it.next();
            maxKey = Math.max(maxKey, curRow.getRowNum()+1);
        }
        return maxKey;
    }

    /**
     * Copia as linhas {numRowErro} de {curSheet} para {retSheet}
     * Se {numRowErro} for null, copia {curSheet} inteiro
     ********************************************************/
    public static void copySheet(XSSFSheet retSheet, Sheet curSheet, List<Integer> numRowErro) {
        if (numRowErro == null) {
            curSheet.iterator().forEachRemaining(row-> copyRowSheet(retSheet.createRow(row.getRowNum()), row));
        } else {
            numRowErro.forEach(i-> copyRowSheet(retSheet.createRow(i), curSheet.getRow(i)));
        }
    }

    /**
     * Copia a linha {curRow} para {retRow}
     ********************************************************/
    public static void copyRowSheet(Row retRow, Row curRow) {
        Integer numCell = 1;
        for (Cell curCell : curRow) {
            Cell retCell = retRow.createCell(numCell);
            switch (curCell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    retCell.setCellValue(curCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    retCell.setCellValue(curCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    retCell.setCellValue(curCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    retCell.setCellValue(curCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    retCell.setCellValue(curCell.getCellFormula());
                    break;
            }
            numCell++;
        }
    }

    public static void formatStatus(String rangetxt, XSSFSheet sheet, String txtIcone) {
        XSSFSheetConditionalFormatting          fs = sheet.getSheetConditionalFormatting();
        IconMultiStateFormatting.IconSet        tipoIcone = IconMultiStateFormatting.IconSet.byName(txtIcone);
        XSSFConditionalFormattingRule           rule = fs.createConditionalFormattingRule(tipoIcone);
        rule.getMultiStateFormatting().setIconOnly(true);
        XSSFConditionalFormattingThreshold[]    eachRule = rule.getMultiStateFormatting().getThresholds();
        eachRule[0].setRangeType(ConditionalFormattingThreshold.RangeType.MIN);
        eachRule[1].setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER);
        eachRule[2].setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER);
//        CellRangeAddress[] rangeAddress = {CellRangeAddress.valueOf("B1:B"+numRow)};
        CellRangeAddress[] rangeAddress = {CellRangeAddress.valueOf(rangetxt)};
        fs.addConditionalFormatting(rangeAddress, rule);
    }

    public static String getNameColumnByIndex(Integer index) {
        StringBuilder retorno = new StringBuilder();
        LinkedList<Integer> ll = new LinkedList();
        Integer quociente = index;
        Integer resto;
        Boolean isFirst = true;
        do {
            resto = quociente%26;
            quociente = quociente/26;
            if (!isFirst) {
                if (resto==0) {
                    resto = 25;
                    quociente = quociente-26;
                } else {
                    resto = resto-1;
                }
            }
            isFirst = false;
            ll.addFirst(resto);
        } while (quociente>=1);
        // char recebe inteiro, com o codigo da tabela asc;
        // A = 65, e como o ll recebe uma lista de index, onde A=0, precisa-se somar 65
        for(Integer l:ll) {
            retorno.append((char) (l+65));
        }
        return retorno.toString();
    }

    public static LinkedHashMap<String, Cell> sheetToHashMap(Sheet sheet) {
        LinkedHashMap<String, Cell> retorno = new LinkedHashMap<>();
        Iterator<Row> listRows = sheet.iterator();
        listRows.forEachRemaining(row->{
            Iterator<Cell> listCell = row.cellIterator();
            listCell.forEachRemaining(cell-> {
                String nomeColuna = getNameColumnByIndex((cell.getColumnIndex())) + (row.getRowNum()+1);
                retorno.put(nomeColuna, cell);
            });
        });
        return retorno;
    }

    public static Object getCellValue(Cell cell) {
        return getCellValue(cell, false);
    }

    public static Object getCellValue(Cell cell, Boolean isDate) {
        if (cell!=null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_FORMULA:
                    try{
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        try{
                            if (isDate) {
                                return cell.getDateCellValue();
                            } else {
                                return cell.getNumericCellValue();
                            }
                        } catch (Exception e2) {
                            try{
                                return cell.getBooleanCellValue();
                            } catch (Exception e3) {}
                        }
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case Cell.CELL_TYPE_NUMERIC:
                    if (isDate) {
                        return cell.getDateCellValue();
                    } else {
                        return cell.getNumericCellValue();
                    }
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
            }
        }
        return null;
    }

    public static CellRangeAddress getRegionCell(Cell cell, Sheet sheet) {
        List<CellRangeAddress> listRegion = sheet.getMergedRegions();
        for(CellRangeAddress region: listRegion)
         {
            if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return region;
            }
        }
        return null;
    }
}

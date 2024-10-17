import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Float.NaN;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;

    CSVReader(String file,String delimiter) throws IOException {
        this(file,delimiter,false);
    }
    CSVReader(String file) throws IOException{
        this(file,",");
    }
    public CSVReader(Reader reader, String delimiter, boolean hasHeader) throws IOException {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();


    }

    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) throws IOException {
        this(new BufferedReader(new FileReader(filename)), delimiter,hasHeader);
    }
    // nazwy kolumn w takiej kolejności, jak w pliku
    List<String> columnLabels = new ArrayList<>();
    // odwzorowanie: nazwa kolumny -> numer kolumny
    Map<String,Integer> columnLabelsToInt = new HashMap<>();
    void parseHeader() throws IOException {
        // wczytaj wiersz
        String line = reader.readLine();
        if (line == null) {
            return;
        }
        // podziel na pola
        String[] header = line.split(delimiter);
        // przetwarzaj dane w wierszu
        for (int i = 0; i < header.length; i++) {
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i],i);
        }//...
    }
    String[]current;
    boolean next() throws IOException {
        // czyta następny wiersz, dzieli na elementy i przypisuje do current
        //
        String regex = String.format("%s(?=([^\"]*\"[^\"]*\")*[^\"]*$)",delimiter);
        String line  = reader.readLine();
        if(line == null){
            return false;
        }
        current = line.split(regex);
        return true;


    }
    public List<String> getColumnLabels(){
        return columnLabels;
    }
    public int getRecordLenght(){
        return current.length;
    }
    public boolean isMissing(int columnIndex){
        if(columnIndex < 0 || columnIndex >= current.length){
            return true;
        }
        return Objects.equals(current[columnIndex], "");
    }
    public boolean isMissing(String columnLabel) {
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        if (columnIndex == null || columnIndex >= current.length) {
            return true;
        }
        String value = current[columnIndex];
        return value == null || value.isEmpty();
    }
    String get(int columnIndex){
        if(isMissing(columnIndex)){
            return "";
        }
        return current[columnIndex];
    }
    String get(String columnLabel){
        return get(columnLabelsToInt.get(columnLabel));
    }
    Integer getInt(int columnIndex) {
        if (get(columnIndex).isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(get(columnIndex));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    Integer getInt(String columnLabel) {
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        if (columnIndex == null) {
            System.out.println("Kolumna o etykiecie " + columnLabel + " nie istnieje.");
            return null;
        }
        return getInt(columnIndex);
    }
    Double getDouble(int columnIndex){
        if(isMissing(columnIndex) || get(columnIndex).isEmpty()){
            return null;
        }
        return Double.parseDouble(get(columnIndex));
    }
    Double getDouble(String columnIndex) {
        if(isMissing(columnIndex) || get(columnIndex).isEmpty()){
            return null;
        }
        return this.getDouble(columnLabelsToInt.get(columnIndex));
    }
    Long getLong(int columnIndex){
        if (get(columnIndex).isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(get(columnIndex));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
    Long getLong(String columnLabel){
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        if (columnIndex == null) {
            System.out.println("Kolumna o etykiecie " + columnLabel + " nie istnieje.");
            return null;
        }
        return getLong(columnIndex);
    }


    public static LocalTime getTime(String timeString, String format) {
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
    }
    public static LocalDate getDate(String dateString, String format) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
    }

}




import java.io.IOException;
import java.util.*;
import java.io.PrintStream;
import java.util.function.Predicate;


public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();
    public void read(String filename) throws IOException {
        CSVReader reader = new CSVReader(filename,",",true);
        int number = 0;
        Map<Long,AdminUnit> LtoA = new HashMap<>();
        Map<AdminUnit,Long> AtoL = new HashMap<>();
        Map<Long,List<AdminUnit>> parentid2child = new HashMap<>();
        while(reader.next()){

            number++;
            AdminUnit adminUnit = new AdminUnit();
            adminUnit.name = reader.get("name");
            if(!reader.isMissing("admin_level") && reader.getInt("admin_level")!=null) {
                adminUnit.adminLevel = reader.getInt("admin_level");
            }
            if(!reader.isMissing("area")) {
                adminUnit.area = reader.getDouble("area");
            }
            if(!reader.isMissing("population")){
                adminUnit.population = reader.getDouble("population");
            }

            adminUnit.density = reader.getDouble("density");
            LtoA.put(reader.getLong("id"),adminUnit);
            if(reader.getLong("parent")==null){
                AtoL.put(adminUnit,null);
            }
            else {
                AtoL.put(adminUnit, reader.getLong("parent"));

            }
            if(reader.getDouble("x1") !=null && reader.getDouble("y1") != null){
                adminUnit.bbox.addPoint(reader.getDouble("x1"),reader.getDouble("y1"));
            }
            if(reader.getDouble("x2") !=null && reader.getDouble("y2") != null) {
                adminUnit.bbox.addPoint(reader.getDouble("x2"), reader.getDouble("y2"));
            }
            if(reader.getDouble("x3") !=null && reader.getDouble("y3") != null) {
                adminUnit.bbox.addPoint(reader.getDouble("x3"), reader.getDouble("y3"));
            }
            if(reader.getDouble("x4") !=null && reader.getDouble("y4") != null) {
                adminUnit.bbox.addPoint(reader.getDouble("x4"), reader.getDouble("y4"));
            }
            if(reader.getDouble("x5") !=null && reader.getDouble("y5") != null) {
                adminUnit.bbox.addPoint(reader.getDouble("x5"), reader.getDouble("y5"));
            }

            Long parentId = reader.getLong("parent");
            AdminUnit childUnit = new AdminUnit();
            List<AdminUnit> childrenList = parentid2child.computeIfAbsent(parentId, k -> new ArrayList<>());
            childrenList.add(childUnit);


            
            units.add(adminUnit);
        }
        for (AdminUnit x : units) {
            Long parentId = AtoL.get(x);


            x.parent = LtoA.get(parentId);
            x.children = parentid2child.get(parentId);
        }


    }
    void list(PrintStream out){
        for(AdminUnit x : units){
            out.println(x.toString());
        }
    }
    void list(PrintStream out,int offset, int limit ){
        for(int i = offset;i<offset+limit;i++){
            out.println(units.get(i).toString());
        }
    }
    AdminUnitList selectByName(String pattern, boolean regex){
        AdminUnitList ret = new AdminUnitList();
        for(AdminUnit x: units){
            if(x.name.equals(pattern)){
                ret.units.add(x);
            }
        }
        return ret;
    }
    public void fixMissingValues(AdminUnit adminUnit) {
        if (adminUnit == null) {
            return;
        }


        if (adminUnit.population == null || adminUnit.density == null) {

            AdminUnit parentUnit = adminUnit.parent;

            fixMissingValues(parentUnit);


            if (parentUnit != null && parentUnit.density != null) {

                double estimatedDensity = parentUnit.density;


                double estimatedPopulation = adminUnit.area * estimatedDensity;


                adminUnit.density = estimatedDensity;
                adminUnit.population = estimatedPopulation;
            }
        }
    }
    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     * @param unit - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */


    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance){
        AdminUnitList Neighbors = new AdminUnitList();
        for(AdminUnit x : units){
            if(unit.adminLevel == x.adminLevel && !unit.name.equals(x.name) &&unit.bbox.intersects(x.bbox) && unit.adminLevel != 8 ){
                Neighbors.units.add(x);
            }
            else if(unit.adminLevel == 8 && unit.bbox.distanceTo(x.bbox) < maxdistance && !unit.name.equals(x.name) && unit.adminLevel == x.adminLevel){
                Neighbors.units.add(x);
            }
        }
        return Neighbors;
    }
    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByName() {
        class MyIterator
        implements Comparator<AdminUnit>
        {
            public int compare(AdminUnit x, AdminUnit y){
                return x.name.compareTo(y.name);
            }

        }
        units.sort(new MyIterator());
        return this;
    }
    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByArea(){
        units.sort(new Comparator<AdminUnit>() {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area,o2.area);
            }
        });{
            return this;
        }
        }
    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByPopulation(){
        units.sort((AdminUnit o1,AdminUnit o2) ->{return Double.compare(o1.population,o2.population);});
        return this;
    }
    AdminUnitList sortInplace(Comparator<AdminUnit> cmp){
        units.sort(cmp);
        return this;
    }
    AdminUnitList sort(Comparator<AdminUnit> cmp){
        // Tworzy wyjściową listę
        // Kopiuje wszystkie jednostki
        // woła sortInPlace
        AdminUnitList list = new AdminUnitList();
        list.units = new ArrayList<>(this.units);
        return list.sortInplace(cmp);
    }
    /**
     *
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred) {
        AdminUnitList list = new AdminUnitList();
        for (AdminUnit x : this.units) {
            if (pred.test(x)) {
                list.units.add(x);
            }
        }
        return list;
    }
    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
        return this.filter(pred,0,limit);
    }
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
        AdminUnitList list = new AdminUnitList();
        AdminUnitList list1 = new AdminUnitList();
        for (AdminUnit x : this.units) {
            if (pred.test(x)) {
                list.units.add(x);
            }
        }
        for(int i = offset;i<offset+limit;i++){
            list1.units.add(list.units.get(i));
        }
        return list1;

    }






}

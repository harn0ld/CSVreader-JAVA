import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.function.Predicate;

import static java.lang.System.out;

public class main {
    public static void main(String[] args) throws IOException {
        AdminUnitList adminUnitList = new AdminUnitList();
        adminUnitList.read("C:\\Users\\Hubert\\IdeaProjects\\lab6\\out\\production\\lab6\\admin-units.csv");
        int o = 0;
        while (o < adminUnitList.units.size()) {
            // Retrieve values from the current AdminUnit
            AdminUnit currentUnit = adminUnitList.units.get(o);
            adminUnitList.fixMissingValues(currentUnit);
            String y = currentUnit.name;






            if(y.equals("Potoka")) {
                System.out.printf(Locale.US, "%s", currentUnit.toString());
                double t1 = System.nanoTime()/1e6;
                AdminUnitList neigb = adminUnitList.getNeighbors(currentUnit,5); //dla miejscowsci
                //AdminUnitList neigb = adminUnitList.getNeighbors(currentUnit.parent,15);

                double t2 = System.nanoTime()/1e6;
                System.out.println();
                System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
                System.out.println();
                int m =0;
                for(AdminUnit k : neigb.units){
                    System.out.println(k.name);
                    m++;

                }
                System.out.println(m);
            }


            o++;
        }


    //   }
    // catch (IOException e){
    //  e.printStackTrace();
    //  }

    /*        String text = "a,b,c\n123.4,567.8,91011.12";
            CSVReader reader2 = new CSVReader(new StringReader(text),",",true);
            while(reader2.next()) {
                for (int i = 0; i < reader2.current.length; i++) {
                    System.out.println(reader2.current[i]);

                }
            }
        }*/
        // adminUnitList.filter(a->a.name.startsWith("w")).sortInplaceByArea().list(out);
        // adminUnitList.filter(a->a.name.startsWith("K")).sortInplaceByName().list(out);
        Predicate<AdminUnit> isMałopolskiePowiat = a ->
                a.parent != null &&
                        "województwo małopolskie".equals(a.parent.name) &&
                        a.adminLevel == 6 && a.name.contains("powiat");
       // adminUnitList
               // .filter(isMałopolskiePowiat);
                //.list(out);
        Predicate<AdminUnit> areaGreaterThan1000 = a -> a.area > 1000;
        Predicate<AdminUnit> isPowiat = a -> a.name.contains("powiat");
        Predicate<AdminUnit> notPowiat = isPowiat.negate();
        Predicate<AdminUnit> startsWithK = a -> a.name.startsWith("K");
        Predicate<AdminUnit> startsWithKOrAreaGreaterThan1000 = startsWithK.or(areaGreaterThan1000);
        Predicate<AdminUnit> startsWithKAndIsMałopolskiePowiat = startsWithK.and(isMałopolskiePowiat);
        //adminUnitList.filter(startsWithKAndIsMałopolskiePowiat).list(out);
        Predicate<AdminUnit> p = new Predicate() {
            @Override
            public boolean test(Object o) {
                return false;
            }

            @Override
            public Predicate and(Predicate other) {
                return Predicate.super.and(other);
            }

            @Override
            public Predicate negate() {
                return Predicate.super.negate();
            }

            @Override
            public Predicate or(Predicate other) {
                return Predicate.super.or(other);
            }
        };
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(adminUnitList)
                .where(a->a.area>1000)
                .or(a->a.name.startsWith("Sz"))
                .sort((a,b)->Double.compare(a.area,b.area))
                .limit(100);
     //   query.execute().list(out);
        AdminUnitQuery query1 = new AdminUnitQuery()
                .selectFrom(adminUnitList)
                .where(a->a.area>2000)
                .or(a->a.name.startsWith("P"))
                .sort((a,b)->Double.compare(a.population,b.population))
                .limit(100);
        //query1.execute().list(out);
        AdminUnitQuery query2 = new AdminUnitQuery()
                .selectFrom(adminUnitList)
                .where(a->a.area>3000)
                .or(a->a.name.startsWith("O"))
                .sort((a,b)->a.name.compareTo(b.name))
                .limit(100);
        query2.execute().list(out);
    }

}





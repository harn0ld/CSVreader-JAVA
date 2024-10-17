import java.util.List;
public class AdminUnit {
        String name;
        int adminLevel;
        Double population;
        double area;
        Double density;
        AdminUnit parent;
        BoundingBox bbox = new BoundingBox();
        List<AdminUnit> children;

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s
                    .append(name)
                    .append(",")
                    .append(adminLevel)
                    .append(",")
                    .append(population)
                    .append(",")
                    .append(area)
                    .append(",")
                    .append(density)
                    .append(",")
                    .append(parent)
                    .append(", Wymiary:")
                    .append(bbox.toString());
            String x =s.toString();
            return x;
        }



}

import static java.lang.Double.NaN;

public class BoundingBox {
    double xmin = NaN;
    double ymin = NaN;
    double xmax = NaN;
    double ymax = NaN;

    /**
     * Powiększa BB tak, aby zawierał punkt (x,y)
     * Jeżeli był wcześniej pusty - wówczas ma zawierać wyłącznie ten punkt
     * @param x - współrzędna x
     * @param y - współrzędna y
     */
    void addPoint(double x, double y){
        if(this.isEmpty()){
            xmin = xmax = x;
            ymin = ymax = y;
        }
        if(x >= xmax){
            xmax = x;
        }
        if(y >=ymax){
            ymax = y;
        }
        if(x<=xmin){
            xmin = x;
        }
        if(y<=ymin){
            ymin = y;
        }

    }
    boolean isEmpty(){
        return Double.isNaN(xmax) && Double.isNaN(xmin);
    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y){
        return x <= xmax && x >= xmin && y <= ymax && y >= ymin;
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb){
        return this.contains(bb.xmax,bb.ymax) && this.contains(bb.xmin,bb.ymin);
    }

    /**
     * Sprawdza, czy dany BB przecina się z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb){
        return this.contains(bb.xmin,bb.ymin) || this.contains(bb.xmin,bb.ymax) || this.contains(bb.xmax,bb.ymax) || this.contains(bb.xmax,bb.ymin)
                || bb.contains(this.xmax,this.ymax) || bb.contains(this.xmin,this.ymin) || bb.contains(this.xmax,this.ymin) || bb.contains(this.xmin,this.ymax);
    }
    /**
     * Powiększa rozmiary tak, aby zawierał bb oraz poprzednią wersję this
     * Jeżeli był pusty - po wykonaniu operacji ma być równy bb
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb){
        if(this.isEmpty()){
            this.xmax = bb.xmax;
            this.xmin = bb.xmin;
            this.ymax = bb.ymax;
            this.ymin = bb.ymin;
            return this;
        }
        else if(bb.isEmpty() || this.contains(bb)){
            return this;
        }
        else{
            this.addPoint(bb.xmax,bb.ymax);
            this.addPoint(bb.xmin,bb.ymin);
            return this;
        }

    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */

    /**
     * Sprawdza czy
     * 1) typem o jest BoundingBox
     * 2) this jest równy bb
     * @return
     */
    public boolean equals(Object o){
        if(this == o) return true;
        if(o== null ||getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return xmax == that.xmax && ymax == that.ymax && xmin == that.xmin && ymin == that.ymin;
    }

    /**
     * Oblicza i zwraca współrzędną x środka
     * @return if !isEmpty() współrzędna x środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterX(){
        if(this.isEmpty()) return NaN;
        else{
            return (xmax+xmin)/2;
        }
    }
    /**
     * Oblicza i zwraca współrzędną y środka
     * @return if !isEmpty() współrzędna y środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterY(){
        if(this.isEmpty()) return NaN;
        else{
            return (ymax + ymin)/2;
        }
    }

    /**
     * Oblicza odległość pomiędzy środkami this bounding box oraz bbx
     * @param bbx prostokąt, do którego liczona jest odległość
     * @return if !isEmpty odległość, else wyrzuca wyjątek lub zwraca maksymalną możliwą wartość double
     * Ze względu na to, że są to współrzędne geograficzne, zamiast odległości użyj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod można znaleźć w Internecie...
     */
    double distanceTo(BoundingBox bbx){
        if(this.isEmpty() || bbx.isEmpty()){
            return NaN;
        }
        else{
            double lat1 = this.getCenterX();
            double lat2 = bbx.getCenterX();
            double lon1 = this.getCenterY();
            double lon2 = bbx.getCenterY();

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);


            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            double a = Math.pow(Math.sin(dLat / 2), 2) +
                    Math.pow(Math.sin(dLon / 2), 2) *
                            Math.cos(lat1) *
                            Math.cos(lat2);
            double rad = 6371;
            double c = 2 * Math.asin(Math.sqrt(a));

            return rad * c;
        }
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        s
                .append(xmax)
                .append(",")
                .append(ymax)
                .append(",")
                .append(xmin)
                .append(",")
                .append(ymin);
        String x = s.toString();
        return x;
    }

}
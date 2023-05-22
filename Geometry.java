import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Geometry <T>{
    public static <T, State> State fold(Iterable<T> i,
                                        final State st0,
                                        BiFunction<State, T, State> f) {
        State st = st0;
        for (final T e : i)
            st = f.apply(st, e);
        return st;
    }

    public static <T> double sumBy(Iterable<T> i, Function<T, Double> f){
        return Geometry.fold(i,0.0,(x,y)->x+f.apply(y) );
    }

    public static <T> int compareBy(T s1, T s2, Function<T, Double> f){
        return (int)(f.apply(s1)-f.apply(s2));
    }
    public static class Edge implements Comparable<Edge> {
        private final double len;
        public Edge(double len) { this.len = len; }
        public double length() { return len; }
        @Override
        public int compareTo(Edge s) {
           return Geometry.compareBy(this,s,Edge::length);
        }
    }


    public interface Surface extends Comparable<Surface> {
        double area();
        double perimiter();
        @Override
        default int compareTo(Surface s) {
            return Geometry.compareBy(this, s, Surface::area);
        }
    }

    public interface Polygon extends Surface, Iterable<Edge> {
        @Override
        default double perimiter() {
            return Geometry.sumBy(this, Edge::length);
        }
    }

    public interface Solid extends Comparable<Solid> {
        double outerArea(); // area laterale totale
        double volume();
        @Override
        default int compareTo(Solid s) {
            return Geometry.compareBy(this,s,Solid::volume);
        }
    }
    public interface Polyhedron<P extends Polygon> extends Solid, Iterable<P> {
        @Override
        default double outerArea() {
            return Geometry.sumBy(this,P::area);
        }
    }

    public  static  class Sfere implements Solid{
        private final double ray;
        public Sfere(double ray){
            this.ray=ray;
        }

        @Override
        public double outerArea(){
            return 4*Math.PI*Math.pow(ray,2);
        }

       @Override
       public double volume(){
            return (4/3)*Math.PI*Math.pow(ray,3);
       }
    }

    public static class Cilinder implements Solid{
        private final double ray,height;
        public Cilinder(double ray, double height){
            this.ray=ray;
            this.height=height;
        }

        @Override
        public double volume(){
            return Math.PI*Math.pow(ray,2)*height;
        }

        @Override
        public double outerArea(){
            return 2*(Math.PI*2*ray)+height*(Math.PI*2*ray);
        }
    }

    public static class Rectangle implements Polygon {
        private final double b,h;
        public Rectangle(double b,double h){
            this.b=b;
            this.h=h;
        }

        public Edge getB(){
            return new Edge(b);
        }
        public Edge getH(){
            return new Edge(h);
        }
        @Override
        public double area(){
            return b*h;
        }

        @Override
        public Iterator<Edge> iterator() {
           return new Iterator<Edge>(){
                private  int cnt=0;
                @Override
               public boolean hasNext(){
                    return cnt<=3;
                }

                @Override
               public Edge next(){
                   switch (cnt){
                       case 0:
                       case 2:
                           ++cnt;
                           return getB();
                       case 1:
                       case 3:
                           ++cnt;
                           return getH();
                       default:
                           return new Edge(0.);
                   }
                }
            };
        }
    }


    public static class Square extends Rectangle{
        private final double i;
        public Square (double i){
            super(i,i);
            this.i=i;
        }
    }


    public class Parallelepiped implements Polyhedron<Rectangle> {
        protected final double width, height, depth;
        public Parallelepiped(double width, double height, double depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;
        }
        @Override
        public double volume() {
            return width*height*depth;
        }
        @Override
        public Iterator<Rectangle> iterator() {
            Rectangle r1 = new Rectangle(width, height),
                    r2 = new Rectangle(width, depth),
                    r3 = new Rectangle(height, depth);
            return List.of(r1, r2, r3, r1, r2, r3).iterator();
        }
    }


    public class Cube extends Parallelepiped{
        public Cube(double i){
            super(i,i,i);
        }
    }



}

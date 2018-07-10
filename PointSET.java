
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {
    private Set<Point2D> point2DS;
    public PointSET() {
        point2DS = new TreeSet<Point2D>();
    } // construct an empty set of points
    public boolean isEmpty() {
        return point2DS.isEmpty();
    } // is the set empty?
    public int size() {
        return point2DS.size();
    } // number of points in the set
    public void insert(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        if(!point2DS.contains(p))
            point2DS.add(p);
    } // add the point to the set (if it is not already in the set)
    public boolean contains(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        return point2DS.contains(p);
    } // does the set contain point p?
    public void draw() {
        for(Point2D point2D:point2DS){
            point2D.draw();
        }
    } // draw all points to standard draw
    public Iterable<Point2D> range(RectHV rect) {
        if(rect == null) throw new IllegalArgumentException();
        Set<Point2D> point2DSet = new TreeSet<Point2D>();
        for(Point2D point2D:this.point2DS){
            if(rect.contains(point2D)){
                point2DSet.add(point2D);
            }
        }
        return point2DSet;
    }  // all points that are inside the rectangle (or on the boundary)
    public Point2D nearest(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        if(this.point2DS.isEmpty())
            return null;
        double xmin = this.point2DS.iterator().next().x();
        double ymin = this.point2DS.iterator().next().y();
        Point2D minPoint2D = new Point2D(xmin,ymin);
        double mindistance = this.point2DS.iterator().next().distanceSquaredTo(p);
        for(Point2D point2D:this.point2DS){
            double tempDistance = point2D.distanceSquaredTo(p);
            if(tempDistance < mindistance){// && !p.equals(point2D)
                mindistance = tempDistance;
                minPoint2D = new Point2D(point2D.x(),point2D.y());
            }
        }
        return minPoint2D;
    } // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(String[] args){

    }                  // unit testing of the methods (optional)
}

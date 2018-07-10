import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class KdTree {
    private static final Boolean XCOLOR = true;
    private static final Boolean YCOLOR = false;
    private Set<Node> nodes;
    private Node root;
    private double minDis;
    private Point2D minP;
    private int size;
    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private int N;
        private boolean color;
        Node(Point2D p,RectHV rect,int N, boolean color){
            this.p = p;
            this.rect = rect;
            this.N = N;
            this.color = color;
        }
    }

    public KdTree() {
        this.root = new Node(null,null,0,XCOLOR);
        this.size = 0;
    } //construct an empty set of points
    public boolean isEmpty() {
        return this.size == 0;
    } // is the set empty?
    public int size() {
        return this.size;
    } // number of points in the set
    public void insert(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        RectHV rectHV = new RectHV(0,0,1,1);
        this.root = insert(root,p,rectHV,XCOLOR);
        this.size = this.root.N;

    }// add the point to the set (if it is not already in the set)
    private Node insert(Node node, Point2D p, RectHV rect, boolean color){
        if(node == null || node.p == null){
            return new Node(p,rect,1,color);
        }
        int compare = 0;
        double xmin = 0.0,ymin = 0.0;
        double xmax = 1.0,ymax = 1.0;
        if(color){
            if(node.p.x() < p.x() || (node.p.x() == p.x() && node.p.y() != p.y())){
                compare = 1;
            }
            if(node.p.x() > p.x()){
                compare = -1;
            }
        }else {
            if(node.p.y() < p.y() || (node.p.y() == p.y() && node.p.x() != p.x())) {
                compare = 1;
            }
            if(node.p.y() > p.y()) {
                compare = -1;
            }
        }
        if(compare > 0) {
            //一个节点绑定一个rect，只在第一次创建节点时创建rect
            if(node.rt == null && color){
                xmin = node.p.x();
                ymin = node.rect.ymin();
                xmax = node.rect.xmax();
                ymax = node.rect.ymax();
                RectHV rectHV = new RectHV(xmin,ymin,xmax,ymax);
                node.rt = insert(node.rt,p,rectHV,!color);
            }else if(node.rt == null && !color){
                xmin = node.rect.xmin();
                ymin = node.p.y();
                xmax = node.rect.xmax();
                ymax = node.rect.ymax();
                RectHV rectHV = new RectHV(xmin,ymin,xmax,ymax);
                node.rt = insert(node.rt,p,rectHV,!color);
            }else {
                node.rt = insert(node.rt, p, null, !color);
            }
        }else if(compare < 0) {
            if(node.lb == null && color) {
                xmin = node.rect.xmin();
                ymin = node.rect.ymin();
                xmax = node.p.x();
                ymax = node.rect.ymax();
                RectHV rectHV = new RectHV(xmin,ymin,xmax,ymax);
                node.lb = insert(node.lb,p,rectHV,!color);
            }else if(node.lb == null && !color){
                xmin = node.rect.xmin();
                ymin = node.rect.ymin();
                xmax = node.rect.xmax();
                ymax = node.p.y();
                RectHV rectHV = new RectHV(xmin,ymin,xmax,ymax);
                node.lb = insert(node.lb,p,rectHV,!color);
            }else {
                node.lb = insert(node.lb,p,null,!color);
            }
        };
        int l,r;
        l = node.lb == null ? 0 : node.lb.N;
        r = node.rt == null ? 0 : node.rt.N;
        node.N = l + r + 1;
        return node;
    }
    public boolean contains(Point2D p){
        if(p == null) throw new IllegalArgumentException();
        Node node = root;

        return get(root,p);
    } // does the set contain point p?
    private boolean get(Node node,Point2D point2D){
        if(node == null || node.p == null) return false;
        if(node.p.x() == point2D.x() && node.p.y() == point2D.y()){
            return  true;
        }
        if((node.color && node.p.x() < point2D.x()) || (!node.color && node.p.y() < point2D.y())){
            return get(node.rt,point2D);
        }else{
            return get(node.lb,point2D);
        }

    }
    public void draw(){
        StdDraw.clear();
        drawall(root);
    } // draw all points to standard draw

    private void drawall(Node node){
        if(node == null) return;
        double xstart,ystart;
        double xend,yend;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        if(node.color == XCOLOR){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            xstart = node.p.x();
            ystart = node.rect.ymin();
            xend = node.p.x();
            yend = node.rect.ymax();
            StdDraw.line(xstart,ystart,xend,yend);
        }else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            xstart = node.rect.xmin();
            ystart = node.p.y();
            xend = node.rect.xmax();
            yend = node.p.y();
            StdDraw.line(xstart,ystart,xend,yend);
        }
        drawall(node.lb);
        drawall(node.rt);
    }
    public Iterable<Point2D> range(RectHV rect) {
        if(rect == null) throw new IllegalArgumentException();
        List<Point2D> point2DList = new ArrayList<Point2D>();
        Node node = root;
        rangeP(node,rect,point2DList);
        return point2DList;
    }// all points that are inside the rectangle (or on the boundary)
    private void rangeP(Node node,RectHV rect,List<Point2D> point2DS){
        if(node == null || node.p == null) return;
        if(rect.intersects(node.rect)) {
            if(rect.contains(node.p)){
                point2DS.add(node.p);
            }
            rangeP(node.lb,rect,point2DS);
            rangeP(node.rt,rect,point2DS);
        }else return;
    }
    public Point2D nearest(Point2D p){
        if(p == null) throw  new IllegalArgumentException();
        if(root == null || root.p == null){
            return null;
        }
        this.minDis = p.distanceSquaredTo(root.p);
        this.minP = root.p;
        nearestP(root,p);
        return this.minP;
    } // a nearest neighbor in the set to point p; null if the set is empty

    private void nearestP(Node node,Point2D p){
        if(node == null) {
            return;
        }
        if(node.rect.distanceTo(p)*node.rect.distanceTo(p) < this.minDis){//node.rect.contains(p) ||
            double dis = p.distanceSquaredTo(node.p);
            if(dis < this.minDis ){
                this.minDis = dis;
                this.minP = new Point2D(node.p.x(),node.p.y());
            }
            boolean b;
            if(node.color = XCOLOR){
                if(p.x() < node.p.x()){
                    b = true;
                }else{
                    b = false;
                }
            }else{
                if(p.y() < node.p.y()){
                    b = true;
                }else {
                    b = false;
                }
            }
            if(b){
                nearestP(node.lb,p);
                nearestP(node.rt,p);
            }else{
                nearestP(node.rt,p);
                nearestP(node.lb,p);
            }

        }else return;
    }
}

package apolloniusjava;

import d3delaunayforprocessing.*;
import java.util.*;

/**
 * Apollonius
 */
public class Apollonius {

    /**
     * Site : disk or point with weight
     */
    public static class Site implements Comparable<Site> {

        double x;
        double y;
        double w;

        /**
         * create a new site
         * 
         * @param x location's x coordinate
         * @param y location's y coordinate
         * @param w weight (or radius of the disk)
         */
        public Site(double x, double y, double w) {
            this.x = x;
            this.y = y;
            this.w = w;
        }

        public double getWeight() {
            return this.w;
        }

        public int compareTo(Site other) {
            if (this.getWeight() > other.getWeight()) {
                return 1;
            } else if (this.getWeight() == other.getWeight()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /**
     * return the square of the distance between two sites (unweighted)
     * 
     * @param a
     * @param b
     * @return
     */
    public static double distSq(Site a, Site b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    /**
     * return the distance between two sites (unweighted)
     * 
     * @param a
     * @param b
     * @return
     */
    public static double dist(Site a, Site b) {
        return Math.sqrt(Apollonius.distSq(a, b));
    }

    /**
     * return distance between two points
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distP(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * sort sites so sites[0].w < sites[1].w < ... < sites[n-1].w
     * 
     * @param sites
     */
    public static void sortSites(Site[] sites) {
        Arrays.sort(sites);
    }

    public int n;
    public double width;
    public double height;
    int widthInt;
    int heightInt;
    // int quality = 5;
    public int scanGap = 1;
    public double[][][] bisectors;
    public Site[] sites = new Site[0];
    public Delaunay delaunay = null;

    /**
     * constructor with width and height
     * 
     * @param width
     * @param height
     */
    public Apollonius(double width, double height) {
        this.width = width;
        this.height = height;
        this.widthInt = (int) width;
        this.heightInt = (int) height;
    }

    /**
     * empty constructor
     */
    public Apollonius() {
        this(100, 100);
    }

    /**
     * constructor with data, data should be a flat array [x0,y0,w0,x1,y1,w1,...]
     * 
     * @param width
     * @param height
     * @param sites
     */
    public Apollonius(double width, double height, double[] data) {
        this.width = width;
        this.height = height;
        this.widthInt = (int) width;
        this.heightInt = (int) height;
        this.setSites(data);

    }

    /**
     * constructor with data
     * 
     * @param data
     */
    public Apollonius(double[] data) {
        this.width = 0;
        this.height = 0;
        this.widthInt = (int) width;
        this.heightInt = (int) height;
        this.setSites(data);
    }

    /**
     * set the sites used for the graph, the input should be a flat array of
     * [x0,y0,w0,x1,y1,w1,...]
     * sites will be moved all together to ensure x, y coordinate are all positive
     * width and height will be adjusted if there are sites fall outside
     * 
     * @param sites
     */
    public void setSites(double[] sites) {
        if (sites.length % 3 != 0)
            throw new Error("invalid sites");
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        this.sites = new Site[sites.length / 3];
        for (int i = 0; i < this.sites.length; i++) {
            if (sites[i * 3] > maxX) {
                maxX = sites[i * 3];
            }
            if (sites[i * 3] < minX) {
                minX = sites[i * 3];
            }

            if (sites[i * 3 + 1] > maxY) {
                maxY = sites[i * 3];
            }
            if (sites[i * 3 + 1] < minY) {
                minY = sites[i * 3];
            }
            this.sites[i] = new Site(sites[i * 3], sites[i * 3 + 1], sites[i * 3 + 2]);
        }

        double xrange = maxX - minX;
        double yrange = maxY - minY;

        if (minX < 0) {
            for (Site s : this.sites) {
                s.x += -minX;
            }
        }

        if (minY < 0) {
            for (Site s : this.sites) {
                s.y += -minY;
            }
        }

        if (xrange > this.width) {
            this.width = xrange;
            this.widthInt = (int) width;
        }

        if (yrange > this.height) {
            this.height = yrange;
            this.heightInt = (int) height;
        }
        this.n = this.sites.length;
    }

    /**
     * set the curve quality (i.e number of bisectors calculated)
     * 
     * @param scanGap a positive integer, default to 1, the larger the less
     *                bisectors get calculated
     */
    public void setScanGap(int scanGap) {
        if (scanGap < 0)
            throw new Error("scanGap should be positive");
        this.scanGap = scanGap;
    }

    public void _setSitesPure(double[] data) {
        this.sites = new Site[data.length / 3];
        for (int i = 0; i < this.sites.length; i++) {
            this.sites[i] = new Site(data[i * 3], data[i * 3 + 1], data[i * 3 + 2]);
        }
        this.n = this.sites.length;
    }

    /**
     * set the sites used for the graph
     * 
     * @param sites
     */
    public void setSites(Site[] sites) {
        this.sites = sites;
        this.n = this.sites.length;
    }

    /**
     * build the graph
     * 
     */
    public void build() {
        this.build(false);
    }

    /**
     * 
     * @param usingDelaunay if true, use delaunay triangulation
     */
    public void build(boolean usingDelaunay) {
        Site[] sites = this.sites;
        ArrayList<double[][]> allBi = new ArrayList<double[][]>();

        Apollonius.sortSites(sites);
        int n = this.n;
        // double x2r = 0, y2r = 0;
        if (usingDelaunay) {
            this.delaunay = new Delaunay(this.siteCenters());
            Set<String> processed = new HashSet<String>();
            for (int i = 0; i < n; i++) {
                Set<Integer> pool = new HashSet<Integer>();
                int[] neighbors = this.delaunay.neighbors(i);
                for (int nei : neighbors) {
                    pool.add(nei);
                    int[] nn = this.delaunay.neighbors(nei);
                    for (int nnn : nn) {
                        pool.add(nnn);
                    }
                }
                for (Integer j : pool) {
                    String key = i < j ? i + " " + j : j + " " + i;
                    if (processed.contains(key)) continue;
                    ArrayList<double[]> locSec = new ArrayList<double[]>();
                    double distIJ = Apollonius.dist(sites[i], sites[j]);
                    double weightDiffJI = sites[j].w - sites[i].w;
                    if (distIJ > weightDiffJI) {
                        double midXIJ,midYIJ,halfDistIJ;
                        halfDistIJ = distIJ/2;
                        midXIJ = (sites[i].x + sites[j].x) / 2; // ?
                        midYIJ = (sites[i].y + sites[j].y) / 2;
                     
                        double deltaY = midYIJ - sites[i].y;
                        double ph = deltaY / (halfDistIJ);
                        double th = Math.atan(ph / Math.sqrt(1 - ph * ph));
                        if (sites[i].x < sites[j].x) {
                            th = Math.PI - Math.atan(ph / Math.sqrt(1 - ph * ph));
                        }
                        int minYInt = -this.heightInt;
                        int maxYInt = this.heightInt;
                        for (int yjInt = minYInt; yjInt <= maxYInt; yjInt += this.scanGap) {
                            double b1y = 4 * (halfDistIJ * weightDiffJI) * (halfDistIJ * weightDiffJI) + 4 * (weightDiffJI * yjInt) * (weightDiffJI * yjInt) - weightDiffJI * weightDiffJI * weightDiffJI * weightDiffJI;
                            double b2y = (16 * halfDistIJ * halfDistIJ - 4 * weightDiffJI * weightDiffJI);
                            double b3y = b1y / b2y;
                            if (b3y >= 0) {
                                double x0 = Math.sqrt(b3y);
                                double x10, y10;
                                x10 = midXIJ + Math.cos(-th) * x0 - Math.sin(-th) * yjInt;
                                y10 = midYIJ + Math.sin(-th) * x0 + Math.cos(-th) * yjInt;
                                if (x10 > 0 && x10 < this.width && y10 > 0 && y10 < this.height) {
                                    double d5 = Apollonius.distP(x10, y10, sites[i].x, sites[i].y) - sites[i].w;
                                    boolean valid = true;
                                    for (Integer k : pool) {
                                        if (k != i && k != j) {
                                            double d6 = Apollonius.distP(x10, y10, sites[k].x, sites[k].y) - sites[k].w;
                                            if (d5 > d6) {
                                                valid = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (valid) {
                                        locSec.add(new double[]{x10, y10});
                                    }
                                }
                            }
                        }
                    }
                    if (locSec.size() > 0) allBi.add(locSec.toArray(new double[][]{}));
                    processed.add(i < j ? i + " " + j : j + " " + i);
                }
            }
        } else {
            for (int i = 1; i < this.n; i++) {
                for (int j = i + 1; j < this.n + 1; j++) {
                    ArrayList<double[]> locSec = new ArrayList<double[]>();
                    double distIJ = Apollonius.dist(sites[i - 1], sites[j - 1]);
                    double weightDiffJI = sites[j - 1].w - sites[i - 1].w;
                    if (distIJ > weightDiffJI) {
                        double halfDistIJ = distIJ / 2;
                        double midXIJ = (sites[i - 1].x + sites[j - 1].x) / 2;
                        double midYIJ = (sites[i - 1].y + sites[j - 1].y) / 2;
                        double deltaY = midYIJ - sites[i - 1].y;
                        double ph = deltaY / halfDistIJ;
                        double th = Math.atan(ph / Math.sqrt(1 - ph * ph));
                        if (sites[i - 1].x < sites[j - 1].x) {
                            th = Math.PI - Math.atan(ph / Math.sqrt(1 - ph * ph));
                        }
                        int minYInt = -this.heightInt;
                        int maxYInt = this.heightInt;
                        for (int yjInt = minYInt; yjInt <= maxYInt; yjInt += this.scanGap) {
                            double b1y = 4 * (halfDistIJ * weightDiffJI) * (halfDistIJ * weightDiffJI)
                                    + 4 * (weightDiffJI * yjInt) * (weightDiffJI * yjInt)
                                    - weightDiffJI * weightDiffJI * weightDiffJI * weightDiffJI;
                            double b2y = (16 * halfDistIJ * halfDistIJ - 4 * weightDiffJI * weightDiffJI);
                            double b3y = b1y / b2y;
                            if (b3y >= 0) {
                                double x0 = Math.sqrt(b3y);
                                double x10, y10;
                                x10 = midXIJ + Math.cos(-th) * x0 - Math.sin(-th) * yjInt;
                                y10 = midYIJ + Math.sin(-th) * x0 + Math.cos(-th) * yjInt;
                                if (x10 > 0 && x10 < this.width && y10 > 0 && y10 < this.height) {
                                    double d5 = Apollonius.distP(x10, y10, sites[i - 1].x, sites[i - 1].y)
                                            - sites[i - 1].w;
                                    boolean valid = true;
                                    for (int k = 1; k < n + 1; k++) {
                                        if (k != i && k != j) {
                                            double d6 = Apollonius.distP(x10, y10, sites[k - 1].x, sites[k - 1].y)
                                                    - sites[k - 1].w;
                                            if (d5 > d6) {
                                                valid = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (valid) {
                                        locSec.add(new double[] { x10, y10 });
                                    }
                                }
                            }
                        }
                    }
                    if (locSec.size() > 0)
                        allBi.add(locSec.toArray(new double[][] {}));
                }
            }
        }
        this.bisectors = allBi.toArray(new double[][][] {});
    }

    /**
     * return site centers as a flat array
     * 
     * @return
     */
    public double[] siteCenters() {
        double[] res = new double[this.sites.length * 2];
        for (int i = 0; i < this.sites.length; i++) {
            res[i * 2] = this.sites[i].x;
            res[i * 2 + 1] = this.sites[i].y;
        }
        return res;
    }

    /**
     * return sites as a flat array [x0,y0,w0,x1,y1,w1,...]
     * 
     * @return
     */
    public double[] flattenSites() {
        double[] res = new double[this.sites.length * 3];
        for (int i = 0; i < this.sites.length; i++) {
            res[i * 3] = this.sites[i].x;
            res[i * 3 + 1] = this.sites[i].y;
            res[i * 3 + 2] = this.sites[i].w;
        }
        return res;
    }

}
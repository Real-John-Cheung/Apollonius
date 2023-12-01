package apolloniusjava;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Apollonius
 */
public class Apollonius {

    /**
     * Site : disk or point with weight
     */
    class Site implements Comparable<Site> {

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
        Site(double x, double y, double w) {
            this.x = x;
            this.y = y;
            this.w = w;
        }

        double getWeight() {
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
    double distSq(Site a, Site b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    /**
     * return the distance between two sites (unweighted)
     * 
     * @param a
     * @param b
     * @return
     */
    double dist(Site a, Site b) {
        return Math.sqrt(distSq(a, b));
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
    double distP(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * sort sites so sites[0].w < sites[1].w < ... < sites[n-1].w
     * 
     * @param sites
     */
    void sortSites(Site[] sites) {
        Arrays.sort(sites);
    }

    int n;
    double width;
    double height;
    int widthInt;
    int heightInt;
    //int quality = 5;
    int scanGap = 1;
    double[][][] bisectors;
    Site[] sites = new Site[0];

    /**
     * constructor with width and height
     * 
     * @param width
     * @param height
     */
    Apollonius(double width, double height) {
        this.width = width;
        this.height = height;
        this.widthInt = (int) width;
        this.heightInt = (int) height;
    }

    /**
     * empty constructor
     */
    Apollonius() {
        this(100, 100);
    }

    /**
     * constructor with data, data should be a flat array [x0,y0,w0,x1,y1,w1,...]
     * 
     * @param width
     * @param height
     * @param sites
     */
    Apollonius(double width, double height, double[] data) {
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
    Apollonius(double[] data) {
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
    void setSites(double[] sites) {
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
            this.width = xrange ;
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
    void setScanGap(int scanGap) {
        if (scanGap < 0) throw new Error("scanGap should be positive");
        this.scanGap = scanGap;
    }

    void _setSitesPure(double[] data) {
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
    void setSites(Site[] sites) {
        this.sites = sites;
        this.n = this.sites.length;
    }

    /**
     * build the graph
     * 
     */
    void build() {
        Site[] sites = this.sites;
        ArrayList<double[][]> allBi = new ArrayList<double[][]>();

        sortSites(sites);
        int n = this.n;
        //double x2r = 0, y2r = 0;
        for (int i = 1; i < this.n; i++) {
            for (int j = i + 1; j < this.n + 1; j++) {
                ArrayList<double[]> locSec = new ArrayList<double[]>();
                // double distIJ = power(power(sites[i-1].x - sites[j-1].x, 2) + power(sites[i-1].y
                // - sites[j-1].y,2), 0.5);//
                double distIJ = dist(sites[i - 1], sites[j - 1]);
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
                    // double minY = 0, maxY = 0;
                    // int rr = 0, xj = 0;
                    // double a2r = 0.5 / weightDiffJI;
                    // while (rr == 0) {
                    //     double a1r = 16 * halfDistIJ * halfDistIJ * xj * xj - 4 * weightDiffJI * weightDiffJI * xj * xj - 4 * halfDistIJ * halfDistIJ * weightDiffJI * weightDiffJI
                    //             + weightDiffJI * weightDiffJI * weightDiffJI * weightDiffJI;
                    //     if (a1r >= 0) {
                    //         double yr = a2r * Math.sqrt(a1r);
                    //         double ymr = -yr;
                    //         x2r = midXIJ + Math.cos(-th) * xj - Math.sin(-th) * ymr;
                    //         y2r = midYIJ + Math.sin(-th) * xj + Math.cos(-th) * ymr;
                    //         if (x2r > 0 && x2r < this.width && y2r > 0 && y2r < this.height) {
                    //             double d3 = distP(x2r, y2r, sites[i - 1].x, sites[i - 1].y) - sites[i - 1].w;
                    //             int br2 = 0;
                    //             for (int k = 1; k < n + 1; k++) {
                    //                 if (k != i && k != j) {
                    //                     double d4 = distP(x2r, y2r, sites[k - 1].x, sites[k - 1].y) - sites[k - 1].w;
                    //                     if (d3 > d4) {
                    //                         br2 = 1;
                    //                         break;
                    //                     }
                    //                 }
                    //             }
                    //             if (br2 == 0) {
                    //                 locSec.add(new double[] { x2r, y2r, -1 });
                    //             }
                    //             if (ymr < minY) {
                    //                 minY = ymr;
                    //             }
                    //             if (ymr > maxY) {
                    //                 maxY = ymr;
                    //             }
                    //         }
                    //     }
                    //     xj+=this.scanGap;
                    //     int rwe = 0;
                    //     if (x2r < 0 || x2r > this.width || y2r < 0 || y2r > this.height) {
                    //         rwe++;
                    //     }
                    //     if (rwe == 1) {
                    //         rr = 1;
                    //     }
                    //     if (xj < this.quality) {// ?
                    //         rr = 0;
                    //     }
                    // }
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
                                double d5 = distP(x10, y10, sites[i - 1].x, sites[i - 1].y) - sites[i - 1].w;
                                boolean valid = true;
                                for (int k = 1; k < n + 1; k++) {
                                    if (k != i && k != j) {
                                        double d6 = distP(x10, y10, sites[k - 1].x, sites[k - 1].y) - sites[k - 1].w;
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
                if (locSec.size() > 0) allBi.add(locSec.toArray(new double[][]{}));
            }
        }
        this.bisectors = allBi.toArray(new double[][][]{});
    }

    /**
     * return site centers as a flat array
     * 
     * @return
     */
    double[] siteCenters() {
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
    double[] flattenSites() {
        double[] res = new double[this.sites.length * 3];
        for (int i = 0; i < this.sites.length; i++) {
            res[i * 3] = this.sites[i].x;
            res[i * 3 + 1] = this.sites[i].y;
            res[i * 3 + 2] = this.sites[i].w;
        }
        return res;
    }

}
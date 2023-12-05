/**
     * Site : disk or point with weight
     */
class Site {


    /**
     * create a new site
     * @param {number} x 
     * @param {number} y 
     * @param {number} w 
     */
    constructor(x, y, w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    /**
     * 
     * @returns weight of the site
     */
    getWeight() {
        return this.w;
    }

}

/**
 * Apollonius
 */
class Apollonius {


    /**
     * return the square of the distance between two sites (unweighted)
     * 
     * @param {Site} a 
     * @param {Site} b 
     * @returns the square of the distance
     */
    static distSq(a, b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    /**
     * return the distance between two sites (unweighted)
     * 
     * @param {Site} a 
     * @param {Site} b 
     * @returns 
     */
    static dist(a, b) {
        return Math.sqrt(Apollonius.distSq(a, b));
    }

    /**
     * return the weighted distance between two sites
     * the distance can be negative, if so it means one of the disk is inside of the another
     * 
     * @param {number} a 
     * @param {number} b 
     * @returns 
     */
    static distW(a,b){
        return Apollonius.dist(a,b) - a.w - b.w;
    }


    /**
     * return distance between two points
     * 
     * @param {number} x1 
     * @param {number} y1 
     * @param {number} x2 
     * @param {number} y2 
     * @returns 
     */
    static distP(x1, y1, x2, y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }


    /**
     * site compare function
     * 
     * @param {Site} a 
     * @param {Site} b 
     * @returns 
     */
    static compareSite(a, b) {
        if (a.getWeight() > b.getWeight()) {
            return 1;
        } else if (a.getWeight() === b.getWeight()) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * sort sites so sites[0].w < sites[1].w < ... < sites[n-1].w
     * 
     * @param {Array} sites
     */
    static sortSites(sites) {
        sites = sites.sort(Apollonius.compareSite);
    }


    /**
     * constructor
     * 
     * @param {number} width 
     * @param {number} height 
     * @param {Array} data data should be a flat array [x0,y0,w0,x1,y1,w1,...]
     */
    constructor(width, height, data) {
        if (arguments.length === 0) {
            width = 100;
            height = 100;
        } else if (arguments.length === 1) {
            data = width;
            width = 100;
            height = 100;
        }
        this.width = width;
        this.height = height;
        this.widthInt = Math.floor(width);
        this.heightInt = Math.floor(height);
        if (data && data.length) this.setSites(data);
        this.scanGap = 1;
        this.bisectors = [];
    }



    /**
     * set the sites used for the graph
     * sites will be moved all together to ensure x, y coordinate are all positive
     * width and height will be adjusted if there are sites fall outside
     * 
     * @param {Array} data input sites, should be a flat array [x0,y0,w0,x1,y1,w1,...]
     * @param {*} pure if true, sites will not be moved and width and height will not be adjusted
     * @returns 
     */
    setSites(data, pure) {
        if (!Array.isArray(data)) throw new Error("data should be an array");
        if (data[0] instanceof Site) {
            this.sites = data;
            return;
        }
        if (data.length % 3 != 0) throw new Error("invalid data");
        if (pure) {
            this.sites = [];
            for (let i = 0; i < data.length / 3; i++) {
                this.sites[i] = new Site(data[i * 3], data[i * 3 + 1], data[i * 3 + 2]);
            }
            this.n = this.sites.length;
            return
        }
        let minX = Number.POSITIVE_INFINITY, minY = Number.POSITIVE_INFINITY, maxX = Number.NEGATIVE_INFINITY, maxY = Number.NEGATIVE_INFINITY;
        this.sites = [];
        for (let i = 0; i < data.length / 3; i++) {
            if (data[i * 3] > maxX) {
                maxX = data[i * 3];
            }
            if (data[i * 3] < minX) {
                minX = data[i * 3];
            }

            if (data[i * 3 + 1] > maxY) {
                maxY = data[i * 3];
            }
            if (data[i * 3 + 1] < minY) {
                minY = data[i * 3];
            }
            this.sites[i] = new Site(data[i * 3], data[i * 3 + 1], data[i * 3 + 2]);
        }

        let xrange = maxX - minX, yrange = maxY - minY;

        if (minX < 0) {
            this.sites.forEach(site => { site.x += -minX });
        }

        if (minY < 0) {
            this.sites.forEach(site => { site.y += -minY });
        }

        if (xrange > this.width) {
            this.width = xrange;
            this.widthInt = Math.floor(width);
        }

        if (yrange > this.height) {
            this.height = yrange;
            this.heightInt = Math.floor(this.height);
        }
        this.n = this.sites.length;
    }

    /**
     * set the curve quality (i.e number of bisectors calculated)
     * 
     * @param {number} scanGap a positive integer, default to 1, the larger the less bisectors get calculated
     */
    setScanGap(scanGap) {
        if (scanGap < 0) throw new Error("scanGap should be positive");
        this.scanGap = scanGap;
    }

    /**
     * build the graph
     * 
     */
    build() {
        let sites = this.sites;
        let allBi = [];

        Apollonius.sortSites(sites);
        let n = this.n;
        //double x2r = 0, y2r = 0;
        for (let i = 1; i < this.n; i++) {
            for (let j = i + 1; j < this.n + 1; j++) {
                let locSec = [];
                // double distIJ = power(power(sites[i-1].x - sites[j-1].x, 2) + power(sites[i-1].y
                // - sites[j-1].y,2), 0.5);//
                let distIJ = Apollonius.dist(sites[i - 1], sites[j - 1]);
                let weightDiffJI = sites[j - 1].w - sites[i - 1].w;
                if (i === 1) text(i, sites[i-1].x,sites[i-1].y)
                text(j,sites[j-1].x,sites[j-1].y)
                if (j === 4) console.log(distIJ, weightDiffJI);
                if (distIJ > weightDiffJI) {
                    let halfDistIJ = distIJ / 2;
                    let midXIJ = (sites[i - 1].x + sites[j - 1].x) / 2;
                    let midYIJ = (sites[i - 1].y + sites[j - 1].y) / 2;
                    let deltaY = midYIJ - sites[i - 1].y;
                    let ph = deltaY / halfDistIJ;
                    let th = Math.atan(ph / Math.sqrt(1 - ph * ph));
                    if (sites[i - 1].x < sites[j - 1].x) {
                        th = Math.PI - Math.atan(ph / Math.sqrt(1 - ph * ph));
                    }
                    let minYInt = -this.heightInt;
                    let maxYInt = this.heightInt;
                    for (let yjInt = minYInt; yjInt <= maxYInt; yjInt += this.scanGap) {
                        let b1y = 4 * (halfDistIJ * weightDiffJI) * (halfDistIJ * weightDiffJI) + 4 * (weightDiffJI * yjInt) * (weightDiffJI * yjInt) - weightDiffJI * weightDiffJI * weightDiffJI * weightDiffJI;
                        let b2y = (16 * halfDistIJ * halfDistIJ - 4 * weightDiffJI * weightDiffJI);
                        let b3y = b1y / b2y;
                        if (b3y >= 0) {
                            let x0 = Math.sqrt(b3y);
                            let x10, y10;
                            x10 = midXIJ + Math.cos(-th) * x0 - Math.sin(-th) * yjInt;
                            y10 = midYIJ + Math.sin(-th) * x0 + Math.cos(-th) * yjInt;
                            if (x10 > 0 && x10 < this.width && y10 > 0 && y10 < this.height) {
                                let d5 = Apollonius.distP(x10, y10, sites[i - 1].x, sites[i - 1].y) - sites[i - 1].w;
                                let valid = true;
                                for (let k = 1; k < n + 1; k++) {
                                    if (k != i && k != j) {
                                        let d6 = Apollonius.distP(x10, y10, sites[k - 1].x, sites[k - 1].y) - sites[k - 1].w;
                                        if (d5 > d6) {
                                            valid = false;
                                            break;
                                        }
                                    }
                                }
                                if (valid) {
                                    locSec.push([x10, y10]);
                                }
                            }
                        }
                    }
                }
                if (locSec.length > 0) allBi.push(locSec);
            }
        }
        this.bisectors = allBi;
    }

    /**
     * return site centers as a flat array
     * 
     * @return
     */
    siteCenters() {
        let res = [];
        for (let i = 0; i < this.sites.length; i++) {
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
    flattenSites() {
        let res = [];
        for (let i = 0; i < this.sites.length; i++) {
            res[i * 3] = this.sites[i].x;
            res[i * 3 + 1] = this.sites[i].y;
            res[i * 3 + 2] = this.sites[i].w;
        }
        return res;
    }

}
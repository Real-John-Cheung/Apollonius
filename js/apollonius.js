class Apollonius {
    /**
     * create a new graph
     * 
     * @param {number} width width of the graph
     * @param {number} height height of the graph
     * @param {Array} data data should be a flat array [x0,y0,w0,x1,y1,w1,...]
     */
    constructor(width, height, data) {
        if (arguments.length < 1) {
            width = 100;
            height = 100;
        } else if (arguments.length === 1) {
            data = arguments[0];
            width = 100;
            height = 100;
        }
        this.width = width;
        this.height = height;
        this.widthInt = Math.floor(width);
        this.heightInt = Math.floor(height);
        this.scanGap = 1;
        if (data && data.length) this.setSites(data);
    }

    /**
     * set the sites used for the graph, the input should be a flat array of
     * [x0,y0,w0,x1,y1,w1,...]
     * if pure is not true, sites will be moved all together to ensure x, y coordinate are all positive
     * width and height will be adjusted if there are sites fall outside
     * @param {Array} data 
     * @param {boolean} pure if true, no change will be made to width and height 
     */
    setSites(data, pure = false) {
        if (!data || !data.length) return;
        if (!Array.isArray(data)) throw new Error("data should be an array");
        if (data[0] instanceof Site) {
            this.sites = data;
            this.n = this.sites.length;
            return;
        }
        if (data.length % 3 !== 0) throw new Error("invalid data");
        if (pure) {
            this.sites = [];
            for (let i = 0; i < data.length / 3; i++) {
                this.sites.push(new Site(data[i * 3], data[i * 3 + 1], data[i * 3 + 2]));
            }
            this.n = this.sites.length;
            return;
        }

        let minX = Number.POSITIVE_INFINITY, minY = Number.POSITIVE_INFINITY, maxX = Number.NEGATIVE_INFINITY, maxY = Number.NEGATIVE_INFINITY;
        this.sites = [];
        for (let i = 0; i < data.length / 3; i++) {
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
        let xRange = maxX - minX, yRange = maxY - minY;

        if (minX < 0) {
            this.sites.forEach(s => { s.x += -minX });
        }

        if (minY < 0) {
            this.sites.forEach(s => { s.y += -minY });
        }

        if (xRange > this.width) {
            this.width = xRange;
            this.widthInt = Math.floor(this.width);
        }

        if (yRange > this.height) {
            this.height = yRange;
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
     */
    build(){
        let sites = this.sites;
        let allBi = [];
        sortSites(sites);
        let n = this.n;
        for (let i = 1; i < this.n - 1; i++){
            for (let j = i + 1; j < this.n + 1; j ++){
                let locSec = [];
                let distIJ = distIJ = dist(sites[i - 1], sites[j - 1]);
                let weightDiffJI = sites[j - 1].w - sites[i - 1].w;
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
                    for (let yjInt = minYInt; yjInt <=maxYInt; yjInt += this.scanGap) {
                        let b1y = 4 * (halfDistIJ * weightDiffJI) * (halfDistIJ * weightDiffJI) + 4 * (weightDiffJI * yjInt) * (weightDiffJI * yjInt) - weightDiffJI * weightDiffJI * weightDiffJI * weightDiffJI;
                        let b2y = (16 * halfDistIJ * halfDistIJ - 4 * weightDiffJI * weightDiffJI);
                        let b3y = b1y/b2y;
                        if (b3y >=0 ){
                            let x0 = Math.sqrt(b3y);
                            let x10,j10;
                            x10 = midXIJ + Math.cos(-th) * x0 - Math.sin(-th) * yjInt;
                            y10 = midYIJ + Math.sin(-th) * x0 + Math.cos(-th) * yjInt;
                            if (x10 > 0 && x10 < this.width && y10 > 0 && y10 < this.height){
                                let d5 = distP(x10, y10, sites[i - 1].x, sites[i - 1].y) - sites[i - 1].w;
                                let valid = true;
                                for (let k = 1 ; k < n; k++){
                                    if (k != i && k != j) {
                                        let d6 = distP(x10, y10, sites[k - 1].x, sites[k - 1].y) - sites[k - 1].w;
                                        if (d5 > d6) {
                                            valid = false;
                                            break;
                                        }
                                    }
                                }
                                if (valid){
                                    locSec.push([x10,y10]);
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
     * @returns 
     */
    siteCenters(){
        return this.sites.map(s => [s.x,s.y]).flat();
    }

    /**
     * return sites as a flat array [x0,y0,w0,x1,y1,w1,...]
     * 
     * @returns 
     */
    flattenSites(){
        return this.sites.map(s => [s.x,s.y,s.w]).flat();
    }

}

class Site {
    /**
     * create a new Site
     * 
     * @param {number} x location's x coordinate
     * @param {number} y location's y coordinate
     * @param {number} w weight (or radius of the disk)
     */
    constructor(x, y, w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    /**
     * 
     * @returns weight of this site
     */
    getWeight() {
        return this.w;
    }
}

/**
 * return the square of the distance between two sites (unweighted)
 * 
 * @param {Site} a site a 
 * @param {Site} b site b
 * @returns the square of the distance
 */
function distSq(a, b) {
    return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
}

/**
 * return the distance between two sites (unweighted)
 * 
 * @param {Site} a 
 * @param {Site} b 
 * @returns the distance
 */
function dist(a, b) {
    return Math.sqrt(distSq(a, b));
}

/**
 * return distance between two points
 * 
 * @param {number} x1 
 * @param {number} x2 
 * @param {number} y1 
 * @param {number} y2 
 * @returns 
 */
function distP(x1, x2, y1, y2) {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

/**
 * sort sites so sites[0].w < sites[1].w < ... < sites[n-1].w
 * @param {Array} sites array of sites
 */
function sortSites(sites) {
    sites.sort(_compare);
}

function _compare(a, b) {
    if (a.getWeight() > b.getWeight()) {
        return 1;
    } else if (a.getWeight() === b.getWeight()) {
        return 0;
    } else {
        return -1;
    }
}
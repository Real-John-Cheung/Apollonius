/**
 * implementation of the additive algorithm proposed by Karavelas, Menelaos & Yvinec, Mariette. (2002). Dynamic Additively Weighted Voronoi Diagrams in 2D. 586-598. 10.1007/3-540-45749-6_52. 
 */

class AWVoronoi {
    /**
     * 
     * @param {number} x 
     * @returns the sign of x
     */
    static sign(x) {
        if (x < 0) return -1;
        if (x > 0) return 1;
        return 0;
    }

    /**
     * 
     * @param {number} sign -1, 0, or 1
     * @returns 
     */
    static opposite(sign) {
        return -sign;
    }

    /**
     * compute the sign of quantity a + b * sqrt(c)
     * @param {*} a 
     * @param {*} b 
     * @param {*} c 
     * @returns 
     */
    static signAPlusBxSqrtC(a, b, c) {
        if (c < 0) throw new Error("invalid sqrt")
        let signA = this.sign(a);
        if (this.sign(c) === 0) return signA;
        let signB = this.sign(b);
        if (signA === signB) return signA;
        if (signA === 0) return signB;
        return signA * this.sign(a * a - b * b * c);
    }

    /**
     * compute the sign of quantity a *sqrt(c) + b * sqrt(d)
     * @param {number} a 
     * @param {number} b 
     * @param {number} c 
     * @param {number} d 
     * @returns 
     */
    static signAxSqrtCPlusBxSqrtD(a, b, c, d) {
        if (c < 0 || d < 0) throw new Error("invalid sqrt");
        let signB = this.sign(b);
        if (this.sign(d) === 0) return this.sign(a * c);
        if (this.sign(c) === 0) return signB;

        let signA = this.sign(a);
        if (signA === signB) return signA;
        if (signA === 0) return signB;

        return signA * (a * a * c - b * b * d);
    }

    /**
     * compute the sign of a + b * sqrt(e) + c * sqrt(f)
     * @param {number} a 
     * @param {number} b 
     * @param {number} c 
     * @param {number} e 
     * @param {number} f 
     * @returns 
     */
    static signAPlusBxSqrtEPlusCxSqrtF(a, b, c, e, f) {
        if (e < 0 || f < 0) throw new Error("invalid sqrt");
        let sAPlusBxSqrtE = this.signAPlusBxSqrtC(a, b, e);
        if (this.sign(f) === 0) return sAPlusBxSqrtE;
        let signC = this.sign(c);
        if (sAPlusBxSqrtE === signC || sAPlusBxSqrtE === 0) return signC;

        return sAPlusBxSqrtE * this.signAPlusBxSqrtC(a * a + b * b * e - c * c * f, 2 * a * b, e);
    }

    /**
     * compute the sign of a + b * sqrt(e) + c * sqrt(f) + d * sqrt(e * f)
     * @param {number} a 
     * @param {number} b 
     * @param {number} c 
     * @param {number} d 
     * @param {number} e 
     * @param {number} f 
     * @returns 
     */
    static signAPlusBxSqrtEPlusCxSqrtFPlusDxSqrtEF(a, b, c, d, e, f) {
        if (e < 0 || f < 0) throw new Error("invalid sqrt");
        let sAPlusBSqrtE = this.signAPlusBxSqrtC(a, b, e);
        let sCPlusDSqrtE = this.signAPlusBxSqrtC(c, d, e);
        if (sAPlusBSqrtE === sCPlusDSqrtE) return sAPlusBSqrtE;
        if (sAPlusBSqrtE === 0) return sAPlusBSqrtE;

        return sAPlusBSqrtE * this.signAPlusBxSqrtC(a * a + b * b * e - c * c * f - d * d * e * f, 2 * (a * b - c * d * d), e);
    }


    /**
     * compute the sign of the first roof of x^2 - bx + c = 0
     * @param {number} signB sign of b 0,1,or-1
     * @param {number} signC sign of c 0,1,or-1
     * @returns 
     */
    static signOfFirstRoot(signB, signC) {
        if (signC === -1) return -1;
        if (signB === 1) return signC;
        if (signB === -1) return -1;
        return this.opposite(signC);
    }

    /**
     * compute the sign of the second roof of x^2 - bx + c = 0
     * @param {number} signB sign of b 0,1,or-1
     * @param {number} signC sign of bc0,1,or-1
     * @returns 
     */
    static signOfSecondRoot(signB, signC) {
        if (signC === -1) {
            return 1;
        }
        if (signB === 1) return -1;
        if (signB === -1) return this.opposite(signC);
        return signC;
    }

    /**
     * 
     * @param {Site} s1 
     * @param {Site} s2 
     * @returns 1 if s1.x > s2.x, 0 if s1.x = s2.x, -1 if s1.x < s2.x
     */
    static compareX(s1, s2){
        if (s1.x > s2.x) return 1;
        if (s1.x === s2.x) return 0;
        return -1;
    }

    /**
     * 
     * @param {Site} s1 
     * @param {Site} s2 
     * @returns 1 if s1.y > s2.y, 0 if s1.y = s2.y, -1 if s1.y < s2.y
     */
    static compareY(s1,s2){
        if (s1.y > s2.y) return 1;
        if (s1.y === s2.y) return 0;
        return -1;
    }

    /**
     * 
     * @param {Site} s1 
     * @param {Site} s2 
     * @returns 1 if s1.w > s2.w, 0 if s1.w = s2.w, -1 if s1.w < s2.w
     */
    static compareWeight(s1,s2){
        if (s1.w > s2.w) return 1;
        if (s1.w === s2.w) return 0;
        return -1;
    }

    /**
     * 
     * @param {number} x1 
     * @param {number} y1 
     * @param {number} x2 
     * @param {number} y2 
     * @returns the square of the Euclidean distance between point [x1,y1] and point[x2,y2]
     */
    static distPSq(x1, y1, x2, y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    /**
     * 
     * @param {number} x1 
     * @param {number} y1 
     * @param {number} x2 
     * @param {number} y2 
     * @returns the Euclidean distance between point [x1,y1] and point[x2,y2]
     */
    static distP(x1, y1, x2, y2) {
        return Math.sqrt(AWVoronoi.distPSq(x1, y1, x2, y2))
    }

    /**
     * 
     * @param {Array} p a point on the 2D plane, [x,y] 
     * @param {Site} d a disk on the 2D plane
     * @returns the distance between the point and the disk
     */
    static distPtoS(p, d) {
        return AWVoronoi.distP(p[0], p[1], d.x, d.y) - d.w;
    }

    /**
     * 
     * @param {Site} a 
     * @param {Site} b 
     * @returns the distance between two sites
     */
    static distS(a, b) {
        return AWVoronoi.distP(a.x, a.y, b.x, b.y) - a.w - b.w;
    }

    /**
     * check if b is contained in a
     * 
     * @param {Site} a 
     * @param {Site} b 
     * @returns true of b is contained in a
     */
    static isContained(a, b) {
        return this.distS(a, b) < -2 * b.w;
    }

    /**
     * check if a or b is closer to q
     * 
     * @param {Array} q [x,y]
     * @param {Site} a 
     * @param {Site} b 
     * @returns the closer site
     */
    static closerSite(q, a, b) {
        const A = b.r - a.r
        const B = -1;
        const C = this.distP(q[0], q[1], b.x, b.y)
        return //AWVoronoi.distPtoS([q.x,q.y], a) < AWVoronoi.distPtoS([q.x,q.y], b) ? a : b;
    }

    constructor() {
        this.sites = [];
        this.nonTrivialSites = [];
    }
}

/**
 * class for Site
 */
class Site {
    /**
     * 
     * @param {number} x 
     * @param {number} y 
     * @param {number} w 
     */
    constructor(x, y, w) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.children = [];
        this.parent = null;
    }
}

/**
 * class for VoronoiCircle
 */
class VoronoiCircle {
    /**
     * 
     * @param {number} x 
     * @param {number} y 
     * @param {number} r 
     */
    constructor(x, y, r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }
}

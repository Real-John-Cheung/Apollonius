import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Color;

//Additively Weighted Voronoi diagram
//Takashi OHYAMA
//Make 1999/5/7
//Update 2010/7/3 Adding comments
public class awvoro extends java.applet.Applet {
    Color col1, col2, col3;
    double piaw = 3.14159265358979;
    int Naw;
    int takaaw, habaaw;
    int kaw, iaw, jaw;
    double alaw, beaw, al2aw, xcaw, ycaw, yyaw, phaw, thaw, minyaw, maxyaw;
    int rraw, xjaw;
    double a1raw, a2raw, yraw;
    double ymraw, x2raw, y2raw, d3aw, d4aw;
    int x2rawI, y2rawI, rweaw;
    int yjawI, minyawI, maxyawI, x10awI, y10awI;
    double b1yaw, b2yaw, b3yaw, x0aw, x10aw, y10aw, d5aw, d6aw;
    int br2aw, br3aw;
    String NS, habaS, takaS;
    double Nd, takad, habad;

    // convert to double doubleŒ^‚É•ÏŠ·
    public double dou(String dous) {
        double dou1;
        dou1 = (Double.valueOf(dous)).doubleValue();
        return dou1;
    }

    // random variable ˆê—l—”‚Ì¶¬
    public double randaw() {
        double rand1aw;
        rand1aw = Math.random();
        return rand1aw;
    }

    // initialize ‰Šúˆ—
    public void init() {
        col1 = Color.black;
        col2 = Color.yellow;
        col3 = Color.white;

        // getting parameters from HTML file HTMLƒtƒ@ƒCƒ‹‚©‚çƒpƒ‰ƒ[ƒ^[‚ðŽæ“¾
        takaS = getParameter("takap");// getting height of screen from HTML file HTML‚©‚ç‰æ–Ê‚Ì‚‚³‚ðŽæ“¾
        habaS = getParameter("habap");// getting width of screen from HTML file HTML‚©‚ç‰æ–Ê‚Ì•‚ðŽæ“¾
        NS = getParameter("Np");// getting number of generators from HTML file HTML‚©‚ç•ê“_‚Ì”‚ðŽæ“¾
        habad = dou(habaS);
        takad = dou(takaS);
        Nd = dou(NS);
        if (Nd == 6) {
            Nd = 4 + 16 * randaw();
        }
        habaaw = (int) habad;// width of screen ‰æ–Ê‚Ì•
        takaaw = (int) takad;// height of screen ‰æ–Ê‚Ì‚‚³
        Naw = (int) Nd;// number of generators “_‚Ì”
    }

    double x1aw[] = new double[100];
    double y1aw[] = new double[100];
    double w1aw[] = new double[100];
    int xaw[] = new int[100];
    int yaw[] = new int[100];
    int waw[] = new int[100];
    double saw[] = new double[100];
    String sssaw[] = new String[100];

    // compute a^b a^b‚ðŒvŽZ
    public double jouaw(double aaw, double baw) {
        double jou1aw;
        jou1aw = Math.pow(aaw, baw);
        return jou1aw;
    }

    // atan arc-tan
    public double artnaw(double ataw) {
        double artn1aw;
        artn1aw = Math.atan(ataw);
        return artn1aw;
    }

    // sin
    public double sainaw(double saiaw) {
        double sain1aw;
        sain1aw = Math.sin(saiaw);
        return sain1aw;
    }

    // cos
    public double kosaw(double kosainaw) {
        double kos1aw;
        kos1aw = Math.cos(kosainaw);
        return kos1aw;
    }

    // sort such that he1[0]<he1[1]<...<he1[NN-1] ‚Æ‚È‚é‚æ‚¤‚Éƒ\[ƒg
    void heapaw(double he1aw[], double he2aw[], double he3aw[], int NNaw) {
        int kkaw, kksaw, iiaw, jjaw, mmaw;
        double b1aw, b2aw, b3aw, c1aw, c2aw, c3aw;
        kksaw = (int) (NNaw / 2);
        for (kkaw = kksaw; kkaw >= 1; kkaw--) {
            iiaw = kkaw;
            b1aw = he1aw[iiaw - 1];
            b2aw = he2aw[iiaw - 1];
            b3aw = he3aw[iiaw - 1];
            while (2 * iiaw <= NNaw) {
                jjaw = 2 * iiaw;
                if (jjaw + 1 <= NNaw) {
                    if (he1aw[jjaw - 1] < he1aw[jjaw]) {
                        jjaw++;
                    }
                }
                if (he1aw[jjaw - 1] <= b1aw) {
                    break;
                }
                he1aw[iiaw - 1] = he1aw[jjaw - 1];
                he2aw[iiaw - 1] = he2aw[jjaw - 1];
                he3aw[iiaw - 1] = he3aw[jjaw - 1];
                iiaw = jjaw;
            } // wend
            he1aw[iiaw - 1] = b1aw;
            he2aw[iiaw - 1] = b2aw;
            he3aw[iiaw - 1] = b3aw;
        } // next kk
        for (mmaw = NNaw - 1; mmaw >= 1; mmaw--) {
            c1aw = he1aw[mmaw];
            c2aw = he2aw[mmaw];
            c3aw = he3aw[mmaw];
            he1aw[mmaw] = he1aw[0];
            he2aw[mmaw] = he2aw[0];
            he3aw[mmaw] = he3aw[0];
            iiaw = 1;
            while (2 * iiaw <= mmaw) {
                kkaw = 2 * iiaw;
                if (kkaw + 1 <= mmaw) {
                    if (he1aw[kkaw - 1] <= he1aw[kkaw]) {
                        kkaw++;
                    }
                }
                if (he1aw[kkaw - 1] <= c1aw) {
                    break;
                }
                he1aw[iiaw - 1] = he1aw[kkaw - 1];
                he2aw[iiaw - 1] = he2aw[kkaw - 1];
                he3aw[iiaw - 1] = he3aw[kkaw - 1];
                iiaw = kkaw;
            } // wend
            he1aw[iiaw - 1] = c1aw;
            he2aw[iiaw - 1] = c2aw;
            he3aw[iiaw - 1] = c3aw;
        } // next mm
    }

    // Main subroutine ƒƒCƒ“•”
    public void paint(java.awt.Graphics g) {
        g.setColor(col1);
        g.fillRect(1, 1, habaaw, takaaw);
        g.setColor(col2);
        g.drawString("N=" + Naw, 15, 15);

        // set coorinates and weights of generators randomly
        // À•W‚Æd‚Ý‚ð—”‚ðŽg‚Á‚ÄŒˆ‚ß‚é
        for (kaw = 1; kaw <= Naw; kaw++) {
            x1aw[kaw - 1] = randaw() * (habaaw - 30) + 15;// x-coordinate xÀ•W
            y1aw[kaw - 1] = randaw() * (takaaw - 30) + 15;// y-coordinate yÀ•W
            w1aw[kaw - 1] = randaw() * 100 + 1;// weights d‚Ý
            xaw[kaw - 1] = (int) (x1aw[kaw - 1] + 0.5);
            yaw[kaw - 1] = (int) (y1aw[kaw - 1] + 0.5);
            waw[kaw - 1] = (int) (w1aw[kaw - 1] + 0.5);
            sssaw[kaw - 1] = "" + waw[kaw - 1];
            g.drawString(sssaw[kaw - 1], xaw[kaw - 1] - 3, yaw[kaw - 1] - 3);
            g.fillOval(xaw[kaw - 1] - 2, yaw[kaw - 1] - 2, 4, 4);
        }
        g.drawLine(habaaw - 150, 10, habaaw - 50, 10);
        g.drawLine(habaaw - 150, 5, habaaw - 150, 15);
        g.drawLine(habaaw - 100, 5, habaaw - 100, 10);
        g.drawLine(habaaw - 50, 5, habaaw - 50, 15);
        g.drawString("0", habaaw - 158, 13);
        g.drawString("100", habaaw - 48, 13);

        // Sort generators such that w1[0]<w1[1]<...<w1[N-1] ‚Æ‚È‚é‚æ‚¤‚É•ê“_‚ðƒ\[ƒg
        heapaw(w1aw, x1aw, y1aw, Naw);
        g.setColor(col3);

        // Consider bisector of i and j. i‚Æj‚É‘Î‚·‚é‹«ŠEü‚ðl‚¦‚Ü‚·
        // Regarding to Additively weighted Voronoi diagram, bisectors are hyperbolic
        // arc, that is, difference of two distances is constant. Two distances are
        // distance to i and distance to j.
        // ‚`‚v(‰Á–@“Id‚Ý•t‚«)ƒ{ƒƒmƒC}‚Å‚Í‹«ŠEü‚Í‘o‹Èü‚É‚È‚è‚Ü‚·B‚±‚ê‚Í“ñ“_‚©‚ç‚Ì‹——£‚Ì·‚ªˆê’è‚È“_‚Ì‹OÕ‚ðˆÓ–¡‚µ‚Ü‚·B
        for (iaw = 1; iaw <= Naw - 1; iaw++) {
            for (jaw = iaw + 1; jaw <= Naw; jaw++) {
                // At first, rotate i and j such that two y-coordinates are same. Next, move two
                // points such that middle point of i and j become origin point. That is, i and
                // j become two foci as (-a,0) and (a,0).
                // Compute hyperbolic arc (x,y) for (-a,0), (a,0) and rotate back, move back.
                // ‚Ü‚¸i‚Æj‚ð‰ñ“]‚³‚¹‚Ä‚™À•W‚ª“¯‚¶‚É‚È‚é‚æ‚¤‚É‚µ‚Ü‚·BŽŸ‚Éi‚Æj‚Ì’†“_‚ªŒ´“_‚É‚È‚é‚æ‚¤‚É•½sˆÚ“®‚³‚¹‚Ü‚·B‚Â‚Ü‚èAi‚Æj‚ª(-a,0),
                // (a,0)‚Æ‚ ‚ç‚í‚¹‚é‚æ‚¤‚É‚È‚èA‚±‚ê‚Åˆê”Ê‚Ì‘o‹Èü‚Å‹c˜_‚ð‚·‚é‚±‚Æ‚ª‚Å‚«‚Ü‚·B
                // ‚µ‚½‚ª‚Á‚ÄA(-a,0),
                // (a,0)‚É‘Î‚µ‚Ä‘o‹Èü‚ÌÀ•W‚ðŒvŽZ‚µA‚»‚ê‚ð•½sˆÚ“®A‰ñ“]‚³‚¹‚é‚±‚Æ‚É‚æ‚èAŒ³‚Ìi,j‚É‘Î‚µ‚Ä‘o‹Èü‚ð•`‚­‚±‚Æ‚ª‚Å‚«‚Ü‚·B

                // Regarding to parameters, see Okabe, Boots, Sugihara, Chiu. Spatial
                // Tessellations, Wiley.@ƒpƒ‰ƒ[ƒ^[‚É‚Â‚¢‚Ä‚ÍAOkabe, Boots, Sugihara, Chiu.
                // Spatial Tessellations, Wiley‚ðŽQÆ
                alaw = jouaw(jouaw(x1aw[iaw - 1] - x1aw[jaw - 1], 2) + jouaw(y1aw[iaw - 1] - y1aw[jaw - 1], 2), 0.5);
                beaw = w1aw[jaw - 1] - w1aw[iaw - 1];
                if (alaw > beaw) {
                    al2aw = alaw / 2;
                    xcaw = (x1aw[iaw - 1] + x1aw[jaw - 1]) / 2;
                    ycaw = (y1aw[iaw - 1] + y1aw[jaw - 1]) / 2;
                    yyaw = ycaw - y1aw[iaw - 1];
                    phaw = yyaw / al2aw;
                    thaw = artnaw(phaw / jouaw(1 - phaw * phaw, 0.5));
                    if (x1aw[iaw - 1] < x1aw[jaw - 1]) {
                        thaw = piaw - artnaw(phaw / jouaw(1 - phaw * phaw, 0.5));
                    }
                    minyaw = 0;
                    maxyaw = 0;
                    rraw = 0;
                    xjaw = 0;// x-coordinate xÀ•W
                    a2raw = 0.5 / beaw;
                    while (rraw == 0) {
                        a1raw = 16 * al2aw * al2aw * xjaw * xjaw - 4 * beaw * beaw * xjaw * xjaw
                                - 4 * al2aw * al2aw * beaw * beaw + jouaw(beaw, 4);
                        if (a1raw >= 0) {
                            yraw = a2raw * jouaw(a1raw, 0.5);
                            ymraw = -yraw;// y-coordinate yÀ•W
                            x2raw = xcaw + kosaw(-thaw) * xjaw - sainaw(-thaw) * ymraw;// move back and rotate back
                                                                                       // •½sˆÚ“®A‰ñ“]ˆÚ“®‚µ‚½•ª‚ðŒ³‚É–ß‚µ‚Ü‚·B
                            y2raw = ycaw + sainaw(-thaw) * xjaw + kosaw(-thaw) * ymraw;
                            if (x2raw > 0 && x2raw < habaaw && y2raw > 0 && y2raw < takaaw) {// inside the screen
                                                                                             // ‰æ–Ê“à‚È‚ç
                                d3aw = jouaw(jouaw(x2raw - x1aw[iaw - 1], 2) + jouaw(y2raw - y1aw[iaw - 1], 2), 0.5)
                                        - w1aw[iaw - 1];// distance from (x,y) to i
                                br2aw = 0;
                                for (kaw = 1; kaw <= Naw; kaw++) {
                                    if (kaw != iaw && kaw != jaw) {
                                        d4aw = jouaw(jouaw(x2raw - x1aw[kaw - 1], 2) + jouaw(y2raw - y1aw[kaw - 1], 2),
                                                0.5) - w1aw[kaw - 1];// distance from (x,y) to k
                                        if (d3aw > d4aw) {// if k is closer, (x,y) is not bisector.
                                                          // k‚Ì•û‚ª‹ß‚¢ê‡‚É‚Í(x,y)‚Í‹«ŠE‚É‚È‚è‚¦‚È‚¢
                                            br2aw = 1;
                                            break;
                                        } // if d3aw>d4aw
                                    } // if kaw!=iaw...
                                } // next kaw
                                if (br2aw == 0) {// All k is not closer than i, so (x,y) is bisector.
                                                 // ‘S‚Ä‚Ìk‚Å(x,y)‚Ö‚Ì‹——£‚ªi‚æ‚è‹ß‚­‚È‚¢ê‡A(x,y)‚ª‹«ŠE‚É‚È‚é‚Ì‚Åplot‚·‚é
                                    x2rawI = (int) (x2raw + 0.5);
                                    y2rawI = (int) (y2raw + 0.5);
                                    g.drawLine(x2rawI, y2rawI, x2rawI, y2rawI);
                                } // if br2aw==0
                                if (ymraw < minyaw) {
                                    minyaw = ymraw;
                                }
                                if (ymraw > maxyaw) {
                                    maxyaw = ymraw;
                                }
                            } // if x2raw>0 && ....1950
                        } // if a1raw>=0 1950
                        xjaw++;// next x
                        rweaw = 0;
                        if (x2raw < 0 || x2raw > habaaw || y2raw < 0 || y2raw > takaaw) {
                            rweaw++;
                        }
                        if (rweaw == 1) {
                            rraw = 1;
                        }
                        if (xjaw < 100) {
                            rraw = 0;
                        }
                    } // while rraw==0
                    minyawI = -takaaw;// (int)(minyaw+0.5);
                    maxyawI = takaaw;// (int)(maxyaw+0.5);
                    // y loop
                    for (yjawI = minyawI; yjawI <= maxyawI; yjawI++) {
                        b1yaw = 4 * jouaw(al2aw * beaw, 2) + 4 * jouaw(beaw * yjawI, 2) - jouaw(beaw, 4);
                        b2yaw = 1 / (16 * al2aw * al2aw - 4 * beaw * beaw);
                        b3yaw = b1yaw * b2yaw;
                        if (b3yaw >= 0) {
                            x0aw = jouaw(b3yaw, 0.5);
                            x10aw = xcaw + kosaw(-thaw) * x0aw - sainaw(-thaw) * yjawI;
                            y10aw = ycaw + sainaw(-thaw) * x0aw + kosaw(-thaw) * yjawI;
                            if (x10aw > 0 && x10aw < habaaw && y10aw > 0 && y10aw < takaaw) {
                                d5aw = jouaw(jouaw(x10aw - x1aw[iaw - 1], 2) + jouaw(y10aw - y1aw[iaw - 1], 2), 0.5)
                                        - w1aw[iaw - 1];
                                br3aw = 0;
                                for (kaw = 1; kaw <= Naw; kaw++) {
                                    if (kaw != iaw && kaw != jaw) {
                                        d6aw = jouaw(jouaw(x10aw - x1aw[kaw - 1], 2) + jouaw(y10aw - y1aw[kaw - 1], 2),
                                                0.5) - w1aw[kaw - 1];
                                        if (d5aw > d6aw) {
                                            br3aw = 1;
                                            break;
                                        } // if d5aw>d6aw
                                    } // if kaw!=iaw
                                } // next kaw
                                if (br3aw == 0) {
                                    x10awI = (int) (x10aw + 0.5);
                                    y10awI = (int) (y10aw + 0.5);
                                    g.drawLine(x10awI, y10awI, x10awI, y10awI);
                                } // if br3==0
                            } // if x10aw>0...
                        } // if b3yaw>=0
                    } // next yjawI
                } // if alaw>beaw
            } // next jmw
        } // next imw
    }
}
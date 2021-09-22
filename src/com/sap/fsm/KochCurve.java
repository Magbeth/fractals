package com.sap.fsm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KochCurve extends JApplet {

    private int iterations;
    private static final double baseLineLength = 300;

    public KochCurve(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public void paint(Graphics g) {
        double p1x = 250;
        double p1y = 400;

        var p2x = p1x + baseLineLength;
        var p2y = p1y;

        var h = Math.sqrt(Math.pow(baseLineLength, 2) - Math.pow((baseLineLength / 2), 2));
        var pmx = (p1x + p2x) / 2;
        var pmy = (p1y + p2y) / 2;
        var p3x = pmx + (h * (p1y - pmy)) / (baseLineLength / 2);
        var p3y = pmy + (h * (p1x - pmx)) / (baseLineLength / 2);

        List<Line2D> lines = List.of(new Line2D.Double(p1x, p1y, p2x, p2y),
                new Line2D.Double(p3x, p3y, p1x, p1y),
                new Line2D.Double(p2x, p2y, p3x, p3y));

        while (iterations > 0) {
            lines = makeCurveFromLines(lines);
            iterations--;
        }

        g.setColor(Color.BLUE);
        lines.parallelStream().forEach(((Graphics2D) g)::draw);
    }

    private static List<Line2D> makeCurveFromLines(Collection<Line2D> lines) {
        return lines.stream()
                .flatMap(line -> {
                    var lineLength = line.getP1().distance(line.getP2());
                    var segmentLength = lineLength / 3;
                    var h = Math.sqrt(Math.pow(segmentLength, 2) - Math.pow((segmentLength / 2), 2) / 4);

                    var start = line.getP1();
                    var end = line.getP2();
                    var middle = new Point2D.Double((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);

                    var p1 = new Point2D.Double(start.getX() + (end.getX() - start.getX()) / 3, start.getY() + (end.getY() - start.getY()) / 3);
                    var p2 = new Point2D.Double(start.getX() + 2 * (end.getX() - start.getX()) / 3, start.getY() + 2 * (end.getY() - start.getY()) / 3);
                    var p3 = new Point2D.Double(
                            middle.getX() + (h * (-p2.getY() + middle.getY())) / (segmentLength / 2),
                            middle.getY() + (h * (p2.getX() - middle.getX())) / (segmentLength / 2)
                    );

                    return Stream.of(new Line2D.Double(start, p1), new Line2D.Double(p1, p3), new Line2D.Double(p3, p2), new Line2D.Double(p2, end));
                })
                .collect(Collectors.toList());
    }
    public static void main(String... args) {
        JFrame f = new JFrame("KochCurve");
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //change number of iterations to receive different curve shape.
        //!!! with iterations > 12 takes too much time and can fail with out of memory. Recursive algorithm is not efficient and created just for demo purposes.
        JApplet applet = new KochCurve(10);
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(800, 800));
        f.show();
    }
}

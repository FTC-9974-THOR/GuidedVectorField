package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Main extends Application {

    private final double WIDTH = 1920 / 1.25, HEIGHT = 1080 / 1.25;

    private GraphicsContext gc;
    private double cY;
    private RBGVFNavigation rbgvfNavigation;
    private Vector2 p0, p1, p2, p3,
                    lP0, lP1, lP2, lP3;
    private CubicBezierCurve curve;
    private boolean interactive;

    private double curveChangeStart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("RBGVF Demo");
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        //scene.setCursor(Cursor.NONE);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        Canvas canvas = (Canvas) root.lookup("#canvas");
        if (canvas == null) {
            System.out.println("Null canvas");
            return;
        }
        canvas.setWidth(WIDTH);
        canvas.setHeight(HEIGHT);
        gc = canvas.getGraphicsContext2D();
        cY = canvas.getHeight();
        rbgvfNavigation = new RBGVFNavigation();

        p0 = new Vector2(800, 50);
        p1 = new Vector2(800, 100);
        p2 = new Vector2(600, 400);
        p3 = new Vector2(600, 450);
        curve = new CubicBezierCurve(p0, p1, p2, p3);

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                interactive = !interactive;
                if (!interactive) {
                    scene.setCursor(Cursor.DEFAULT);
                } else {
                    scene.setCursor(Cursor.NONE);
                }
            }
        });
        canvas.setOnMouseMoved(this::drawInteractive);

        final long updateStartNanos = System.nanoTime();
        AnimationTimer updateLoop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (!interactive) {
                    double millisTime = (l - updateStartNanos) / 1e6;
                    double curveChangeTimer = millisTime - curveChangeStart;
                    if (curve.getP0().subtract(p0).getMagSq() < 50 || curveChangeTimer > 2500) {
                        curveChangeStart = millisTime;
                        System.out.println("Generating new curve");
                        generateNewCurve();
                    }
                    curve.setP0(Vector2.slerp(curve.getP0(), p0, 0.1));
                    curve.setP1(Vector2.slerp(curve.getP1(), p1, 0.1));
                    curve.setP2(Vector2.slerp(curve.getP2(), p2, 0.1));
                    curve.setP3(Vector2.slerp(curve.getP3(), p3, 0.1));
                    drawVectorField();
                }
            }
        };
        updateLoop.start();
    }

    private void generateNewCurve() {
        Random random = new Random();
        random.setSeed(System.nanoTime());
        double MARGIN = 100;
        DoubleStream xDoubles = random.doubles(MARGIN, WIDTH + 1 - MARGIN);
        DoubleStream yDoubles = random.doubles(MARGIN, HEIGHT + 1 - MARGIN);
        DoubleStream tDoubles = random.doubles(-0.75 * Math.PI, 0.75 * Math.PI);
        DoubleStream fDoubles = random.doubles(50, 501);
        Iterator<Double> xIt = xDoubles.iterator(),
                         yIt = yDoubles.iterator(),
                         tIt = tDoubles.iterator(),
                         fIt = fDoubles.iterator();
        p0 = new Vector2(xIt.next(), yIt.next());
        p3 = new Vector2(xIt.next(), yIt.next());

        Vector2 p0ToP3 = p3.subtract(p0);
        Vector2 p3ToP0 = p0.subtract(p3);

        double f = fIt.next();
        double t = p0ToP3.getHeading() + tIt.next();
        p1 = p0.add(Vector2.polar(f, t));

        f = fIt.next();
        t = p3ToP0.getHeading() + tIt.next();
        p2 = p3.add(Vector2.polar(f, t));
    }

    private void drawPoint(Vector2 point, double size) {
        gc.fillOval(point.getX() - 0.5 * size, HEIGHT - point.getY() - 0.5 * size, size, size);
    }

    private void drawLine(Vector2 a, Vector2 b) {
        gc.strokeLine(a.getX(), HEIGHT - a.getY(), b.getX(), HEIGHT - b.getY());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void drawVectorField() {
        long startTime = System.nanoTime();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setStroke(Color.WHITE);
        double X_POINTS = 90, Y_POINTS = 50;
        for (int i = 0; i < X_POINTS + 1; i++) {
            for (int j = 0; j < Y_POINTS + 1; j++) {
                Vector2 point = new Vector2(i * (WIDTH / X_POINTS), j * (HEIGHT / Y_POINTS));
                Vector2 movementVector = rbgvfNavigation.calculateGuidanceVector(curve, point).scalarMultiply(10);
                drawLine(point, point.add(movementVector));
            }
        }
        gc.setStroke(Color.LIGHTGREEN);
        int SAMPLE_DENSTIY = 50;
        for (int i = 0; i < SAMPLE_DENSTIY; i++) {
            double t = i / (double) (SAMPLE_DENSTIY - 1);
            gc.setStroke(Color.hsb((1 - t) * 108 + t * 0, 1, 1));
            Vector2 vec0 = curve.calculate(i / (double) SAMPLE_DENSTIY);
            Vector2 vec1 = curve.calculate((i + 1) / (double) SAMPLE_DENSTIY);
            drawLine(vec0, vec1);
        }
        gc.fillRect(0, 0, 290, 37);
        gc.setStroke(Color.WHITE);
        gc.strokeText("Model=RBGVF::VectorField (random Bezier curve)", 10, 20);
        //System.out.println(String.format("Rendered Vector Field in %.3fms", (System.nanoTime() - startTime) / 1e6));
    }

    public void drawInteractive(MouseEvent mouseEvent) {
        if (!interactive) return;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        Vector2 mousePosition = new Vector2(mouseEvent.getX(), HEIGHT - mouseEvent.getY());
        /*drawPoint(p0, 5);
        drawPoint(p1, 5);
        drawPoint(p2, 5);
        drawPoint(p3, 5);
        drawLine(p0, p1);
        drawLine(p1, p2);
        drawLine(p2, p3);*/
        gc.setStroke(Color.WHITE);
        int SAMPLE_DENSTIY = 50;
        for (int i = 0; i < SAMPLE_DENSTIY; i++) {
            Vector2 vec0 = curve.calculate(i / (double) SAMPLE_DENSTIY);
            Vector2 vec1 = curve.calculate((i + 1) / (double) SAMPLE_DENSTIY);
            drawLine(vec0, vec1);
        }
        gc.setFill(Color.LIGHTGREEN);
        drawPoint(p0, 5);
        gc.setFill(Color.RED);
        drawPoint(p3, 5);
        /*SAMPLE_DENSTIY = 100;
        for (int i = 1; i < SAMPLE_DENSTIY; i++) {
            Vector2 vec0 = curve.calculate(i / (double) SAMPLE_DENSTIY);
            drawPoint(vec0, 5);
        }*/

        //drawPoint(mousePosition, 5);
        double closestT = rbgvfNavigation.findClosestPoint(curve, mousePosition);
        Vector2 closestPoint = curve.calculate(closestT);
        //drawPoint(closestPoint, 5);
        //drawLine(mousePosition, closestPoint);
        Vector2 dt = curve.derivative(closestT);
        dt = dt.scalarDivide(dt.getMagnitude()).scalarMultiply(30);
        //drawLine(closestPoint, closestPoint.add(dt));
        Vector2 movementVector = rbgvfNavigation.calculateGuidanceVector(curve, mousePosition).scalarMultiply(30);
        drawLine(mousePosition, mousePosition.add(movementVector));
    }
}

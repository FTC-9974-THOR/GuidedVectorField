package sample;

public class CubicBezierCurve {

    private Vector2 p0, p1, p2, p3;

    public CubicBezierCurve(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Vector2 getP0() {
        return p0;
    }

    public void setP0(Vector2 p0) {
        this.p0 = p0;
    }

    public Vector2 getP1() {
        return p1;
    }

    public void setP1(Vector2 p1) {
        this.p1 = p1;
    }

    public Vector2 getP2() {
        return p2;
    }

    public void setP2(Vector2 p2) {
        this.p2 = p2;
    }

    public Vector2 getP3() {
        return p3;
    }

    public void setP3(Vector2 p3) {
        this.p3 = p3;
    }

    public Vector2 calculate(double t) {
        // (1 - t)^3 * P0 + 3 * t * (1 - t)^2 * P1 + 3 * t^2 * (1 - t) * P2 + t^3 * P3
        double w = 1 - t;
        Vector2 firstTerm = p0.scalarMultiply(w * w * w);
        Vector2 secondTerm = p1.scalarMultiply(3 * t * w * w);
        Vector2 thirdTerm = p2.scalarMultiply(3 * t * t * w);
        Vector2 fourthTerm = p3.scalarMultiply(t * t * t);
        return firstTerm.add(secondTerm).add(thirdTerm).add(fourthTerm);
    }

    public Vector2 derivative(double t) {
        double w = 1 - t;
        Vector2 firstTerm = p1.subtract(p0).scalarMultiply(3 * w * w);
        Vector2 secondTerm = p2.subtract(p1).scalarMultiply(6 * w * t);
        Vector2 thirdTerm = p3.subtract(p2).scalarMultiply(3 * t * t);
        return firstTerm.add(secondTerm).add(thirdTerm);
    }

    public double slope(double t) {
        Vector2 dt = derivative(t);
        return dt.getY() / dt.getX();
    }

    public double heading(double t) {
        return derivative(t).getHeading();
    }
}

package sample;

public class Vector2 {
    private double x, y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 polar(double r, double t) {
        return new Vector2(r * Math.cos(t), r * Math.sin(t));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeading() {
        return Math.atan2(y, x);
    }

    public double getMagnitude() {
        return Math.hypot(x, y);
    }

    public double getMagSq() {
        return x * x + y * y;
    }

    // Operations

    public Vector2 add(Vector2 other) {
        return add(this, other);
    }

    public Vector2 subtract(Vector2 other) {
        return subtract(this, other);
    }

    public Vector2 scalarMultiply(double scalar) {
        return scalarMultiply(this, scalar);
    }

    public Vector2 scalarDivide(double scalar) {
        return scalarDivide(this, scalar);
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public static Vector2 subtract(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public static Vector2 scalarMultiply(Vector2 vec, double scalar) {
        return new Vector2(vec.x * scalar, vec.y * scalar);
    }

    public static Vector2 scalarDivide(Vector2 vec, double scalar) {
        return new Vector2(vec.x / scalar, vec.y / scalar);
    }

    public static Vector2 slerp(Vector2 a, Vector2 b, double t) {
        double aMag = a.getMagnitude();
        double aHead = a.getHeading();
        double bMag = b.getMagnitude();
        double bHead = b.getHeading();
        return polar((1 - t) * aMag + t * bMag, (1 - t) * aHead + t * bHead);
    }
}

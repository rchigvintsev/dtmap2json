package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class Quaternion {
    private double _w;
    private double _x;
    private double _y;
    private double _z;

    public double getW() {
        return _w;
    }

    public double getX() {
        return _x;
    }

    public double getY() {
        return _y;
    }

    public double getZ() {
        return _z;
    }

    public Quaternion setFromRotationMatrix(Matrix4 m) {
        // http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
        // assumes the upper 3x3 of m is a pure rotation matrix (i.e, unscaled)

        double[] te = m.elements;
        double m11 = te[0], m12 = te[4], m13 = te[8],
                m21 = te[1], m22 = te[5], m23 = te[9],
                m31 = te[2], m32 = te[6], m33 = te[10],
                trace = m11 + m22 + m33, s;

        if (trace > 0) {
            s = 0.5 / Math.sqrt(trace + 1.0);
            _w = 0.25 / s;
            _x = (m32 - m23) * s;
            _y = (m13 - m31) * s;
            _z = (m21 - m12) * s;
        } else if (m11 > m22 && m11 > m33) {
            s = 2.0 * Math.sqrt(1.0 + m11 - m22 - m33);
            _w = (m32 - m23) / s;
            _x = 0.25 * s;
            _y = (m12 + m21) / s;
            _z = (m13 + m31) / s;
        } else if (m22 > m33) {
            s = 2.0 * Math.sqrt(1.0 + m22 - m11 - m33);
            _w = (m13 - m31) / s;
            _x = (m12 + m21) / s;
            _y = 0.25 * s;
            _z = (m23 + m32) / s;
        } else {
            s = 2.0 * Math.sqrt(1.0 + m33 - m11 - m22);
            _w = (m21 - m12) / s;
            _x = (m13 + m31) / s;
            _y = (m23 + m32) / s;
            _z = 0.25 * s;
        }

        return this;
    }

    public String toJson() {
        return "[" + _x + ", " + _y + ", " + _z + ", " + _w + ']';
    }

    @Override
    public String toString() {
        return toJson();
    }
}

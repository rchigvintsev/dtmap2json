package org.briarheart.doomthree.util;

import org.apache.commons.math3.util.Precision;

import java.util.Arrays;

/**
 * @author Roman Chigvintsev
 */
public class Matrix4 {
    private static final double ERROR = 0.00001;

    double[] elements = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public Matrix4 identity() {
        set(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
        return this;
    }

    private Matrix4 set(double n11,
                        double n12,
                        double n13,
                        double n14,
                        double n21,
                        double n22,
                        double n23,
                        double n24,
                        double n31,
                        double n32,
                        double n33,
                        double n34,
                        double n41,
                        double n42,
                        double n43,
                        double n44) {
        double[] te = elements;
        te[0] = n11;
        te[4] = n12;
        te[8] = n13;
        te[12] = n14;
        te[1] = n21;
        te[5] = n22;
        te[9] = n23;
        te[13] = n24;
        te[2] = n31;
        te[6] = n32;
        te[10] = n33;
        te[14] = n34;
        te[3] = n41;
        te[7] = n42;
        te[11] = n43;
        te[15] = n44;
        return this;
    }

    public Matrix4 lookAt(Vector3 eye, Vector3 target, Vector3 up) {
        Vector3 x, y, z;

        double[] te = this.elements;
        z = Vector3.subVectors(eye, target);
        if (z.lengthSq() == 0) {
            // eye and target are in the same position
            z = new Vector3(z.x, z.y, 1);
        }

        z = z.normalize();
        x = Vector3.crossVectors(up, z);

        if (x.lengthSq() == 0) {
            // up and z are parallel
            if (Math.abs(up.z) == 1) {
                z = new Vector3(z.x + 0.0001, z.y, z.z);
            } else {
                z = new Vector3(z.x, z.y, z.z + 0.0001);
            }
            z = z.normalize();
            x = Vector3.crossVectors(up, z);
        }

        x = x.normalize();
        y = Vector3.crossVectors(z, x);

        te[0] = x.x;
        te[4] = y.x;
        te[8] = z.x;
        te[1] = x.y;
        te[5] = y.y;
        te[9] = z.y;
        te[2] = x.z;
        te[6] = y.z;
        te[10] = z.z;

        return this;
    }

    public Matrix4 compose(Vector3 position, Quaternion quaternion) {
        makeRotationFromQuaternion(quaternion);
        setPosition(position);
        return this;
    }

    public Matrix4 getInverse(Matrix4 m) {
        // based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
        double[] te = elements, me = m.elements;

        double n11 = me[0], n21 = me[1], n31 = me[2], n41 = me[3],
                n12 = me[4], n22 = me[5], n32 = me[6], n42 = me[7],
                n13 = me[8], n23 = me[9], n33 = me[10], n43 = me[11],
                n14 = me[12], n24 = me[13], n34 = me[14], n44 = me[15],

                t11 = n23 * n34 * n42 - n24 * n33 * n42 + n24 * n32 * n43 - n22 * n34 * n43 - n23 * n32 * n44 + n22 * n33 * n44,
                t12 = n14 * n33 * n42 - n13 * n34 * n42 - n14 * n32 * n43 + n12 * n34 * n43 + n13 * n32 * n44 - n12 * n33 * n44,
                t13 = n13 * n24 * n42 - n14 * n23 * n42 + n14 * n22 * n43 - n12 * n24 * n43 - n13 * n22 * n44 + n12 * n23 * n44,
                t14 = n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33 + n13 * n22 * n34 - n12 * n23 * n34;

        double det = n11 * t11 + n21 * t12 + n31 * t13 + n41 * t14;

        if (det == 0) {
            System.err.println("THREE.Matrix4.getInverse(): can't invert matrix, determinant is 0");
            return this.identity();
        }

        double detInv = 1 / det;

        te[0] = t11 * detInv;
        te[1] = (n24 * n33 * n41 - n23 * n34 * n41 - n24 * n31 * n43 + n21 * n34 * n43 + n23 * n31 * n44 - n21 * n33 * n44) * detInv;
        te[2] = (n22 * n34 * n41 - n24 * n32 * n41 + n24 * n31 * n42 - n21 * n34 * n42 - n22 * n31 * n44 + n21 * n32 * n44) * detInv;
        te[3] = (n23 * n32 * n41 - n22 * n33 * n41 - n23 * n31 * n42 + n21 * n33 * n42 + n22 * n31 * n43 - n21 * n32 * n43) * detInv;

        te[4] = t12 * detInv;
        te[5] = (n13 * n34 * n41 - n14 * n33 * n41 + n14 * n31 * n43 - n11 * n34 * n43 - n13 * n31 * n44 + n11 * n33 * n44) * detInv;
        te[6] = (n14 * n32 * n41 - n12 * n34 * n41 - n14 * n31 * n42 + n11 * n34 * n42 + n12 * n31 * n44 - n11 * n32 * n44) * detInv;
        te[7] = (n12 * n33 * n41 - n13 * n32 * n41 + n13 * n31 * n42 - n11 * n33 * n42 - n12 * n31 * n43 + n11 * n32 * n43) * detInv;

        te[8] = t13 * detInv;
        te[9] = (n14 * n23 * n41 - n13 * n24 * n41 - n14 * n21 * n43 + n11 * n24 * n43 + n13 * n21 * n44 - n11 * n23 * n44) * detInv;
        te[10] = (n12 * n24 * n41 - n14 * n22 * n41 + n14 * n21 * n42 - n11 * n24 * n42 - n12 * n21 * n44 + n11 * n22 * n44) * detInv;
        te[11] = (n13 * n22 * n41 - n12 * n23 * n41 - n13 * n21 * n42 + n11 * n23 * n42 + n12 * n21 * n43 - n11 * n22 * n43) * detInv;

        te[12] = t14 * detInv;
        te[13] = (n13 * n24 * n31 - n14 * n23 * n31 + n14 * n21 * n33 - n11 * n24 * n33 - n13 * n21 * n34 + n11 * n23 * n34) * detInv;
        te[14] = (n14 * n22 * n31 - n12 * n24 * n31 - n14 * n21 * n32 + n11 * n24 * n32 + n12 * n21 * n34 - n11 * n22 * n34) * detInv;
        te[15] = (n12 * n23 * n31 - n13 * n22 * n31 + n13 * n21 * n32 - n11 * n23 * n32 - n12 * n21 * n33 + n11 * n22 * n33) * detInv;

        return this;
    }

    private void makeRotationFromQuaternion(Quaternion q) {
        double[] te = elements;

        double x = q.getX(), y = q.getY(), z = q.getZ(), w = q.getW();
        double x2 = x + x, y2 = y + y, z2 = z + z;
        double xx = x * x2, xy = x * y2, xz = x * z2;
        double yy = y * y2, yz = y * z2, zz = z * z2;
        double wx = w * x2, wy = w * y2, wz = w * z2;

        te[0] = 1 - (yy + zz);
        te[4] = xy - wz;
        te[8] = xz + wy;

        te[1] = xy + wz;
        te[5] = 1 - (xx + zz);
        te[9] = yz - wx;

        te[2] = xz - wy;
        te[6] = yz + wx;
        te[10] = 1 - (xx + yy);

        // last column
        te[3] = 0;
        te[7] = 0;
        te[11] = 0;

        // bottom row
        te[12] = 0;
        te[13] = 0;
        te[14] = 0;
        te[15] = 1;
    }

    private void setPosition(Vector3 v) {
        double[] te = elements;
        te[12] = v.x;
        te[13] = v.y;
        te[14] = v.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix4 matrix4 = (Matrix4) o;

        for (int i = 0; i < elements.length; i++) {
            double element = elements[i];
            double otherElement = matrix4.elements[i];
            if (!Precision.equals(element, otherElement, ERROR))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }
}

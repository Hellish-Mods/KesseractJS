package dev.latvian.kubejs.bindings;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.*;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class KMath {
    public static final double E = 2.7182818284590452354;
    public static final double PI = 3.14159265358979323846;
    public static final double DEGREES_TO_RADIANS = 0.017453292519943295;
    public static final double RADIANS_TO_DEGREES = 57.29577951308232;

    public static BlockPos block(double x, double y, double z) {
        return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    // why
    public static Vec3 v3(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static Vector3d v3d(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }

    public static Vector3f v3f(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public static Vector4f v4f(float x, float y, float z, float w) {
        return new Vector4f(x, y, z, w);
    }

    public static Matrix3f m3f() {
        return new Matrix3f();
    }

    public static Matrix4f m4f() {
        return new Matrix4f();
    }

    public static float distSquared(Vector3f vec1, Vector3f vec2) {
        val x = vec1.x() - vec2.x();
        val y = vec1.y() - vec2.y();
        val z = vec1.z() - vec2.z();
        return x * x + y * y + z * z;
    }

    public static double dist(Vector3f vec1, Vector3f vec2) {
        return Math.sqrt(distSquared(vec1, vec2));
    }

    public static double rad(double value) {
        return value * DEGREES_TO_RADIANS;
    }

    public static double deg(double value) {
        return value * RADIANS_TO_DEGREES;
    }

    public static long floor(double value) {
        long i = (long) value;
        return value < i ? i - 1L : i;
    }

    public static long ceil(double value) {
        long i = (long) value;
        return value > i ? i + 1L : i;
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    public static double lerp(double value, double min, double max) {
        return min + value * (max - min);
    }

    public static double map(double value, double min0, double max0, double min1, double max1) {
        return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
    }

    public static double clampedLerp(double value, double min, double max) {
        return value < 0 ? min : value > 1.0 ? max : lerp(value, min, max);
    }

    public static double wrapDegrees(double d) {
        double e = d % 360.0;
        if (e >= 180.0) {
            e -= 360.0;
        }

        if (e < -180.0) {
            e += 360.0;
        }

        return e;
    }

    public static double degreesDifference(double current, double target) {
        return wrapDegrees(target - current);
    }

    public static double rotateIfNecessary(double current, double target, double max) {
        double i = degreesDifference(current, target);
        double j = clamp(i, -max, max);
        return target - j;
    }

    public static double approach(double current, double target, double speed) {
        speed = Math.abs(speed);
        return current < target ? clamp(current + speed, current, target) : clamp(current - speed, target, current);
    }

    public static double approachDegrees(double current, double target, double speed) {
        double i = degreesDifference(current, target);
        return approach(current, current + i, speed);
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }
}

package parspice;

import org.junit.jupiter.api.Test;
import spice.basic.CSPICE;
import spice.basic.SpiceErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class ReflectionTest {


    @Test // test native call for correctness
    void nativeTest() {
        System.loadLibrary("JNISpice");
        double[][] m1 = {{1,2,3}, {4,5,6}, {7,8,9}};
        double[][] m2 = {{1,2,3}, {4,5,6}, {7,8,9}};

        try {
            System.out.println(Arrays.deepToString(CSPICE.mxm(m1, m2)));
        } catch (SpiceErrorException e) {
            System.out.println("error");
        }
    }

    @Test // test reflection for correctness
    void reflectionTest() {
        double[][] m1 = {{1,2,3}, {4,5,6}, {7,8,9}};
        double[][] m2 = {{1,2,3}, {4,5,6}, {7,8,9}};
        try {
            Method method = CSPICE.class.getDeclaredMethod("mxm", double[][].class, double[][].class);
            System.out.println(Arrays.deepToString((double[][])method.invoke(null, m1, m2)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("error");
        }
    }

    @Test // test native call speed
    void nativeTestTime() {
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            try {
                CSPICE.mxm(m1, m2);
            } catch (SpiceErrorException e) {
                System.out.println("error");
            }
        }
        stopTime = System.currentTimeMillis();
        System.out.printf("Native call = %d millis \n", stopTime-startTime);
    }

    @Test // test reflection on every iteration speed
    void reflectionTestEveryTime() {
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            try {
                Method method = CSPICE.class.getDeclaredMethod("mxm", double[][].class, double[][].class);
                double[][] res = (double[][])method.invoke(null, m1, m2);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                System.out.println("error");
            }
        }
        stopTime = System.currentTimeMillis();
        System.out.printf("Using reflection every iteration = %d millis\n", stopTime-startTime);
    }

    @Test // test only one reflexive call speed
    void reflectionTestOnce() {
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        try {
            Method method = CSPICE.class.getDeclaredMethod("mxm", double[][].class, double[][].class);
            for (int i = 0; i < 100000; i++) {
                try {
                    double[][] res = (double[][])method.invoke(null, m1, m2);
                } catch ( IllegalAccessException | InvocationTargetException e) {
                    System.out.println("error");
                }
            }
        } catch (NoSuchMethodException e ) {
            System.out.println("error");
        }
        stopTime = System.currentTimeMillis();
        System.out.printf("Using reflection only once = %d millis\n", stopTime-startTime);
    }
}

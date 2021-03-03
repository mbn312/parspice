package parspice;

import org.junit.jupiter.api.Test;
import spice.basic.CSPICE;
import spice.basic.SpiceErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ReflectionTest {

    @Test // test native call speed
    void nativeTest() {
        // init
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;


        startTime = System.currentTimeMillis();
        try {
            CSPICE.mxm(m1, m2);
        } catch (SpiceErrorException e) {
            System.out.println("error");
        }
        stopTime = System.currentTimeMillis();
        System.out.println(stopTime-startTime);
    }

    @Test // test reflection speed
    void reflectionTestEveryTime() {
        // init
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        try {
            Method method = CSPICE.class.getDeclaredMethod("mxm");
            method.invoke(null, m1, m2);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("error");
        }
        stopTime = System.currentTimeMillis();
        System.out.println(stopTime-startTime);
    }

    @Test
    void reflectionTestOnce() {
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        try {
            Method method = CSPICE.class.getDeclaredMethod("mxm");
            method.invoke(null, m1, m2);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("error");
        }
        stopTime = System.currentTimeMillis();
        System.out.println(stopTime-startTime);
    }

}

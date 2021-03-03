package parspice;

import org.junit.jupiter.api.Test;
import spice.basic.CSPICE;
import spice.basic.SpiceErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class ReflectionTest {

    @Test
    void nativeTest() {
        // init args
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};

        // test native call speed
        try {
            System.out.println(CSPICE.mxm(m1, m2));
        } catch (SpiceErrorException e) {
                System.out.println("wow you're trash");
        }

    }

    @Test // test reflection speed
    void reflectionTest() {
        // init args
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};

        try {
            Method method = CSPICE.class.getDeclaredMethod("mxm");
            System.out.println(method.invoke(null, m1, m2));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("wow you're trash");
        }
    }

    @Test // string->method map with lambdas
    void lambdaTest() {
        // init args
        double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
        double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};

        Map<String, TwoParamFunction<double[][], double[][], double[][]>> funcs = new HashMap<>();

        TwoParamFunction<double[][], double[][], double[][]> mxmLambda = CSPICE::mxm;
        SixParamFunction<String, Double, Double, Double, Double, Double, double[][]> dpgrdrLambda = CSPICE::dpgrdr;

        funcs.put("mxm", mxmLambda);
        funcs.put("dpgrdr", dpgrdrLambda);

        try {
            funcs.get("mxm").apply(m1, m2);
            funcs.get("dpgrdr").apply();
        } catch (SpiceErrorException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface TwoParamFunction<T, U, R>  {
        public R apply(T t, U u) throws SpiceErrorException;
    }

    @FunctionalInterface
    public interface SixParamFunction<T, U, V, W, X, Y, R> extends Operation {
        public R apply(T t, U u, V v, W w, X x, Y y) throws SpiceErrorException;
    }

    public interface Operation<T, R> {
        R apply(T... operands);
    }

}

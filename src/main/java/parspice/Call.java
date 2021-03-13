package parspice;

/**
 * An individual call of a SPICE function.
 *
 * Subclasses of Call will contain public fields corresponding to the arguments of the
 * function, and its return value if it has one. The fields have exactly the same names
 * as their corresponding SPICE arguments. The return value field is called {@code ret}
 * if the return type is not void. If any errors are generated when running the call,
 * the text of the error will be put in the {@code String error} field.
 *
 * JNISpice often uses array reference arguments to output results. In these cases, it was required in
 * base JNISpice to create an array outside of the function call so the results would not
 * be lost; this is not the same in ParSPICE. You still need to create the array yourself, but
 * you don't need to (and probably shouldn't) store it outside the call. Example from the Batch docs:
 *
 * <pre>
 *     {@code
 *          SpkezrBatch spkezr = parspice.spkezr();
 *          for (Scs2eCall scs2eCall : scs2e) {
 *              spkezr.call("CASSINI", scs2eCall.ret, "ECLIPJ2000", "NONE", "SUN", new double[6], new double[1]);
 *          }
 *          spkezr.run();
 *     }
 * </pre>
 *
 * The new array will be stored in the Call object for you, and only the length of the array will be sent
 * to the worker.
 */
public abstract class Call {
    public String error = "";
}

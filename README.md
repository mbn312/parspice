# ParSPICE

TODO

## Preparation

#### Pre-Requisites
* [Gradle](https://gradle.org/install/)  
* [JDK version 1.8](https://adoptopenjdk.net)

### Download [JNISpice](https://naif.jpl.nasa.gov/pub/naif/misc/JNISpice/)

This guide assumes you will be using ParSPICE to run JNISpice jobs. Technically, ParSPICE can be
used for any job that meets its design restrictions (not just spice things), so this step is optional
if you don't intend to use JNISpice.

### Set environment variable JNISPICE_ROOT as the path to JNISpice.
   ```bash
   export JNISPICE_ROOT=/path/to/JNISpice
   ```
   To permanently set this variable add this command to your `.bashrc`(Linux), `.zshrc` (MacOS) 
   or follow [these instructions](https://www.howtogeek.com/51807/how-to-create-and-use-global-system-environment-variables/) for windows

   This is used in the benchmark only. If you don't need to run the benchmark, this is optional.

### Clone ParSPICE and build
   ```bash
   > git clone https://github.com/JoelCourtney/parspice.git
   > cd parspice
   > ./gradlew build
   ```
   This builds ParSPICE and runs the tests.

## Usage

The following assumes that your project uses Gradle. All example code is written in Java, but Kotlin
works fine and is even recommended, because it simplifies the boilerplate that ParSPICE requires you to write.
No ParSPICE-specific performance difference has been observed between Java and Kotlin.

### Adding to Gradle Dependencies through mavenLocal

   To use ParSPICE in another project, publish it to the local maven repo with: 
   ```bash
   ./gradlew publishToMavenLocal
   ```
   This should store copies of the packaged outputs in `~/.m2/repository/org/parspice/`
   
   In your own `build.gradle` file you should then be able to import the implementation dependency with `mavenLocal()` in the
   repositories list and `implementation 'org.parspice:parspice.implementation:1.0-SNAPSHOT'` in
   the dependencies list.

   [Here is an example build.gradle](https://github.com/JoelCourtney/parspice-playground/blob/main/build.gradle)

### Adding to Gradle Dependencies with direct filepaths

This is not recommended, but it should work even if maven local does not. Simply add:

```groovy
implementation files("/path/to/ParSPICE/src")
```

to your dependencies list.

### Implementing a worker

Each worker contains two key functions that you have to implement: `void setup()` and `task()`.

- When the worker is started, `setup()` will be called once. If you have to load the JNISpice native library or furnsh a kernel, do it in setup.
  Remember that the worker is a separate process entirely; any setup you did on the main process is
  not available to the workers.
- Then, `task()` is called repeatedly. The return type and input argument type are determined by you (in the next section).
  `task`'s behavior should depend *only* on its inputs.

#### IO

Choose what IO you need for your worker. For performance, less IO is usually better if you can
get away with it. Any datatype can be sent as input or output, but default networking behavior is only provided
for basic types and arrays of basic types. (see next section for details.)

- Output: Most tasks will need to return output data of some type (such as `ResultType` in the
  above example). Whatever data you return from the `task()` function will be sent back to the main
  process, collected in an `ArrayList`, and returned from the `ParSPICE.run()` call.
  If by some miracle you do not need to return data, you can implement a `void task()`
  instead, and you'll have no network overhead for it.
- Input: if you absolutely need to give custom input arguments to each iteration of `task()`, you'll
  need to aggregate those arguments into a `List` (see examples). But if you can get away with it, you
  can instead use the default argument `int i` which indicates which iteration you are on. For example,
  if you have single-threaded code that can be written in the form:
  ```java
  List<ResultType> results = new ArrayList<ResultType>();
  for (int i = 0; i < numIterations; i++) {
    var arg = ...; // some calculation depending only on i
    // do things with arg
    results.add(someResult);
  }
  ```
  Then you can easily translate this into a task with no network input:
  ```java
  @Override
  public ResultType task(int i) throws Exception {
    var arg = ...; // some calculation depending only on i
    // do things with arg
    return someResult;
  }
  ```

  
The four IO configurations each have an abstract Worker superclass for you to extend:

- Both Input and Output: extend `IOWorker<I,O>`, override `O task(I input)`
- Only Output: extend `OWorker<O>`, override `O task(int i)`
- Only Input: extend `IWorker<I>`, override `void task(I input)`
- No input or output: extend `AutoWorker`, override `void task(int i)`

#### Sending data

All data, for both inputs and outputs, is sent over network sockets by implementers of the
`Sender<T>` interface.

##### Pre-built senders

Senders have already been implemented for twelve types:

Type | Constructor(s)
:---:|:---:
`Boolean` | `BooleanSender()`
`Integer` | `IntSender()`
`Double` | `DoubleSender()`
`String` | `StringSender()`
`boolean[]` | `BooleanArraySender()`<br/>`BooleanArraySender(int length)`
`int[]` | `IntArraySender()`<br/>`IntArraySender(int length)`
`double[]` | `DoubleArraySender()`<br/>`DoubleArraySender(int length)`
`String[]` | `StringArraySender()`<br/>`StringArraySender(int length)`
`boolean[][]` | `BooleanMatrixSender()`<br/>`BooleanMatrixSender(int width, int height)`
`int[][]` | `IntMatrixSender()`<br/>`IntMatrixSender(int width, int height)`
`double[][]` | `DoubleMatrixSender()`<br/>`DoubleMatrixSender(int width, int height)`
`String[][]` | `StringMatrixSender()`<br/>`StringMatrixSender(int width, int height)`

The Array/Matrix senders allow you to specify the dimensions of the array/matrix,
*as long as the dimensions are constant*. If you do not specify the dimensions,
you are allowed to send arrays/matrices of varying sizes, at the cost of slightly more network
overhead (negligible for arrays/matrices with more than a few elements).

##### Custom Senders

If you need to send other types, or combinations of these types, you have to create your own sender.
For example, if you want to return both an int and a double for each iteration:

```java
public class IntAndDouble {
    public int i;
    public double d;
    
    public IntAndDouble(int i, double d) {
        this.i = i;
        this.d = d;
    }
}

public class IntAndDoubleSender implements Sender<IntAndDouble> {
    private static final Sender<Integer> intSender = new IntSender();
    private static final Sender<Double> doubleSender = new DoubleSender();
    
    @Override
    public IntAndDouble read(ObjectInputStream ois) throws IOException {
        return new IntAndDouble(
                intSender.read(ois),
                doubleSender.read(ois)
        );
    }
    
    @Override
    public void write(IntAndDouble out, ObjectOutputStream oos) throws Exception {
        intSender.write(out.i, oos);
        doubleSender.write(out.d, oos);
    }
}
```

In a simple case like this, you could also directly call `ois.readInt()`, `ois.readDouble()`,
`oos.writeInt(out.i)`, and `oos.writeDouble(out.d)`, which is what the Int and Double Senders
do anyway.

#### Assembling the fat Worker Jar

Now that you have a worker implemented, along with custom Senders if needed, you need to package them
and all their dependencies in a fat jar. No main class is needed; so, if you are using the same jar for
other purposes, the main class can be whatever you want. You can include as many workers as you want
in a single jar. The following is a simple (but also overkill) solution for gradle, assuming all dependencies
for your worker is under the `implementation` configuration.

```groovy
jar {
  from {
    configurations.compileClasspath.collect { it.isDirectory() ? it : zipTree(it) }
  }
  archiveBaseName.set("worker")
}
```

If you want to get fancier and only include worker-specific dependencies (not *all* dependencies, as above),
you can use custom source sets:

```groovy
sourceSets {
  worker
}

jar {
  from {
    configurations.workerCompileClasspath.collect { it.isDirectory() ? it : zipTree(it) }
  }
  archiveBaseName.set("worker")
}
```

You would then add worker dependencies with `workerImplementation`, and put your worker source code under
`src/worker/java` instead of `src/main/java`.

Both of the above examples would produce jar files called `build/libs/worker-<version>.jar` where `version` is
set elsewhere in your `build.gradle` file.

#### Running the worker

TODO (easy)

#### Examples

The following is an `OWorker<double[]>` that calls `CSPICE.mxv` and `CSPICE.vhat` based on a fixed matrix
and a vector constructed from the input `int i` (the actual matrix and vector are nonsense values,
just a proof of concept):

```java
// imports omitted

public class MxvhatWorker extends OWorker<double[]> {

    // Just some dumb matrix
    static final double[][] mat = new double[][]{
            {1.0, 2.0, 3.0},
            {10.0, -2.0, 0.0},
            {1.3, 1.0, 0.5},
    };

    public MxvhatWorker() {
        // Give OWorker an instance of the output sender.
        // Note that length=3 is specified. Now, attempting to
        // send arrays of any other length is undefined behavior.
        super(new DoubleArraySender(3));
    }

    @Override
    public void setup() {
        // Load the native library once when the worker starts
        System.loadLibrary("JNISpice");
    }

    @Override
    public double[] task(int i) throws Exception {
        // Perform some sequence of operations that returns a double[] of length 3
        // and depends only on i.
        double[] u = new double[]{1, 2, i};
        double[] v = CSPICE.mxv(mat, u);
        return CSPICE.vhat(v);
    }
}

public class MainProcess {
    public static void main(String[] args) {
        // create the ParSPICE instance.
        ParSPICE par = new ParSPICE("build/libs/worker.jar", 50050);
        
        // run 1000 iterations with 5 workers in parallel.
        ArrayList<double[]> results = par.run(new MxvhatWorker(), 1000, 5);
    }
}
```

### Troubleshooting
   TBD -> will list common problems and subsequents solutions with building and running this repo


## Benchmarking

You need JNISpice installed to run the benchmark.

Ensure that the JNISpice native library is somewhere in your library path, and set the environment variable
`JNISPICE_ROOT` to the path to the JNISpice sources, one level above the `src` directory. For example, on my machine that would be

```bash
export JNISPICE_ROOT="/usr/local/JNISpice"
```

Use `gradle benchmark` to run the benchmark. It could take several minutes.

You can print out the benchmark analysis again just by running `./gradlew benchmark` again (the results are cached). To re-run the entire
benchmark, run `./gradlew clean` first.

### Runtime Estimation

When the benchmark is done, it will output a regression model of the form

<pre>
        T_0
T = B_1 --- + B_2 D
         W

where	T   = total time to run task through ParSPICE
	T_0 = total time to run task singlethreaded
	W   = number of workers used
	D   = total amount of data transferred between processes, in MB
</pre>

B_1 is typically between 1 and 2, so if you have a task big enough to make you consider ParSPICE,
it will almost certainly run faster in ParSPICE (unless you have to transfer hundreds of bytes per iteration).

### Break-Even Point Estimation

The benchmark also outputs a break-even point estimate which compares the amount of data sent per iteration with the
average time it takes to run a single iteration. (This is found by setting `T - T0 = 0` and solving for `d = D/I`
where `I` is the total number of iterations.)

<pre>
d = (1/B_2 - (B_1/B_2)/w)[B/ns] t

where:  d = data sent per iteration, in bytes
        w = number of workers
        t = average single-threaded time per iteration, in ns
</pre>

For large tasks, this estimates the upper limit of data sent per iteration such that ParSPICE is still more performant
than running the task directly.

### Caveats

The benchmark runs a series of tasks, with varying computational and network costs. This means that the model
is biased by a few very high leverage observations of very expensive tasks. So don't expect the model to be accurate
for short, inexpensive tasks with only a few iterations (but in those cases, it probably isn't worth the time to port the task to
ParSPICE anyway, even if it is slightly faster).

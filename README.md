# ParSPICE

ParSPICE is a minimal multi-processing framework designed to run JNISpice jobs in parallel. SPICE is not thread-safe, so naively running concurrent SPICE jobs either results in errors, incorrect results, or (in the case of JNISpice) longer runtimes compared to running the job single-threaded.

Our solution is to break the job into multiple processes instead of multiple threads. Sadly, since this is the JVM, you can't easily share memory between processes, or even clone the memory space with a c-style `fork()`. So all communication between processes in our design uses local network requests.

In broad strokes, the general process of using ParSPICE is three steps:

1. Create a Worker class, which will run in parallel on the worker processes.
2. Create a fat jar containing your Worker, and all its dependencies.
3. In the main process, create an instance of the `parspice.ParSPICE` class, and call `parSPICE.run(...)` on it.

If you want to get right to it, you can use a template Java or Kotlin project from [this repo](https://github.com/JoelCourtney/parspice-templates). (You will need to install ParSPICE to the Maven Local repo first, though, [details below](#publish)).
### Table of Contents
* [Preparation](#prep)
  * [Pre-Requisites](#prereq)
  * [Set Environment Variable JNISPICE_ROOT](#setenv)
  * [Clone ParSPICE and build](#cloneps)
  * [Publish to Maven Local](#publish)
* [Usage](#usage)
  * [Implementing a worker](#implworker)
      * [Worker Types and IO Combinations](#workertypes)
      * [Sending data](#sendingdata)
        * [Built-in senders](#bisenders)
        * [Custom Senders](#csenders)
      * [Creating the fat Worker Jar](#fatjar)
      * [Running the worker](#runworker)
  * [Examples](#examples)
      * [Worker](#worker)
      * [Main Process](#main)
  * [Error handling](#error)
* [Benchmarking](#bench)
  * [Runtime Estimation](#runtime)
  * [Break-Even Point Estimation](#breakeven)
  * [Caveats](#caveats)

<a id="prep"></a>
## Preparation

<a id="prereq"></a>
### Pre-Requisites
* [Gradle](https://gradle.org/install/) (optional)
  * You can use the `./gradlew` (unix) or `./gradlew.bat` (windows) wrapper scripts instead of installing Gradle. These instructions assume you are using the `./gradlew` wrapper.
* [JDK version 1.8](https://adoptopenjdk.net)
* [JNISpice](https://naif.jpl.nasa.gov/pub/naif/misc/JNISpice/) (optional)
  * This guide assumes you will be using ParSPICE to run JNISpice jobs. Technically, ParSPICE can be
used for any job that meets its design restrictions (not just spice things), so this step is optional
if you don't intend to use JNISpice.

<a id="setenv"></a>
### Set environment variable JNISPICE_ROOT (optional)
  If you intend to run the ParSPICE benchmark, you need the JNISpice source installed, and you need to set the `JNISPICE_ROOT` environment variable.

   ```bash
   > export JNISPICE_ROOT=/path/to/JNISpice
   ```

   To permanently set this variable add this command to your `.bashrc`(Linux), `.zshrc` (MacOS) 
   or follow [these instructions](https://www.howtogeek.com/51807/how-to-create-and-use-global-system-environment-variables/) for windows

<a id="cloneps"></a>
### Clone ParSPICE and build
   ```bash
   > git clone https://github.com/JoelCourtney/parspice.git
   > cd parspice
   > ./gradlew build
   ```
   This builds ParSPICE and runs the tests.

<a id="publish"></a>
### Publish to Maven Local

To use ParSPICE in other projects, we recommend doing so through the Maven Local repo.

```bash
> ./gradlew publishToMavenLocal
```

This should store copies of the packaged outputs in `~/.m2/repository/parspice/`

In your own `build.gradle` file you should then be able to import the implementation dependency with `mavenLocal()` in the repositories list and `implementation 'parspice:parspice:1.0'` in the dependencies list. Examples of that can be found in the [templates repo](https://github.com/JoelCourtney/parspice-templates).

<a id="usage"></a>
## Usage

The following assumes that your project uses Gradle. All example code is written in Java, but Kotlin
works fine and is even recommended, because it simplifies the boilerplate that ParSPICE requires you to write.
No ParSPICE-specific performance difference has been observed between Java and Kotlin.

<a id="implworker"></a>
### Implementing a worker

Each worker contains two key functions that you have to implement: `void setup()` and `task(...)`.

- When the worker is started, `setup()` will be called once. If you have to load the JNISpice native library or furnsh a kernel, do it in setup. Remember that the worker is a separate process entirely; any setup you did on the main process is not available to the workers.
- Then, `task(...)` is called repeatedly. The return type and input argument type are determined by you (in the next section). `task(...)` is the smallest piece of your job's logic that has to run sequentially (i.e. if you ran your job single-threaded in a for loop, `task(...)` is the body of the loop). `task(...)`'s behavior should depend *only* on its inputs.

For example, if you need to perform some orbital calculation 100,000 times over some time-window, you should load the JNISpice library and your kernels in `setup()`, and `task(...)` should perform the calculation *once*.

<a id="workertypes"></a>
#### Worker Types and IO Combinations

Choose what IO you need for your worker. For performance, less IO is usually better if you can
get away with it. Any data type can be sent as input or output, but default networking behavior is only provided
for basic types and arrays of basic types. (see next section for details.)

- Output: Most tasks will need to return output data of some type (such as `ResultType` in the
  below example). Whatever data you return from the `task(...)` function will be sent back to the main
  process, collected in an `ArrayList`, and returned from the `ParSPICE.run(...)` call.
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
  Then you can easily translate this into a task with *no network overhead for the inputs*:
  ```java
  @Override
  public ResultType task(int i) throws Exception {
    var arg = ...; // some calculation depending only on i
    // do things with arg
    return someResult;
  }
  ```
  
The four combinations of IO each have an abstract Worker superclass for you to extend:

- Both Input and Output: extend `IOWorker<I,O>`, override `O task(I input)`
- Only Output: extend `OWorker<O>`, override `O task(int i)`
- Only Input: extend `IWorker<I>`, override `void task(I input)`
- No communication at all: extend `AutoWorker`, override `void task(int i)`
  
<a id="sendingdata"></a>
#### Sending data

All data, for both inputs and outputs, is sent over network sockets by implementers of the
`Sender<T>` interface.

<a id="bisenders"></a>
##### Built-in senders

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
overhead (negligible for arrays/matrices with more than a few elements). If you *do* specify the dimensions,
attempting to send an array of a different size is undefined behavior and may *or may not* result in an error.
When in doubt, just don't specify the length.

<a id="csenders"></a>
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

At the end of the day, you can do whatever you want to the Input and Output object streams; these are supposed to be as bare-bones and efficient as possible, so ParSPICE has no checks to make sure you are handling them correctly. Try to avoid using `writeObject` and `readObject` though; they use reflection and are *very* slow. They also cache object addresses, which can cause bugs if you want to send data from the same location multiple times with different contents.

<a id="fatjar"></a>
#### Creating the fat Worker Jar

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
we recommend custom source sets. The [templates repo](https://github.com/JoelCourtney/parspice-templates) contains examples of this, along with a custom `gradle workerJar` task, so you can create a different jar for the main process if you want.

<a id="runworker"></a>
#### Running the worker

Running your ParSPICE job in parallel is easy. Create a new instance of the `parspice.ParSPICE` class; arguments are the path to the worker jar, and the minimum port number to use for networking.

Then call `parSPICE.run` on your instance; arguments are an instance of your worker, either the number of iterations to run (for no-input tasks) or the list of inputs to run on (for tasks with network input), and the number of workers to use.

<a id="examples"></a>
#### Examples

The following is an `OWorker<double[]>` that calls `CSPICE.mxv` and `CSPICE.vhat` based on a fixed matrix
and a vector constructed from the input `int i` (the actual matrix and vector are nonsense values,
just a proof of concept):

<a id="worker"></a>
**Worker:**

```java
import parspice.worker.OWorker;
import parspice.sender.DoubleArraySender;

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
```
<a id="main"></a>
**Main Process:**

```java
import parspice.ParSPICE;

public class Main {
    public static void main(String[] args) {
        // create the ParSPICE instance.
        ParSPICE par = new ParSPICE("build/libs/worker.jar", 50050);
        
        // run 1000 iterations with 5 workers in parallel.
        ArrayList<double[]> results = par.run(new MxvhatWorker(), 1000, 5);
    }
}
```

The `build.gradle` file would be copied from the [templates repo's build.gradle](https://github.com/JoelCourtney/parspice-templates/blob/java/build.gradle)
<a id="error"></a>
### Error handling

The Worker superclasses allow `setup()` and `task(...)` to throw arbitrary errors. If any error is thrown on the worker process, the stacktrace will be printed to `/tmp/worker_log_i` where `i` is the ID of the worker, ranging from 0 to one less than the number of workers.

<a id="bench"></a>
## Benchmarking

You need JNISpice installed to run the benchmark.

Ensure that the JNISpice native library is somewhere in your library path, and set the environment variable
`JNISPICE_ROOT` to the path to the JNISpice sources, one level above the `src` directory. For example, on my machine that would be

```bash
> export JNISPICE_ROOT="/usr/local/JNISpice"
```

Use `./gradlew benchmark` to run the benchmark. It could take several minutes. It prints updates as it finishes cases; if you don't finish case 0 within a minute, something might have broken.

You can print out the benchmark analysis again just by running `./gradlew benchmark` again (the results are cached). To re-run the entire benchmark, run `./gradlew clean` first.

<a id="runtime"></a>
### Runtime Estimation

When the benchmark is done, it will output a regression model of the form

<pre>
        T_0
T = B_1 --- + B_2[ms/MB] D
         W

where	T   = total time to run task through ParSPICE
	T_0 = total time to run task singlethreaded
	W   = number of workers used
	D   = total amount of data transferred between processes, in MB
</pre>

B_1 is typically between 1 and 2 on modern consumer machines, which means that if you have a job big enough to make you consider ParSPICE, it will almost certainly run faster in ParSPICE (unless you have to transfer hundreds of bytes per iteration). B_1 being less than 1 is mathematically impossible (that would mean ParSPICE has negative overhead), so if that happens something's gone horribly wrong.

B_2 it typically between 1 and 10, which means that if you only need to send a small, fixed number of integers or doubles each iteration, you shouldn't need to worry about the network overhead making ParSPICE slower than single-threaded. In fact, we've found that on average consumer machines, if you have to send so much data that the overhead makes it slower, you'll first run out of memory storing all of that data anyway.

<a id="breakeven"></a>
### Break-Even Point Estimation

The benchmark also outputs a break-even point estimate which compares the amount of data sent per iteration with the average time it takes to run a single iteration. (This is found by setting `T - T0 = 0` and solving for `d = D/I` where `I` is the total number of iterations.)

<pre>
d = (1/B_2 - (B_1/B_2)/w)[B/ns] t

where:  d = data sent per iteration, in bytes
        w = number of workers
        t = average single-threaded time per iteration, in ns
</pre>

For large tasks, this estimates the upper limit of data sent per iteration such that ParSPICE is still more performant than running the task directly.

<a id="caveats"></a>
### Caveats

The benchmark runs a series of tasks, with varying computational and network costs. This means that the model is biased by a few very high leverage observations of very expensive tasks. So don't expect the model to be accurate for short, inexpensive tasks with only a few iterations (but in those cases, it probably isn't worth the time to port the task to ParSPICE anyway, even if it is slightly faster).

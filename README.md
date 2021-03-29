# ParSPICE with Custom Workers

The general idea is that the user writes their own worker code. Their worker
gets compiled into its own jar file, and the ParSPICE api starts several of them
up like before. The difference is, there don't need to be any network requests,
and there is only one round of responses.

For tasks without network inputs, the custom worker code has a `O task(int i)` function
that is called repeatedly, where `O` is any type. The user must find a way to convert that
integer into the arguments of their first
spice call, but after that they write *identical* code to what they would do directly
with JNISpice. The only network overhead is when returning the responses. The user can return
any type they want from `task`, but they have to implement the `Sender`
interface to serialize and deserialize the object if it doesn't have a built-in sender. In
most cases, this will be very easy.

For tasks with network inputs, the custom worker has a `O task(I in)` function instead of
accepting an integer. `I` can also be any type, as long as the user uses a built-in sender
or implements their own. This version can be much slower than tasks with just outputs, but
it is still slightly faster than direct JNISpice even in a bad case.

## Building

Build with `gradle publishToMavenLocal`. This should store copies of the packaged outputs in
`~/.m2/repository/org/parspice/`

You should then be able to import the implementation dependency with `mavenLocal()` in the
repositories list and `implementation 'org.parspice:parspice.implementation:1.0-SNAPSHOT'` in
the dependencies list.

## Usage

The user needs to compile a fat jar of their project that includes all dependencies needed for the
worker. Then they should create a subclass of either `OutputWorker` or `InputOutputWorker`,
and call the appropriate ParSPICE method from the main process.

See [this repo](https://github.com/JoelCourtney/parspice-playground) for an example.

## Benchmarking & Runtime Prediction

You need JNISpice installed to run the benchmark.

Ensure that the JNISpice native library is somewhere in your library path, and set the environment variable
`JNISPICE_ROOT` to the path to the JNISpice sources. For example, on my machine that would be

```bash
export JNISPICE_ROOT="/usr/local/JNISpice"
```

Use `gradle benchmark` to run the benchmark. It could take several minutes. When its done, it will output a regression model of the form

        T_0
T = B_1 --- + B_2 D
         W

where	T   = total time to run task through ParSPICE
	T_0 = total time to run task singlethreaded
	W   = number of workers used
	D   = total amount of data transferred between processes, in MB

B_1 is typically between 1 and 2, so if you have a task big enough to make you consider ParSPICE,
it will almost certainly run faster in ParSPICE (unless you have to transfer hundreds of bytes per iteration).


The benchmark runs a series of tasks, with varying computational and network costs. This means that the model
is biased by a few very high leverage observations of very expensive tasks. So don't expect the model to be accurate
for short, inexpensive tasks with only a few iterations (but in those cases, it probably isn't worth the time to port the task to
ParSPICE anyway, even if it is slightly faster).

You can print out the benchmark analysis again just by running `gradle benchmark` again (the results are cached). To re-run the entire
benchmark, run `gradle clean` first.

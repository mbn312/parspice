# parspice

A concurrent wrapper for JNISpice.

## Setup

You will need the unzipped JNISpice source somewhere on your system. Then, set the environment variable `JNISPICE_ROOT`:

```
JNISPICE_ROOT=/absolute/path/to/JNISpice
```

You can set it with `export` in your shell rc file, or by prepending the above command to every `gradle build` command.

You also need [gradle](https://gradle.org/) installed. Alternatively, you can use the `./gradlew` script instead of the `gradle` command.

## Building

Run `gradle build` in the root of the repo.

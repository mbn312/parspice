# Built-in Sender Generation

Java doesn't allow you to use primitive types as generic arguments (which is *so spectacularly frustrating*), so in order to provide the user with built-in senders for primitives, primitive arrays, and primitive matrices, *all* of them have to be hardcoded.

They all have very similar code that cannot be consolidated *at all* (seriously, the ArraySenders and MatrixSenders cannot even have a common superclass with a generic argument because that would require the argument to be a primitive), so we are generating the source instead. Since the generated source is unlikely to change, we are including the output in the repo instead of forcing users to generate it on build (which would a python3 dependency on PyYAML).

But if the source does need to change, that's what this is for.

## Configuration

The `yaml/senders.yaml` file has the configuration for generating all of the sender implementations. It is an array of objects of the following format:

```yaml
- name: Name to prepend to class name
  types:
    - data type for bare sender
    - data type for array sender
    - data type for matrix sender
  stream: function name to append to `ois.read` and `oos.write`
```

For example:

```yaml
- name: Int
  types:
    - Integer
    - int
    - int
  stream: Int
- name: String
  types:
    - String
    - String
    - String
  stream: UTF
```

## Templates

The template source files can be found in `src/gen/java/parspice/sender`. They have template arguments like `###NAME###`, `###TYPE###`, and `###STREAM###` that get searched-and-replaced with the values from the yaml file.

## Re-generating

If you make changes to the senders.yaml list, or to the template sources, just re-generate them. You'll need the PyYAML python3 library installed.

```bash
> cd src/gen/python3
> python3 generate_senders.py
```

You'll then have to add the generated files to git of course.

The file paths in the script use forward slashes, so it might not work on Windows. Honestly I don't think it's a big deal because no one is likely ever going to read this entire file. If I'm wrong, hi!

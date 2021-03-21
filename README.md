# ParSPICE

The general idea is that the user writes their own worker code. Their worker
gets compiled into its own jar file, and the Dispatcher starts several of them
up like before. The difference is, there are no requests, and only one round of
responses.

The custom worker code has an `iterate(int i)` function that is called repeatedly.
The user must find a way to convert that integer into the arguments of their first
spice call, but after that they write *identical* code to what they would do directly
with JNISpice.

The only network overhead is when returning the responses. The user can return
any type they want from `iterate`, but they have to implement the `Returner`
interface to serialize and deserialize the object from the output stream. In
most cases, this will be very easy, and maybe a little tedious.

## Basic Performance

10,000,000 `CSPICE.vhat` calls: ParSPICE takes 2100 ms, direct CSPICE takes 3450 ms

# GET REKT SPICE

The important code is in implementation/. Currently, the user would have to write a client Main class, a Worker subclass, and a Returner implementer class.

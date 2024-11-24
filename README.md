Is Latin 1
==========

Test a file whether it contains only printable [ISO-8859-1](https://en.wikipedia.org/wiki/ISO/IEC_8859-1) characters, allowing [LR](https://en.wikipedia.org/wiki/Newline) and [CR](https://en.wikipedia.org/wiki/Carriage_return).

The code uses Java Vector API and memory mapped IO.

Running
-------

Once initialized the program runs allocation free and can be run with Epsilon GC

```sh
-Xmx256m -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC
```

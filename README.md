# LCR algorithm assignment

In the terminal (assuming Java SDK is installed):
```
> javac *.java
> java Scheduler
> java RingsScheduler
```

## Basic ring network

The **only** file that should be changed to run a desired simulation on a basic 
ring network is the `Scheduler.java` file. There, at the top of the source code
you will find:

```java
// the number of nodes to put in basic ring network
public static final int NUM_NODES_BASIC = 12;
// randomness "lever" to make IDs more interesting
private final int RAND_VARIANCE_BASIC = 5;
// I didn't really mess around with this but you can do so if you wish
private final int MAX_WAKE_ROUNDS_BASIC = 12;
```

With comments detailing exactly what changing each constant will do. In 
`Scheduler.java` you may also want to look at the `start()` method which
contains a debug function called `printRing()` currently commented out:
```java
public void start() {
        this.allNodes = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.wakeRounds = new ArrayList<>();

        System.out.println("[INIT] Generating ring network");

        generateIds();
        generateWakeRounds();
        generateNodes();
        giveNeighbours();

        System.out.println("[DONE] Ring network generated!");

        // UNCOMMENT ME TO PRINT NODES BEING LINKED TO EACH OTHER
        // printRing(); <========================================
        // UNCOMMENT ME TO PRINT NODES BEING LINKED TO EACH OTHER
}
```

## Ring of rings network

The **only** file that should be changed to run a desired simulation on a ring
of rings network is the `RingsScheduler.java` file. There, at the top of the
source code you will find:

```java
// number of nodes in main ring
public final static int NUM_NODES_MAIN = 12;
// number of interface nodes ⊆ number of nodes in main ring
private final static int[] NUM_NODES_INTERFACE = { 2, 5, 8 };
// number of nodes possible within subrings
private final static int NUM_NODES_MAIN_INTERFACE = NUM_NODES_INTERFACE.length;
// ID generation can be "ascending", "descending" or "random"
private final static String ID_GEN_BEHAVIOUR = "ascending";
// I didn't really mess around with this but you can do so if you wish
private final static int MAX_WAKE_ROUND = 12;
```

To be more specific, `NUM_NODES_INTERFACE` will be the actual sub-rings
generated. In the above example, the main central ring will contain 12 nodes,
3 of which will be interface nodes. They interface with each of the 3 declared
sub-rings of sizes 2, 5, and 8 respectively. IDs are then assigned to
non-interface nodes in ascending order. Nodes are assigned in the same way they
are in `Scheduler.java` so I didn't bother making a debug to print these links.
I wouldn't even know how to format that kind of print in the first place.

---

Thank you for looking through my work, if there are any issues I will try to keep
an eye on my email at <sgkcarol@liverpool.ac.uk> 😊
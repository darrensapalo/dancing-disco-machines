dancing-disco-machines
======================
There can only be 0001 machine on the dance floor!

## Goal
Solve the mutex problem on distributed systems.

### Distributed systems and network 101
Have at least 1-4 machines running the application, who can be dancing (processing) individually. However, all of them want to be on the spotlight and dance it out on the dance floor.

## Notes about the system
On the `Driver` class, the first process that must run is the leader. This means that the boolean on the `NodeApplication` class must be set to true.

```
new NodeApplication(4040, true);
```

The remaining processes running on other machines must run with the boolean set to false.
```
new NodeApplication(4040, false);
```



### About
This is a small project for ADVANOS Term 2, Academic Year 2014-2015.
By Kristine Kalaw, Kyle Dela Cruz, and Darren Karl Sapalo.

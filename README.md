storm-beginner
==============

For Storm beginner.  Show you how to learn to use Storm with multiple instance.

==============
# Basic konwledge about Storm

Each spout or bolt are running X instances in parallel (called tasks).  
Groupings are used to decide which task in the subscribing bolt, the tuple is sent to.  
A bolt can subscribe to an unlimited number of streams, by chaining groupings.  

    TopologyBuilder builder = new TopologyBuilder();
     
    /** Create stream called ”words” , Run 10 tasks **/
    builder.setSpout("words", new TestWordSpout(), 10); 
     
    /** Create stream called ”exclaim1” , Run 3 tasks , 
        Subscribe to stream ”words”, using shufflegrouping 
    **/
    builder.setBolt("exclaim1", new ExclamationBolt(), 3)
           .shuffleGrouping("words"); 
     
    /** Create stream called ”exclaim2" , Run 2 tasks , 
        Subscribe to stream ”exclaim1”,using shufflegrouping**/ 
    builder.setBolt("exclaim2", new ExclamationBolt(), 2) 
           .shuffleGrouping("exclaim1"); 
###Fault tolerance
Zookeeper stores metadata in a very robust way.  
Nimbus and Supervisor are stateless and only need metadata from ZK to work/restart.  

When a node dies:  
*The tasks will time out and be reassigned to other workers by Nimbus.  

When a worker dies:  
*The supervisor will restart the worker.  
*Nimbus will reassign worker to another supervisor, if no heartbeats are sent.  
*If not possible (no free ports), then tasks will be run on other workers in topology. If more capacity is added to the cluster later, STORM will automatically initialize a new worker and spread out the tasks.  

When nimbus or supervisor dies:  
*Workers will continue to run
*Workers cannot be reassigned without Nimbus
*Nimbus and Supervisor should be run using a process monitoring tool, to restarts them automatically if they fail.

###AT-LEAST-ONCE PROCESSING
STORM guarantees at-least-once processing of tuples.  

* *Message id*  gets assigned to a tuple when emitting from spout or bolt. Is 64 bits long.  
* *Tree of tuples*  is the tuples generated (directly and indirectly) from a spout tuple.  
* *Ack*  is called on spout, when tree of tuples for spout tuple is fully processed.   
* *Fail*  is called on spout, if one of the tuples in the tree of tuples fails or the tree of tuples is not fully processed within a specified timeout (default is 30 seconds).
It is possible to specify the message id, when emitting a tuple. This might be useful for replaying tuples from a queue.  

IMAGE-3  

* *Anchoring* is used to copy the spout tuple message id(s) to the new tuples generated. In this way, every tuple knows the message id(s) of all spout tuples.
* *Multi-anchoring* is when multiple tuples are anchored. If the tuple tree fails, then multiple spout tuples will be replayed. Useful for doing streaming joins and more.
* *Ack* called from a bolt, indicates the tuple has been processed as intented
* *Fail* called from a bolt, replays the spout tuple(s)

Every tuple must be acked/failed or the task will run out of memory at some point.  

==============
# List of instance:
wordcount-clj
wordcount-java

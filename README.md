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


==============
# List of instance:
wordcount-clj
wordcount-java

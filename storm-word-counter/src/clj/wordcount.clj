(ns wordcount
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))

(defspout sentence-spout ["sentence"]
  [conf context collector]
  (let  [sentences ["a little brown dog"
                    "the man petted the dog"
                    "four score and seven years ago"
                    "an apple a day keep the doctor away"]]
    (println "spout1: sentences.....")
    (spout 
      (nextTuple []
                 (Thread/sleep 100)
                 (emit-spout! collector [(rand-nth sentences)])
                 )
      (ack [id]
           ;;this function only for reliable spout
           ))))

(defspout sentence-spout-parameterized ["word"] {:params [sentences] :prepare false}
  [collector]
  (Thread/sleep 500)
  (println "spout2: param..")
  (emit-spout! collector [(rand-nth sentences)]))

(defbolt split-sentence ["word"] [tuple collector]
  (let [words (.split (.getString tuple 0) " ")]
    (println "bolt1: split-sentence")
    (doseq [w words]
      (emit-bolt! collector [w] :anchor tuple))
    (ack! collector tuple)
    ))

(defbolt word-count ["word" "count"] {:prepare true}
  [conf context collector]
  (let [counts (atom {})]
    (println "bolt2: word-count")
    (bolt
      (execute [tuple]
               (let [word (.getString tuple 0)]
                 (swap! counts (partial merge-with +) {word 1})
                 (emit-bolt! collector [word (@counts word)] :anchor tuple)
                 (ack! collector tuple)
                 )))))

(defn mk-topology []
  (topology
    {"data-random-en" (spout-spec sentence-spout)
     "data-random-cn" (spout-spec (sentence-spout-parameterized 
                                    ["新 年 快 乐"
                                     "恭 喜 发 财 哈 哈 哈"])
                                  :p 2)}
    {"proc-split" (bolt-spec {"data-random-en" :shuffle "data-random-cn" :shuffle}
                             split-sentence
                             :p 5)
     "proc-count" (bolt-spec {"proc-split" ["word"]}
                             word-count
                             :p 6)}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "word-count" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 2000)
    (.shutdown cluster)
    ))

(defn submit-topology! [name]
  (System/setProperty "storm.jar" (.. (Class/forName "wordcount") getProtectionDomain getCodeSource getLocation getPath))
  (println "begin submit topology....");;(System/getProperty "storm.jar"))
  (StormSubmitter/submitTopology
    name
    {TOPOLOGY-DEBUG true
     TOPOLOGY-WORKERS 3}
    (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))

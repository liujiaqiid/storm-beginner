(defproject word-counter "0.1.0-SNAPSHOT"
  :source-paths ["src/clj"]
  :aot :all
  :description "Word-counter Proj"
  :url "http://github.com/liujiaqiid"
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev
             {:dependencies [[storm "0.8.2"]]}})

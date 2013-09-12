(defproject storm-word-counter "0.1.0-SNAPSHOT"
  :source-paths ["src/clj"]
  :aot :all
  :description "Use storm to build word counter system"
  :url "http://github.com/liujiaqiid"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev
             {:dependencies [[storm "0.8.2"]]}})

(defproject asyncomponent "0.1.0-SNAPSHOT"
  :description "clojure.core.async and component example"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [org.clojure/core.async "0.2.374"]
                 [clojurewerkz/quartzite "2.0.0"]
                 [org.clojure/tools.nrepl "0.2.11"]]

  :main asyncomponent.bin)

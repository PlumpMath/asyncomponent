(ns asyncomponent.bin
  (:require
   [com.stuartsierra.component :as component]
   [asyncomponent.system :as sys]
   [clojure.edn :as edn]))

(def system nil)

(defn read-config
  [config-path]
  (let [config (-> config-path slurp (edn/read-string))]
    (print config)
    config))

(defn start
  "Starts asyncomponent system"
  [config-path]
  (println "Starting System")
  (let [config (read-config config-path)]
    (alter-var-root #'system (fn [_] (component/start (sys/new-instance config))))))


(defn -main
  [& args]
  (if (not (empty? args))
    (start (first args))
    (start nil)))

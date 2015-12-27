(ns asyncomponent.repl
  (:use [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:require
   [com.stuartsierra.component :as component]))

(def ^:const PORT 7888)
(def ^:const NAME "REPL Component")

(defrecord Repl [port server]
  component/Lifecycle

  (start [this]
    (println ";; Starting " NAME " @0.0.0.0:" port)
    (assoc this :server (start-server :port port)))
  (stop [this]
    (println ";; Stopping " NAME)
    (when server
      (stop-server server))))



(defn new-instance
  "Creates new instance of REPL Component.
  ### PARAMETERS:
  port: REPL Port to Start. (default: 7888)"
  [port]
  (map->Repl {:port (or port PORT)}))

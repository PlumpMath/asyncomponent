(ns asyncomponent.log
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(def ^:const NAME "Logger Component")

(defn- log
  [in log-fn]
  (async/go
    (while true
      (log-fn (async/<! in)))))

(defrecord Logger [inbound outbound log-fn]
  component/Lifecycle

  (start [this]
    (println ";; Starting " NAME)
    (log inbound log-fn))

  (stop [this]
    (println ";; Stopping " NAME)))

(defn- default-writer
  [event]
  (println "LOGGER: " event))

(defn new-instance
  "Creates new instance of Logger Component
  #### Parameters:
  inbound (optional) : Inbound comminucation channel to logger component.
  outbound (optional): Outbound comminucation channel where logger component puts its results.
  log-fn (optional): log function to do operation on log event.
  "
  [& {:keys [inbound outbound log-fn]
      :or {inbound (async/chan)
           outbound (async/chan)
           log-fn default-writer}}]
  (map->Logger {:inbound inbound
                :outbound outbound
                :log-fn log-fn}))

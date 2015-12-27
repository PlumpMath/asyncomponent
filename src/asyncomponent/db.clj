(ns asyncomponent.db
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            ))

(def ^:const NAME "Database Component")

(def counter (atom 0))

(def ^:private generate-id (fn [] (swap! counter inc)))

(defn- operate
  "Generates random data for db query simulation"
  []
  [{:id (generate-id)
     :value (/ (rand-int 10) (* 1.0 (rand-int 10)))
     :date (java.util.Date.)}
   {:id (generate-id)
     :value (/ (rand-int 10) (* 1.0 (rand-int 10)))
     :date (java.util.Date.)}])

(defn- trigger
  "Triggers database query when anything happens in inbound channel.
  ### Parameters:
  in: inbound channel to look for triggering.
  out: outbound channel to write query results
  log: log channel to put any log statements.
  "
  [in out log]
  (async/go
    (while (async/<! in)
      (try
        (doseq [data (operate)]
          (async/>! out data))
        (catch Exception e
          (async/>! log {:from :database :type :exception :class (class e) :message (.getMessage e)})))
      )))

(defrecord Database [host port inbound outbound log]
  component/Lifecycle

  (start [this]
    (println ";; Starting " NAME)
    (trigger inbound outbound log))

  (stop [this]
    (println ";; Stopping " NAME)))


(defn new-instance
  "Creates new instance of Database Component.
  #### Parameters:
  NOTE: optional -> (async/chan)
  host: Database host
  port: Database port
  inbound (optional) : Inbound comminucation channel to database component.
  outbound (optional): Outbound comminucation channel where database component puts its results.
  log (optional): log comminucation channel where database component puts its logs.
  "
  [host port & {:keys [inbound outbound log] :or {inbound (async/chan) outbound (async/chan) log (async/chan)}}]
  (map->Database
   {:host host :port port
    :inbound inbound
    :outbound outbound
    :log log}))

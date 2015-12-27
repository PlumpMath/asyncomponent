(ns asyncomponent.transformer
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(def ^:const NAME "Transformer Component")

(defn- transform
  "Transforms data given from inbound channel and writes transformed data to outbound channel.
### PARAMETERS:
  in: inbound channel to accept data.
  out: outbound channel to write transformed data.
  log: logging channel to write log events during transformation."
  [in out log]
  (async/go
    (while true
      (let [data (async/<! in)
            tr-data (assoc data :value (+ 1 (:value data)))]
        (async/>! out tr-data)
        (async/>! log {:component :transformer :before data :after tr-data})))))

(defrecord Transformer [inbound outbound log]
  component/Lifecycle

  (start [this]
    (println ";; Starting " NAME)
    (transform inbound outbound log))

  (stop [this]
    (println ";; Stopping " NAME)))


(defn new-instance
  "Creates new instance of Transformer Component.
  ### PARAMETERS:
  NOTE: optional -> (async/chan)
  inbound (optinal): inbound communication channel to transformer component. (default value: new-channel).
  outbound (optional): outbound communication channel where transformer component put transformed data into.
  log (optional): log comminucation channel where transformer component put log data."
  [& {:keys [inbound outbound log] :or {inbound (async/chan) outbound (async/chan) log (async/chan)}}]
  (map->Transformer
   {:inbound inbound
    :outbound outbound
    :log log}))

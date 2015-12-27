(ns asyncomponent.writer
  (:require
   [com.stuartsierra.component :as component]
   [clojure.core.async :as async]))

(defn- write
  [in out log]
  (async/go
    (while true
      (let [message (async/<! in)
            id (:id message)]
        (println "### WRITER: Received Message: " (:id message))
        (async/>! log id)))))


(defrecord Writer [inbound outbound log]
  component/Lifecycle

  (start [this]
    (println ";; Starting Writer Component")
    (write inbound outbound log))

  (stop [this]
    (println ";; Stopping Writer Component")))


(defn new-instance
  "Creates new instance of Writer Component"
  [& {:keys [inbound outbound log] :or {inbound (async/chan) outbound (async/chan) log (async/chan)}}]
  (map->Writer {:inbound inbound :outbound outbound :log log}))

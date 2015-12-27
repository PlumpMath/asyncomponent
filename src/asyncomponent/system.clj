(ns asyncomponent.system
  (:require
   [com.stuartsierra.component :as component]
   [clojure.core.async :as async]
   [asyncomponent.db :as db]
   [asyncomponent.scheduler :as scheduler]
   [asyncomponent.transformer :as transformer]
   [asyncomponent.log :as log]
   [asyncomponent.repl :as repl]
   [asyncomponent.writer :as writer]))

(def system-components [:scheduler :db :logger :transformer :repl :writer])

(defrecord AsynComponentSystem [db scheduler transformer logger repl]
  component/Lifecycle

  (start [this]
    (component/start-system this system-components))

  (stop [this]
    (component/stop-system this system-components)))

(defn new-instance
  "Creates new instance of AsynComponentSystem."
  [{:keys [scheduler db logger transformer repl] :as config-options}]
  (let [log-channel (async/chan)
        data-in-channel (async/chan)
        data-out-channel (async/chan)
        transformer-channel (async/chan)]
    (map->AsynComponentSystem
     {:logger (log/new-instance :inbound log-channel)
      :db (db/new-instance (:host db) (:port db) :inbound data-in-channel :outbound data-out-channel :log log-channel)
      :transformer (transformer/new-instance :inbound data-out-channel :outbound transformer-channel :log log-channel)
      :scheduler (scheduler/new-instance (:period scheduler) :outbound data-in-channel :log log-channel)
      :repl (repl/new-instance (:port repl))
      :writer (writer/new-instance :log log-channel :inbound transformer-channel)
      })))

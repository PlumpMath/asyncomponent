(ns asyncomponent.scheduler
  (:require
   [clojure.core.async :as async]
   [com.stuartsierra.component :as component]
   [clojurewerkz.quartzite.scheduler :as qs]
   [clojurewerkz.quartzite.conversion :as qc]
   [clojurewerkz.quartzite.triggers :as t]
   [clojurewerkz.quartzite.jobs :as j]
   [clojurewerkz.quartzite.jobs :refer [defjob]]
   [clojurewerkz.quartzite.schedule.cron :as cron]
   [clojurewerkz.quartzite.schedule.daily-interval
    :refer [schedule on-every-day starting-daily-at time-of-day ending-daily-at with-interval-in-minutes
            with-interval-in-seconds]]))

  (defjob ScheduledJob
    [ctx]
    (let [data (qc/from-job-data ctx)
          out (get data "outbound" nil)
          log (get data "log" nil)]
      (async/go
        (let [value (rand-int 10)
              push? (odd? value)
              data {:from :scheduler :date (java.util.Date.) :message "trigger"}]
          (async/>! out data)
          (async/>! log data)))))

(def build-job
  (fn [out log]
    (j/build
     (j/of-type ScheduledJob)
     (j/using-job-data
      {"outbound" out
       "log" log}))))

(defn- run-scheduler
  "Runs scheduler over given api"
  [time out log]
  (let [trigger (t/build
                 (t/with-identity "daily4")
                 (t/start-now)
                 (t/with-schedule
                   (cron/schedule
                    (cron/cron-schedule time))))
        job (build-job out log)
        instance (-> (qs/initialize)
                     qs/start)]
    (qs/schedule instance job trigger)))


(defrecord Scheduler [period inbound outbound log]
  component/Lifecycle

  (start [this]
    (println ";; Starting Scheduler Component with period " period)
    (run-scheduler period outbound log))

  (stop [this]
    (println ";; Stopping Scheduler Component")))


(defn new-instance
  "Creates new instance of Scheduler Component"
  [period & {:keys [inbound outbound log] :or {inbound (async/chan) outbound (async/chan) log (async/chan)}}]
  (map->Scheduler
   {:period period
    :inbound inbound
    :outbound outbound
    :log log}))

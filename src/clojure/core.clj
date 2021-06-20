(ns core
  (:require [protobuf.core :as pb]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clj-http.client :as http]
            [java-time :as time]
            [java-time.format :as tf])
  (:import (mdg.engine.proto Reports$ReportHeader Reports$FullTracesReport Reports$Traces Reports$Trace Reports$TracesReport)))

(def endpoint "https://usage-reporting.api.apollographql.com/api/ingress/traces")

(def X-Api-Key "")

(defn ->Trace [{:keys [path parentType fieldName returnType startOffset duration]}]
  #_(let [startTime  (+ startTime startOffset)
          parentType (:parentType trace)
          root       nil])
  (pb/create Reports$Trace
             {:start_time  ""
              :end_time    ""
              :duration_ns duration}))

(defn aggregate-tracing [tracing]
  (let [t1     (->Trace {:duration 1000000})
        traces (pb/create Reports$Traces
                          {:trace [t1]})]
    [{:key   "q1"
      :value traces}]))

(defn ->TracesReport [tracing]
  (let [header (pb/create Reports$ReportHeader
                          {})
        t1     (->Trace {:duration 1000000})]
    (pb/create Reports$TracesReport
               {:header header
                :trace  [t1]})))

(defn ->FullTracesReport [tracing]
  (let [header           (pb/create Reports$ReportHeader
                                    {:hostname   "api-clj.farmmorning.com"
                                     :schemaTag  ""
                                     :schemaHash ""})
        traces_per_query (aggregate-tracing tracing)]
    (pb/create Reports$FullTracesReport
               {:header           header
                :traces_per_query traces_per_query})))


(defn send-report [tracing]
  (let [report (->TracesReport tracing)]
    (try
      (http/post endpoint
                 {:headers      {"X-Api-Key" X-Api-Key}
                  :content-type :application/protobuf
                  :body         (io/input-stream
                                  (pb/->bytes report))})
      (catch Exception e
        (ex-data e)))))


(comment

  (def sample
    (json/read-str (slurp (io/resource "sample.json"))))

  (def tracing
    (read-string (slurp (io/resource "lacinia-tracing.edn"))))


  (let [start-time   (time/zoned-date-time s)
        start-offset (time/nanos 1000000000)]
    (time/plus start-time start-offset))

  (aggregate-tracing tracing)
  (->FullTracesReport tracing)

  (send-report tracing)

  )
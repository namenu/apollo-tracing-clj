(ns core
  (:require [protobuf.core :as pb]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [java-time :as time]
            [java-time.format :as tf])
  (:import (mdg.engine.proto Reports$Field Reports$ReportHeader)))

(pb/create Reports$ReportHeader
           {:hostname   "api-clj.farmmorning.com"
            :schemaTag  ""
            :schemaHash ""}) )


(let [start-time   (time/zoned-date-time s)
      start-offset (time/nanos 1000000000)]
  (time/plus start-time start-offset))

(defn ->Trace [{:keys [path parentType fieldName returnType startOffset duration]}]
  (let [startOffset
        parentType (:parentType trace)
        ]
    (pb/create Reports$Trace
               {:endTime ""
                :startTime})))

(-> (pb/create Reports$Field
               {:name        "Alice"
                :return_type "ret"})
    )

(def sample
  (json/read-str (slurp (io/resource "sample.json"))))

(def tracing
  (read-string (slurp (io/resource "lacinia-tracing.edn"))))

(def trace
  (first (get-in tracing [:tracing :execution :resolvers])))

(->Trace trace)
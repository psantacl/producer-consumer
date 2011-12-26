(ns producer-consumer.simulation)

(def *start-time* (atom nil))
(def *end-time* (atom nil))
(def *num-of-integers* (atom nil))


(defn time-taken-ms []
  (float (/ (- @*end-time* @*start-time*)
            (* 1000 1000))))

(defn time-taken-s []
  (float (/ (- @*end-time* @*start-time*)
            (* 1000 1000 1000))))

(defn run-simulation [producer consumer num-of-integers]
  (reset! *start-time* (System/nanoTime))
  (reset! *num-of-integers* num-of-integers)
  (.start consumer )
  (.start producer))


(ns producer-consumer.ring-buffer
  (:require [clj-ring-buffer.core :as rbuf])
  (:use producer-consumer.simulation
        [clj-etl-utils.lang-utils :only [raise caused-by-seq]]))

(def *rb* (rbuf/make-ring-buffer 1 1 10000))
(def *results* (atom []))

(defn force-put [producer-fn v]
  (loop [done false]
    (if-not done 
      (let [done (producer-fn v)]
        (recur done)))))

(def *producer*
  (Thread.
   (fn []
     (let [producer (:producer-1 *rb*)]
       (doseq [i (range @*num-of-integers*)]
         (force-put producer i))))))



(def *consumer*
  (Thread.
   (fn []
     (let [consumer (:consumer-1 *rb*)]
       (loop []
         #_(println "CONSUMER: looping.")
         
         (let [next-int  (consumer)]
           (if-not (nil? next-int)
             (do
               (swap! *results* conj next-int)
               (if (zero? (rem next-int 1000))
                 (println (format "CONSUMER: got something from ring buffer: %s" next-int)))
               (if (not= next-int (dec @*num-of-integers*))
                 (recur)
                 (reset! *end-time* (System/nanoTime))))
             (do
               (Thread/yield)
               (recur)))))))))

(comment
  (count @*results*)
  
  (run-simulation *producer* *consumer* 1000000)  
  
  (time-taken-ms)


  ;;1_000_000 integers, 10k ring buffer
  ;; 4221.145
  ;; 2565.992
  ;; 2723.414
  ;; 3366.57
  
  (rbuf/peek (:ring-buffer *rb*)))
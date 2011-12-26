(ns producer-consumer.stm
  (:use producer-consumer.simulation
        [clj-etl-utils.lang-utils :only [raise caused-by-seq]]))


(def *q* (ref clojure.lang.PersistentQueue/EMPTY))
(def *results* (atom []))

(def *producer*
  (Thread.
   (fn []
     (doseq [i (range @*num-of-integers*)]
       (dosync
        (alter *q* conj i))))))


(def *consumer*
  (Thread.
   (fn []
     (loop []
       (if-let [next-int (dosync
                          (let [next-int (first (ensure *q*))]
                            (alter *q* pop)
                            next-int))]
         (do 
           (swap! *results* conj next-int)
           (if (zero? (rem next-int 1000))
             (println (format "CONSUMER: got something from the queue: %s" next-int)))
           (if (not= next-int (dec @*num-of-integers*))
             (recur)
             (reset! *end-time* (System/nanoTime))))
         (do
           (println "queue is empty. yielding to producer...")
           (Thread/yield)
           (recur)))))))


(comment
  (def *chicken* (ref clojure.lang.PersistentQueue/EMPTY))

  (dosync
   (alter *chicken* conj 69))
  
  (.size @*chicken*)
  
  (dosync
   (let [next-int (first (ensure *chicken*))]
     (alter *chicken* pop)
     next-int))

  
  (count @*results*)
  
  (run-simulation *producer* *consumer* 1000000)
  
  (time-taken-ms)
  8066.034

  )
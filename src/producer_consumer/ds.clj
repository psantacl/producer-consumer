(ns producer-consumer.ds
  (:use producer-consumer.simulation
        [clj-etl-utils.lang-utils :only [raise caused-by-seq]]))

(comment
  (def size 1000000)

  (def *a* (int-array 10))
  (time
   (doseq [i (range 10)]
     (aset *a* i 69)))

  ;; "Elapsed time: 15638.722 msecs"


  (do 
    (def *q* (ref clojure.lang.PersistentQueue/EMPTY))
    (time
     (doseq [i (range size)]
       (dosync
        (alter *q* conj i )))))




  ;; "Elapsed time: 4027.949 msecs"
  ;; "Elapsed time: 2896.376 msecs"

  (def *l* (ref '()))
  (time
   (doseq [i (range size)]
     (dosync
      (alter *l* conj i ))))
  ;; "Elapsed time: 3520.982 msecs"


  (def *v* (ref []))
  (time
   (doseq [i (range size)]
     (dosync
      (alter *v* assoc  i i ))))
  "Elapsed time: 4109.86 msecs"
  nil



  (def ^java.util.ArrayList *al*
    (doto
        (java.util.ArrayList. size)))
  
  (.size *al*)


  (time
   (doseq [i (range size)]
     (.add *al* i  69)))

  (.lastIndexOf *al* 69)
  
  (time   
   (doseq [i (range size)]
     (.set *al* i  69)))
  
  "Elapsed time: 140.123 msecs"  
  "Elapsed time: 131.331 msecs"
  "Elapsed time: 116.498 msecs"


  


  (set! *warn-on-reflection* true)


  )

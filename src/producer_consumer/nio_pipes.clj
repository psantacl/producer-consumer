(ns producer-consumer.nio-pipes
  (:use producer-consumer.simulation)
  (:import
   [java.nio.channels Pipe]
   [java.nio ByteBuffer]))


(def *out-buffer* (ByteBuffer/allocate 4))
(def *in-buffer*  (ByteBuffer/allocate 4))

(let [p (Pipe/open)]
  (def *sink* (.sink p))
  (def *source* (.source p)))

(defn write-bb-to-pipe [byte-buffer sink]
  (loop []
    (if (.hasRemaining byte-buffer)
      (do
        (.write sink byte-buffer)
        (recur )))))

(def *producer*
  (Thread.
   (fn []
     (doseq [i (range @*num-of-integers*)]
       (.putInt *out-buffer* i)
       (.flip *out-buffer*)
       #_(println (format "PRODUCER: writing %s"  i))
       (write-bb-to-pipe *out-buffer* *sink*)
       (.clear *out-buffer*)))))


(def *consumer*
  (Thread.
   (fn []     
     (loop []
       #_(println "CONSUMER: looping.")
       (let [bytes-read  (.read *source* *in-buffer*)]
         (if (> bytes-read 0)
           (do
             (.flip *in-buffer*)
             (let [next-int (.getInt *in-buffer*) ]
               (if (zero? (rem next-int 1000))
                 (println (format "CONSUMER: got something from pipe: %s" next-int)))
               (.clear *in-buffer*)
               (if (not= next-int (dec @*num-of-integers*))
                 (recur)
                 (reset! *end-time* (System/nanoTime)))))
           (do
            (Thread/yield)
            (recur))))))))




(comment
  (run-simulation *producer* *consumer* 1000000)
  

  (time-taken-s)
  (time-taken-ms)


  ;;1_000_000
  ;;  65385.44  
  ;;  64001.016
  ;;  69226.25
  ;;  67768.5
  
  (.getState *consumer*)
  (.getState *producer*)

  (.stop *consumer*)
  (.stop *producer*)

  (do
    (.configureBlocking *source* false)
    (.configureBlocking *sink* false))

  (.isBlocking *source*)
  (.isBlocking *sink*)
  )


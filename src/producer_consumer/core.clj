(ns producer-consumer.core
  (:require
   [producer-consumer.ring-buffer :as ring-buffer])
  (:import
   [java.nio.channels Pipe]
   [java.nio ByteBuffer]))


(def *out-buffer* (ByteBuffer/allocate 4))
(def *in-buffer*  (ByteBuffer/allocate 4))
(def *start-time* (atom nil))
(def *end-time* (atom nil))
(def *num-of-integers* (atom nil))

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




(defn run-simulation [num-of-integers]
  (reset! *start-time* (System/nanoTime))
  (reset! *num-of-integers* num-of-integers)
  (.start *consumer* )
  (.start *producer*))


(defn time-taken-ms []
  (float (/ (- @*end-time* @*start-time*)
            (* 1000 1000))))

(defn time-taken-s []
  (float (/ (- @*end-time* @*start-time*)
            (* 1000 1000 1000))))


(comment
  (run-simulation 1000000)
  (time-taken-ms)
  (time-taken-s)
  ;; 63.678246  nonblocking
  ;; 64.12581   blocking
  


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


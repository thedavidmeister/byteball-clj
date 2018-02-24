(ns unit.process
 (:require
  clojure.core.async
  unit.spec
  storage.datascript.api
  [clojure.spec.alpha :as spec]))

(defonce units (atom #{}))

(defn lowest-mci
 [us]
 (reduce min (remove nil? (map :main-chain-index us))))

; https://stackoverflow.com/questions/33620388/how-to-properly-batch-messages-with-core-async
(defn batch [in out max-time max-count]
  (let [lim-1 (dec max-count)]
    (clojure.core.async/go-loop [buf [] t (clojure.core.async/timeout max-time)]
      (let [[v p] (clojure.core.async/alts! [in t])]
        (cond
          (= p t)
          (do
            (clojure.core.async/>! out buf)
            (recur [] (clojure.core.async/timeout max-time)))

          (nil? v)
          (if (seq buf)
            (clojure.core.async/>! out buf))

          (== (count buf) lim-1)
          (do
            (clojure.core.async/>! out (conj buf v))
            (recur [] (clojure.core.async/timeout max-time)))

          :else
          (recur (conj buf v) t))))))

(defn -unit-chan
 "Returns a channel that takes units and batches/persists them internally."
 []
 (let [i (clojure.core.async/chan 1000)
       o (clojure.core.async/chan 1000)]
  (batch i o 1000 100)
  (clojure.core.async/go-loop []
   (let [us (clojure.core.async/<! o)]
    (storage.datascript.api/persist-units! us)
    (recur)))
  i))
(def unit-chan (memoize -unit-chan))

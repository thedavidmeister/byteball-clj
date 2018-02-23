(ns unit.process
 (:require
  clojure.core.async
  unit.spec
  [clojure.spec.alpha :as spec]))

(defonce units (atom #{}))

(defn lowest-mci
 [us]
 (reduce min (remove nil? (map :main-chain-index us))))

(defn -unit-chan
 []
 (let [c (clojure.core.async/chan 1000)]
  (clojure.core.async/go-loop []
   (let [u (clojure.core.async/<! c)]
    (spec/assert :unit/unit-data u)
    (swap! units conj u)
    (recur)))
  c))
(def unit-chan (memoize -unit-chan))

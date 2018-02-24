(ns storage.datascript.api
 (:require
  [datascript.core :as d]
  [clojure.spec.alpha :as spec]
  unit.spec
  storage.datascript.data))

(defn -conn
 []
 (d/create-conn storage.datascript.data/schema))
(def conn (memoize -conn))

(defn persist-units!
 ([units] (persist-units! (conn) units))
 ([conn units]
  {:pre [(d/conn? conn)
         (spec/valid? :unit/units units)]}
  (when (seq units)
   (d/transact!
    conn
    units))))

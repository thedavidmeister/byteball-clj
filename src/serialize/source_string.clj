(ns serialize.source-string
 (:require
  [clojure.test :refer [deftest is]]))

(def string-join-char "\u0000")

(def source-seq->source-string (partial clojure.string/join string-join-char))

; https://github.com/byteball/byteballcore/blob/master/string_utils.js#L11
(defprotocol ISourceString
 (->source-string [this] "Converts this to a source string"))

(extend-protocol ISourceString
 java.lang.String
 (->source-string [this] (source-seq->source-string ["s" this]))

 java.lang.Long
 (->source-string [this] (source-seq->source-string ["n" this]))

 java.lang.Double
 (->source-string [this]
  ; emulate JS float handling - 1.0 => "1"
  (if (== (int this) this)
   (->source-string (int this))
   (source-seq->source-string ["n" this])))

 java.lang.Boolean
 (->source-string [this] (source-seq->source-string ["b" this]))

 clojure.lang.PersistentVector
 (->source-string [this]
  (assert
   (pos-int? (count this))
   "Empty array when generating source-string")
  (str
   "["
   (source-seq->source-string (map ->source-string this))
   "]"))

 clojure.lang.PersistentArrayMap
 (->source-string [this]
  (->> this
   (into (sorted-map))
   (map (fn [[k v]] [(name k) (->source-string v)]))
   flatten
   (clojure.string/join string-join-char)))

 clojure.lang.Keyword
 (->source-string [this] (->source-string (name this))))

; TESTS

; mirroring byteballcore fns
(deftest ??simple-string
 (is
  (=
   (source-seq->source-string ["s" "simple test string"])
   (->source-string "simple test string")))

 (is (= 20 (count (->source-string "simple test string")))))

(deftest ??integer
 (is
  (=
   (source-seq->source-string ["n" 27090])
   (->source-string 27090))))

(deftest ??boolean
 (is
  (=
   (source-seq->source-string ["b" false])
   (->source-string false))))

(deftest ??vector
 (let [ts ["s" "n" "s" "s" "n" "b"]
       vs ["a" 81 "b" "c" 3.6903690369 true]]
  (is
   (=
    (str
     "["
     (source-seq->source-string
      (map
       (fn [t v] (source-seq->source-string [t v]))
       ts vs))
     "]")
    (->source-string vs)))))

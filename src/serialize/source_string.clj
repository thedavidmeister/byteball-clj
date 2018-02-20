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
  (source-seq->source-string
   [
    "["
    (source-seq->source-string (map ->source-string this))
    "]"]))

 clojure.lang.PersistentArrayMap
 (->source-string [this]
  (->> this
   (into (sorted-map))
   (map (fn [[k v]] [(name k) (->source-string v)]))
   flatten
   source-seq->source-string))

 clojure.lang.Keyword
 (->source-string [this] (->source-string (name this))))

; TESTS

; @see string_utils.test.js

; mirroring byteballcore fns
(deftest ??primitives
 (doseq [[i prefix c] [; simple string
                       ["simple test string" "s" 20]
                       ; integer
                       [27090 "n" 7]
                       ; float
                       [8.103 "n" 7]
                       ; float int
                       [1.0 "n" 3]
                       ; boolean
                       [false "b" 7]]]
  (is
   (=
    (source-seq->souce-string [prefix i])
    (->source-string i)))

  (is (= c (count (->source-string i))))))

(deftest ??vector
 (let [ts ["s" "n" "s" "s" "n" "b"]
       vs ["a" 81 "b" "c" 3.6903690369 true]]
  (is
   (=
    (source-seq->source-string
     [
      "["
      (source-seq->source-string
       (map
        (fn [t v] (source-seq->source-string [t v]))
        ts vs))
      "]"])
    (->source-string vs)))

  (is (= 42 (count (->source-string vs))))))

(deftest ??hash-map
 (let [v {:unit "cluster"
          :bunch 9
          :witness {:name "axe"
                    :index 18}};]));]))
       expected
       (source-seq->source-string
        ["bunch" (->source-string 9)
         "unit" (->source-string "cluster")
         "witness"
         "index" (->source-string 18)
         "name" (->source-string "axe")])]
  (is (= expected (->source-string v)))
  (is (= 54 (count (->source-string v))))))

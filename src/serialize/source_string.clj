(ns serialize.source-string)

(def string-join-char "\u0000")

; https://github.com/byteball/byteballcore/blob/master/string_utils.js#L11
(defprotocol ISourceString
 (->source-string [this] "Converts this to a source string"))

(extend-protocol ISourceString
 java.lang.String
 (->source-string [this] (str "s" this))

 java.lang.Long
 (->source-string [this] (str "n" this))

 java.lang.Boolean
 (->source-string [this] (str "b" this))

 clojure.lang.PersistentVector
 (->source-string [this]
  (assert
   (pos-int? (count this))
   "Empty array when generating source-string")
  (str
   "["
   (clojure.string/join string-join-char (map ->source-string this))
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

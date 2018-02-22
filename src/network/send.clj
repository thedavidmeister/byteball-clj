(ns network.send
 (:require
  manifold.stream
  cheshire.core
  [clojure.spec.alpha :as spec]))

; https://github.com/byteball/byteballcore/blob/master/network.js#L92
(defn message!
 [conn type content]
 {:pre [(spec/valid? :message/type type)]}
 (manifold.stream/put!
  conn
  (cheshire.core/generate-string
   [(name type) content])))

; https://github.com/byteball/byteballcore/blob/master/network.js#L100
(defn just-saying!
 [conn subject body]
 {:pre [(spec/valid? :justsaying/subject subject)]}
 (message! conn :justsaying {:subject subject :body body}))

(defn request!
 ([conn command] (request! conn command nil))
 ([conn command params]
  (message!
   conn
   :request
   (merge
    {:command command
     :tag (gensym)}
    (when params {:params params})))))

(defn error!
 [conn error]
 (just-saying! conn "error" error))

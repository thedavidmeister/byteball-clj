(ns network.core
 (:require
  aleph.http
  network.spec
  manifold.stream
  network.data
  [clojure.spec.alpha :as spec]))

(defn -conn
 ([] (-conn network.data/hub-url))
 ([url]
  (aleph.http/websocket-client url)))
(def conn (memoize -conn))

; https://github.com/byteball/byteballcore/blob/master/network.js#L92
(defn send-message!
 [conn type content]
 {:pre [(spec/valid? :message/type type)]}
 (prn [(name type) content])
 (manifold.stream/put!
  @conn
  [(name type) content]))

; https://github.com/byteball/byteballcore/blob/master/network.js#L100
(defn just-sayin!
 [conn subject body]
 (send-message! conn :justsayin {:subject subject :body body}))

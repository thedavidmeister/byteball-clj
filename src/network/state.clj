(ns network.state
 (:require
  aleph.http
  manifold.stream
  network.data
  network.dispatch))

(defn -conn
 ([] (-conn network.data/hub-url))
 ([url]
  (let [c @(aleph.http/websocket-client url)]
   (manifold.stream/consume (partial network.dispatch/event-msg-handler c) c)
   c)))
(def conn (memoize -conn))

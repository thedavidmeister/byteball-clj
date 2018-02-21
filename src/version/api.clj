(ns version.api
 (:require
  network.send
  taoensso.timbre
  manifold.stream))

(defn incompatible!
 [conn s mine yours
  (network.send/error!
   conn
   (str "Incompatible " s ", mine " mine ", yours " yours))
  (taoensso.timbre/error "incompatible " s)
  (manifold.stream/close! conn)])

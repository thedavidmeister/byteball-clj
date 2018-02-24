(ns network.subscribe
 (:require
  network.send))

(defn subscribe!
 [conn]
 (network.send/request!
  conn
  "subscribe"
  {:subscription_id (gensym)
   :last_mci 0}))

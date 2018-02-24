(ns byteball.api
 (:require
  network.state))

(defn hub-conn!
 "initialises and/or returns a connection to the hub"
 []
 (network.state/conn))

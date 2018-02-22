(ns network.core
 (:require
  network.spec
  network.data
  network.dispatch
  network.send

  ; bootstrap msg handlers
  network.justsaying
  network.request))

(defn joints-since!
 [conn mci]
 (network.send/just-saying! conn "refresh" mci))

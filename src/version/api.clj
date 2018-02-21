(ns version.api
 (:require
  network.send
  taoensso.timbre
  manifold.stream
  version.data))

(defn incompatible!
 [conn s mine yours]
 (network.send/error!
  conn
  (str "Incompatible " s ", mine " mine ", yours " yours))
 (taoensso.timbre/error "incompatible " s)
 (manifold.stream/close! conn))

(defn version->int
 [version]
 (let [[major minor patch] (clojure.string/split version ".")]
  (int
   (+
    (* major 10000)
    (* minor 100)
    patch))))

(defn old-core?
 [version-map]
 (let [{:keys [library-version]} version-map]
  (and
   (string? library-version)
   (<
    (version->int library-version)
    (version->int "0.2.70"))
   (= "1.0" version.data/protocol-version))))

(ns version.api
 (:require
  network.send
  taoensso.timbre
  manifold.stream
  version.data
  version.spec
  [clojure.spec.alpha :as spec]))

(defn incompatible!
 [conn s mine yours]
 (network.send/error!
  conn
  (str "Incompatible " s ", mine " mine ", yours " yours))
 (taoensso.timbre/error "incompatible " s)
 (manifold.stream/close! conn))

(defn version-onto-conn!
 [conn version]
 (spec/assert :version/version version)
 (alter-meta! conn merge version))

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
   (spec/assert :version/library-version library-version)
   (<
    (version->int library-version)
    (version->int "0.2.70"))
   (= "1.0" version.data/protocol-version))))

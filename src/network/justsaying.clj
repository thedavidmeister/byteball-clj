(ns network.justsaying
 (:require
  network.dispatch
  taoensso.timbre
  version.data
  version.api
  manifold.stream
  network.send
  [clojure.spec.alpha :as spec]))

(defmulti -justsaying-msg-handler "Multimethod to handle `justsaying-msg`s" :subject)

(defmethod network.dispatch/-event-msg-handler :justsaying [msg]
 (spec/assert :justsaying/subject msg)
 (-justsaying-msg-handler msg))

(defmethod -justsaying-msg-handler "version" [msg]
 (let [body (:body msg)
       conn (:network/conn msg)]
  (when body
   (cond
    (not= version.data/protocol-version (:protocol-version body))
    (version.api/incompatible! conn "versions" version.data/protocol-version (:protocol-version body))

    (not= version.data/alt (:alt body))
    (version/api/incompatible! conn "alts" version.data/alt (:alt body))

    :default
    (do
     (reset! version.data/library-version (:library-version body)))))))

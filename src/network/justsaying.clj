(ns network.justsaying
 (:require
  network.dispatch
  taoensso.timbre
  version.data
  version.api
  manifold.stream
  network.send
  network.login
  [clojure.spec.alpha :as spec]))

(defmulti -justsaying-msg-handler "Multimethod to handle `justsaying-msg`s" :subject)

(defmethod network.dispatch/-event-msg-handler :justsaying [msg]
 (spec/assert :justsaying/subject (:subject msg))
 (taoensso.timbre/debug (dissoc msg :network/conn))
 (-justsaying-msg-handler msg))

(defmethod -justsaying-msg-handler "version" [msg]
 (when-let [body (:body msg)]
  (let [conn (:network/conn msg)]
   (cond
    (not= version.data/protocol-version (:protocol-version body))
    (version.api/incompatible! conn "versions" version.data/protocol-version (:protocol-version body))

    (not= version.data/alt (:alt body))
    (version.api/incompatible! conn "alts" version.data/alt (:alt body))

    :default
    (alter-meta! conn merge body)))))

(defmethod -justsaying-msg-handler "hub/challenge" [msg]
 (when-let [body (:body msg)]
  (network.login/login!
   (:network/conn msg)
   body)))

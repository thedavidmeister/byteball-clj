(ns network.justsaying
 (:require
  network.dispatch
  taoensso.timbre
  version.data
  version.api
  manifold.stream
  network.send
  network.login
  network.subscribe
  unit.process
  unit.spec
  clojure.core.async
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
    (version.api/version-onto-conn! conn body)))))

(defmethod -justsaying-msg-handler "hub/challenge" [msg]
 (when-let [body (:body msg)]
  (network.login/login!
   (:network/conn msg)
   body)))

(defmethod -justsaying-msg-handler "joint" [msg]
 (when-let [unit-data (-> msg :body :unit)]
  (clojure.core.async/>!! (unit.process/unit-chan) unit-data)))

(defmethod -justsaying-msg-handler "hub/push_project_number" [msg]
 (network.subscribe/subscribe!
  (:network/conn msg)))

(defmethod -justsaying-msg-handler "hub/message_box_status" [msg])
(defmethod -justsaying-msg-handler "exchange_rates" [msg])
(defmethod -justsaying-msg-handler "info" [msg])
(defmethod -justsaying-msg-handler "error" [msg])
(defmethod -justsaying-msg-handler "free_joints_end" [msg])

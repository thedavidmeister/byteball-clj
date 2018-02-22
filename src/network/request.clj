(ns network.request
 (:require
  taoensso.timbre
  network.send
  [clojure.spec.alpha :as spec]))

(defmulti -request-msg-handler "Multimethod to handle `request`s" :command)

(defmethod network.dispatch/-event-msg-handler :request [msg]
 (spec/assert :request/request msg)
 (taoensso.timbre/debug (dissoc msg :network/conn))
 (-request-msg-handler msg))

(defmethod -request-msg-handler "subscribe" [msg])
 ; https://github.com/thedavidmeister/byteball-clj/issues/24

(defmethod -request-msg-handler "heartbeat" [msg]
 ())

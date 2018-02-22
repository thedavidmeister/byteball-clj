(ns network.response
 (:require
  network.dispatch
  [clojure.spec.alpha :as spec]
  taoensso.timbre))

(defmulti -response-msg-handler "Multimethod to handle `response`s" :command)

(defmethod network.dispatch/-event-msg-handler :response [msg]
 (taoensso.timbre/debug (dissoc msg :network/conn)))
 ; (-request-msg-handler msg))

; (defmethod -request-msg-handler "subscribe" [msg])
 ; https://github.com/thedavidmeister/byteball-clj/issues/24

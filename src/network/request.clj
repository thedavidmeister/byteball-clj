(ns network.request
 (:require
  taoensso.timbre
  [clojure.spec.alpha :as spec]))

(defmulti -request-msg-handler "Multimethod to handle `request`s" :command)

(defmethod network.dispatch/-event-msg-handler :request [msg]
 (spec/assert :request/command (:command msg))
 (taoensso.timbre/debug (dissoc msg :network/conn))
 (-request-msg-handler msg))

(defmethod -request-msg-handler "subscribe" [msg])
 ; https://github.com/thedavidmeister/byteball-clj/issues/24

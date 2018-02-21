(ns network.justsaying
 (:require
  network.dispatch
  taoensso.timbre
  [clojure.spec.alpha :as spec]))

(defmulti -justsaying-msg-handler "Multimethod to handle `justsaying-msg`s" :subject)

(defmethod network.dispatch/-event-msg-handler :justsaying [msg]
 (spec/assert :justsaying/subject msg)
 (-justsaying-msg-handler msg))

; (defmethod -justsaying-msg-handler)

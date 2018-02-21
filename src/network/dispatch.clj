(ns network.dispatch
 (:require
  cheshire.core
  camel-snake-kebab.extras
  camel-snake-kebab.core
  [clojure.spec.alpha :as spec]
  network.spec))

; @see onWebsocketMessage
(defmulti -event-msg-handler "Multimethod to handle `event-msg`s" :message/type)

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [ev-msg]
  (let [parsed (cheshire.core/parse-string ev-msg)
        [t msg] (camel-snake-kebab.extras/transform-keys camel-snake-kebab.core/->kebab-case-keyword parsed)
        t' (keyword t)
        msg' (merge {:message/type t'} msg)]
   (spec/assert :message/type t')
   (prn "*" msg')
   (-event-msg-handler msg')))

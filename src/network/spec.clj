(ns network.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/def :message/type #{:justsayin})

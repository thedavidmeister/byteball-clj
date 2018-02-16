(ns network.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/def :message/type #{:justsayin})

(spec/def :justsayin/subject #{; request data
                               "refresh"
                               ; login to hub
                               "hub/login"})

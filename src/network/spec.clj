(ns network.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/def :message/type #{:justsaying :request :response})

(spec/def :justsaying/subject #{; request data
                                "refresh"
                                ; login to hub
                                "hub/login"})

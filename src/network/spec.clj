(ns network.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/check-asserts true)

(spec/def :message/type #{:justsaying :request :response})

(spec/def :justsaying/subject #{; request data
                                "refresh"
                                "version"
                                ; login to hub
                                "hub/login"})

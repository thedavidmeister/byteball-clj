(ns network.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/check-asserts true)

(spec/def :message/type #{:justsaying :request :response})

(spec/def :justsaying/subject #{"refresh"
                                "version"
                                "joint"
                                "hub/login"
                                "hub/challenge"
                                "info"
                                "error"
                                "hub/push_project_number"
                                "hub/message_box_status"
                                "exchange_rates"})

(spec/def :request/command #{"subscribe"})

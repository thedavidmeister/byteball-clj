(ns network.spec
 (:require
  unit.spec
  [clojure.spec.alpha :as spec]))

(spec/check-asserts true)

(spec/def :message/type #{:justsaying :request :response})

(spec/def :justsaying/subject #{"refresh"
                                "version"
                                "joint"
                                "free_joints_end"
                                "hub/login"
                                "hub/challenge"
                                "info"
                                "error"
                                "hub/push_project_number"
                                "hub/message_box_status"
                                "exchange_rates"})

(spec/def :request/command #{"subscribe"})
(spec/def :request/tag string?)
(spec/def :request/params map?)
(spec/def :request/request
 (spec/keys
  :req-un [:request/command
           :request/tag
           :request/params]))

(spec/def :subscribe/subscription-id string?)
(spec/def :subscribe/last-mci :unit/main-chain-index)
(spec/def :subscribe/params
 (spec/merge
  :request/params
  not-empty
  (spec/keys
   :req-un [:subscribe/subscription-id
            :subscribe/last-mci])))

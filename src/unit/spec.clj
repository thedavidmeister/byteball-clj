(ns unit.spec
 (:require
  version.spec
  [clojure.spec.alpha :as spec]))

(spec/check-asserts true)

; https://github.com/thedavidmeister/byteball-clj/issues/29
(spec/def :unit/hash string?)
(spec/def :unit/main-chain-index pos-int?)

; https://github.com/thedavidmeister/byteball-clj/issues/30
(spec/def :unit.message.payload/input map?)
(spec/def :unit.message.payload/inputs
 (spec/coll-of
  :unit.message.payload/input
  :kind vector?))
(spec/def :unit.message.payload/output map?)
(spec/def :unit.message.payload/outputs
 (spec/coll-of
  :unit.message.payload/output
  :kind vector?))

(spec/def :unit.message/app
 #{"payment"
   "data_feed"
   "data"
   "profile"})
(spec/def :unit.message/payload-hash :unit/hash)
(spec/def :unit.message/payload-location #{"inline"})
(spec/def :unit.message.payload/payload
 (spec/keys
  :opt-un [:unit.message.payload/inputs
           :unit.message.payloads/outputs]))

(spec/def :unit.message/message
 (spec/keys
  :req-un [:unit.message/app
           :unit.message/payload-hash
           :unit.message/payload-location
           :unit.message.payload/payload]))
(spec/def :unit.message/messages
 (spec/coll-of
  :unit.message/message
  :kind vector?))

(spec/def :unit/payload-commission int?)
(spec/def :unit/alt :version/alt)

(spec/def :unit/id :unit/hash)
(spec/def :unit/unit :unit/id)
(spec/def :unit/parent-units
 (spec/coll-of
  :unit/id
  :kind vector?))
(spec/def :unit/last-ball :unit/id)

(spec/def :unit/headers-commission int?)
(spec/def :unit/witness-list-unit :unit/id)
(spec/def :unit/version :version/protocol-version)
(spec/def :unit/timestamp pos-int?)

; https://github.com/thedavidmeister/byteball-clj/issues/31
(spec/def :unit.author/address string?)
; https://github.com/thedavidmeister/byteball-clj/issues/32
(spec/def :unit.author/authentifiers map?)
(spec/def :unit.author/author
 (spec/keys
  :req-un [:unit.author/address
           :unit.author/authentifiers]))
(spec/def :unit.author/authors
 (spec/coll-of
  :unit.author/author
  :kind vector?))

(spec/def :unit/last-ball-unit :unit/id)

(spec/def :unit/unit-data
 (spec/keys
  :req-un [:unit/payload-commission
           :unit/alt
           :unit/unit
           :unit/parent-units
           :unit/last-ball
           :unit.message/messages
           :unit/main-chain-index
           :unit/headers-commission
           :unit/witness-list-unit
           :unit/version
           :unit/timestamp
           :unit/authors
           :unit/last-ball-unit]))

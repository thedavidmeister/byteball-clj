(ns network.login
 (:require
  serialize.source-string
  secp256k1.core
  secp256k1.formatting.base-convert
  crypto.keys
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]]))

(defn challenge-message->hash
 [message]
 (secp256k1.formatting.base-convert/byte-array-to-base
  (secp256k1.hashes/sha256
   (serialize.source-string/->source-string message))
  :hex))

(defn challenge-message->signature
 [message private-key]
 (let [signed-der
       (secp256k1.core/sign-hash
        private-key
        (challenge-message->hash message)
        :private-key-format :hex
        :input-format :hex
        :output-format :hex)
       signed
       (secp256k1.formatting.der-encoding/DER-decode-ECDSA-signature signed-der)
       signed-hex (str (:R signed) (:S signed))]
  (secp256k1.formatting.base-convert/base-to-base signed-hex :hex :base64)))

(defn unsigned-response-message
 [challenge public-key]
 {:challenge challenge
  :pubkey public-key})

(defn challenge->login-creds
 [challenge private-key public-key]
 (let [message (unsigned-response-message challenge public-key)
       signature (challenge-message->signature message private-key)]
  (merge
   message
   {:signature signature})))

(defn login!
 ([conn challenge]
  (let [ks (crypto.keys/ks)]
   (login! conn challenge (:private-key ks) (:public-key ks))))
 ([conn challenge private-key public-key]
  (network.send/just-saying! conn "hub/login" (challenge->login-creds challenge private-key public-key))))

; TESTS

(def ??message (unsigned-response-message crypto.test-data/hub-challenge crypto.test-data/public-key))
(def ??sig "cAT/c5zn4nb+5UnT5B++9ePvYdEE24qmPFTXbxYd2IE+4gQQNiHogRbyQRlXOLNto09JmRK0jHOyGeIttELkNA==")

(deftest ??challenge-message->hash
 (is
  (=
   "1ac78e688e34a4e70a2e9ccde66ed015fb7d16203691834f702b1f76e53baaa8"
   (challenge-message->hash ??message))))

(deftest ??challenge-message->signature
 (is
  (=
   "cAT/c5zn4nb+5UnT5B++9ePvYdEE24qmPFTXbxYd2IE+4gQQNiHogRbyQRlXOLNto09JmRK0jHOyGeIttELkNA=="
   (challenge-message->signature
    ??message
    crypto.test-data/private-key))))

(deftest ??challenge->login-creds
 (is
  (=
   (challenge->login-creds crypto.test-data/hub-challenge crypto.test-data/private-key crypto.test-data/public-key)
   (merge
    ??message
    {:signature ??sig}))))

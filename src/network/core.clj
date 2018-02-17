(ns network.core
 (:require
  aleph.http
  network.spec
  manifold.stream
  network.data
  cheshire.core
  pandect.algo.sha256
  serialize.source-string
  secp256k1.core
  pandect.utils.convert
  buddy.core.codecs.base64
  secp256k1.formatting.base-convert
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]])
 (:import org.bitcoin.NativeSecp256k1))

(defn -conn
 ([] (-conn network.data/hub-url))
 ([url]
  (aleph.http/websocket-client url)))
(def conn (memoize -conn))

(defn -ks
 []
 (let [raw (secp256k1.core/generate-address-pair)]
  {:public-key (secp256k1.core/x962-encode (:public-key raw) :output-format :base64)
   ; hex string is easier to cross-reference against byteballcore as it can be
   ; used directly in Buffer() in byteballcore but there is no biginteger in JS
   :private-key (.toString
                 (biginteger (:private-key raw))
                 16)}))
(def ks (memoize -ks))

; https://github.com/byteball/byteballcore/blob/master/network.js#L92
(defn send-message!
 [conn type content]
 {:pre [(spec/valid? :message/type type)]}
 (manifold.stream/put!
  @conn
  (cheshire.core/generate-string
   [(name type) content])))

; https://github.com/byteball/byteballcore/blob/master/network.js#L100
(defn just-sayin!
 [conn subject body]
 {:pre [(spec/valid? :justsayin/subject subject)]}
 (send-message! conn :justsayin {:subject subject :body body}))

(defn challenge-message->hash
 [message]
 (pandect.algo.sha256/sha256
  (serialize.source-string/->source-string message)))

(defn challenge-message->signature
 [message private-key]
 (let [private-bytes (buddy.core.codecs/hex->bytes private-key)
       hash-bytes (buddy.core.codecs/hex->bytes
                   (challenge-message->hash message))]
  (assert (. org.bitcoin.NativeSecp256k1 secKeyVerify private-bytes))

  (.encodeToString (java.util.Base64/getEncoder)
   (. org.bitcoin.NativeSecp256k1 sign
    hash-bytes
    private-bytes))))

(defn challenge->login-creds
 [challenge private-key public-key]
 (let [message {:challenge challenge
                :pubkey public-key}
       ; signature (secp256k1.core/sign-hash
       ;            private-key
       ;            (challenge-message->hash message)
       ;            :private-key-format :hex
       ;            :input-format :hex
       ;            :output-format :base64)]
       signature (challenge-message->signature message private-key)]

  (merge
   message
   {:signature signature})))

(defn login!
 [conn challenge private-key public-key]
 (just-sayin! conn "hub/login" (challenge->login-creds challenge private-key public-key)))

(defn joints-since!
 [conn mci]
 (just-sayin! conn "refresh" mci))

; TESTS

(def ??challenge "bUSwwUmABqPGAyRteUPKdaaq/wDM5Rqr+UL3sO/a")
(def ??priv "18d8bc95d3b4ae8e7dd5aaa77158f72d7ec4e8556a11e69b20a87ee7d6ac70b4")
(def ??pub "AqUMbbXfZg6uw506M9lbiJU/f74X5BhKdovkMPkspfNo")

(deftest ??challenge-message->hash
 (is
  (=
   "1ac78e688e34a4e70a2e9ccde66ed015fb7d16203691834f702b1f76e53baaa8"
   (challenge-message->hash {:challenge ??challenge :pubkey ??pub}))))

(deftest ??challenge-message->signature
 (is
  (=
   "cAT/c5zn4nb+5UnT5B++9ePvYdEE24qmPFTXbxYd2IE+4gQQNiHogRbyQRlXOLNto09JmRK0jHOyGeIttELkNA=="
   (challenge-message->signature
    {:challenge ??challenge :pubkey ??pub}
    ??priv))))

(deftest ??challenge->login-creds
 (is
  (=
   (challenge->login-creds ??challenge ??priv ??pub)
   {:challenge "bUSwwUmABqPGAyRteUPKdaaq/wDM5Rqr+UL3sO/a"
    :pubkey "AqUMbbXfZg6uw506M9lbiJU/f74X5BhKdovkMPkspfNo"
    :signature "cAT/c5zn4nb+5UnT5B++9ePvYdEE24qmPFTXbxYd2IE+4gQQNiHogRbyQRlXOLNto09JmRK0jHOyGeIttELkNA=="})))

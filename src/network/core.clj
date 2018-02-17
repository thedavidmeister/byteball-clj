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
  secp256k1.formatting.base-convert
  [clojure.spec.alpha :as spec]
  clojure.core.async
  [clojure.test :refer [deftest is]])
 (:import org.bitcoin.NativeSecp256k1))

(def responses (atom []))

(defn -conn
 ([] (-conn network.data/hub-url))
 ([url]
  (let [c @(aleph.http/websocket-client url)]
        ; chan (clojure.core.async/chan)]
   ; (manifold.stream/consume prn c)
   ; (manifold.stream/connect c chan)
   ; (clojure.core.async/go
   ;  (swap! responses conj (clojure.core.async/<! chan)))
   c)))
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
  conn
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

(defn valid-signature?
 [hash sig public-key]
 (. org.bitcoin.NativeSecp256k1 verify
  hash
  sig
  public-key))

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

(defn challenge->login-creds
 [challenge private-key public-key]
 (let [message {:challenge challenge
                :pubkey public-key}
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
(def ??message {:challenge ??challenge :pubkey ??pub})
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
    ??priv))))

(deftest ??challenge->login-creds
 (is
  (=
   (challenge->login-creds ??challenge ??priv ??pub)
   (merge
    ??message
    {:signature ??sig}))))

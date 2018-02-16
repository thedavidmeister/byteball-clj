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
  [clojure.spec.alpha :as spec]))

(defn -conn
 ([] (-conn network.data/hub-url))
 ([url]
  (aleph.http/websocket-client url)))
(def conn (memoize -conn))

(defn -ks
 []
 (let [raw (secp256k1.core/generate-address-pair)]
  {:public-key (secp256k1.core/x962-encode (:public-key raw) :output-format :base64 :compressed false)
   :private-key (:private-key raw)}))
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

(defn challenge->login-creds
 [challenge private-key public-key]
 (let [message {:challenge challenge
                :pubkey public-key}
       signature (secp256k1.core/sign
                  private-key
                  (serialize.source-string/->source-string message))]
  (merge
   message
   {:signature signature})))

(defn login!
 [conn challenge private-key public-key]
 (just-sayin! conn "hub/login" (challenge->login-creds challenge private-key public-key)))

(defn joints-since!
 [conn mci]
 (just-sayin! conn "refresh" mci))

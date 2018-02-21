(ns crypto.keys
 (:require
  secp256k1.core
  crypto.test-data
  [clojure.test :refer [deftest is]]))

(defn -ks
 "Generates a new public/private key pair. Public key is base64 encoded EC point, private key is hex string."
 []
 (let [raw (secp256k1.core/generate-address-pair)]
  {:public-key (secp256k1.core/x962-encode (:public-key raw) :output-format :base64)
   ; hex string is easier to cross-reference against byteballcore as it can be
   ; used directly in Buffer() in byteballcore but there is no biginteger in JS
   :private-key (.toString
                 (biginteger (:private-key raw))
                 16)}))
(def ks (memoize -ks))

(defn private-key?
 [k]
 (= java.math.BigInteger (type k)))

; TESTS

; @see login.test.js

(deftest ??private-key
 ; smoke test, not exactly equivalent to `ecdsa.privateKeyVerify()` in core
 (is (bytes? (secp256k1.core/private-key crypto.test-data/private-key))))

(deftest ??public-key
 ; (is (secp256k1.core/valid-point? (secp256k1.core/public-key crypto.test-data/public-key)))
 ; parsing our test public key is equivalent to generating one from the test private key
 (is
  (=
   (secp256k1.core/public-key crypto.test-data/public-key)
   (secp256k1.core/public-key (secp256k1.core/private-key crypto.test-data/private-key)))))

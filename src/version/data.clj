(ns version.data
 (:require
  [clojure.spec.alpha :as spec]))

(def protocol-version "1.0")
(def alt "1")

(spec/assert :version/protocol protocol-version)
(spec/assert :version/alt alt)

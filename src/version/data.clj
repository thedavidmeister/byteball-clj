(ns version.data
 (:require
  version.spec
  [clojure.spec.alpha :as spec]))

(def protocol-version "1.0")
(def alt "1")

(spec/assert :version/protocol-version protocol-version)
(spec/assert :version/alt alt)

(def version.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/check-asserts true)

(spec/def :version/alt #{"1"})
(spec/def :version/protocol-version #{"1.0"})
(spec/def :version/library-version #{"0.2.70"})

(spec/def :version/version
 (spec/keys
  :req-un [:version/alt
           :version/protocol-version
           :version/library-version]))

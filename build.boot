(def project 'thedavidmeister/byteball-clj)
(def version "0.1.0-SNAPSHOT")

(set-env!
 :source-paths #{"src"}
 :dependencies
 '[; scaffolding...
   [org.clojure/clojure "1.9.0"]
   [aleph "0.4.4"]
   [manifold "0.1.6"]
   [cheshire "5.8.0"]
   [samestep/boot-refresh "0.1.0"]])

(require
 '[samestep.boot-refresh :refer [refresh]])

(deftask repl-server
 []
 (comp
  (watch)
  (refresh)
  (repl :server true)))

(deftask repl-client
 []
 (repl :client true))

(ns unit.api
 (:require
  network.send))

(defn free-joints!
 [conn]
 ; don't send a high mci as it makes little sense for the unstable DAG
 (network.send/just-saying! conn "refresh" 0))

(defn catchup!
 [conn]
 (network.send/request!
  conn
  "catchup"
  {:witnesses ["BVVJ2K7ENPZZ3VYZFWQWK7ISPCATFIW3"
               "DJMMI5JYA5BWQYSXDPRZJVLW3UGL3GJS"
               "FOPUBEUPBC6YLIQDLKL6EW775BMV7YOH"
               "GFK3RDAPQLLNCMQEVGGD2KCPZTLSG3HN"
               "H5EZTQE7ABFH27AUDTQFMZIALANK6RBG"
               "I2ADHGP4HL6J37NQAD73J7E5SKFIXJOT"
               "JEDZYC2HMGDBIDQKG3XSTXUSHMCBK725"
               "JPQKPRI5FMTQRJF4ZZMYZYDQVRD55OTC"
               "OYW2XTDKSNKGSEZ27LMGNOPJSYIXHBHC"
               "S7N5FE42F6ONPNDQLCF64E2MGFYKQR2I"
               "TKT4UESIKTTRALRRLWS4SENSTJX6ODCW"
               "UENJPVZ7HVHM6QGVGT6MWOJGGRTUTJXQ"]
   :last_stable_mci 1935241
   :last_known_mci 1935251}))

(defn subscribe!
 [conn]
 (network.send/request!
  conn
  "subscribe"
  {:subscription_id (gensym)
   :last_mci 0}))

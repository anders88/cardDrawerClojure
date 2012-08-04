(ns cardDrawerClojure.server
  (:use noir.core)
  (:use noir.request)
  (:require [noir.server :as server]))

(defpage "/" []
    "Welcome to Noiraazz")


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "1337"))
        ]
    (server/start port {:mode mode
                        :ns 'sweepergame})))
(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js]]
        )
  (:require [noir.server :as server])
)

(def counter (ref 0))

(defpartial status-content [value]
  [:div {:id "someid"} (str "Date is " (new java.util.Date) " Value is " value)]
  )

(defpage "/" []
    (html5 
      [:head
    [:title "Dummy title"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 "Headline"] (status-content @counter)])
)

(defpage "/update.html" []
  (status-content @counter)
  )

(defpage "/increase" []
  (dosync (ref-set counter (inc @counter)))
  (html5 [:body (str "Counter increased to " @counter)])
  )

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "1337"))
        ]
    (server/start port {:mode mode
                        :ns 'sweepergame})))
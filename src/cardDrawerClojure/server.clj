(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js]]
        )
  (:require [noir.server :as server])
)

(defpartial status-content []
  [:div {:id "someid"} (str "Date is " (new java.util.Date))]
  )

(defpage "/" []
    (html5 
      [:head
    [:title "Dummy title"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 "Headline"] (status-content)])
)

(defpage "/update.html" []
  (status-content)
  )


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "1337"))
        ]
    (server/start port {:mode mode
                        :ns 'sweepergame})))
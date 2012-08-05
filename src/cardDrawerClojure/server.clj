(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js]]
        [hiccup.form-helpers]
        [noir.response :only [redirect]]
        )
  (:require [noir.server :as server])
)

(def counter (ref 0))

(defpartial status-content [value]
  [:div {:id "someid"} (str "Date is " (new java.util.Date) " Value is " value)]
  )

(defpartial update-form []
  (form-to [:post "/updatevalue"]
     (label "newval" "New value:")
     (text-field "newval")
     (submit-button "Update value")
  ))

(defn to-int [s]
  (try (Integer/parseInt s) (catch NumberFormatException e nil)))

(defpage [:post "/updatevalue"] {:as updateobject}
  (let [aval (to-int (updateobject :newval))]
    (if (not (nil? aval)) (dosync (ref-set counter aval))) 
  )
  (redirect "/status")
  )

(defpage "/" []
  (html5 [:body "This is a mainpage"])
  )

(defpage "/status" []
    (html5 
      [:head
    [:title "Dummy title"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 "Headline"] (status-content @counter) (update-form)])
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
(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js]]
        [hiccup.form-helpers]
        [noir.response :only [redirect]]
        )
  (:require [noir.server :as server])
)

(def game (ref {:cards {:deck (vec (range 1 21))}}))

(defpartial status-content [game]
  [:div {:id "someid"} 
    [:p (str "Date is " (new java.util.Date))]
    [:ul
      [:li (str "Cards in deck " (count ((game :cards) :deck)))]
      ]
  ]
  )

(defpartial update-form []
  (form-to [:post "/updatevalue"]
     (label "newval" "New value:")
     (text-field "newval")
     (submit-button "Update value")
  ))

(defn to-int [s]
  (try (Integer/parseInt s) (catch NumberFormatException e nil)))


(defpage [:post "/register"] {:as registerobject}
  (let [name (registerobject :name)]
    (redirect (str "/status?name=" name))
  )
  )

(defpartial name-reg-form []
  (form-to [:post "/register"]
     (label "newval" "Your name")
     (text-field "name")
     (submit-button "To the game"))
  )

(defpage "/" []
  (html5 [:body [:h1 "Welcome"] (name-reg-form)])
  )

(defpartial name-part [name]
  [:div {:id "namediv", :style "display: none;"} name]
  )

(defpartial reload-part [name]
  [:p (status-content @game)]
  )


(defpage  [:get "/status"] {:as nameobject}
    (html5 
      [:head
    [:title "Dummy title"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 "Headline"] 
            (name-part (nameobject :name)) 
           (reload-part (nameobject :name)) [:p (update-form)]])
)


(defpage [:get "/update.html"] {:as nameobject}
  (reload-part (nameobject :name))
  )


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "1337"))
        ]
    (server/start port {:mode mode
                        :ns 'sweepergame})))
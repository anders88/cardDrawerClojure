(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js]]
        [hiccup.form-helpers]
        [noir.response :only [redirect]]
        )
  (:require [noir.server :as server])
)

(def game (ref {:cards {"Anders" [33 34] :deck (vec (range 1 21)) :discarded [30 25 32]}}))

(defn format-list [cards]
  (reduce #(str %1 ", " %2) (sort cards))
  )

(defpartial status-content [game player-name]
  [:div {:id "someid"} 
    [:p (str "Date is " (new java.util.Date))]
    [:ul
      [:li (str "Your cards: " (format-list ((game :cards) player-name)))]
      [:li (str "Cards in deck: " (count ((game :cards) :deck)))]
      [:li (str "Discarded cards: " (format-list ((game :cards) :discarded)))]
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
  (let [player-name (registerobject :name)]
    (redirect (str "/status?name=" player-name))
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

(defpartial name-part [player-name]
  [:div {:id "namediv", :style "display: none;"} player-name]
  )

(defpartial reload-part [name]
  [:p (status-content @game name)]
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
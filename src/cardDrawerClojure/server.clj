(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [html5 include-js link-to]]
        [hiccup.form-helpers]
        [noir.response :only [redirect]]
        [cardDrawerClojure.core]  
        )
  (:require [noir.server :as server])
)

(def game (ref {:cards {:deck (vec (range 1 9)) :discarded []} :maxc 8}))


(defn format-list [cards]
  (if (empty? cards) "None"
  (reduce #(str %1 ", " %2) (sort cards))
  ))

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

(defpage [:post "/register"] {:as registerobject}
  (let [player-name (registerobject :name)]
    (dosync (ref-set game (register-player @game player-name)))
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

(defn handle-result-part [player-name result]
  (if (map? result)
  (let [player player-name]
  (dosync (ref-set game result))
  (redirect (str "/status?name=" player-name)))
  (html5 [:body [:h1 "Error"] [:p result] [:p (link-to (str "/status?name=" player-name) "Back")]])
  ))


(defpartial draw-card-part [name]
  (form-to [:post "/drawCard"]
     (hidden-field "name" name)     
     (submit-button "Draw card"))
  )
  
(defpage [:post "/drawCard"] {:as registerobject}
  (let [player-name (registerobject :name)]    
    (handle-result-part player-name (draw-card @game player-name))    
  )
  )

(defpartial discard-card-part [name]
  (form-to [:post "/discardCard"]
     (hidden-field "name" name)
     (text-field "card")  
     (submit-button "Discard card"))
  )


(defpage  [:get "/status"] {:as nameobject}
    (html5 
      [:head
    [:title "Dummy title"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 "Headline"] 
            (name-part (nameobject :name)) 
           (reload-part (nameobject :name))
           (draw-card-part (nameobject :name))
           (discard-card-part (nameobject :name))])
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
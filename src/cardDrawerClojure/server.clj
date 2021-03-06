(ns cardDrawerClojure.server
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [html5 include-js link-to unordered-list]]
        [hiccup.form-helpers]
        [noir.response :only [redirect]]
        [cardDrawerClojure.core]
        )
  (:require [noir.server :as server])
)

(def game (ref {:cards {:deck (vec (range 1 9)) :discarded [] :oop []} :maxc 8 :lastDrawn {} :roll 0 :rollBy "None" :numRolls 0 :eventlog []}))


(defn format-list [cards]
  (if (empty? cards) "None"
  (reduce #(str %1 ", " %2) (sort cards))
  ))

(defpartial status-content [game player-name]
  [:div {:id "someid"}
    [:p (str "Date is " (new java.util.Date))]
    [:ul
      [:li (str "Last drawn card: " ((game :lastDrawn) player-name))]
      [:li (str "Your cards: " (format-list ((game :cards) player-name)))]
      [:li (str "Number of cards in deck: " (count ((game :cards) :deck)))]
      [:li (str "Discarded cards: " (format-list ((game :cards) :discarded)))]
      [:li (str "Out of play: " (format-list ((game :cards) :oop)))]
      [:li (str "Admin seen by " (game :adminSeenBy) " at " (game :adminSeen))]
      ]
   [:h3 (str "Last roll " (game :roll) " by " (game :rollBy) " (Total rolls is " (game :numRolls) ")")]
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

(defn add-log-message [game message]
  (assoc game :eventlog (conj (game :eventlog) (str (new java.util.Date) "->" message)))
)

(defn handle-result-part [player-name result message]
  (if (map? result)
  (let [player player-name res-with-log (add-log-message result message)]
  (dosync (ref-set game res-with-log))
  (redirect (str "/status?name=" player-name)))
  (html5 [:body [:h1 "Error"] [:p result] [:p (link-to (str "/status?name=" player-name) "Back")]])
  ))


(defpartial draw-card-part [name]
  (form-to [:post "/drawCard"]
     (hidden-field "name" name)
     (submit-button "Draw card"))
  )

(defpartial roll-dice-part [name]
  (form-to [:post "/rollDice"]
     (hidden-field "name" name)
     (submit-button "Roll dice"))
  )

(defpage [:post "/drawCard"] {:as registerobject}
  (let [player-name (registerobject :name)]
    (handle-result-part player-name (draw-card @game player-name) (str "Card drawn by" player-name))
  )
  )

(defpartial discard-card-part [name]
  (form-to [:post "/discardCard"]
     (hidden-field "name" name)
     (text-field "card")
     (submit-button "Discard card"))
  )

(defpage [:post "/discardCard"] {:as registerobject}
  (handle-result-part (registerobject :name) (discard-card @game (registerobject :card)) (str "Discarded card " (registerobject :card)))
  )

(defpartial oop-card-part [name]
  (form-to [:post "/oopCard"]
     (hidden-field "name" name)
     (text-field "card")
     (submit-button "Out of play"))
  )

(defpage [:post "/oopCard"] {:as registerobject}
  (handle-result-part (registerobject :name) (out-of-play-card @game (registerobject :card)) (str "Card out of play " (registerobject :card)))
  )

(defpartial add-card-part [name]
  (form-to [:post "/addCard"]
     (hidden-field "name" name)
     (text-field "card")
     (submit-button "Add cards to deck"))
  )


(defpage [:post "/addCard"] {:as registerobject}
  (handle-result-part (registerobject :name) (add-new-cards @game (registerobject :card)) (str "New cards added max " (registerobject :card)))
  )

(defpartial add-log-part [name]
  (form-to [:post "/addLog"]
     (hidden-field "name" name)
     (text-field "logmessage")
     (submit-button "Add to log"))
  )


(defpage [:post "/addLog"] {:as registerobject}
   (let [new-game (add-log-message @game (registerobject :logmessage))]
     (dosync (ref-set game new-game))
     (redirect (str "/status?name=" (registerobject :name))))
)

(defpage [:post "/rollDice"] {:as registerobject}
  (let [new-game (roll-dice @game (registerobject :name))]
  (handle-result-part (registerobject :name) new-game (str "Dice roll " (new-game :roll) " by " (registerobject :name)))
))

(defpage [:get "/score"] {:as nameobject}
  (reload-part (nameobject :name))
)


(defpage  [:get "/status"] {:as nameobject}
    (html5
      [:head
    [:title "Draw card game"]
    (include-js "/jquery-1.7.2.js") (include-js "/reload.js")]
      [:body [:h1 (str "Hello " (nameobject :name))]
            (name-part (nameobject :name))
           (reload-part (nameobject :name))
           (draw-card-part (nameobject :name))
           (roll-dice-part (nameobject :name))
           (discard-card-part (nameobject :name))
           (oop-card-part (nameobject :name))
           [:p (link-to (str "/admin?name=" (nameobject :name)) "Admin")]
           [:p (link-to (str "/newgame?name=" (nameobject :name)) "New game")]
           (add-card-part (nameobject :name))
           (add-log-part (nameobject :name))
           [:p (link-to (str "/log?name=" (nameobject :name)) "Log")]

      ])
)


(defpage [:get "/update.html"] {:as nameobject}
  (reload-part (nameobject :name))
  )

(defn card-status-display [status-elem player-list player-name]
  (let [card (status-elem :cardNo)]
  (html [:div (str card ": " (status-elem :status) " - ")
  (str "<a href=\"/adminupdate?card=" card "&status=zdeck&name=" player-name "\">To deck</a>, ")
  (str "<a href=\"/adminupdate?card=" card "&status=zdiscard&name=" player-name "\">Discard</a>, ")
  (str "<a href=\"/adminupdate?card=" card "&status=zoop&name=" player-name "\">Out of play</a>, ")
  (reduce #(str %1 ", " %2)
  (map
    #(str "<a href=\"/adminupdate?card=" card "&status=" % "&name=" player-name "\">" % "</a>, ")
     player-list))
  ])
  ))

(defpage  [:get "/admin"] {:as nameobject}
  (dosync (ref-set game (assoc @game :adminSeen (str (new java.util.Date)) :adminSeenBy (nameobject :name))))
  (let [summary (admin-summary @game)]
  (html5 [:body
    [:h1 "Admin page"]
    (unordered-list (map #(card-status-display % (summary :players) (nameobject :name))
     (summary :cards)))
    (link-to (str "/status?name=" (nameobject :name)) "Back")
         ]))
)

(defpage [:get "/log"] {:as nameobject}
  (html5 [:body
          [:h1 "Eventlog"]
          (unordered-list (@game :eventlog))
          (link-to (str "/status?name=" (nameobject :name)) "Back")
          ])
)


(defn compute-adminstatus [stat]
  (cond
    (= stat "zdeck") :deck
    (= stat "zdiscard") :discard
    (= stat "zoop") :oop
    :else stat
  )
  )

(defpage [:get "/adminupdate"]  {:as updateobject}
  (let [stat (compute-adminstatus (updateobject :status))]
  (dosync (ref-set game (move-card @game (to-int (updateobject :card)) stat)))
  )
  (redirect (str "/admin?name=" (updateobject :name)))
  )

(defpage [:get "/newgame"] {:as nameobject}
  (html5
  [:body [:h1 "Start new game"]
    (form-to [:post "/createGame"]
     (hidden-field "name" (nameobject :name))
      (label "newval" "Number of cards:")
     (text-field "numcards")
     (submit-button "Start new game"))]
  ))

(defpage [:post "/createGame"] {:as newobject}
  (handle-result-part (newobject :name) (create-new-game @game (newobject :numcards)))
  )

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "1337"))
        ]
    (server/start port {:mode mode
                        :ns 'sweepergame})))
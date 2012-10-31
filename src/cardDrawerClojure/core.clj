(ns cardDrawerClojure.core)

(defn to-int [s]
  (try (Integer/parseInt s) (catch NumberFormatException e nil)))

(defn read-card [card game]
  (let [card-no (to-int card)]
    (cond
      (nil? card-no) "Illegal card"
      (or (> card-no (game :maxc)) (< card-no 1)) "Illegal card"
      :else card-no
    )))

(defn register-player [game name]
  (assoc game :cards (assoc (game :cards) name []))
)

(defn update-deck [game player new-hand]
  (assoc game :cards (assoc (game :cards) player new-hand))
  )

(defn remove-item [listing number]
  (vec (concat (subvec (vec listing) 0 number)
        (if (= (inc number) (count listing)) [] (subvec (vec listing) (inc number))))
  ))

(defn remove-from-all [items remove-value]
  (reduce #(merge %1 %2) (map (fn [entry] {(entry 0) (vec (filter #(not (= % remove-value)) (entry 1)))}) items))
)

(defn move-card [game card move-to]
  (assoc game :cards (assoc
    (remove-from-all (game :cards) card) move-to (conj ((game :cards) move-to) card))
  )
)


(defn draw-card [game player]
  (let [deck ((game :cards) :deck) discarded ((game :cards) :discarded)]
  (cond
    (and (empty? deck) (empty? discarded)) "No cards left"
    (empty? deck) (draw-card (assoc game :cards (assoc (game :cards) :deck discarded :discarded [])) player)
  :else
  (let [pick (((game :cards) :deck) (rand-int (count ((game :cards) :deck))))]
      (assoc (move-card game pick player) :lastDrawn (assoc (game :lastDrawn) player pick)))
  )))


(defn discard-card [game card]
  (let [card-no (read-card card game)]
  (if (integer? card-no) (move-card game card-no :discarded)
  card-no
 ))
)

(defn out-of-play-card [game card]
  (let [card-no (read-card card game)]
  (if (integer? card-no) (move-card game card-no :oop)
  card-no
  ))
)

(defn card-status [game card-no]
  (first (first (filter #(contains? (set (second %)) card-no) (game :cards))))
  )

(defn player-list [game]
  (set (map #(first %) (filter #(string? (first %)) (game :cards))))
  )

(defn admin-summary [game]
  {:cards
  (map (fn [card-no] {:cardNo card-no :status (card-status game card-no)}) (range 1 (inc (game :maxc))))
  :players (player-list game)
  }
  )

(defn create-new-game [game numcards]
  (let [numc (read-card numcards (assoc game :maxc 300))]
  (if (integer? numc)
  (assoc game :maxc numc :cards
    (merge (reduce merge (map (fn [el] {el []}) (player-list game))) {:deck (vec (range 1 (inc numc))) :discarded [] :oop []})
    )
  "Card must be between 1 and 300"
  )
))

(defn add-new-cards [game numcards]
  (let [numc (read-card numcards (assoc game :maxc 300)) cards (game :cards)]
  (if (integer? numc)
    (assoc game :maxc numc :cards
           (assoc cards :deck (concat (cards :deck) (range (inc (game :maxc)) (inc numc)))))
    (str "Number of cards must be between " (inc (game :maxc)) " and 300")
  )))
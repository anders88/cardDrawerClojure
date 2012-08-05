(ns cardDrawerClojure.core)

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


(defn draw-card [game player]
  (let [hand ((game :cards) player) deck ((game :cards) :deck) pick (rand-int (count ((game :cards) :deck)))]
    (update-deck (update-deck game player (conj hand (deck pick)))
       :deck (remove-item deck pick)
    )

  ))

(defn remove-from-all [items remove-value]
  (reduce #(merge %1 %2) (map (fn [entry] {(entry 0) (vec (filter #(not (= % remove-value)) (entry 1)))}) items))
)

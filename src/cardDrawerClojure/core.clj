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
      (move-card game pick player))
  )))


(defn discard-card [game card]
  (move-card game card :discarded)
)
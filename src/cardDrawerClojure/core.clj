(ns cardDrawerClojure.core)

(defn register-player [game name]
  (assoc game :players (assoc (game :players) name []))
)

(defn update-player-deck [game player new-hand]
  (assoc game :players (assoc (game :players) player new-hand))
  )

(defn remove-item [listing number]
  (vec (concat (subvec (vec listing) 0 number) 
        (if (= (inc number) (count listing)) [] (subvec (vec listing) (inc number))))
  ))


(defn draw-card [game player]
  (let [hand ((game :players) player) deck (game :deck) pick (rand-int (count (game :deck)))]
  (update-player-deck (assoc game :deck (remove-item deck pick)) player (conj hand (deck pick)))

  ))



(defn pick-random [listing picks]
  (if (<= picks 0) []
  (let [number (rand-int (count listing))]
  (cons ((vec listing) number) (pick-random 
        (remove-item listing number)  
        (dec picks)))
  )))
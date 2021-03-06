(ns cardDrawerClojure.core-test
  (:use clojure.test
        cardDrawerClojure.core))

(deftest register-test
  (testing "That one can register and start a new game"
    (is (= {:cards {"Darth" []}} (register-player {:cards {}} "Darth")) "First player")
    (is (= {:cards {"Darth" [] "Luke" []}} (register-player {:cards {"Darth" []}} "Luke")) "Second player")
))

(deftest draw-card-test
  (testing "Drawing cards"
    (is (= {:cards {"Darth" [1] :deck [] :discarded []} :lastDrawn {"Darth" 1}} (draw-card {:cards {"Darth" [] :deck [1] :discarded []} :lastDrawn {}} "Darth")) "Drawing the last card")
    (is (= {:cards {"Darth" [1] :deck [] :discarded []} :lastDrawn {"Darth" 1}} (draw-card {:cards {"Darth" [] :deck [] :discarded [1]} :lastDrawn {}} "Darth")) "Reshuffle discarded")
    (is (= "No cards left" (draw-card {:cards {"Darth" [1] :deck [] :discarded []} :lastDrawn {}} "Darth")) "Errormessage when no cards left")
    )
  )


(deftest remove-from-all-test
  (testing "Remove all"
    (is (= {:a [1 2 3]} (remove-from-all {:a [1 2 4 3]} 4)))
    )
  )

(deftest discard-test
  (testing "Discarding cards"
    (is (= {:cards {"Darth" [] :deck [4] :discarded [3]} :maxc 10}
      (discard-card {:cards {"Darth" [3] :deck [4] :discarded []} :maxc 10} "3")) "Discarding a card")
    (is (= "Illegal card"
      (discard-card {:cards {"Darth" [3] :deck [4] :discarded []} :maxc 10} "11")) "To high card")
    )
  )

(deftest admin-summary-test
  (testing "Admin summary"
    (is (= {:cards [
      {:cardNo 1 :status :deck}
      {:cardNo 2 :status "Darth"}
      {:cardNo 3 :status "Luke"}
      {:cardNo 4 :status :deck}
      {:cardNo 5 :status :discarded}
      {:cardNo 6 :status :oop}
      ] :players #{"Darth" "Luke"}}
      (admin-summary {:cards {"Darth" [2] "Luke" [3] :deck [1 4] :discarded [5] :oop [6]} :maxc 6})
    ) "Cheking summary")
    (is (= "Luke" (card-status {:cards {"Darth" [2] "Luke" [3] :deck [1 4] :discarded [5] :oop [6]} :maxc 5} 3))
       "Cheking card status")
))

(deftest new-game-test
  (testing "New game"
    (is (= {:cards {"Darth" [] "Luke" [] :deck [1 2 3 4 5] :discarded [] :oop []} :maxc 5}
      (create-new-game {:cards {"Darth" [2] "Luke" [3] :deck [1 4] :discarded [5] :oop [6]} :maxc 6} "5"))
    )
    (is (= "Card must be between 1 and 300"
      (create-new-game {:cards {"Darth" [2] "Luke" [3] :deck [1 4] :discarded [5] :oop [6]} :maxc 6} "400"))
    )
    )
  )

(deftest add-more-card-test
  (testing "Adding more cards"
    (is (= {:cards {"Darth" [4 5] "Luke" [2] :deck [1 3 6 7 8 9 10] :discarded [] :oop []} :maxc 10}
           (add-new-cards {:cards {"Darth" [4 5] "Luke" [2] :deck [1 3] :discarded [] :oop []} :maxc 5} "10")
           ))
    (is (= "Number of cards must be between 6 and 300"
           (add-new-cards {:cards {"Darth" [4 5] "Luke" [2] :deck [1 3] :discarded [] :oop []} :maxc 5} "xx")
           ))

    ))

(deftest roll-dice-test
  (testing "Rolling"
    (let [gameres (roll-dice {:roll 0 :rollBy "None" :numRolls 0} "Darth")]
    (is (= "Darth" (gameres :rollBy)))
    (is (= 1 (gameres :numRolls)))
    (is (> (gameres :roll) 0))
    (is (< (gameres :roll) 7))
)))
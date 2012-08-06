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
    (is (= {:cards {"Darth" [1] :deck [] :discarded []}} (draw-card {:cards {"Darth" [] :deck [1] :discarded []}} "Darth")) "Drawing the last card")
    (is (= {:cards {"Darth" [1] :deck [] :discarded []}} (draw-card {:cards {"Darth" [] :deck [] :discarded [1]}} "Darth")) "Reshuffle discarded")
    (is (= "No cards left" (draw-card {:cards {"Darth" [1] :deck [] :discarded []}} "Darth")) "Errormessage when no cards left")
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
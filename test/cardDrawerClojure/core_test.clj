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
    (is (= {:cards {"Darth" [1] :deck []}} (draw-card {:cards {"Darth" [] :deck [1]}} "Darth")) "Drawing the last card")
    )
  )

(deftest remove-from-all-test
  (testing "Remove all"
    (is (= {:a [1 2 3]} (remove-from-all {:a [1 2 4 3]} 4)))
    )
  )